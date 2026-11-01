package dataaccess.sql;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {
    private final GameDAO gameDAO = new SQLGameDAO();

    @BeforeEach
    void clear() throws DataAccessException {
        SQLDatabase.configureDatabase();
        gameDAO.clear();
    }

    @Test
    void createGame() throws DataAccessException {
        var game = new GameData(0, null, null, "Test Game", new ChessGame());

        var result = gameDAO.createGame(game);

        assertTrue(result.gameID() > 0);
        assertEquals("Test Game", gameDAO.getGame(result.gameID()).gameName());
    }

    @Test
    void createMultipleGames() throws DataAccessException {
        var game1 = new GameData(0, null, null, "Game One", new ChessGame());
        var game2 = new GameData(0, null, null, "Game Two", new ChessGame());

        var result1 = gameDAO.createGame(game1);
        var result2 = gameDAO.createGame(game2);

        assertNotEquals(result1.gameID(), result2.gameID());
    }

    @Test
    void getGame() throws DataAccessException {
        var game = new GameData(0, null, null, "Test Game", new ChessGame());
        var result = gameDAO.createGame(game);

        var stored = gameDAO.getGame(result.gameID());

        assertEquals(result.gameID(), stored.gameID());
        assertEquals(result.gameName(), stored.gameName());
        assertEquals(result.game().getBoard(), stored.game().getBoard());
    }

    @Test
    void getMissingGame() throws DataAccessException {
        assertNull(gameDAO.getGame(-1));
    }

    @Test
    void listGames() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "Game One", new ChessGame()));
        gameDAO.createGame(new GameData(0, null, null, "Game Two", new ChessGame()));

        assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    void listEmptyGames() throws DataAccessException {
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    void updateGame() throws DataAccessException {
        var game = new GameData(0, null, null, "Test Game", new ChessGame());
        var result = gameDAO.createGame(game);

        var updated = new GameData(result.gameID(), "ryan", null, "Updated Game", result.game());

        gameDAO.updateGame(updated);

        var stored = gameDAO.getGame(result.gameID());

        assertEquals("ryan", stored.whiteUsername());
        assertEquals("Updated Game", stored.gameName());
    }

    @Test
    void updateMissingGame() {
        var game = new GameData(-1, null, null, "Missing Game", new ChessGame());

        assertThrows(DataAccessException.class,
                () -> gameDAO.updateGame(game));
    }

    @Test
    void clearGames() throws DataAccessException {
        gameDAO.createGame(new GameData(0, null, null, "Test Game", new ChessGame()));

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }


    @Test
    void saveGameAfterMove() throws DataAccessException, InvalidMoveException {
        var chessGame = new ChessGame();
        chessGame.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));

        var game = new GameData(0, null, null, "Test Game", chessGame);
        var result = gameDAO.createGame(game);
        var stored = gameDAO.getGame(result.gameID());

        assertEquals(chessGame.getBoard(), stored.game().getBoard());
        assertEquals(chessGame.getTeamTurn(), stored.game().getTeamTurn());
    }


    @Test
    void databaseErrors() throws Exception {
        var properties = new Properties();

        try (var stream = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            assertNotNull(stream);
            properties.load(stream);
        }

        var load = DatabaseManager.class.getDeclaredMethod("loadProperties", Properties.class);
        var reset = DatabaseManager.class.getDeclaredMethod("loadPropertiesFromResources");

        load.setAccessible(true);
        reset.setAccessible(true);

        properties.setProperty("db.name", "information_schema");

        try {
            load.invoke(null, properties);

            var userDAO = new SQLUserDAO();
            var authDAO = new SQLAuthDAO();
            var game = new GameData(1, null, null, "Test Game", new ChessGame());

            assertThrows(DataAccessException.class, () -> userDAO.getUser("test"));
            assertThrows(DataAccessException.class, userDAO::clear);

            assertThrows(DataAccessException.class, () -> authDAO.getAuth("token123"));
            assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("token123"));
            assertThrows(DataAccessException.class, authDAO::clear);

            assertThrows(DataAccessException.class, () -> gameDAO.createGame(game));
            assertThrows(DataAccessException.class, () -> gameDAO.getGame(1));
            assertThrows(DataAccessException.class, gameDAO::listGames);
            assertThrows(DataAccessException.class, () -> gameDAO.updateGame(game));
            assertThrows(DataAccessException.class, gameDAO::clear);

        } finally {
            reset.invoke(null);
        }
    }
}
