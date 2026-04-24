package com.quiz.service;

import com.quiz.model.ApiResponse;
import com.quiz.model.Event;
import com.quiz.model.LeaderboardEntry;
import com.quiz.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Core business logic service for handling quiz data processing.
 * Handles API polling, deduplication, score aggregation, and leaderboard generation.
 */
public class QuizService {
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    private static final int POLL_COUNT = 10;

    private final String apiUrl;
    private final String submitUrl;

    public QuizService(String apiUrl, String submitUrl) {
        this.apiUrl = apiUrl;
        this.submitUrl = submitUrl;
    }

    /**
     * Polls the API multiple times to collect quiz score data.
     * @return Set of unique events (deduplicated by roundId + participant)
     */
    public Set<Event> pollApiMultipleTimes() {
        Set<String> uniqueKeys = new HashSet<>();
        Set<Event> uniqueEvents = new HashSet<>();

        logger.info("Starting API polling - {} times", POLL_COUNT);

        for (int i = 1; i <= POLL_COUNT; i++) {
            try {
                logger.info("Poll #{}: Fetching data from {}", i, apiUrl);
                ApiResponse response = HttpClientUtil.getQuizData(apiUrl);

                if (response != null && response.getData() != null) {
                    for (Event event : response.getData()) {
                        String uniqueKey = event.getUniqueKey();
                        
                        if (uniqueKeys.add(uniqueKey)) {
                            uniqueEvents.add(event);
                            logger.debug("Added unique event: {}", event);
                        } else {
                            logger.debug("Skipped duplicate event with key: {}", uniqueKey);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Error during poll #{}: {}", i, e.getMessage(), e);
            }

            // Small delay between polls to avoid overwhelming the API
            if (i < POLL_COUNT) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Poll thread interrupted");
                    break;
                }
            }
        }

        logger.info("API polling completed. Collected {} unique events", uniqueEvents.size());
        return uniqueEvents;
    }

    /**
     * Aggregates scores by participant from the unique events.
     * @param events Set of unique events
     * @return Map of participant names to their total scores
     */
    public Map<String, Integer> aggregateScores(Set<Event> events) {
        Map<String, Integer> scoreMap = new HashMap<>();

        for (Event event : events) {
            scoreMap.merge(event.getParticipant(), event.getScore(), Integer::sum);
        }

        logger.info("Score aggregation completed. {} participants found", scoreMap.size());
        scoreMap.forEach((participant, score) -> 
            logger.debug("Participant: {}, Total Score: {}", participant, score)
        );

        return scoreMap;
    }

    /**
     * Generates a leaderboard sorted by total score in descending order.
     * @param scoreMap Map of participants and their total scores
     * @return List of LeaderboardEntry objects sorted by score (descending)
     */
    public List<LeaderboardEntry> generateLeaderboard(Map<String, Integer> scoreMap) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            leaderboard.add(new LeaderboardEntry(entry.getKey(), entry.getValue()));
        }

        // Sort by score in descending order (highest first)
        Collections.sort(leaderboard);

        // Assign ranks
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        logger.info("Leaderboard generated with {} entries", leaderboard.size());
        return leaderboard;
    }

    /**
     * Submits the generated leaderboard back to the API.
     * @param leaderboard List of leaderboard entries
     * @return true if submission was successful, false otherwise
     */
    public boolean submitLeaderboard(List<LeaderboardEntry> leaderboard) {
        ApiResponse response = new ApiResponse();
        List<Event> leaderboardEvents = new ArrayList<>();

        for (LeaderboardEntry entry : leaderboard) {
            Event event = new Event("leaderboard", entry.getParticipant(), entry.getTotalScore());
            leaderboardEvents.add(event);
        }

        response.setData(leaderboardEvents);
        response.setStatus("success");
        response.setMessage("Leaderboard aggregated from " + leaderboard.size() + " participants");

        logger.info("Submitting leaderboard to {}", submitUrl);
        return HttpClientUtil.submitLeaderboard(submitUrl, response);
    }

    /**
     * Main orchestration method that runs the complete workflow.
     * @return true if the entire process completes successfully
     */
    public boolean executeWorkflow() {
        logger.info("========== QUIZ LEADERBOARD WORKFLOW STARTED ==========");

        try {
            // Step 1: Poll API and collect unique events
            Set<Event> uniqueEvents = pollApiMultipleTimes();
            logger.info("Step 1 Complete: {} unique events collected", uniqueEvents.size());

            // Step 2: Aggregate scores
            Map<String, Integer> scoreMap = aggregateScores(uniqueEvents);
            logger.info("Step 2 Complete: {} participants with aggregated scores", scoreMap.size());

            // Step 3: Generate leaderboard
            List<LeaderboardEntry> leaderboard = generateLeaderboard(scoreMap);
            logger.info("Step 3 Complete: Leaderboard generated with {} entries", leaderboard.size());

            // Step 4: Display leaderboard
            displayLeaderboard(leaderboard);

            // Step 5: Submit leaderboard
            boolean submitted = submitLeaderboard(leaderboard);
            if (submitted) {
                logger.info("Step 5 Complete: Leaderboard submitted successfully");
            } else {
                logger.warn("Step 5 Failed: Could not submit leaderboard");
            }

            logger.info("========== QUIZ LEADERBOARD WORKFLOW COMPLETED ==========");
            return submitted;

        } catch (Exception e) {
            logger.error("Workflow execution failed", e);
            return false;
        }
    }

    /**
     * Displays the leaderboard in a formatted manner.
     * @param leaderboard List of leaderboard entries
     */
    private void displayLeaderboard(List<LeaderboardEntry> leaderboard) {
        logger.info("\n========== FINAL LEADERBOARD ==========");
        logger.info(String.format("%-6s %-20s %-15s", "Rank", "Participant", "Total Score"));
        logger.info("========================================");

        for (LeaderboardEntry entry : leaderboard) {
            logger.info(String.format("%-6d %-20s %-15d", 
                entry.getRank(), entry.getParticipant(), entry.getTotalScore()));
        }
        logger.info("========================================\n");
    }
}
