package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import model.GameData;
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

    public int createGame(String authToken, String gameName) {
        var game = new JsonObject();
        game.addProperty("gameName", gameName);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(game)))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Could not create game.");
            }

            var result = gson.fromJson(response.body(), JsonObject.class);
            return result.get("gameID").getAsInt();

        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not connect to the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Creating game was interrupted.", e);
        }
    }

    public GameData[] listGames(String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authToken)
                .GET()
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Could not list games.");
            }

            var result = gson.fromJson(response.body(), JsonObject.class);
            return gson.fromJson(result.get("games"), GameData[].class);

        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not connect to the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Listing games was interrupted.", e);
        }
    }

    public void joinGame(String authToken, int gameID, String playerColor) {
        var game = new JsonObject();
        game.addProperty("gameID", gameID);
        game.addProperty("playerColor", playerColor);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(game)))
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Could not join game.");
            }

        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not connect to the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Joining game was interrupted.", e);
        }
    }
}
