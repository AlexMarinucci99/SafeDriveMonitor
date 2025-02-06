package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.Reading;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConductorResultsController {

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

    private DatabaseManager dbManager;
    private String driverId;

    // Non inizializziamo dbManager qui in modo da poterlo impostare esternamente
    @FXML
    public void initialize() {
        alcoholCol.setCellValueFactory(new PropertyValueFactory<>("alcoholLevel"));
        thcCol.setCellValueFactory(new PropertyValueFactory<>("thcLevel"));
        cocaineCol.setCellValueFactory(new PropertyValueFactory<>("cocaineLevel"));
        mdmaCol.setCellValueFactory(new PropertyValueFactory<>("mdmaLevel"));
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
    }

    // Setter per driverId; controlla se dbManager è già stato impostato per caricare i risultati
    public void setDriverId(String driverId) {
        this.driverId = driverId;
        if(dbManager != null) {
            loadResults();
        }
    }
    
    // Nuovo setter per DatabaseManager
    public void setDbManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.dbManager.initDB();
        // Se il driverId è già stato impostato, carica subito i risultati
        if(driverId != null) {
            loadResults();
        }
    }

    private void loadResults() {
        if(dbManager == null || driverId == null) return;

        List<Reading> readings = new ArrayList<>();
        String sql = "SELECT alcohol_level, thc_level, cocaine_level, mdma_level, result, timestamp "
                   + "FROM readings WHERE driver_id=? ORDER BY id DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driverId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reading r = new Reading();
                r.setAlcoholLevel(rs.getDouble("alcohol_level"));
                r.setThcLevel(rs.getDouble("thc_level"));
                r.setCocaineLevel(rs.getDouble("cocaine_level"));
                r.setMdmaLevel(rs.getDouble("mdma_level"));
                r.setResult(rs.getString("result"));
                r.setTimestamp(rs.getString("timestamp"));
                readings.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resultsTable.getItems().setAll(readings);
    }
}
