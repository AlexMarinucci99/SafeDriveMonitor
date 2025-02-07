package it.safedrivemonitor.controller;

// Importazione delle classi necessarie per il funzionamento dell'interfaccia grafica e per la gestione del database
import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.Reading;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    // Riferimenti alle componenti dell'interfaccia definiti nel file FXML
    @FXML
    private TableView<Reading> tableView;
    @FXML
    private TableColumn<Reading, Number> idCol;
    @FXML
    private TableColumn<Reading, String> driverIdCol;
    @FXML
    private TableColumn<Reading, Number> alcoholCol;
    @FXML
    private TableColumn<Reading, Number> thcCol;
    @FXML
    private TableColumn<Reading, Number> cocaineCol;
    @FXML
    private TableColumn<Reading, Number> mdmaCol;
    @FXML
    private TableColumn<Reading, String> resultCol;
    @FXML
    private TableColumn<Reading, String> timestampCol;

    // Istanza del DatabaseManager per la gestione della connessione al database
    private final DatabaseManager dbManager = new DatabaseManager();

    /**
     * Metodo di inizializzazione chiamato automaticamente dopo il caricamento del file FXML.
     * Associa le colonne della TableView alle proprietà della classe Reading.
     */
    @FXML
    public void initialize() {
        // Associa le colonne ai rispettivi campi della classe Reading tramite PropertyValueFactory
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        driverIdCol.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        alcoholCol.setCellValueFactory(new PropertyValueFactory<>("alcoholLevel"));
        thcCol.setCellValueFactory(new PropertyValueFactory<>("thcLevel"));
        cocaineCol.setCellValueFactory(new PropertyValueFactory<>("cocaineLevel"));
        mdmaCol.setCellValueFactory(new PropertyValueFactory<>("mdmaLevel"));
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    }

    /**
     * Metodo invocato dal pulsante "Visualizza Log" presenti nell'interfaccia admin_view.fxml.
     * Avvia un nuovo thread per eseguire la query sul database e aggiornare la TableView con i dati letti.
     */
    @FXML
    private void onViewLog() {
        new Thread(() -> {
            List<Reading> readings = new ArrayList<>();
            // Query SQL per recuperare tutte le letture dalla tabella "readings"
            String sql = "SELECT id, driver_id, driver_name, alcohol_level, thc_level, cocaine_level, mdma_level, result, timestamp "
                    + "FROM readings";

            // Gestione della connessione, preparazione e esecuzione della query in modalità try-with-resources
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                // Itera sui record risultanti dalla query e per ogni record crea un oggetto Reading con i dati
                while (rs.next()) {
                    Reading r = new Reading();
                    r.setId(rs.getInt("id"));
                    r.setDriverName(rs.getString("driver_name"));
                    r.setDriverId(rs.getString("driver_id"));
                    r.setAlcoholLevel(rs.getDouble("alcohol_level"));
                    r.setThcLevel(rs.getDouble("thc_level"));
                    r.setCocaineLevel(rs.getDouble("cocaine_level"));
                    r.setMdmaLevel(rs.getDouble("mdma_level"));
                    r.setResult(rs.getString("result"));
                    r.setTimestamp(rs.getString("timestamp"));
                    readings.add(r);
                }
            } catch (SQLException e) {
                // Stampa dell'errore in console se si verifica un'eccezione di SQL
                e.printStackTrace();
            }

            // Aggiorna la TableView sul thread JavaFX, poiché le operazioni sull'interfaccia devono avvenire sul thread UI
            Platform.runLater(() -> tableView.getItems().setAll(readings));
        }).start();
    }

    /**
     * Metodo invocato dal pulsante "Indietro" presente nell'interfaccia admin_view.fxml.
     * Carica la schermata di login (login_view.fxml) mantenendo le dimensioni attuali della finestra.
     */
    @FXML
    private void onBack() {
        try {
            // Recupera il riferimento alla finestra corrente tramite la TableView
            Stage stage = (Stage) tableView.getScene().getWindow();
            // Salva le dimensioni attuali della finestra per ripristinarle in seguito
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            // Carica il file FXML della schermata di login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Scene scene = new Scene(loader.load());

            // Imposta la nuova scena sulla finestra, passa alla modalità a schermo intero e ripristina le dimensioni
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setWidth(currentW);
            stage.setHeight(currentH);

            // Imposta il titolo della finestra e mostra la nuova interfaccia utente
            stage.setTitle("SafeDriveMonitor-Home");
            stage.show();
        } catch (IOException e) {
            // Stampa l'errore in console se si verifica un'eccezione durante il caricamento dell'FXML
            e.printStackTrace();
        }
    }

}
