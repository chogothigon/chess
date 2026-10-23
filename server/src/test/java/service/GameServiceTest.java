
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
}
