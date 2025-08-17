package com.currency;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class ApiClient {
    private final String apiKey;
    private final String base;
    private final String urlTemplate;
    private final int timeoutSeconds;

    public ApiClient(String apiKey, String base, String urlTemplate, int timeoutSeconds) {
        this.apiKey = apiKey;
        this.base = base;
        this.urlTemplate = urlTemplate;
        this.timeoutSeconds = timeoutSeconds;
    }

    public String fetchRates() throws IOException, InterruptedException {
        String url = urlTemplate.replace("{API_KEY}", apiKey).replace("{BASE}", base);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " al consultar la API. URL: " + url);
        }
   
        Files.writeString(Path.of("respuesta.json"), response.body());
        return response.body();
    }
}
