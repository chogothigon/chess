package service;

import dataaccess.*;
import model.AuthData;
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

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException, ResponseException {
        if (gameID <= 0 || (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor))) {
            throw new ResponseException(400, "Error: bad request");
        }

        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        String white = game.whiteUsername();
        String black = game.blackUsername();

        if (playerColor.equals("WHITE")) {
            if (white != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            white = auth.username();
        }
        else {
            if (black != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            black = auth.username();
        }

        GameData updatedGame = new GameData(game.gameID(), white, black, game.gameName(), game.game());
        gameDAO.updateGame(updatedGame);
    }
}
