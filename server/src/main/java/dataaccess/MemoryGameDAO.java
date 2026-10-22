package dataaccess;

import model.GameData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{

    private final Map<String, GameData> games = new HashMap<>();

    public void createGame(GameData game) {
        
    }

    public GameData getGame(int gameID) {

    }

    public Collection<GameData> listGames() {

    }

    public void updateGame(GameData game) {

    }

    public void clear() {
        games.clear();
    }
}
