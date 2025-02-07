package it.safedrivemonitor.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
        deviceSpinner.setVisible(true);
        deviceStatusLabel.setText("Rilevazione del dispositivo...");
        
        // Effetto "pulsante" sullo spinner per dare un senso di attesa
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1), deviceSpinner);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(ScaleTransition.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // Pausa iniziale di 5 secondi
        PauseTransition initialPause = new PauseTransition(Duration.seconds(5));
        initialPause.setOnFinished(e -> {
            // Arresta l'effetto pulsante
            pulse.stop();
            
            // Dissolvenza e riduzione in scala dello spinner
            FadeTransition spinnerFadeOut = new FadeTransition(Duration.seconds(1), deviceSpinner);
            spinnerFadeOut.setFromValue(1.0);
            spinnerFadeOut.setToValue(0.0);
            ScaleTransition spinnerScaleDown = new ScaleTransition(Duration.seconds(1), deviceSpinner);
            spinnerScaleDown.setFromX(1.2);
            spinnerScaleDown.setFromY(1.2);
            spinnerScaleDown.setToX(0.0);
            spinnerScaleDown.setToY(0.0);
            ParallelTransition spinnerTransition = new ParallelTransition(spinnerFadeOut, spinnerScaleDown);
            spinnerTransition.setOnFinished(ev -> deviceSpinner.setVisible(false));

            // Animazione della label: dissolvenza e scorrimento laterale per aggiornare il messaggio
            FadeTransition labelFadeOut = new FadeTransition(Duration.seconds(1), deviceStatusLabel);
            labelFadeOut.setFromValue(1.0);
            labelFadeOut.setToValue(0.0);
            TranslateTransition labelSlideLeft = new TranslateTransition(Duration.seconds(1), deviceStatusLabel);
            labelSlideLeft.setFromX(0);
            labelSlideLeft.setToX(-50);
            ParallelTransition labelOutTransition = new ParallelTransition(labelFadeOut, labelSlideLeft);
            labelOutTransition.setOnFinished(ev -> {
                deviceStatusLabel.setText("Dispositivo Collegato");
                // Imposta la posizione fuori schermo a destra per il successivo ingresso
                deviceStatusLabel.setTranslateX(50);
                // Animazione per portare la label al centro con effetto dissolvenza
                FadeTransition labelFadeIn = new FadeTransition(Duration.seconds(1), deviceStatusLabel);
                labelFadeIn.setFromValue(0.0);
                labelFadeIn.setToValue(1.0);
                TranslateTransition labelSlideCenter = new TranslateTransition(Duration.seconds(1), deviceStatusLabel);
                labelSlideCenter.setFromX(50);
                labelSlideCenter.setToX(0);
                ParallelTransition labelInTransition = new ParallelTransition(labelFadeIn, labelSlideCenter);
                labelInTransition.play();
            });
            
            // Avvia le animazioni dello spinner e della label contemporaneamente
            spinnerTransition.play();
            labelOutTransition.play();
            
            // Pausa dopo le animazioni prima di cambiare schermata
            PauseTransition postTransitionPause = new PauseTransition(Duration.seconds(5));
            postTransitionPause.setOnFinished(ev -> goToConductorMain());
            postTransitionPause.play();
        });
        initialPause.play();
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
