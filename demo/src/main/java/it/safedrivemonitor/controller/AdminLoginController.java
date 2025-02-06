package it.safedrivemonitor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;
import it.safedrivemonitor.model.DatabaseManager;

public class AdminLoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final DatabaseManager dbManager;

    public AdminLoginController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @FXML
    private void onLogin() {
        System.out.println("Using dbManager: " + dbManager); // Use the dbManager field
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if ("admin".equals(user) && "admin".equals(pass)) {
            try {
                // Carichiamo la vista 'admin_view.fxml' e aggiorniamo la scena attuale
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_view.fxml"));
                Parent adminView = loader.load();

                // Otteniamo lo Stage corrente dalla scena esistente
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.getScene().setRoot(adminView); // Impostiamo il nuovo contenuto
                stage.setFullScreen(true);
                stage.setTitle("Amministratore");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Mostra l'errore nella scena
            errorLabel.setText("Credenziali errate. Riprova!");
        }
    }

    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setFullScreen(true);

            stage.setWidth(currentW);
            stage.setHeight(currentH);

            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
