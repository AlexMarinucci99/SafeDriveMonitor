package it.safedrivemonitor.controller;

// Importazione delle classi necessarie per il funzionamento dell'interfaccia grafica e per la gestione del database
import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.model.Reading;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.scene.control.Label;

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
    @FXML
    private Label statsLabel;

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
        tableView.setColumnResizePolicy(param -> {
            double width = tableView.getWidth();
            int visibleColumns = tableView.getVisibleLeafColumns().size();
            double newWidth = width / visibleColumns;
            for (TableColumn<?, ?> col : tableView.getVisibleLeafColumns()) {
                col.setPrefWidth(newWidth);
            }
            return true;
        });
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
                    Reading r = new Reading(
                        rs.getInt("id"),
                        rs.getString("driver_id"),
                        rs.getString("driver_name"),
                        rs.getDouble("alcohol_level"),
                        rs.getDouble("thc_level"),
                        rs.getDouble("cocaine_level"),
                        rs.getDouble("mdma_level"),
                        rs.getString("result"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                    );
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

    @FXML
    private void onViewStatistics() {
        Task<String> statsTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                StringBuilder statsResult = new StringBuilder();
                // Soglie definite (stesse usate nel MonitoringController)
                final double ALCOHOL_THRESHOLD = 0.5;
                final double THC_THRESHOLD = 15.0;
                final double COCAINE_THRESHOLD = 10.0;
                final double MDMA_THRESHOLD = 50.0;
                
                int totalUsers = 0;
                int posAlcohol = 0;
                int posThc = 0;
                int posCocaine = 0;
                int posMdma = 0;
                
                try (Connection conn = dbManager.getConnection()) {
                    // Query per contare tutti gli utenti registrati nella tabella drivers
                    String countDriversSql = "SELECT COUNT(*) AS total_users FROM drivers";
                    try (PreparedStatement ps = conn.prepareStatement(countDriversSql);
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            totalUsers = rs.getInt("total_users");
                        }
                    }

                    /* Query che aggrega, dalla tabella readings, i test per ciascun driver.
                       In questo modo, per ogni driver registrato con almeno un test, 
                       si calcola il massimo valore rilevato per ogni sostanza. */
                    String aggregateSql = """
                        SELECT 
                               SUM(CASE WHEN alcohol_level > ? THEN 1 ELSE 0 END) as pos_alcohol,
                               SUM(CASE WHEN thc_level > ? THEN 1 ELSE 0 END) as pos_thc,
                               SUM(CASE WHEN cocaine_level > ? THEN 1 ELSE 0 END) as pos_cocaine,
                               SUM(CASE WHEN mdma_level > ? THEN 1 ELSE 0 END) as pos_mdma
                        FROM (
                            SELECT driver_id,
                                   MAX(alcohol_level) as alcohol_level,
                                   MAX(thc_level) as thc_level,
                                   MAX(cocaine_level) as cocaine_level,
                                   MAX(mdma_level) as mdma_level
                            FROM readings
                            GROUP BY driver_id
                        ) sub
                        """;

                    try (PreparedStatement ps = conn.prepareStatement(aggregateSql)) {
                        ps.setDouble(1, ALCOHOL_THRESHOLD);
                        ps.setDouble(2, THC_THRESHOLD);
                        ps.setDouble(3, COCAINE_THRESHOLD);
                        ps.setDouble(4, MDMA_THRESHOLD);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                posAlcohol = rs.getInt("pos_alcohol");
                                posThc = rs.getInt("pos_thc");
                                posCocaine = rs.getInt("pos_cocaine");
                                posMdma = rs.getInt("pos_mdma");
                            }
                        }
                    }
                }
                
                // Calcola le percentuali sul totale degli utenti registrati (presi dalla tabella drivers)
                double percentAlcohol = totalUsers > 0 ? ((double) posAlcohol / totalUsers * 100) : 0;
                double percentThc = totalUsers > 0 ? ((double) posThc / totalUsers * 100) : 0;
                double percentCocaine = totalUsers > 0 ? ((double) posCocaine / totalUsers * 100) : 0;
                double percentMdma = totalUsers > 0 ? ((double) posMdma / totalUsers * 100) : 0;

                statsResult.append("Numero utenti registrati: ").append(totalUsers).append("\n")
                           .append(String.format("Positivi all'Alcol: %.2f%%\n", percentAlcohol))
                           .append(String.format("Positivi al THC: %.2f%%\n", percentThc))
                           .append(String.format("Positivi alla Cocaina: %.2f%%\n", percentCocaine))
                           .append(String.format("Positivi all'MDMA: %.2f%%", percentMdma));

                return statsResult.toString();
            }
        };

        statsTask.setOnSucceeded(event -> {
            String stats = statsTask.getValue();
            Platform.runLater(() -> statsLabel.setText(stats));
        });

        statsTask.setOnFailed(event -> {
            Throwable error = statsTask.getException();
            error.printStackTrace();
        });

        new Thread(statsTask).start();
    }

}
