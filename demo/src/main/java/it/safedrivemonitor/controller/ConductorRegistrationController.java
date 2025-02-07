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

    // Riferimenti ai componenti dell'interfaccia definiti nel file FXML
    @FXML
    private TextField driverNameField;
    @FXML
    private TextField driverIdField;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField emailField;

    // Gestore della connessione al database
    private final DatabaseManager dbManager = new DatabaseManager();

    // Metodo di inizializzazione chiamato dopo il caricamento dell'FXML
    @FXML
    public void initialize() {
        // Limita l'inserimento dell'ID a un massimo di 6 cifre.
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,6}")) {
                return change; // Permette l'input se rispetta il pattern
            }
            return null; // Rigetta l'input non conforme
        };
        // Imposta il TextFormatter per il campo driverIdField
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        driverIdField.setTextFormatter(formatter);
    }

    // Metodo invocato al click del pulsante di registrazione
    @FXML
    private void onRegister() {
        String name = driverNameField.getText().trim();
        String id = driverIdField.getText().trim();
        String email = emailField.getText().trim();

        // Verifica che tutti i campi siano compilati
        if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
            errorLabel.setText("Tutti i campi sono obbligatori. Riprova.");
            return;
        }

        // Se il conducente esiste già, mostra un messaggio di errore
        if (isDriverExists(name, id)) {
            errorLabel.setText("Nome o ID già registrati. Riprova.");
            return;
        }

        // Prova ad inserire i dati sul database
        try (Connection conn = dbManager.getConnection()) {
            String sql = "INSERT INTO drivers (driver_id, driver_name, email) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.executeUpdate();
                // Se l'inserimento è andato a buon fine, notifica l'utente
                errorLabel.setText("Registrazione completata!");
                errorLabel.setStyle("-fx-text-fill: green;");

                // Imposta una pausa di 5 secondi prima di cambiare schermata
                PauseTransition pause = new PauseTransition(Duration.seconds(5));
                pause.setOnFinished(e -> {
                    // Ottieni la finestra corrente
                    Stage stage = (Stage) driverIdField.getScene().getWindow();
                    // Carica il file FXML della schermata di login
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_login.fxml"));
                    Parent root;
                    try {
                        root = loader.load();
                        // Imposta la nuova scena mantenendo la dimensione della finestra
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

    // Metodo per controllare se il conducente è già presente nel database
    private boolean isDriverExists(String name, String id) {
        String sql = "SELECT COUNT(*) FROM drivers WHERE driver_name = ? OR driver_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            ResultSet rs = pstmt.executeQuery();
            // Ritorna true se viene trovato almeno un record
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // In caso di errore, blocca la registrazione
        }
    }

    // Metodo invocato al click del pulsante "Indietro"
    @FXML
    private void onBack() {
        try {
            // Recupera la finestra corrente
            Stage stage = (Stage) driverIdField.getScene().getWindow();

            // Carica il file FXML della schermata di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_login.fxml"));
            Parent root = loader.load();

            // Imposta la nuova scena mantenendo la dimensione corrente della finestra
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
