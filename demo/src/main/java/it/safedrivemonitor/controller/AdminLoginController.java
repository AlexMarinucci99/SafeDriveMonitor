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
    // Campo di testo per l'inserimento del nome utente
    @FXML
    private TextField usernameField;
    // Campo per l'inserimento della password (con mascheratura)
    @FXML
    private PasswordField passwordField;

    // Etichetta per visualizzare messaggi di errore
    @FXML
    private Label errorLabel;

    // Gestore del database passato tramite il costruttore
    private final DatabaseManager dbManager;

    // Costruttore che inizializza il controller con un'istanza di DatabaseManager
    public AdminLoginController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // Metodo invocato quando si clicca sul pulsante di login
    @FXML
    private void onLogin() {
        // Stampa in console l'istanza di dbManager per eventuali debug
        System.out.println("Using dbManager: " + dbManager);
        
        // Recupera il testo inserito dall'utente nei campi username e password
        String user = usernameField.getText();
        String pass = passwordField.getText();

        // Verifica se le credenziali sono corrette (in questo caso "admin"/"admin")
        if ("admin".equals(user) && "admin".equals(pass)) {
            try {
                // Carica il file FXML della vista amministratore
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_view.fxml"));
                Parent adminView = loader.load();

                // Ottiene l'attuale stage (finestra) e imposta il nuovo contenuto
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.getScene().setRoot(adminView);
                // Configura lo stage: modalità a schermo intero e titolo aggiornato
                stage.setFullScreen(true);
                stage.setTitle("Amministratore");
            } catch (IOException e) {
                // Gestione delle eccezioni in caso di errori nel caricamento del file FXML
                e.printStackTrace();
            }
        } else {
            // Visualizza un messaggio di errore se le credenziali non sono corrette
            errorLabel.setText("Credenziali errate. Riprova!");
        }
    }

    // Metodo chiamato quando si decide di tornare alla schermata di login
    @FXML
    private void onBack() {
        try {
            // Ottiene l'attuale stage e memorizza le dimensioni correnti della finestra
            Stage stage = (Stage) usernameField.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            // Carica il file FXML della vista di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            // Crea una nuova scena con la vista appena caricata
            Scene newScene = new Scene(root);
            // Aggiorna lo stage con la nuova scena e riapplica le impostazioni precedenti
            stage.setScene(newScene);
            stage.setFullScreen(true);
            stage.setWidth(currentW);
            stage.setHeight(currentH);
            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            // Gestione delle eccezioni per errori nel caricamento del file FXML
            e.printStackTrace();
        }
    }
}
