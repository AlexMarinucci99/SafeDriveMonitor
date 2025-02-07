package it.safedrivemonitor.controller;

// Importazioni necessarie per JavaFX
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

    // Campi FXML collegati dall'interfaccia grafica
    @FXML
    private TextField driverNameField;   // Campo per il nome del conducente

    @FXML
    private TextField driverIdField;     // Campo per l'ID del conducente

    @FXML
    private Label errorLabel;            // Etichetta per visualizzare messaggi di errore

    // Metodo di inizializzazione eseguito automaticamente al caricamento del controller
    @FXML
    public void initialize() {
        // Limita l'input dell'ID a 6 cifre numeriche (massimo 1 milione di utenti attualmente)
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
                return change; // Accetta l'input se rispetta il pattern
            }
            return null; // Rifiuta l'input se non rispetta il pattern
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        driverIdField.setTextFormatter(formatter);
    }

    // Metodo invocato al tentativo di login, passaggio alla schermata device_detection.fxml
    @FXML
    private void onLogin() {
        // Ottieni e pulisci il testo inserito dall'utente
        String name = driverNameField.getText().trim();
        String id = driverIdField.getText().trim();

        // Verifica se il nome o l'ID sono vuoti
        if (name.isEmpty() || id.isEmpty()) {
            errorLabel.setText("Nome o ID vuoti. Riprova.");
            return;
        }

        // Verifica se il driver risulta valido nel database
        if (!isDriverValid(name, id)) {
            errorLabel.setText("Nome o ID non valido o non corrispondente. Riprova.");
            return;
        }
        
        // Salva i dati del conducente in una sessione statica per il prototipo
        ConductorSession.name = name;
        ConductorSession.id = id;

        // Carica la nuova schermata (device_detection.fxml)
        try {
            // Ottieni la finestra corrente dalla scena associata al campo dell'ID
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();

            // Carica il file FXML della nuova scena
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

    // Metodo per verificare se il conducente esiste nel database con nome e ID corrispondenti
    private boolean isDriverValid(String name, String id) {
        // Query SQL per contare le righe che corrispondono al nome e all'ID forniti
        String sql = "SELECT COUNT(*) FROM drivers WHERE driver_name = ? AND driver_id = ?";
        try (Connection conn = new DatabaseManager().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, id);

            // Esegue la query
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Utente trovato
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Utente non trovato o errore nella query
    }

    // Metodo invocato al click sul pulsante per il reset dell'ID
    @FXML
    private void onResetIdClicked() {
        try {
            // Ottiene la finestra corrente dalla scena del driverIdField
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            // Carica il file FXML relativo al reset ID
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reset_id.fxml"));
            Parent root = loader.load();

            // Imposta una nuova scena con dimensioni specificate
            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setTitle("Reset ID Conducente");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo invocato al click sul pulsante "indietro"
    @FXML
    private void onBack() {
        try {
            // Ottieni la finestra corrente e le sue dimensioni
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            // Carica il file FXML della schermata di login
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

    // Metodo invocato al click sul pulsante per la registrazione del conducente
    @FXML
    private void onRegister() {
        try {
            // Carica il file FXML della schermata di registrazione
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_registration.fxml"));
            Parent root = loader.load();

            // Recupera lo stage dalla scena corrente tramite driverIdField
            Stage stage = (Stage) driverIdField.getScene().getWindow();
            // Crea una nuova scena utilizzando le dimensioni correnti dello stage
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
