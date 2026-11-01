package dataaccess.sql;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    static final UserDAO userDAO = new SQLUserDAO();

    @BeforeEach
    void clear() throws DataAccessException {
        SQLDatabase.configureDatabase();
        userDAO.clear();
    }

    @Test
    void createUser() throws DataAccessException {
        var user = new UserData("test", "test", "test@gmail.com");

        userDAO.createUser(user);

        assertEquals(user, userDAO.getUser("test"));
    }

    @Test
    void createDuplicateUser() throws DataAccessException {
        var user = new UserData("test", "test", "test@gmail.com");
        userDAO.createUser(user);

        assertThrows(DataAccessException.class,
                () -> userDAO.createUser(user));
    }

    @Test
    void getMissingUser() throws DataAccessException {
        assertNull(userDAO.getUser("unknown"));
    }

    @Test
    void clearUsers() throws DataAccessException {
        userDAO.createUser(
                new UserData("test", "test", "test@gmail.com")
        );

        userDAO.clear();

        assertNull(userDAO.getUser("test"));
    }
}
