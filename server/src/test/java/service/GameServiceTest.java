
package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    static final GameDAO gameDAO = new MemoryGameDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();
    static final GameService service = new GameService(gameDAO, authDAO);

    @BeforeEach
    void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
        authDAO.createAuth(new AuthData("token123", "ryan"));
    }

    @Test
    void createGame() throws DataAccessException, ResponseException {
        var game = new GameData(0, null, null, "Test Game", null);

        var result = service.createGame("token123", game);

        assertEquals("Test Game", result.gameName());
        assertTrue(result.gameID() > 0);
        assertNotNull(result.game());
        assertEquals(result, gameDAO.getGame(result.gameID()));
    }

    @Test
    void createGameInvalidToken() {
        var game = new GameData(0, null, null, "Test Game", null);

        var exception = assertThrows(ResponseException.class,
                () -> service.createGame("invalidToken", game));

        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void createGameMissingToken() {
        var game = new GameData(0, null, null, "Test Game", null);

        var exception = assertThrows(ResponseException.class,
                () -> service.createGame(null, game));

        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void listGames() throws DataAccessException, ResponseException {
        var game1 = gameDAO.createGame(new GameData(0, null, null, "Game 1", new ChessGame()));
        var game2 = gameDAO.createGame(new GameData(0, null, null, "Game 2", new ChessGame()));

        var games = service.listGames("token123");

        assertEquals(2, games.size());
        assertTrue(games.contains(game1));
        assertTrue(games.contains(game2));
    }

    @Test
    void listGamesEmpty() throws DataAccessException, ResponseException {
        var games = service.listGames("token123");
        assertTrue(games.isEmpty());
    }

    @Test
    void listGamesInvalidToken() {
        var exception = assertThrows(ResponseException.class, () -> service.listGames("invalidToken"));
        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void listGamesMissingToken() {
        var exception = assertThrows(ResponseException.class, () -> service.listGames(null));
        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void joinGameWhite() throws DataAccessException, ResponseException {
        var game = service.createGame("token123",
                new GameData(0, null, null, "Test Game", null));

        service.joinGame("token123", game.gameID(), "WHITE");

        var updated = gameDAO.getGame(game.gameID());

        assertEquals("ryan", updated.whiteUsername());
        assertNull(updated.blackUsername());
    }

    @Test
    void joinGameBlack() throws DataAccessException, ResponseException {
        var game = service.createGame("token123",
                new GameData(0, null, null, "Test Game", null));

        service.joinGame("token123", game.gameID(), "BLACK");

        var updated = gameDAO.getGame(game.gameID());

        assertEquals("ryan", updated.blackUsername());
        assertNull(updated.whiteUsername());
    }

    @Test
    void joinGameColorTaken() throws DataAccessException, ResponseException {
        var game = service.createGame("token123",
                new GameData(0, null, null, "Test Game", null));

        service.joinGame("token123", game.gameID(), "WHITE");

        var exception = assertThrows(ResponseException.class,
                () -> service.joinGame("token123", game.gameID(), "WHITE"));

        assertEquals(403, exception.toHttpStatusCode());
    }

    @Test
    void joinGameInvalidID() {
        var exception = assertThrows(ResponseException.class,
                () -> service.joinGame("token123", 999, "WHITE"));

        assertEquals(400, exception.toHttpStatusCode());
    }

    @Test
    void joinGameInvalidColor() throws DataAccessException, ResponseException {
        var game = service.createGame("token123",
                new GameData(0, null, null, "Test Game", null));

        var exception = assertThrows(ResponseException.class,
                () -> service.joinGame("token123", game.gameID(), "GREEN"));

        assertEquals(400, exception.toHttpStatusCode());
    }

    @Test
    void joinGameInvalidToken() throws DataAccessException, ResponseException {
        var game = service.createGame("token123",
                new GameData(0, null, null, "Test Game", null));

        var exception = assertThrows(ResponseException.class,
                () -> service.joinGame("invalidToken", game.gameID(), "WHITE"));

        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void joinGameBothPlayers() throws DataAccessException, ResponseException {
        authDAO.createAuth(new AuthData("token456", "alex"));

        var game = service.createGame("token123",
                new GameData(0, null, null, "Test Game", null));

        service.joinGame("token123", game.gameID(), "WHITE");
        service.joinGame("token456", game.gameID(), "BLACK");

        var updated = gameDAO.getGame(game.gameID());

        assertEquals("ryan", updated.whiteUsername());
        assertEquals("alex", updated.blackUsername());
    }
}
