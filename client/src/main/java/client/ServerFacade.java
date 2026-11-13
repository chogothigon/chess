package client;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) {
        var user = new UserData(username, password, email);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Registration failed username may already exist.");
            }

            return gson.fromJson(response.body(), AuthData.class);

        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not connect to the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Registration was interrupted.", e);
        }
    }

    public AuthData login(String username, String password) {
        var user = new UserData(username, password, null);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Login failed.");
            }

            return gson.fromJson(response.body(), AuthData.class);

        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not connect to the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Login was interrupted.", e);
        }
    }

    public void logout(String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("authorization", authToken)
                .DELETE()
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Logout failed.");
            }

        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not connect to the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Logout was interrupted.", e);
        }
    }
}
