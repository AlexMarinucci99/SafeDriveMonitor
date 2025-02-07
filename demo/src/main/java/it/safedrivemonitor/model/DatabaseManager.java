package it.safedrivemonitor.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {

    // Logger per gestire i log
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    // URL del database SQLite
    private static final String DB_URL = "jdbc:sqlite:monitoring.db";

    // Stringhe SQL come costanti
    private static final String SQL_CREATE_READINGS = """
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

    private static final String SQL_CREATE_ALERTS = """
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

    private static final String SQL_CREATE_DRIVERS = """
            CREATE TABLE IF NOT EXISTS drivers (
                driver_id TEXT PRIMARY KEY,
                driver_name TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                phone TEXT
            );
            """;

    private static final String SQL_ADD_PHONE_COLUMN = "ALTER TABLE drivers ADD COLUMN phone TEXT;";

    // Metodo per inizializzare il database e creare le tabelle necessarie
    public void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                executeSQL(conn, SQL_CREATE_READINGS);
                executeSQL(conn, SQL_CREATE_ALERTS);
                createOrUpdateDriversTable(conn);
            }
        } catch (SQLException e) {
            logger.error("Errore durante l'inizializzazione del database", e);
        }
    }

    // Crea o aggiorna la tabella drivers
    private void createOrUpdateDriversTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(SQL_CREATE_DRIVERS);
            try {
                stmt.execute(SQL_ADD_PHONE_COLUMN);
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("duplicate column name: phone")) {
                    throw e;
                } else {
                    logger.info("La colonna 'phone' esiste già nella tabella drivers.");
                }
            }
        } catch (SQLException ex) {
            logger.error("Errore nella creazione/aggiornamento della tabella drivers", ex);
        }
    }

    // Metodo ausiliario per eseguire una query SQL
    private void executeSQL(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.error("Errore durante l'esecuzione della query SQL: {}", sql, e);
        }
    }

    // Metodo che restituisce una connessione al database
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
