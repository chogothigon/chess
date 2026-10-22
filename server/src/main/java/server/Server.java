package server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import dataaccess.*;
import service.ClearService;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ClearService clearService;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .exception(DataAccessException.class, this::dataAccessError);
    }

    private void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result("{}");
    }

    private void dataAccessError(DataAccessException ex, Context ctx) {
        ctx.status(500);
        ctx.contentType("application/json");
        ctx.result("{\"message\":\"Error: data access failure\"}");
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
