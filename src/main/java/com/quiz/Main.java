package com.quiz;

import com.quiz.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Quiz Leaderboard Application.
 * Configures API endpoints and initiates the complete workflow.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // External API endpoints
    private static final String FETCH_URL = "https://api.example.com/quiz/scores";  // Replace with actual API endpoint
    private static final String SUBMIT_URL = "https://api.example.com/quiz/leaderboard";  // Replace with actual API endpoint

    public static void main(String[] args) {
        logger.info("Starting Quiz Leaderboard Application");

        // Check for custom API URL from environment or command line arguments
        String fetchUrl = FETCH_URL;
        String submitUrl = SUBMIT_URL;

        if (args.length >= 2) {
            fetchUrl = args[0];
            submitUrl = args[1];
            logger.info("Using custom API endpoints from arguments");
        } else {
            logger.warn("Using default API endpoints. Consider passing custom URLs as arguments");
            logger.warn("Usage: java -jar quiz-leaderboard.jar <fetchUrl> <submitUrl>");
        }

        logger.info("Fetch URL: {}", fetchUrl);
        logger.info("Submit URL: {}", submitUrl);

        try {
            // Initialize the service
            QuizService quizService = new QuizService(fetchUrl, submitUrl);

            // Execute the complete workflow
            boolean success = quizService.executeWorkflow();

            if (success) {
                logger.info("Application completed successfully");
                System.exit(0);
            } else {
                logger.error("Application completed with errors");
                System.exit(1);
            }
        } catch (Exception e) {
            logger.error("Fatal error in main application", e);
            System.exit(1);
        }
    }
}
