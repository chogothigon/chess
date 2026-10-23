package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    static final UserDAO userDAO = new MemoryUserDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();
    static final GameDAO gameDAO = new MemoryGameDAO();

    static final ClearService service = new ClearService(userDAO, authDAO, gameDAO);

    @BeforeEach
    void setup() throws DataAccessException {
        service.clear();
    }

    @Test
    void clear() throws DataAccessException {
        userDAO.createUser(new UserData("ryan", "password", "test@gmail.com"));
        authDAO.createAuth(new AuthData("test123", "ryan"));
        GameData game = gameDAO.createGame(
                new GameData(0, null, null, "Test Game", new ChessGame())
        );

        service.clear();

        assertNull(userDAO.getUser("ryan"));
        assertNull(authDAO.getAuth("test123"));
        assertNull(gameDAO.getGame(game.gameID()));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}
