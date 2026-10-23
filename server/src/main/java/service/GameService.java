package service;

import dataaccess.*;
import model.GameData;
import java.util.Collection;
import chess.ChessGame;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public GameData createGame(String authToken, GameData game) throws DataAccessException, ResponseException {
        if (game == null || game.gameName() == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        GameData newGame = new GameData(0, null, null, game.gameName(), new ChessGame());
        return gameDAO.createGame(newGame);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException, ResponseException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        return gameDAO.listGames();
    }
}
