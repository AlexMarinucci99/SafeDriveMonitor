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

public class ConductorController {

    private final MonitoringController monitoringController;

    @FXML
    private TextField driverNameField;
    @FXML
    private TextField driverIdField;

    @FXML
    private ProgressIndicator deviceSpinner;
    @FXML
    private Label deviceStatusLabel;

    @FXML
    private Button executeTestButton;

    @FXML
    private Label testResultLabel;
    @FXML
    private Label vehicleStatusLabel;
    @FXML
    private Label authorityLabel;

    public ConductorController(MonitoringController monitoringController) {
        this.monitoringController = monitoringController;
    }

    // ================== 1) Simulazione Rilevamento Dispositivo ==================
    // //
    @FXML
    private void onSimulateDeviceDetection() {
        deviceSpinner.setVisible(true);
        deviceStatusLabel.setText("Rilevazione del dispositivo...");
        executeTestButton.setDisable(true);

        // Primo tempo: 5 secondi
        PauseTransition pause1 = new PauseTransition(Duration.seconds(5));
        pause1.setOnFinished(e -> {
            // Fine dei primi 5s: Dispositivo Collegato
            deviceSpinner.setVisible(false);
            deviceStatusLabel.setText("Dispositivo Collegato");
            deviceStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            // Secondo tempo: altri 5 secondi
            PauseTransition pause2 = new PauseTransition(Duration.seconds(5));
            pause2.setOnFinished(e2 -> {
                deviceStatusLabel.setText("");
                // Ora abilitiamo il pulsante "Esegui Test"
                executeTestButton.setDisable(false);
            });
            pause2.play();
        });
        pause1.play();
    }

    // ================== 2) Eseguire il Test ================== //
    @FXML
    private void onExecuteTest() {
        String driverName = driverNameField.getText();
        String driverId = driverIdField.getText();

        if (driverName == null || driverName.isEmpty()
                || driverId == null || driverId.isEmpty()) {
            testResultLabel.setText("Nome/ID non inseriti!");
            return;
        }

        // Verifica/crea l'utente nel DB (tabella drivers)
        loginOrCreateDriver(driverName, driverId);

        MonitoringController.TestResult result = monitoringController.executeTest(driverId, driverName);

        // Applichiamo classi CSS per rendere più belle le scritte:
        testResultLabel.getStyleClass().removeAll("test-success-label", "test-fail-label");
        vehicleStatusLabel.getStyleClass().removeAll("test-success-label", "test-fail-label");

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
    @FXML
    private void onViewMyResults() {
        // Esempio di come in futuro potresti filtrare i test dell'utente
        System.out.println("Visualizzo i risultati di " + driverNameField.getText());
    }

    // ================== 4) Torna alla finestra di login mantenendo dimensioni
    // ================== //
    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            stage.setScene(newScene);

            stage.setWidth(currentW);
            stage.setHeight(currentH);

            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================== 5) Creare/Recuperare driver nel DB per evitare duplicati
    // ================== //
    private void loginOrCreateDriver(String name, String id) {
        try (Connection conn = monitoringController.getDbManager().getConnection()) {
            // Crea la tabella se non esiste
            String sqlTable = """
                    CREATE TABLE IF NOT EXISTS drivers (
                        driver_id TEXT PRIMARY KEY,
                        driver_name TEXT UNIQUE NOT NULL
                    );
                    """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sqlTable);
            }

            // Controlla se il conducente esiste
            String sqlCheck = "SELECT driver_id FROM drivers WHERE driver_id = ? OR driver_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    // Aggiungi il nuovo conducente
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
