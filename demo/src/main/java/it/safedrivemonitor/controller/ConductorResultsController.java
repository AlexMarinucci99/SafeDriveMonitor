package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.Reading;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConductorResultsController {

    // Riferimenti alla TableView e alle colonne della tabella nel file FXML
    @FXML
    private TableView<Reading> resultsTable;
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

    // Gestore del DB e identificativo del driver
    private DatabaseManager dbManager;
    private String driverId;

    // Metodo di inizializzazione chiamato automaticamente da JavaFX
    @FXML
    public void initialize() {
        // Imposta la proprietà corretta per ogni colonna utilizzando il PropertyValueFactory
        alcoholCol.setCellValueFactory(new PropertyValueFactory<>("alcoholLevel"));
        thcCol.setCellValueFactory(new PropertyValueFactory<>("thcLevel"));
        cocaineCol.setCellValueFactory(new PropertyValueFactory<>("cocaineLevel"));
        mdmaCol.setCellValueFactory(new PropertyValueFactory<>("mdmaLevel"));
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    }

    // Setter per impostare il driverId e caricare i risultati se il dbManager è già disponibile
    public void setDriverId(String driverId) {
        this.driverId = driverId;
        if(dbManager != null) {
            loadResults();  // Carica i risultati se il DatabaseManager è già stato settato
        }
    }
    
    // Setter per impostare il DatabaseManager
    public void setDbManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.dbManager.initDB(); // Inizializza il database
        // Se il driverId è già stato impostato, carica immediatamente i risultati
        if(driverId != null) {
            loadResults();
        }
    }

    // Metodo per caricare i risultati dal database e popolare la TableView
    private void loadResults() {
        // Controlla se il DatabaseManager e il driverId sono stati impostati
        if(dbManager == null || driverId == null) return;

        List<Reading> readings = new ArrayList<>();
        // Query SQL per selezionare i campi necessari dalla tabella 'readings' per un determinato driver
        String sql = "SELECT alcohol_level, thc_level, cocaine_level, mdma_level, result, timestamp "
                   + "FROM readings WHERE driver_id=? ORDER BY id DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Imposta il parametro della query con il driverId
            pstmt.setString(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            // Cicla sul result set e crea un oggetto Reading per ogni record
            while (rs.next()) {
                int id = 0;
                String driverIdFromDB = ConductorResultsController.this.driverId;
                String driverName = "";
                double alcoholLevel = rs.getDouble("alcohol_level");
                double thcLevel = rs.getDouble("thc_level");
                double cocaineLevel = rs.getDouble("cocaine_level");
                double mdmaLevel = rs.getDouble("mdma_level");
                String result = rs.getString("result");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                Reading r = new Reading(id, driverIdFromDB, driverName, alcoholLevel, thcLevel, cocaineLevel, mdmaLevel, result, timestamp);
                readings.add(r);
            }
        } catch (SQLException e) {
            // Stampa l'errore in caso di eccezione SQL
            e.printStackTrace();
        }
        // Aggiorna i dati mostrati nella TableView
        resultsTable.getItems().setAll(readings);
    }
}
