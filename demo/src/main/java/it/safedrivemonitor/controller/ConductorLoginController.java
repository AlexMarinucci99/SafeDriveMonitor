package it.safedrivemonitor.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

import it.safedrivemonitor.model.DatabaseManager;

public class ConductorLoginController {

    @FXML
    private TextField driverNameField;

    @FXML
    private TextField driverIdField;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        // Limita a 6 cifre numeriche 1 milioni di utenti possibili attualmente.
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
                return change; // ok
            }
            return null; // rigetta l'input
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        driverIdField.setTextFormatter(formatter);
    }

    // Nel onLogin passiamo a device_detection.fxml
    @FXML
    private void onLogin() {
        String name = driverNameField.getText().trim();
        String id = driverIdField.getText().trim();

        if (name.isEmpty() || id.isEmpty()) {
            errorLabel.setText("Nome o ID vuoti. Riprova.");
            return;
        }

        if (!isDriverValid(name, id)) {
            errorLabel.setText("Nome o ID non valido o non corrispondente. Riprova.");
            return;
        }
        // Salviamo in una "conductor temp" o passiamo parametri
        // Più semplice se memorizziamo in uno static var per prototipo:
        ConductorSession.name = name;
        ConductorSession.id = id;

        // Passo alla device_detection.fxml
        try {
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/device_detection.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setTitle("Rilevamento Dispositivo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDriverValid(String name, String id) {
        String sql = "SELECT COUNT(*) FROM drivers WHERE driver_name = ? AND driver_id = ?";
        try (Connection conn = new DatabaseManager().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void onResetIdClicked() {
        try {
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reset_id.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setTitle("Reset ID Conducente");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setWidth(currentW);
            stage.setHeight(currentH);

            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRegister() {
        try {
            // Carica il file FXML della registrazione
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_registration.fxml"));
            Parent root = loader.load();
            // Recupera lo stage dalla scena corrente (usa driverIdField come riferimento)
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            // Crea la nuova scena con le dimensioni dello stage corrente
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setTitle("Registrazione Conducente");
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
