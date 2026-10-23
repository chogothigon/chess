package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static final UserDAO userDAO = new MemoryUserDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();
    static final UserService service = new UserService(userDAO, authDAO);

    @BeforeEach
    void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void register() throws DataAccessException, ResponseException {
        var user = new UserData("ryan", "password", "test@gmail.com");
        var result = service.register(user);

        assertEquals("ryan", result.username());
        assertNotNull(result.authToken());
        assertEquals(user, userDAO.getUser("ryan"));
        assertEquals(result, authDAO.getAuth(result.authToken()));
    }

    @Test
    void registerDuplicate() throws DataAccessException, ResponseException {
        var user = new UserData("ryan", "password", "test@gmail.com");
        service.register(user);

        var exception = assertThrows(ResponseException.class,
                () -> service.register(user));

        assertEquals(403, exception.toHttpStatusCode());
    }

    @Test
    void registerMissingUsername() {
        var user = new UserData(null, "password", "test@gmail.com");

        var exception = assertThrows(ResponseException.class,
                () -> service.register(user));

        assertEquals(400, exception.toHttpStatusCode());
    }

    @Test
    void login() throws DataAccessException, ResponseException {
        userDAO.createUser(new UserData("ryan", "password", "test@gmail.com"));

        var user = new UserData("ryan", "password", null);
        var result = service.login(user);

        assertEquals("ryan", result.username());
        assertNotNull(result.authToken());
        assertEquals(result, authDAO.getAuth(result.authToken()));
    }

    @Test
    void loginWrongPassword() throws DataAccessException {
        userDAO.createUser(new UserData("ryan", "password", "test@gmail.com"));

        var user = new UserData("ryan", "wrongpassword", null);

        var exception = assertThrows(ResponseException.class,
                () -> service.login(user));

        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void loginInvalidUser() {
        var user = new UserData("unknown", "password", null);

        var exception = assertThrows(ResponseException.class,
                () -> service.login(user));

        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void loginMissingPassword() {
        var user = new UserData("ryan", null, null);

        var exception = assertThrows(ResponseException.class,
                () -> service.login(user));

        assertEquals(400, exception.toHttpStatusCode());
    }

    @Test
    void logout() throws DataAccessException, ResponseException {
        var user = new UserData("ryan", "password", "test@email.com");
        var auth = service.register(user);

        service.logout(auth.authToken());

        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void logoutInvalidToken() {
        var exception = assertThrows(ResponseException.class,
                () -> service.logout("invalidToken"));

        assertEquals(401, exception.toHttpStatusCode());
    }

    @Test
    void logoutMissingToken() {
        var exception = assertThrows(ResponseException.class,
                () -> service.logout(null));

        assertEquals(401, exception.toHttpStatusCode());
    }
}
