package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.*;
import service.*;

import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final Gson gson = new Gson();

    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        this(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());
    }

    public Server(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::responseError)
                .exception(Exception.class, this::serverError);
    }

    private void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }

    private void register(Context ctx) throws DataAccessException, ResponseException {
        UserData user = parseBody(ctx, UserData.class);
        sendJson(ctx, userService.register(user));
    }

    private void login(Context ctx) throws DataAccessException, ResponseException {
        UserData user = parseBody(ctx, UserData.class);
        sendJson(ctx, userService.login(user));
    }

    private void logout(Context ctx) throws DataAccessException, ResponseException {
        userService.logout(ctx.header("authorization"));
        sendJson(ctx, Map.of());
    }

    private void createGame(Context ctx) throws DataAccessException, ResponseException {
        GameData game = parseBody(ctx, GameData.class);
        GameData result = gameService.createGame(ctx.header("authorization"), game);
        sendJson(ctx, Map.of("gameID", result.gameID()));
    }

    private void listGames(Context ctx) throws DataAccessException, ResponseException {
        var games = gameService.listGames(ctx.header("authorization"));
        JsonArray gameList = new JsonArray();

        for (GameData game : games) {
            JsonObject gameJson = new JsonObject();
            gameJson.addProperty("gameID", game.gameID());
            gameJson.addProperty("whiteUsername", game.whiteUsername());
            gameJson.addProperty("blackUsername", game.blackUsername());
            gameJson.addProperty("gameName", game.gameName());
            gameList.add(gameJson);
        }

        sendJson(ctx, Map.of("games", gameList));
    }

    private void joinGame(Context ctx) throws DataAccessException, ResponseException {
        JsonObject request = parseBody(ctx, JsonObject.class);

        if (request == null ||
                !request.has("gameID") ||
                !request.has("playerColor") ||
                !request.get("gameID").isJsonPrimitive() ||
                !request.get("gameID").getAsJsonPrimitive().isNumber() ||
                !request.get("playerColor").isJsonPrimitive() ||
                !request.get("playerColor").getAsJsonPrimitive().isString()) {
            throw new ResponseException(400, "Error: bad request");
        }

        try {
            int gameID = Integer.parseInt(request.get("gameID").getAsString());
            String playerColor = request.get("playerColor").getAsString();

            gameService.joinGame(ctx.header("authorization"), gameID, playerColor);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: bad request");
        }

        sendJson(ctx, Map.of());
    }

    private <T> T parseBody(Context ctx, Class<T> type) throws ResponseException {
        try {
            T body = gson.fromJson(ctx.body(), type);
            if (body == null) {
                throw new ResponseException(400, "Error: bad request");
            }
            return body;
        } catch (JsonParseException e) {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    private void sendJson(Context ctx, Object body) {
        ctx.contentType("application/json");
        ctx.result(gson.toJson(body));
    }

    private void responseError(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        sendJson(ctx, Map.of("message", ex.getMessage()));
    }

    private void serverError(Exception ex, Context ctx) {
        ex.printStackTrace();
        ctx.status(500);
        sendJson(ctx, Map.of("message", "Error: internal server error"));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
