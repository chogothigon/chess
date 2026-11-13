package client;

import org.junit.jupiter.api.*;
import server.Server;
import dataaccess.*;
import dataaccess.sql.*;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        new SQLAuthDAO().clear();
        new SQLGameDAO().clear();
        new SQLUserDAO().clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerSuccess() {
        var result = facade.register("player1", "password", "test@gmail.com");
        assertEquals("player1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerDuplicate() {
        facade.register("player1", "password", "test@gmail.com");
        assertThrows(RuntimeException.class, () -> facade.register("player1", "password", "test@gmail.com"));
    }

    @Test
    void loginSuccess() {
        facade.register("player1", "password", "test@gmail.com");
        var result = facade.login("player1", "password");
        assertEquals("player1", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        var auth = facade.register("player1", "password", "test@gmail.com");
        facade.logout(auth.authToken());
        assertNull(new SQLAuthDAO().getAuth(auth.authToken()));
    }

    @Test
    void logoutInvalidToken() {
        assertThrows(RuntimeException.class, () -> facade.logout("invalidToken"));
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        var auth = facade.register("player1", "password", "test@gmail.com");
        int gameID = facade.createGame(auth.authToken(), "First Game");
        assertTrue(gameID > 0);
        assertEquals("First Game", new SQLGameDAO().getGame(gameID).gameName());
    }

    @Test
    void createGameInvalidToken() {
        assertThrows(RuntimeException.class, () -> facade.createGame("invalidToken", "Test Game"));
    }

    @Test
    void listGamesSuccess() {
        var auth = facade.register("player1", "password", "test@gmail.com");

        facade.createGame(auth.authToken(), "Game One");
        facade.createGame(auth.authToken(), "Game Two");

        var games = facade.listGames(auth.authToken());

        assertEquals(2, games.length);
        assertEquals("Game One", games[0].gameName());
        assertEquals("Game Two", games[1].gameName());
    }

    @Test
    void listGamesInvalidToken() {
        assertThrows(RuntimeException.class, () -> facade.listGames("invalidToken"));
    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        var auth = facade.register("player1", "password", "test@gmail.com");
        int gameID = facade.createGame(auth.authToken(), "Test Game");

        facade.joinGame(auth.authToken(), gameID, "WHITE");

        var game = new SQLGameDAO().getGame(gameID);

        assertEquals("player1", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameInvalidToken() {
        var auth = facade.register("player1", "password", "test@gmail.com");
        int gameID = facade.createGame(auth.authToken(), "Test Game");

        assertThrows(RuntimeException.class, () -> facade.joinGame("invalidToken", gameID, "WHITE"));
    }

    @Test
    void joinGameInvalidGame() {
        var auth = facade.register("player1", "password", "test@gmail.com");

        assertThrows(RuntimeException.class, () -> facade.joinGame(auth.authToken(), -1, "WHITE"));
    }

    @Test
    void joinGameOccupied() {
        var player1 = facade.register("player1", "password", "test1@gmail.com");
        var player2 = facade.register("player2", "password", "test2@gmail.com");

        int gameID = facade.createGame(player1.authToken(), "Test Game");

        facade.joinGame(player1.authToken(), gameID, "WHITE");

        assertThrows(RuntimeException.class, () -> facade.joinGame(player2.authToken(), gameID, "WHITE"));
    }
}
