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

    public AuthData register(UserData user) throws DataAccessException {

        userDAO.createUser(user);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());
        authDAO.createAuth(auth);

        return auth;
    }

    //public LoginResult login(LoginRequest request)
    //        throws DataAccessException, ResponseException {
    //}

    //public void logout(String authToken)
    //        throws DataAccessException, ResponseException {
    //}
}