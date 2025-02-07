package it.safedrivemonitor.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // URL del database SQLite
    private static final String DB_URL = "jdbc:sqlite:monitoring.db";

    // Metodo per inizializzare il database e creare le tabelle necessarie
    public void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                // Crea la tabella "readings"
                createReadingsTable(conn);

                // Crea la tabella "alerts"
                createAlertsTable(conn);

                // Crea la tabella "drivers"
                createDriversTable(conn);
            }
        } catch (SQLException e) {
            // Stampa l'errore in caso di eccezione SQL
            e.printStackTrace();
        }
    }

    // Metodo per creare la tabella "readings"
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
        // Esegue la query SQL
        executeSQL(conn, sql);
    }

    // Metodo per creare la tabella "alerts"
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
        // Esegue la query SQL
        executeSQL(conn, sqlAlerts);
    }

    // Metodo per creare la tabella "drivers" e aggiungere eventualmente la colonna "phone"
    private void createDriversTable(Connection conn) {
        // Query per creare la tabella "drivers" se non esiste
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS drivers (
                    driver_id TEXT PRIMARY KEY,
                    driver_name TEXT UNIQUE NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    phone TEXT
                );
                """;

        // Query per aggiungere la colonna "phone"
        String addColumnSQL = "ALTER TABLE drivers ADD COLUMN phone TEXT;";

        try (Statement stmt = conn.createStatement()) {
            // Crea la tabella "drivers"
            stmt.execute(createTableSQL);
            // Prova ad aggiungere la colonna "phone"
            stmt.execute(addColumnSQL);
        } catch (SQLException e) {
            // Se l'errore non è dato dal fatto che la colonna esiste già, stampa l'eccezione
            if (!e.getMessage().contains("duplicate column name: phone")) {
                e.printStackTrace();
            }
        }
    }

    // Metodo ausiliario per eseguire le query SQL passate come parametro
    private void executeSQL(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            // Stampa l'errore in caso di eccezione SQL
            e.printStackTrace();
        }
    }

    // Metodo che restituisce una connessione al database
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
