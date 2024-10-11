package com.saddham.todolist.client;

import com.saddham.todolist.model.Task;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class TodoListClient {
    private static final int BASE_DELAY = 1000; // Base delay in milliseconds
    private static final int MAX_DELAY = 30000; // Maximum delay in milliseconds
    private static final int MAX_RETRIES = 5;

    private HttpClient httpClient;
    private String apiServerUrl;
    private String apiKey;

    public TodoListClient(String apiServerUrl, String apiKey) {
        this.apiServerUrl = apiServerUrl;
        this.apiKey = apiKey;
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMinutes(1))
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public void createTodoList(List<String> todoList) {
        try {
            HttpRequest httpRequest;

            for(String task : todoList) {
                httpRequest = HttpRequest.newBuilder(defaultTodoListHttpRequest("/task"), (a,b) -> true)
                        .POST(HttpRequest.BodyPublishers.ofString(task))
                        .build();

                HttpResponse<String> httpResponse = makeRequest(httpRequest);
                if(httpResponse.statusCode() != 201) {
                    throw new RuntimeException(String.format("Failed to create task %s. HttpResponse: %s", task, httpResponse));
                }

                System.out.println(String.format("Created task: %s. Http response: %s", task, httpResponse));
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> makeRequest(HttpRequest httpRequest) throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = null;

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                int statusCode =  httpResponse.statusCode();
                if(statusCode >= 500) {
                    applyExpopentialBackoff(attempt);
                } else {
                    return httpResponse;
                }
            } catch (IOException ioException) {
                applyExpopentialBackoff(attempt);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return httpResponse;
    }

    private HttpRequest defaultTodoListHttpRequest(String path) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(apiServerUrl + path))
                .header("x-api-key", apiKey)
                .build();
    }

    private void applyExpopentialBackoff(int attempt) {
        System.out.println("Operation failed. Attempt " + (attempt + 1) + " of " + MAX_RETRIES);
        int delay = calculateDelay(attempt);
        System.out.println("Retrying in " + delay + " milliseconds...");

        try {
            Thread.sleep(delay); // Wait before retrying
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    private int calculateDelay(int attempt) {
        int delay = (int) (BASE_DELAY * Math.pow(2, attempt)); // Exponential backoff
        return Math.min(delay, MAX_DELAY); // Cap the delay at MAX_DELAY
    }
}
