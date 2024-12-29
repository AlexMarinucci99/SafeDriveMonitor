package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import java.util.Properties;

//import javax.mail.*;
//import javax.mail.internet.*;

public class ResetIdController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField driverNameField;

    @FXML
    private void onSendResetEmail() {
        String name = driverNameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            System.out.println("Tutti i campi sono obbligatori!");
            return;
        }

        String newId = generateNewId();
        if (resetDriverId(name, email, newId)) {
            System.out.println("Email inviata con il nuovo ID: " + newId);
            sendEmail(email, newId);
        } else {
            System.out.println("Nome o email non trovati. Riprova.");
        }
    }

    private boolean resetDriverId(String name, String email, String newId) {
        String sql = "UPDATE drivers SET driver_id = ? WHERE driver_name = ? AND email = ?";
        try (Connection conn = new DatabaseManager().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendEmail(String recipient, String newId) {
        String subject = "Reset ID Conducente";
        String content = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <style>"
                + "        h1 { color: #333; }"
                + "        .highlight { color: #b71c1c; font-weight: bold; }"
                + "        body { font-family: Arial, sans-serif; }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <h1>Caro utente di SafeDriveMonitor,</h1>"
                + "    <p>Abbiamo resettato il tuo ID conducente, quello nuovo è : "
                + "       <span class='highlight'>" + newId + "</span>."
                + "    </p>"
                + "    <p>Ti consigliamo di conservarlo con cura.</p>"
                + "    <p>Grazie per aver scelto SafeDriveMonitor.</p>"
                + "    \"<p>Ricorda di non bere alcolici o fare uso di sostanze se devi guidare.</p>"
                + "</body>"
                + "</html>";

        EmailService emailService = new EmailService();
        emailService.sendEmail(recipient, subject, content);
    }

    private String generateNewId() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // Genera un ID casuale di 6 cifre
    }

    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
