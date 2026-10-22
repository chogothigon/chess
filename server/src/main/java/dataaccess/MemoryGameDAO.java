package dataaccess;

import model.GameData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class MemoryGameDAO implements GameDAO{

    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    public GameData createGame(GameData game) {
        game = new GameData(nextGameID++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(game.gameID(), game);
        return game;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public void clear() {
        games.clear();
        nextGameID = 1;
    }
}
