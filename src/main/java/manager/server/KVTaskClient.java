package manager.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private static final String URL = "http://localhost:8078/";
    private final HttpClient httpClient;
    private URI serverUri;
    private String apiToken;

    public KVTaskClient(String serverUri) throws IOException {
        this.httpClient = HttpClient.newHttpClient();
        this.serverUri = URI.create(serverUri);
        this.apiToken = register();
    }

    private String register() throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "register"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Плохой ответ, не 200, а: " + response.statusCode());
            }

            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не получается сделать запрос");
        }
    }

    public void put(String key, String json) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Плохой ответ, не 200, а: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не получается сделать запрос");
        }
    }

    public String load(String key) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return " ";
            }
            if (response.statusCode() != 200) {
                throw new RuntimeException("Плохой ответ, не 200, а: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не получается сделать запрос");
        }
    }
}