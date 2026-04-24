package com.quiz.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quiz.model.ApiResponse;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class for HTTP operations with JSON serialization/deserialization.
 * Handles GET requests to fetch quiz data and POST requests to submit leaderboards.
 */
public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final Gson gson = new Gson();

    /**
     * Sends a GET request to the specified URL and deserializes the response to ApiResponse.
     * @param url The URL to fetch data from
     * @return ApiResponse object parsed from JSON response
     * @throws IOException if the request fails
     */
    public static ApiResponse getQuizData(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");

            return httpClient.execute(httpGet, response -> {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode == 200) {
                    logger.info("Successfully fetched data from {}", url);
                    try {
                        return gson.fromJson(responseBody, ApiResponse.class);
                    } catch (JsonSyntaxException e) {
                        logger.error("Failed to parse JSON response: {}", responseBody, e);
                        throw new IOException("Invalid JSON format", e);
                    }
                } else {
                    logger.error("HTTP Error {} from {}: {}", statusCode, url, responseBody);
                    throw new IOException("HTTP Error: " + statusCode);
                }
            });
        }
    }

    /**
     * Sends a POST request with leaderboard data to the specified URL.
     * @param url The URL to post leaderboard data to
     * @param apiResponse The leaderboard data to submit
     * @return true if the POST was successful, false otherwise
     */
    public static boolean submitLeaderboard(String url, ApiResponse apiResponse) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");

            String jsonPayload = gson.toJson(apiResponse);
            StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            return httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode >= 200 && statusCode < 300) {
                    logger.info("Successfully submitted leaderboard to {}", url);
                    return true;
                } else {
                    logger.error("Failed to submit leaderboard. HTTP Error {} from {}: {}", 
                        statusCode, url, responseBody);
                    return false;
                }
            });
        } catch (IOException e) {
            logger.error("Error submitting leaderboard to {}", url, e);
            return false;
        }
    }
}
