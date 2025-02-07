package it.safedrivemonitor.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class DeviceDetectionController {

    // Riferimenti agli elementi grafici definiti nel file FXML
    @FXML
    private Label deviceStatusLabel; // Etichetta per mostrare lo stato del dispositivo

    @FXML
    private ProgressIndicator deviceSpinner; // Indicatore grafico per mostrare il caricamento

    // Metodo inizializzato automaticamente dopo il caricamento del file FXML
    @FXML
    public void initialize() {
        // Mostra immediatamente lo spinner e imposta il primo messaggio
        deviceSpinner.setVisible(true);
        deviceStatusLabel.setText("Rilevazione del dispositivo...");

        // Imposta una pausa di 5 secondi
        PauseTransition p1 = new PauseTransition(Duration.seconds(5));
        p1.setOnFinished(e -> {
            // Dopo 5 secondi nasconde lo spinner e aggiorna il messaggio
            deviceSpinner.setVisible(false);
            deviceStatusLabel.setText("Dispositivo Collegato");

            // Avvia un'altra pausa di 5 secondi
            PauseTransition p2 = new PauseTransition(Duration.seconds(5));
            p2.setOnFinished(e2 -> {
                // Al termine della seconda pausa, passa alla schermata successiva
                goToConductorMain();
            });
            p2.play();
        });
        p1.play();
    }

    // Metodo per il passaggio alla schermata "conductor_main.fxml"
    private void goToConductorMain() {
        try {
            // Recupera la finestra corrente dal contesto dell'etichetta
            Stage stage = (Stage) deviceStatusLabel.getScene().getWindow();
            // Salva le dimensioni attuali della finestra
            double w = stage.getWidth();
            double h = stage.getHeight();

            // Carica il file FXML per la schermata successiva
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_main.fxml"));
            Parent root = loader.load();

            // Crea una nuova scena con la schermata caricata
            Scene scene = new Scene(root);
            stage.setScene(scene);
            // Imposta la finestra a schermo intero e ripristina le dimensioni precedenti
            stage.setFullScreen(true);
            stage.setWidth(w);
            stage.setHeight(h);

            // Imposta il titolo della finestra e mostra la schermata
            stage.setTitle("Conducente - Test");
            stage.show();
        } catch (IOException e) {
            // Gestione dell'eventuale eccezione di input/output durante il caricamento dell'FXML
            e.printStackTrace();
        }
    }
}
