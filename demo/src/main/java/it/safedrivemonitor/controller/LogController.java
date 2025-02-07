package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.Reading;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogController {

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
        
        // Carica i dati in background
        loadData();
    }

    private void loadData() {
        Task<List<Reading>> loadDataTask = new Task<>() {
            @Override
            protected List<Reading> call() throws Exception {
                List<Reading> readings = new ArrayList<>();
                String sql = "SELECT id, driver_id, alcohol_level, thc_level, cocaine_level, mdma_level, result, timestamp FROM readings";
                try (Connection conn = dbManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        // Assuming the driver_name column is not available in the query, we pass an empty string.
                        // Convert the timestamp from String to LocalDateTime.
                        LocalDateTime timestamp = LocalDateTime.parse(rs.getString("timestamp"));
                        Reading reading = new Reading(
                            rs.getInt("id"),
                            rs.getString("driver_id"),
                            "",
                            rs.getDouble("alcohol_level"),
                            rs.getDouble("thc_level"),
                            rs.getDouble("cocaine_level"),
                            rs.getDouble("mdma_level"),
                            rs.getString("result"),
                            timestamp
                        );
                        readings.add(reading);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw e;
                }
                return readings;
            }
        };

        loadDataTask.setOnSucceeded(event ->
            // Aggiorna l'interfaccia utente
            tableView.getItems().setAll(loadDataTask.getValue())
        );

        loadDataTask.setOnFailed(event -> {
            // Gestisci l'errore in modo appropriato (ad esempio, mostrando un alert)
            Throwable error = loadDataTask.getException();
            error.printStackTrace();
        });

        new Thread(loadDataTask).start();
    }
}
