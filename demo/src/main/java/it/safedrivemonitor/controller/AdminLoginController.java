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
import java.util.logging.Level;
import java.util.logging.Logger;
import it.safedrivemonitor.model.DatabaseManager;

public class AdminLoginController {
    private static final Logger LOGGER = Logger.getLogger(AdminLoginController.class.getName());
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

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
        LOGGER.info("Using dbManager: " + dbManager);
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (ADMIN_USERNAME.equals(user) && ADMIN_PASSWORD.equals(pass)) {
            navigateToView("/fxml/admin_view.fxml", "Amministratore", true);
        } else {
            errorLabel.setText("Credenziali errate. Riprova!");
        }
    }

    @FXML
    private void onBack() {
        navigateToView("/fxml/login_view.fxml", "SafeDriveMonitor-Home", true);
    }

    // Metodo ausiliario per gestire la navigazione tra le viste
    private void navigateToView(String fxmlPath, String title, boolean fullScreen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(view);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setFullScreen(fullScreen);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento della vista: " + fxmlPath, e);
            errorLabel.setText("Errore nel caricamento della vista.");
        }
    }
}
