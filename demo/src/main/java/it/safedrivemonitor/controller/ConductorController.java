package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.MonitoringController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.*;

/**
 * Controller per la gestione del conducente e delle operazioni correlate.
 */
public class ConductorController {

    // Riferimento al controller di monitoraggio per eseguire i test
    private final MonitoringController monitoringController;

    // Campi di input e componenti della UI definiti nel file FXML
    @FXML
    private TextField driverNameField;  // Campo per il nome del conducente
    @FXML
    private TextField driverIdField;    // Campo per l'ID del conducente

    @FXML
    private ProgressIndicator deviceSpinner; // Indicatore di caricamento per la simulazione del dispositivo
    @FXML
    private Label deviceStatusLabel;         // Etichetta per lo stato del dispositivo

    @FXML
    private Button executeTestButton;        // Bottone per eseguire il test

    @FXML
    private Label testResultLabel;           // Etichetta per visualizzare il risultato del test
    @FXML
    private Label vehicleStatusLabel;        // Etichetta per lo stato del veicolo
    @FXML
    private Label authorityLabel;            // Etichetta per notifiche dirette alle autorità

    /**
     * Costruttore che inizializza il controller con una istanza di MonitoringController.
     */
    public ConductorController(MonitoringController monitoringController) {
        this.monitoringController = monitoringController;
    }

    // ================== 1) Simulazione Rilevamento Dispositivo ================== //
    /**
     * Metodo per simulare il rilevamento del dispositivo.
     * Visualizza un indicatore di caricamento e aggiorna lo stato del dispositivo con ritardi prestabiliti.
     */
    @FXML
    private void onSimulateDeviceDetection() {
        deviceSpinner.setVisible(true);  // Mostra l'indicatore di caricamento
        deviceStatusLabel.setText("Rilevazione del dispositivo..."); // Mostra messaggio iniziale
        executeTestButton.setDisable(true); // Disabilita il bottone per eseguire il test

        // Primo intervallo: simulazione per 5 secondi
        PauseTransition pause1 = new PauseTransition(Duration.seconds(5));
        pause1.setOnFinished(e -> {
            // Al termine dei primi 5 secondi: il dispositivo è "collegato"
            deviceSpinner.setVisible(false); // Nasconde l'indicatore
            deviceStatusLabel.setText("Dispositivo Collegato"); // Aggiorna lo stato
            deviceStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            // Secondo intervallo: attesa di altri 5 secondi per pulire lo stato
            PauseTransition pause2 = new PauseTransition(Duration.seconds(5));
            pause2.setOnFinished(e2 -> {
                deviceStatusLabel.setText(""); // Rimuove il messaggio di stato
                executeTestButton.setDisable(false); // Abilita il bottone per eseguire il test
            });
            pause2.play();
        });
        pause1.play();
    }

    // ================== 2) Eseguire il Test ================== //
    /**
     * Metodo chiamato per eseguire il test del veicolo.
     * Verifica i dati inseriti, gestisce il login o la creazione del conducente, 
     * esegue il test e aggiorna l'interfaccia utente in base al risultato.
     */
    @FXML
    private void onExecuteTest() {
        // Recupera i dati inseriti dall'utente
        String driverName = driverNameField.getText();
        String driverId = driverIdField.getText();

        // Verifica che entrambi i campi non siano vuoti
        if (driverName == null || driverName.isEmpty()
                || driverId == null || driverId.isEmpty()) {
            testResultLabel.setText("Nome/ID non inseriti!");
            return;
        }

        // Verifica o crea il conducente nel database per evitare duplicati
        loginOrCreateDriver(driverName, driverId);

        // Esegue il test e ottiene il risultato
        MonitoringController.TestResult result = monitoringController.executeTest(driverId, driverName);

        // Rimuove le classi CSS precedenti per gli stili
        testResultLabel.getStyleClass().removeAll("test-success-label", "test-fail-label");
        vehicleStatusLabel.getStyleClass().removeAll("test-success-label", "test-fail-label");

        // Aggiorna l'interfaccia in base al risultato del test
        if (result.passed) {
            testResultLabel.setText("TEST SUPERATO");
            testResultLabel.getStyleClass().add("test-success-label");

            vehicleStatusLabel.setText("VEICOLO SBLOCCATO");
            vehicleStatusLabel.getStyleClass().add("test-success-label");

            authorityLabel.setText("");
        } else {
            testResultLabel.setText("TEST NON SUPERATO");
            testResultLabel.getStyleClass().add("test-fail-label");

            vehicleStatusLabel.setText("VEICOLO BLOCCATO");
            vehicleStatusLabel.getStyleClass().add("test-fail-label");

            authorityLabel.setText("Notifica alle autorità inviata");
        }
    }

    // ================== 3) Vedere i risultati ================== //
    /**
     * Metodo di esempio per vedere i risultati dei test del conducente.
     * Attualmente stampa a console il nome del conducente.
     */
    @FXML
    private void onViewMyResults() {
        System.out.println("Visualizzo i risultati di " + driverNameField.getText());
    }

    // ================== 4) Torna alla finestra di login mantenendo dimensioni ================== //
    /**
     * Metodo per tornare alla finestra di login.
     * Mantiene le dimensioni della finestra corrente e carica il layout del login.
     */
    @FXML
    private void onBack() {
        try {
            // Recupera la finestra corrente
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            // Carica il file FXML della finestra di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            // Crea una nuova scena con il layout caricato e aggiorna la finestra corrente
            Scene newScene = new Scene(root);
            stage.setScene(newScene);

            // Ripristina le dimensioni della finestra
            stage.setWidth(currentW);
            stage.setHeight(currentH);

            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================== 5) Creare/Recuperare driver nel DB per evitare duplicati ================== //
    /**
     * Metodo per controllare l'esistenza di un conducente nel database o crearlo se non esiste.
     * Evita duplicati eseguendo query di controllo e inserimento nella tabella "drivers".
     *
     * @param name il nome del conducente
     * @param id   l'ID del conducente
     */
    private void loginOrCreateDriver(String name, String id) {
        try (Connection conn = monitoringController.getDbManager().getConnection()) {
            // Crea la tabella "drivers" se non esiste
            String sqlTable = """
                    CREATE TABLE IF NOT EXISTS drivers (
                        driver_id TEXT PRIMARY KEY,
                        driver_name TEXT UNIQUE NOT NULL
                    );
                    """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sqlTable);
            }

            // Verifica se il conducente esiste già nel database
            String sqlCheck = "SELECT driver_id FROM drivers WHERE driver_id = ? OR driver_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    // Se il conducente non esiste, viene aggiunto al database
                    String sqlInsert = "INSERT INTO drivers (driver_id, driver_name) VALUES (?, ?)";
                    try (PreparedStatement pstmt2 = conn.prepareStatement(sqlInsert)) {
                        pstmt2.setString(1, id);
                        pstmt2.setString(2, name);
                        pstmt2.executeUpdate();
                        System.out.println("Conducente aggiunto: " + name + " (" + id + ")");
                    }
                } else {
                    System.out.println("Conducente già registrato.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
