package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    public GameData createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, gameJSON) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, gson.toJson(game.game()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int gameID = rs.getInt(1);
                    return new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                }
            }
            throw new DataAccessException("Unable to create game");

        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game", e);
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readGame(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game", e);
        }

        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM games";
        Collection<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                games.add(readGame(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games", e);
        }

        return games;
    }

    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJSON = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, gson.toJson(game.game()));
            ps.setInt(5, game.gameID());

            if (ps.executeUpdate() == 0) {
                throw new DataAccessException("Game not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update game", e);
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear games", e);
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        return new GameData(
                rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
                gson.fromJson(rs.getString("gameJSON"), ChessGame.class)
        );
    }
}
