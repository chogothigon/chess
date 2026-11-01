package dataaccess.sql;

import dataaccess.*;
import model.AuthData;
import java.sql.*;

public class SQLAuthDAO implements AuthDAO {

    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth", e);
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth", e);
        }

        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete auth", e);
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear auth", e);
        }
    }
}
