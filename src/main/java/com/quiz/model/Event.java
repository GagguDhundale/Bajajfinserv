package com.quiz.model;

/**
 * Represents a quiz event with roundId, participant name, and score.
 * Used for data from API responses and deduplication.
 */
public class Event {
    private String roundId;
    private String participant;
    private int score;

    public Event() {
    }

    public Event(String roundId, String participant, int score) {
        this.roundId = roundId;
        this.participant = participant;
        this.score = score;
    }

    public String getRoundId() {
        return roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Generates a unique identifier for deduplication (roundId + participant)
     */
    public String getUniqueKey() {
        return roundId + "|" + participant;
    }

    @Override
    public String toString() {
        return "Event{" +
                "roundId='" + roundId + '\'' +
                ", participant='" + participant + '\'' +
                ", score=" + score +
                '}';
    }
}
