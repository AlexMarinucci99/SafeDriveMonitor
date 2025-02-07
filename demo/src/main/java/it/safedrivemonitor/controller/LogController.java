package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.Reading;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.application.Platform;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogController {

    // Riferimenti ai componenti UI definiti nel file FXML
    
    @FXML
    private TableView<Reading> tableView; // Tabella per mostrare le letture
    @FXML
    private TableColumn<Reading, Number> idCol; // Colonna ID
    @FXML
    private TableColumn<Reading, String> driverIdCol; // Colonna per l'ID del driver
    @FXML
    private TableColumn<Reading, Number> alcoholCol; // Colonna per il livello di alcol
    @FXML
    private TableColumn<Reading, Number> thcCol; // Colonna per il livello di THC
    @FXML
    private TableColumn<Reading, Number> cocaineCol; // Colonna per il livello di cocaina
    @FXML
    private TableColumn<Reading, Number> mdmaCol; // Colonna per il livello di MDMA
    @FXML
    private TableColumn<Reading, String> resultCol; // Colonna per il risultato della lettura
    @FXML
    private TableColumn<Reading, String> timestampCol; // Colonna per il timestamp della lettura

    // Istanza del gestore del database
    private final DatabaseManager dbManager = new DatabaseManager();

    @FXML
    public void initialize() {
        // Associa le proprietà dell'oggetto Reading alle colonne della TableView
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        driverIdCol.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        alcoholCol.setCellValueFactory(new PropertyValueFactory<>("alcoholLevel"));
        thcCol.setCellValueFactory(new PropertyValueFactory<>("thcLevel"));
        cocaineCol.setCellValueFactory(new PropertyValueFactory<>("cocaineLevel"));
        mdmaCol.setCellValueFactory(new PropertyValueFactory<>("mdmaLevel"));
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // Carica i dati dalla base di dati
        loadData();
    }

    // Metodo per il caricamento dei dati dal database in un thread separato
    private void loadData() {
        new Thread(() -> {
            List<Reading> readings = new ArrayList<>();
            // Query SQL per selezionare i dati dalla tabella "readings"
            String sql = "SELECT id, driver_id, alcohol_level, thc_level, cocaine_level, mdma_level, result, timestamp FROM readings";
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                // Cicla sui risultati della query e crea oggetti Reading
                while (rs.next()) {
                    Reading r = new Reading();
                    r.setId(rs.getInt("id"));
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
                // Gestione delle eccezioni SQL: stampa dello stack trace dell'errore
                e.printStackTrace();
            }

            // Aggiorna l'interfaccia utente nel thread JavaFX utilizzando Platform.runLater
            Platform.runLater(() -> tableView.getItems().setAll(readings));
        }).start();
    }
}
