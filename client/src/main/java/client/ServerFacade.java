package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
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
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);

        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) {
        var user = new UserData(username, password, null);
        var request = buildRequest("POST", "/session", user, null);
        var response = sendRequest(request);

        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);

        handleResponse(response, null);
    }

    public int createGame(String authToken, String gameName) {
        var game = new JsonObject();
        game.addProperty("gameName", gameName);

        var request = buildRequest("POST", "/game", game, authToken);
        var response = sendRequest(request);

        var result = handleResponse(response, JsonObject.class);
        return result.get("gameID").getAsInt();
    }

    public GameData[] listGames(String authToken) {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);

        var result = handleResponse(response, JsonObject.class);
        return gson.fromJson(result.get("games"), GameData[].class);
    }

    public void joinGame(String authToken, int gameID, String playerColor) {
        var game = new JsonObject();
        game.addProperty("gameID", gameID);
        game.addProperty("playerColor", playerColor);

        var request = buildRequest("PUT", "/game", game, authToken);
        var response = sendRequest(request);

        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null) {
            request.header("Content-Type", "application/json");
        }

        if (authToken != null) {
            request.header("authorization", authToken);
        }

        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(gson.toJson(body));
        }
        return HttpRequest.BodyPublishers.noBody();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to the chess server.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request was interrupted.", e);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        if (response.statusCode() != 200) {
            throw new RuntimeException("Request failed.");
        }

        if (responseClass == null) {
            return null;
        }

        return gson.fromJson(response.body(), responseClass);
    }
}
