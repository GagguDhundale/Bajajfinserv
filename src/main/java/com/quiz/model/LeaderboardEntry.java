package com.quiz.model;

/**
 * Represents a leaderboard entry with participant name and aggregated total score.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private String participant;
    private int totalScore;
    private int rank;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(String participant, int totalScore) {
        this.participant = participant;
        this.totalScore = totalScore;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Compares entries by totalScore in descending order (highest score first).
     */
    @Override
    public int compareTo(LeaderboardEntry other) {
        return Integer.compare(other.totalScore, this.totalScore);
    }

    @Override
    public String toString() {
        return "LeaderboardEntry{" +
                "rank=" + rank +
                ", participant='" + participant + '\'' +
                ", totalScore=" + totalScore +
                '}';
    }
}
