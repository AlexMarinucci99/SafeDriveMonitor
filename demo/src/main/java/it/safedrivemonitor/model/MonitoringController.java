package it.safedrivemonitor.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe che simula il test di alcol/sostanze e salva i dati nel database.
 */
public class MonitoringController {

    // Soglie predefinite per determinare il superamento dei limiti
    private static final double ALCOHOL_THRESHOLD = 0.5;
    private static final double THC_THRESHOLD = 15;
    private static final double COCAINE_THRESHOLD = 10;
    private static final double MDMA_THRESHOLD = 50;

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

        // Imposta il risultato in base al test
        String result = passed ? "NEGATIVO" : "POSITIVO";

        // Salva la lettura nel database
        saveReading(driverId, driverName, alcohol, thc, cocaine, mdma, result);

        // Se il test non è passato, salva anche un alert nel database
        if (!passed) {
            saveAlert(driverId, driverName, alcohol, thc, cocaine, mdma);
        }
        return new TestResult(passed, alcohol, thc, cocaine, mdma);
    }

    /**
     * Salva la lettura dei test nel database.
     *
     * @param driverId   l'identificativo del conducente
     * @param driverName il nome del conducente
     * @param alcohol    livello di alcol
     * @param thc        livello di THC
     * @param cocaine   livello di cocaina
     * @param mdma       livello di MDMA
     * @param result     risultato del test (NEGATIVO o POSITIVO)
     */
    private void saveReading(String driverId, String driverName,
                             double alcohol, double thc, double cocaine, double mdma, String result) {

        String sql = """
                    INSERT INTO readings(driver_id, driver_name,
                        alcohol_level, thc_level, cocaine_level, mdma_level, result)
                    VALUES (?,?,?,?,?,?,?)
                """;
        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Imposta i parametri della query
            pstmt.setString(1, driverId);
            pstmt.setString(2, driverName);
            pstmt.setDouble(3, alcohol);
            pstmt.setDouble(4, thc);
            pstmt.setDouble(5, cocaine);
            pstmt.setDouble(6, mdma);
            pstmt.setString(7, result);
            // Esegue l'inserimento nel database
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Gestione dell'errore di SQL
            e.printStackTrace();
        }
    }

    /**
     * Salva un alert nel database in caso di test fallito.
     *
     * @param driverId   l'identificativo del conducente
     * @param driverName il nome del conducente
     * @param alcohol    livello di alcol
     * @param thc        livello di THC
     * @param cocaine   livello di cocaina
     * @param mdma       livello di MDMA
     */
    private void saveAlert(String driverId, String driverName,
                           double alcohol, double thc, double cocaine, double mdma) {

        String sql = """
                    INSERT INTO alerts(driver_id, driver_name,
                        alcohol_level, thc_level, cocaine_level, mdma_level)
                    VALUES(?,?,?,?,?,?)
                """;
        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Imposta i parametri della query per l'alert
            pstmt.setString(1, driverId);
            pstmt.setString(2, driverName);
            pstmt.setDouble(3, alcohol);
            pstmt.setDouble(4, thc);
            pstmt.setDouble(5, cocaine);
            pstmt.setDouble(6, mdma);
            // Esegue l'inserimento dell'alert nel database
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Gestione dell'errore di SQL
            e.printStackTrace();
        }
    }

    /**
     * Classe interna che racchiude i risultati del test.
     */
    public static class TestResult {
        // Indica se il test è stato superato (true) o fallito (false)
        public final boolean passed;
        // Livelli riscontrati per le diverse sostanze
        public final double alcohol;
        public final double thc;
        public final double cocaine;
        public final double mdma;

        // Costruttore per inizializzare i risultati
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
