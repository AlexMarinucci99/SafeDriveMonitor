package it.safedrivemonitor.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.util.Duration;
import java.util.function.UnaryOperator;

import it.safedrivemonitor.model.DatabaseManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class ConductorRegistrationController {

    @FXML
    private TextField driverNameField;
    @FXML
    private TextField driverIdField;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField emailField;

    private final DatabaseManager dbManager = new DatabaseManager();

    @FXML
    public void initialize() {
        // Limita l'inserimento dell'ID a 6 cifre
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
                return change; // Permette l'input
            }
            return null; // Rigetta l'input
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        driverIdField.setTextFormatter(formatter);
        }

        @FXML
        private void onRegister() {
            String name = driverNameField.getText().trim();
            String id = driverIdField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
            errorLabel.setText("Tutti i campi sono obbligatori. Riprova.");
            return;
            }

            if (isDriverExists(name, id)) {
            errorLabel.setText("Nome o ID già registrati. Riprova.");
            return;
            }

            try (Connection conn = dbManager.getConnection()) {
            String sql = "INSERT INTO drivers (driver_id, driver_name, email) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.executeUpdate();
                errorLabel.setText("Registrazione completata!");
                errorLabel.setStyle("-fx-text-fill: green;");

                PauseTransition pause = new PauseTransition(Duration.seconds(5));
                pause.setOnFinished(e -> {
                Stage stage = (Stage) driverIdField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_login.fxml"));
                Parent root;
                try {
                    root = loader.load();
                    Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                    stage.setScene(scene);
                    stage.setFullScreen(true);
                    stage.setTitle("SafeDriveMonitor-Login Conducente");
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                });
                pause.play();
            }
            } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Errore durante la registrazione.");
            }
        }

        private boolean isDriverExists(String name, String id) {
        String sql = "SELECT COUNT(*) FROM drivers WHERE driver_name = ? OR driver_id = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) driverIdField.getScene().getWindow();

            // Salva le dimensioni attuali della finestra
          

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            // Carica la scena con le dimensioni della finestra corrente
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setTitle("SafeDriveMonitor-Home");
            
            stage.show();

        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
