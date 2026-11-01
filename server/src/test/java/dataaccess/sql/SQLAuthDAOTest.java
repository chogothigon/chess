
package dataaccess.sql;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private final AuthDAO authDAO = new SqlAuthDAO();

    @BeforeEach
    void clear() throws DataAccessException {
        SQLDatabase.configureDatabase();
        authDAO.clear();
    }

    @Test
    void createAuth() throws DataAccessException {
        var auth = new AuthData("token123", "ryan");

        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("token123"));
    }

    @Test
    void createDuplicateAuth() throws DataAccessException {
        var auth = new AuthData("token123", "ryan");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(auth));
    }

    @Test
    void getMissingAuth() throws DataAccessException {
        assertNull(authDAO.getAuth("invalidToken"));
    }

    @Test
    void deleteAuth() throws DataAccessException {
        var auth = new AuthData("token123", "ryan");
        authDAO.createAuth(auth);

        authDAO.deleteAuth("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    void clearAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("token123", "ryan"));
        authDAO.createAuth(new AuthData("token456", "ryan"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token123"));
        assertNull(authDAO.getAuth("token456"));
    }
}
