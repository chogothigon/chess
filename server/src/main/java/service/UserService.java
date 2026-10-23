package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException, ResponseException {
        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new ResponseException(403, "Error: already taken");
        }

        userDAO.createUser(user);
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        authDAO.createAuth(auth);
        return auth;
    }

    public AuthData login(UserData user) throws DataAccessException, ResponseException {
        if (user == null || user.username() == null || user.password() == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        UserData existingUser = userDAO.getUser(user.username());

        if (existingUser == null || !existingUser.password().equals(user.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        authDAO.createAuth(auth);
        return auth;
    }

    public void logout(String authToken) throws DataAccessException, ResponseException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}
