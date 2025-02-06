package it.safedrivemonitor.controller;

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
        // Mappa le colonne ai campi di Reading
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
     * Metodo invocato dal pulsante "Visualizza Log" in admin_view.fxml
     */
    @FXML
    private void onViewLog() {
        new Thread(() -> {
            List<Reading> readings = new ArrayList<>();
            String sql = "SELECT id, driver_id,driver_name, alcohol_level, thc_level, cocaine_level, mdma_level, result, timestamp "
                    + "FROM readings";

            try (Connection conn = dbManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    ResultSet rs = pstmt.executeQuery()) {

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
                e.printStackTrace();
            }

            // Aggiorniamo la TableView sul thread JavaFX
            Platform.runLater(() -> tableView.getItems().setAll(readings));
        }).start();
    }

    @FXML
    private void onBack() {
        try {
            Stage stage = (Stage) tableView.getScene().getWindow();
            double currentW = stage.getWidth();
            double currentH = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
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
