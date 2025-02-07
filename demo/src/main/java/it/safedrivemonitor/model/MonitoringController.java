package it.safedrivemonitor.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe che simula il test di alcol/sostanze e salva i dati nel database.
 */
public class MonitoringController {

    private static final Logger logger = Logger.getLogger(MonitoringController.class.getName());

    // Soglie predefinite per determinare il superamento dei limiti
    private static final double ALCOHOL_THRESHOLD = 0.5;
    private static final double THC_THRESHOLD = 15;
    private static final double COCAINE_THRESHOLD = 10;
    private static final double MDMA_THRESHOLD = 50;

    // Query SQL come costanti
    private static final String INSERT_READING_SQL = """
                INSERT INTO readings(driver_id, driver_name,
                    alcohol_level, thc_level, cocaine_level, mdma_level, result)
                VALUES (?,?,?,?,?,?,?)
            """;

    private static final String INSERT_ALERT_SQL = """
                INSERT INTO alerts(driver_id, driver_name,
                    alcohol_level, thc_level, cocaine_level, mdma_level)
                VALUES (?,?,?,?,?,?)
            """;

    // Riferimento al gestore del database per ottenere connessioni
    private final DatabaseManager dbManager;

    // Costruttore che inizializza il MonitoringController con un DatabaseManager
    public MonitoringController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Esegue un test simulato per un dato driverId.
     * Genera valori casuali per alcol e altre sostanze, ne verifica il superamento delle soglie,
     * salva i risultati e gli eventuali alert nel database.
     *
     * @param driverId   l'identificativo del conducente
     * @param driverName il nome del conducente
     * @return un oggetto TestResult contenente i risultati del test
     */
    public TestResult executeTest(String driverId, String driverName) {
        double alcohol = Math.random(); // Genera un valore casuale tra 0.0 e 1.0 per l'alcol
        double thc = Math.random() * 30; // Genera un valore casuale tra 0 e 30 per il THC
        double cocaine = Math.random() * 20; // Genera un valore casuale tra 0 e 20 per la cocaina
        double mdma = Math.random() * 100; // Genera un valore casuale tra 0 e 100 per l'MDMA

        // Determina se il test è passato confrontando i valori con le soglie
        boolean passed = alcohol <= ALCOHOL_THRESHOLD
                && thc <= THC_THRESHOLD
                && cocaine <= COCAINE_THRESHOLD
                && mdma <= MDMA_THRESHOLD;

        String result = passed ? "NEGATIVO" : "POSITIVO";

        // Salva la lettura nel database
        saveRecord(INSERT_READING_SQL, ps -> {
            ps.setString(1, driverId);
            ps.setString(2, driverName);
            ps.setDouble(3, alcohol);
            ps.setDouble(4, thc);
            ps.setDouble(5, cocaine);
            ps.setDouble(6, mdma);
            ps.setString(7, result);
        });

        // Se il test non è passato, salva anche un alert nel database
        if (!passed) {
            saveRecord(INSERT_ALERT_SQL, ps -> {
                ps.setString(1, driverId);
                ps.setString(2, driverName);
                ps.setDouble(3, alcohol);
                ps.setDouble(4, thc);
                ps.setDouble(5, cocaine);
                ps.setDouble(6, mdma);
            });
        }
        return new TestResult(passed, alcohol, thc, cocaine, mdma);
    }

    /**
     * Metodo helper per eseguire un aggiornamento nel database.
     *
     * @param sql             la query SQL
     * @param statementSetter una lambda per impostare i parametri del PreparedStatement
     */
    private void saveRecord(String sql, SQLConsumer<PreparedStatement> statementSetter) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            statementSetter.accept(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'operazione SQL", e);
        }
    }

    /**
     * Interfaccia funzionale per gestire eccezioni SQL nelle lambda.
     */
    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

    /**
     * Classe interna che racchiude i risultati del test.
     */
    public static class TestResult {
        public final boolean passed;
        public final double alcohol;
        public final double thc;
        public final double cocaine;
        public final double mdma;

        public TestResult(boolean passed, double alcohol, double thc, double cocaine, double mdma) {
            this.passed = passed;
            this.alcohol = alcohol;
            this.thc = thc;
            this.cocaine = cocaine;
            this.mdma = mdma;
        }
    }

    /**
     * Restituisce il DatabaseManager utilizzato per le operazioni sul database.
     *
     * @return il DatabaseManager associato
     */
    public DatabaseManager getDbManager() {
        return this.dbManager;
    }
}
