package server;

import com.google.gson.Gson;
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

    public Server() {
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
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
