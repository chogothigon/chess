package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{

    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    public void createGame(GameData game) {
        GameData newGame = new GameData(nextGameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(nextGameID, newGame);
        nextGameID += 1;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return;
    }

    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public void clear() {
        games.clear();
    }
}
