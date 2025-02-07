package it.safedrivemonitor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import it.safedrivemonitor.model.DatabaseManager;

public class LoginController {

    // Bottone per l'accesso del conducente
    @FXML
    private Button onConductorAccess;
    // Bottone per l'accesso dell'amministratore
    @FXML
    private Button onAdminAccess;

    // Metodo chiamato quando si preme il bottone per il login del conducente
    @FXML
    private void onConductorAccess() {
        try {
            // Carica il file FXML del login per il conducente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_login.fxml"));

            // Crea la scena utilizzando il contenuto del file FXML caricato
            Scene scene = new Scene(loader.load());
            // Ottiene lo stage corrente dalla finestra associata al bottone
            Stage stage = (Stage) onConductorAccess.getScene().getWindow();
            // Imposta la nuova scena nello stage
            stage.setScene(scene);
            // Imposta lo stage in modalità schermo intero
            stage.setFullScreen(true);
            // Imposta il titolo della finestra
            stage.setTitle("Login Conducente");
            // Mostra lo stage aggiornato
            stage.show();
        } catch (IOException e) {
            // Gestione dell'eccezione: stampa l'errore in console
            e.printStackTrace();
        }
    }

    // Metodo chiamato quando si preme il bottone per il login dell'amministratore
    @FXML
    private void onAdminAccess() {
        try {
            // Carica il file FXML del login per l'amministratore
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_login.fxml"));

            // Imposta la factory per creare il controller con dipendenza su DatabaseManager
            loader.setControllerFactory(type -> {
                if (type == AdminLoginController.class) {
                    // Crea ed inizializza un nuovo DatabaseManager
                    DatabaseManager db = new DatabaseManager();
                    db.initDB();
                    // Restituisce una nuova istanza del controller con il DatabaseManager iniettato
                    return new AdminLoginController(db);
                }
                try {
                    // Se il controller non è AdminLoginController, utilizza il costruttore predefinito
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    // In caso di errore, lancia una RuntimeException
                    throw new RuntimeException(e);
                }
            });

            // Crea una nuova scena con dimensioni specifiche (1000x600)
            Scene scene = new Scene(loader.load(), 1000, 600);
            // Ottiene lo stage corrente dalla finestra associata al bottone
            Stage stage = (Stage) onAdminAccess.getScene().getWindow(); 
            // Imposta la nuova scena nello stage
            stage.setScene(scene);
            // Imposta lo stage in modalità schermo intero
            stage.setFullScreen(true);
            // Imposta il titolo della finestra
            stage.setTitle("Login Admin");
            // Mostra lo stage aggiornato
            stage.show();
        } catch (IOException e) {
            // Gestione dell'eccezione: stampa l'errore in console
            e.printStackTrace();
        }
    }

}
