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

    @FXML
    private Label deviceStatusLabel;

    @FXML
    private ProgressIndicator deviceSpinner;

    @FXML
    public void initialize() {
        // Subito mostri lo spinner e il primo messaggio
        deviceSpinner.setVisible(true);
        deviceStatusLabel.setText("Rilevazione del dispositivo...");

        // 5 secondi
        PauseTransition p1 = new PauseTransition(Duration.seconds(5));
        p1.setOnFinished(e -> {
            deviceSpinner.setVisible(false);
            deviceStatusLabel.setText("Dispositivo Collegato");

            // Altri 5 secondi
            PauseTransition p2 = new PauseTransition(Duration.seconds(5));
            p2.setOnFinished(e2 -> {
                // Fine -> passiamo a conductor_main.fxml
                goToConductorMain();
            });
            p2.play();
        });
        p1.play();
    }

    private void goToConductorMain() {
        try {
            Stage stage = (Stage) deviceStatusLabel.getScene().getWindow();
            double w = stage.getWidth();
            double h = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setWidth(w);
            stage.setHeight(h);

            stage.setTitle("Conducente - Test");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
