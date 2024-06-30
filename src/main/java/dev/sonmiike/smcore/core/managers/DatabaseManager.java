package dev.sonmiike.smcore.core.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.sonmiike.smcore.core.model.MuteInfo;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatabaseManager {

    private final DataSource dataSource;


    public DatabaseManager(String host, int port, String database, String username, String password) {
        HikariConfig databaseConfig = new HikariConfig();
        databaseConfig.setJdbcUrl(STR."jdbc:postgresql://\{host}:\{port}/\{database}");
        databaseConfig.setUsername(username);
        databaseConfig.setPassword(password);
        databaseConfig.setDriverClassName("org.postgresql.Driver");
        databaseConfig.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(databaseConfig);
        createTables();
    }

    public void executeUpdate(String sql, Consumer<PreparedStatement> statementConsumer) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statementConsumer.accept(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL update: " + sql, e);
        }
    }

    public <T> T executeQuery(String sql, Function<ResultSet, T> resultMapper, Consumer<PreparedStatement> statementConsumer) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statementConsumer.accept(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultMapper.apply(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL query: " + sql, e);
        }
    }

    public void close() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    public UUID getUUIDFromUsername(String username) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT uuid FROM players WHERE playerName = ?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUsernameFromUUID(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT playerName FROM players WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("playerName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void banPlayer(UUID uuid, String reason, String bannedBy, LocalDateTime banDate, LocalDateTime expiresAt) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO bans (uuid, reason, banned_by, ban_date, expires_at, is_active) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, reason);
            statement.setString(3, bannedBy);
            statement.setTimestamp(4, Timestamp.valueOf(banDate));
            statement.setTimestamp(5, expiresAt != null ? Timestamp.valueOf(expiresAt) : null);
            statement.setBoolean(6, true);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mutePlayer(UUID uuid, String reason, String mutedBy, LocalDateTime muteDate, LocalDateTime expiresAt) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO mutes (uuid, reason, muted_by, mute_date, expires_at, is_active) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, reason);
            statement.setString(3, mutedBy);
            statement.setTimestamp(4, Timestamp.valueOf(muteDate));
            statement.setTimestamp(5, expiresAt != null ? Timestamp.valueOf(expiresAt) : null);
            statement.setBoolean(6, true);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMuteStatus(UUID uuid, boolean isActive) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE mutes SET is_active = ? WHERE uuid = ?");
            statement.setBoolean(1, isActive);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addPlayer(UUID uuid, String username, String ipAddress) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO players (uuid, playerName, first_join, last_join, ip_address) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, uuid.toString());
            statement.setString(2, username);
            LocalDateTime now = LocalDateTime.now();
            statement.setTimestamp(3, Timestamp.valueOf(now));
            statement.setTimestamp(4, Timestamp.valueOf(now));
            statement.setString(5, ipAddress);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updatePlayerLastJoin(UUID uuid, String ipAddress) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE players SET last_join = ?, ip_address = ? WHERE uuid = ?");
            LocalDateTime now = LocalDateTime.now();
            statement.setTimestamp(1, Timestamp.valueOf(now));
            statement.setString(2, ipAddress);
            statement.setString(3, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT uuid FROM players WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean isPlayerMuted(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM mutes WHERE uuid = ? AND is_active = TRUE AND (expires_at IS NULL OR expires_at > ?)");
            statement.setString(1, uuid.toString());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPlayerBanned(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM bans WHERE uuid = ? AND is_active = TRUE AND (expires_at IS NULL OR expires_at > ?)");
            statement.setString(1, uuid.toString());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MuteInfo> loadAllMutedPlayers() {
        List<MuteInfo> mutedPlayers = new ArrayList<>();
        String sql = "SELECT uuid, reason, muted_by, mute_date, expires_at FROM mutes WHERE is_active = TRUE";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String reason = rs.getString("reason");
                String mutedBy = rs.getString("muted_by");
                LocalDateTime muteDate = rs.getTimestamp("mute_date").toLocalDateTime();
                LocalDateTime expiresAt = rs.getTimestamp("expires_at") != null ? rs.getTimestamp("expires_at").toLocalDateTime() : null;

                mutedPlayers.add(new MuteInfo(uuid, reason, mutedBy, muteDate, expiresAt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mutedPlayers;
    }


    private void createTables() {
        String playerSql = "CREATE TABLE IF NOT EXISTS players ("
            + "uuid VARCHAR(50) NOT NULL PRIMARY KEY,"
            + "playerName VARCHAR(16) NOT NULL,"
            + "first_join TIMESTAMP NOT NULL,"
            + "last_join TIMESTAMP NOT NULL,"
            + "ip_address VARCHAR(16) NOT NULL"
            + ")";
        executeUpdate(playerSql, _ -> {});

        String bansSql = "CREATE TABLE IF NOT EXISTS bans (" +
            "id SERIAL PRIMARY KEY," +
            "uuid VARCHAR(50) NOT NULL," +
            "reason TEXT NOT NULL," +
            "banned_by VARCHAR(16) NOT NULL," +
            "ban_date TIMESTAMP NOT NULL," +
            "expires_at TIMESTAMP," +
            "is_active BOOLEAN NOT NULL DEFAULT TRUE," +
            "FOREIGN KEY (uuid) REFERENCES players(uuid)" +
            ")";
        executeUpdate(bansSql, _ -> {});

        String mutesSql = "CREATE TABLE IF NOT EXISTS mutes (" +
            "id SERIAL PRIMARY KEY," +
            "uuid VARCHAR(50) NOT NULL," +
            "reason TEXT," +
            "muted_by VARCHAR(16) NOT NULL," +
            "mute_date TIMESTAMP NOT NULL," +
            "expires_at TIMESTAMP," +
            "is_active BOOLEAN NOT NULL DEFAULT TRUE," +
            "FOREIGN KEY (uuid) REFERENCES players(uuid)" +
            ")";
        executeUpdate(mutesSql, _ -> {});
    }
}
