package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.MonitoringController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
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

public class ConductorMainController {

    @FXML
    private Label testResultLabel;
    @FXML
    private Label vehicleStatusLabel;
    @FXML
    private Label authorityLabel;

    private MonitoringController monitoringController;
    private DatabaseManager dbManager;

    @FXML
    public void initialize() {
        dbManager = new DatabaseManager();
        dbManager.initDB();
        monitoringController = new MonitoringController(dbManager);
    }

    @FXML
    private void onExecuteTest() {
        String driverName = ConductorSession.name;
        String driverId = ConductorSession.id;

        // Eseguire test (ora con 2 parametri)
        MonitoringController.TestResult result = monitoringController.executeTest(driverId, driverName);

        // Gestione scritte
        if (result.passed) {
            testResultLabel.setText("✅ TEST SUPERATO ✅");
            testResultLabel.getStyleClass().add("test-success-label");
            testResultLabel.setStyle("-fx-text-fill:rgb(255, 255, 255); -fx-font-size: 28px; " +
                    "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,100,0,0.3), 10, 0.5, 0.0, 0.0);");

            vehicleStatusLabel.setText("🚗 VEICOLO SBLOCCATO 🚗");
            vehicleStatusLabel.getStyleClass().add("vehicle-blocked-label");
            vehicleStatusLabel.setStyle("-fx-text-fill:rgb(0, 29, 0); -fx-font-size: 28px; " +
                    "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,100,0,0.3), 10, 0.5, 0.0, 0.0);");

            authorityLabel.setText("");
        } else {

            testResultLabel.setText("❌ TEST NON SUPERATO ❌");
            testResultLabel.getStyleClass().add("test-fail-label");
            testResultLabel.setStyle("-fx-text-fill: rgb(139,0,0); -fx-font-size: 28px; " +
                    "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(139,0,0,0.3), 10, 0.5, 0.0, 0.0);");

            vehicleStatusLabel.setText("🔒 VEICOLO BLOCCATO 🔒");
            vehicleStatusLabel.getStyleClass().add("vehicle-blocked-label");
            vehicleStatusLabel.setStyle("-fx-text-fill: rgb(139,0,0); -fx-font-size: 28px; " +
                    "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(139,0,0,0.3), 10, 0.5, 0.0, 0.0);");

            animatePoliceCar();

        }
    }

    private void animatePoliceCar() {
        authorityLabel.setText("🚓Notifica alle autorità inviata 🚓");
        authorityLabel.getStyleClass().add("authorities-notification-label");
        authorityLabel.setStyle("-fx-text-fill: rgb(0, 139, 7); -fx-font-size: 28px; " +
                "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0, 139, 0, 0.8), 15, 0.7, 0.0, 0.0)," +
                "glow(rgb(0, 139, 7), 0.5);");

        // Animazione multipla con effetti combinati
        Timeline moveTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(authorityLabel.translateXProperty(), -200)),
                new KeyFrame(Duration.seconds(2), new KeyValue(authorityLabel.translateXProperty(), 200)));
        moveTimeline.setAutoReverse(true);
        moveTimeline.setCycleCount(2);

        // Aggiunta animazione di scaling per effetto pulsante
        Timeline scaleTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(authorityLabel.scaleXProperty(), 1.0),
                        new KeyValue(authorityLabel.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(authorityLabel.scaleXProperty(), 1.2),
                        new KeyValue(authorityLabel.scaleYProperty(), 1.2)));
        scaleTimeline.setAutoReverse(true);
        scaleTimeline.setCycleCount(4);

        // Esegui entrambe le animazioni
        moveTimeline.setOnFinished(event -> authorityLabel.setTranslateX(0));
        moveTimeline.play();
        scaleTimeline.play();
    }
        @FXML
        private void onViewMyResults() {
            try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_results.fxml"));
            Parent root = loader.load();

            ConductorResultsController controller = loader.getController();
            // Pass the DatabaseManager and driverId so results can be loaded
            // Pass the DatabaseManager and driverId so results can be loaded
            controller.setDbManager(dbManager);
            controller.setDriverId(ConductorSession.id);
            controller.setDriverId(ConductorSession.id);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initOwner(testResultLabel.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setScene(scene);
            stage.setTitle("I miei risultati");
            stage.showAndWait();
            } catch (IOException e) {
            e.printStackTrace();
            }
        }

        @FXML
        private void onBack() {
        try {
            Stage stage = (Stage) testResultLabel.getScene().getWindow();
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
