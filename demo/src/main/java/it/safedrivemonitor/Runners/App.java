package it.safedrivemonitor.Runners;

import it.safedrivemonitor.model.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Classe principale JavaFX che avvia l'applicazione.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 1. Inizializziamo il Database
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initDB();

        // 2. Carichiamo la vista iniziale (login_view.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        primaryStage.setTitle("SafeDriveMonitor-Home");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
