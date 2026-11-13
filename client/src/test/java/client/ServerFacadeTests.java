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

}
