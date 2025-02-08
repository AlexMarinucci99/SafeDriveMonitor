package it.safedrivemonitor.controller;

// Importazioni necessarie per il funzionamento del controller e la gestione della GUI
import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.MonitoringController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/*
 * ConductorMainController: controller principale per la vista del conducente.
 * Gestisce l'esecuzione dei test, la visualizzazione dei risultati e la navigazione tra le viste.
 */
public class ConductorMainController {

    // Label per mostrare il risultato del test
    @FXML
    private Label testResultLabel;
    // Label per mostrare lo stato del veicolo (sbloccato o bloccato)
    @FXML
    private Label vehicleStatusLabel;
    // Label per notificare alle autorità (animazione visiva)
    @FXML
    private Label authorityLabel;

    // Controller per la gestione del monitoraggio
    private MonitoringController monitoringController;
    // Gestore del database
    private DatabaseManager dbManager;

    /*
     * Metodo di inizializzazione chiamato automaticamente dopo il caricamento del file FXML.
     * Inizializza il DatabaseManager e il MonitoringController.
     */
    @FXML
    public void initialize() {
        dbManager = new DatabaseManager();
        dbManager.initDB();
        monitoringController = new MonitoringController(dbManager);
    }

    /*
     * Metodo associato all'azione di esecuzione del test.
     * Recupera le informazioni del conducente e avvia il test.
     * In base al risultato, aggiorna le label e, in caso di fallimento, avvia l'animazione della polizia.
     */
    @FXML
    private void onExecuteTest() {
        // Recupera nome e ID del conducente dalla sessione corrente
        String driverName = ConductorSession.name;
        String driverId = ConductorSession.id;

        // Esecuzione del test con due parametri: ID e nome del conducente
        MonitoringController.TestResult result = monitoringController.executeTest(driverId, driverName);

        // Gestione del risultato del test
        if (result.passed) {
            // Imposta il testo e lo stile in caso di test superato con colori più vividi
            testResultLabel.setText("✅ TEST SUPERATO ✅");
            testResultLabel.getStyleClass().add("test-success-label");
            testResultLabel.setStyle(
                "-fx-text-fill: #00FF00; " +         // Verde brillante
                "-fx-font-size: 35px; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0.5, 0.0, 0.0);"
            );

            vehicleStatusLabel.setText("🚗 VEICOLO SBLOCCATO 🚗");
            vehicleStatusLabel.getStyleClass().add("vehicle-success-label");
            vehicleStatusLabel.setStyle(
                "-fx-text-fill: #00FF00; " +         // Verde brillante
                "-fx-font-size: 35px; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0.5, 0.0, 0.0);"
            );

            // Nessuna notifica alle autorità in caso positivo
            authorityLabel.setText("");
        } else {
            // Imposta il testo e lo stile in caso di test non superato con colori più definiti
            testResultLabel.setText("❌ TEST NON SUPERATO ❌");
            testResultLabel.getStyleClass().add("test-fail-label");
            testResultLabel.setStyle(
                "-fx-text-fill: #FF4500; " +         // Rosso arancione acceso
                "-fx-font-size: 35px; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0.5, 0.0, 0.0);"
            );

            vehicleStatusLabel.setText("🔒 VEICOLO BLOCCATO 🔒");
            vehicleStatusLabel.getStyleClass().add("vehicle-fail-label");
            vehicleStatusLabel.setStyle(
                "-fx-text-fill: #FF4500; " +         // Rosso arancione acceso
                "-fx-font-size: 35px; " +
                "-fx-font-weight: bold; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 10, 0.5, 0.0, 0.0);"
            );
            
            // Avvia l'animazione che notifica l'invio di una segnalazione alle autorità
            animatePoliceCar();
        }
    }

    /*
     * Metodo per animare la notifica visiva alle autorità in caso di test fallito.
     * Imposta il testo e lo stile della label, e avvia animazioni di movimento, scaling, fade (senza scomparire)
     * e una rotazione per risaltare la notifica all'occhio.
     */
    private void animatePoliceCar() {
        // Imposta il testo e lo stile della label per la notifica
        authorityLabel.setText("🚓 NOTIFICA ALLE AUTORITÁ INVIATA 🚓");
        authorityLabel.getStyleClass().add("authorities-notification-label");
        authorityLabel.setStyle("-fx-text-fill: rgb(24, 26, 24); -fx-font-size: 50px; " +
                "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,139,0,0.8), 15, 0.7, 0.0, 0.0), " +
                "glow(rgb(0,139,7), 0.5);");

        // Animazione di movimento con Interpolator.EASE_BOTH per rendere il movimento più fluido
        Timeline moveTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(authorityLabel.translateXProperty(), -200, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(2), new KeyValue(authorityLabel.translateXProperty(), 200, Interpolator.EASE_BOTH))
        );
        moveTimeline.setAutoReverse(true);
        moveTimeline.setCycleCount(2);

        // Animazione di scaling con un piccolo aumento per effetto pulsante
        Timeline scaleTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(authorityLabel.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                        new KeyValue(authorityLabel.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(authorityLabel.scaleXProperty(), 1.3, Interpolator.EASE_BOTH),
                        new KeyValue(authorityLabel.scaleYProperty(), 1.3, Interpolator.EASE_BOTH))
        );
        scaleTimeline.setAutoReverse(true);
        scaleTimeline.setCycleCount(4);

        // Aggiunta di una transizione fade per far apparire la notifica senza farla scomparire
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), authorityLabel);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(false);

        // Nuova animazione: una leggera rotazione per risaltare ulteriormente la notifica all'occhio
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), authorityLabel);
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(10);
        rotateTransition.setCycleCount(2);
        rotateTransition.setAutoReverse(true);

        // Al termine dell'animazione di movimento, reimposta la posizione della label
        moveTimeline.setOnFinished(event -> authorityLabel.setTranslateX(0));

        // Avvia tutte le animazioni
        moveTimeline.play();
        scaleTimeline.play();
        fadeTransition.play();
        rotateTransition.play();
    }
    /*
     * Metodo per la visualizzazione dei risultati del conducente.
     * Carica la nuova finestra (FXML) che mostra i risultati precedenti e passa il DatabaseManager e l'ID del conducente.
     */
    @FXML
    private void onViewMyResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_results.fxml"));
            Parent root = loader.load();

            // Recupera il controller della finestra dei risultati
            ConductorResultsController controller = loader.getController();
            // Imposta il DatabaseManager e l'ID del conducente per il caricamento dei dati
            controller.setDbManager(dbManager);
            controller.setDriverId(ConductorSession.id);
            controller.setDriverId(ConductorSession.id);

            // Imposta la scena e configura la nuova finestra modale
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initOwner(testResultLabel.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setScene(scene);
            stage.setTitle("I miei risultati");
            stage.showAndWait();
        } catch (IOException e) {
            // Gestisce eventuali errori durante il caricamento dell'FXML
            e.printStackTrace();
        }
    }

    /*
     * Metodo per tornare alla schermata precedente (login_view).
     * Carica il file FXML della vista di login e sostituisce la scena corrente.
     */
    @FXML
    private void onBack() {
        try {
            // Recupera la finestra corrente e ne mantiene le dimensioni
            Stage stage = (Stage) testResultLabel.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            // Carica il file FXML della vista di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            // Crea una nuova scena e la imposta nella finestra corrente
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setFullScreen(true);

            // Ripristina le dimensioni originali della finestra
            stage.setWidth(currentW);
            stage.setHeight(currentH);

            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            // Gestisce eventuali errori durante il caricamento dell'FXML
            e.printStackTrace();
        }
    }
}
