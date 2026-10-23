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

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
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
                .exception(ResponseException.class, this::responseError)
                .exception(DataAccessException.class, this::dataAccessError);
    }

    private void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }

    private void register(Context ctx) throws DataAccessException, ResponseException {
        UserData user;

        try {
            user = gson.fromJson(ctx.body(), UserData.class);
        } catch (JsonParseException e) {
            throw new ResponseException(400, "Error: bad request");
        }

        AuthData auth = userService.register(user);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(auth));
    }

    private void login(Context ctx) throws DataAccessException, ResponseException {
        UserData user;

        try {
            user = gson.fromJson(ctx.body(), UserData.class);
        } catch (JsonParseException e) {
            throw new ResponseException(400, "Error: bad request");
        }

        AuthData auth = userService.login(user);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(auth));
    }

    private void logout(Context ctx) throws DataAccessException, ResponseException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }

    private void createGame(Context ctx) throws DataAccessException, ResponseException {
        String authToken = ctx.header("authorization");
        GameData game;

        try {
            game = gson.fromJson(ctx.body(), GameData.class);
        } catch (JsonParseException e) {
            throw new ResponseException(400, "Error: bad request");
        }

        GameData result = gameService.createGame(authToken, game);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("gameID", result.gameID())));
    }

    private void listGames(Context ctx) throws DataAccessException, ResponseException {
        String authToken = ctx.header("authorization");
        var games = gameService.listGames(authToken);
        JsonArray gameList = new JsonArray();

        for (GameData game : games) {
            JsonObject gameJson = new JsonObject();
            gameJson.addProperty("gameID", game.gameID());
            gameJson.add("whiteUsername", gson.toJsonTree(game.whiteUsername()));
            gameJson.add("blackUsername", gson.toJsonTree(game.blackUsername()));
            gameJson.addProperty("gameName", game.gameName());
            gameList.add(gameJson);
        }

        JsonObject result = new JsonObject();
        result.add("games", gameList);

        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(result));
    }

    private void responseError(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("message", ex.getMessage())));
    }

    private void dataAccessError(DataAccessException ex, Context ctx) {
        ctx.status(500);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("message", "Error: data access failure")));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
