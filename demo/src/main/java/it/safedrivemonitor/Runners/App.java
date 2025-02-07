package it.safedrivemonitor.Runners;

// Importa le classi necessarie per gestire il database, la GUI e le risorse (immagini, file FXML)
import it.safedrivemonitor.model.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Classe principale JavaFX che avvia l'applicazione.
 * Estende Application per configurare e mostrare l'interfaccia grafica.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 1. Inizializzazione del Database
        // Crea un'istanza di DatabaseManager e inizializza il database
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initDB();

        // 2. Caricamento della vista iniziale
        // Utilizza FXMLLoader per caricare il file FXML che descrive
        // la struttura della vista iniziale (login_view.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));

        // Crea una nuova scena impostando come root l'albero dei nodi definito nel file FXML
        Scene scene = new Scene(loader.load());
        
        // Imposta la scena sullo stage principale (finestra)
        primaryStage.setScene(scene);
        
        // Abilita la visualizzazione a schermo intero
        primaryStage.setFullScreen(true);
        
        // Imposta l'icona della finestra caricata da un file immagine
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        
        // Imposta il titolo della finestra principale
        primaryStage.setTitle("SafeDriveMonitor-Home");
        
        // Visualizza la finestra
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Punto di ingresso dell'applicazione.
        // Il metodo launch() si occupa di inizializzare JavaFX e chiamare il metodo start.
        launch(args);
    }
}
