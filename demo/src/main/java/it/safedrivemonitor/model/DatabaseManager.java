package it.safedrivemonitor.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:monitoring.db";

    public void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                // Crea tabella readings
                createReadingsTable(conn);

                // Crea tabella alerts
                createAlertsTable(conn);

                // Crea tabella drivers
                createDriversTable(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createReadingsTable(Connection conn) {
        String sql = """
                CREATE TABLE IF NOT EXISTS readings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    driver_name TEXT,
                    driver_id TEXT,
                    alcohol_level REAL,
                    thc_level REAL,
                    cocaine_level REAL,
                    mdma_level REAL,
                    result TEXT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
        executeSQL(conn, sql);
    }

    private void createAlertsTable(Connection conn) {
        String sqlAlerts = """
                CREATE TABLE IF NOT EXISTS alerts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    driver_name TEXT,
                    driver_id TEXT,
                    alcohol_level REAL,
                    thc_level REAL,
                    cocaine_level REAL,
                    mdma_level REAL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
        executeSQL(conn, sqlAlerts);
    }

    private void createDriversTable(Connection conn) {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS drivers (
                    driver_id TEXT PRIMARY KEY,
                    driver_name TEXT UNIQUE NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    phone TEXT
                );
                """;

        String addColumnSQL = "ALTER TABLE drivers ADD COLUMN phone TEXT;";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL); // Crea la tabella se non esiste
            stmt.execute(addColumnSQL); // Aggiunge la colonna se non esiste (non ha effetti collaterali se già esiste)
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name: phone")) {
                e.printStackTrace();
            }
        }
    }

    private void executeSQL(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
