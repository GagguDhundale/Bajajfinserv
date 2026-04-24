package com.quiz.model;

import java.util.List;

/**
 * Represents the API response structure containing quiz events/scores.
 */
public class ApiResponse {
    private List<Event> data;
    private String status;
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(List<Event> data, String status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "data=" + data +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
