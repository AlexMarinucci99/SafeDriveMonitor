package it.safedrivemonitor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.IOException;
import it.safedrivemonitor.model.DatabaseManager;

public class LoginController {

    @FXML
    private Button onConductorAccess;
    @FXML
    private Button onAdminAccess;

    @FXML
    private void onConductorAccess() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_login.fxml"));

            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) onConductorAccess.getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreen(true);
            stage.setTitle("Login Conducente");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAdminAccess() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_login.fxml"));

            loader.setControllerFactory(type -> {
                if (type == AdminLoginController.class) {
                    DatabaseManager db = new DatabaseManager();
                    db.initDB();
                    return new AdminLoginController(db);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Scene scene = new Scene(loader.load(), 1000, 600);
            Stage stage = (Stage) onAdminAccess.getScene().getWindow(); // Ottieni lo stage corrente
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setTitle("Login Admin");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
