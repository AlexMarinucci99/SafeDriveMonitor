package it.safedrivemonitor.controller;

import it.safedrivemonitor.model.DatabaseManager;
import it.safedrivemonitor.service.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import it.safedrivemonitor.service.SmsService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class ResetIdController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField driverNameField;
    @FXML
    private RadioButton emailOption;
    @FXML
    private RadioButton smsOption;

    private final EmailService emailService = new EmailService();
    private final SmsService smsService = new SmsService();

    @FXML
    private void initialize() {
        // Aggiunge il toggle group per i radio button
        ToggleGroup toggleGroup = new ToggleGroup();
        emailOption.setToggleGroup(toggleGroup);
        smsOption.setToggleGroup(toggleGroup);
    }

    @FXML
    private void onSendReset() {
        String name = driverNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || (email.isEmpty() && phone.isEmpty())) {
            System.out.println("Inserisci Nome e Cognome e almeno un contatto (Email o Telefono)!");
            return;
        }

        String newId = generateNewId();

        if (resetDriverId(name, email, phone, newId)) {
            if (emailOption.isSelected() && !email.isEmpty()) {
                System.out.println("Invio ID via Email...");
                sendEmail(email, newId);
            } else if (smsOption.isSelected() && !phone.isEmpty()) {
                System.out.println("Invio ID via SMS...");
                sendSms(phone, newId);
            } else {
                System.out.println("Seleziona un'opzione valida e compila il campo corrispondente.");
            }
        } else {
            System.out.println("Nome o contatti non trovati. Riprova.");
        }
    }

    private boolean resetDriverId(String name, String email, String newId, String phone) {
        String sql = "UPDATE drivers SET driver_id = ? WHERE driver_name = ? AND (email = ? OR phone = ?)";
        try (Connection conn = new DatabaseManager().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendSms(String phoneNumber, String newId) {
        String message = "SafeDriveMonitor: Il tuo nuovo ID conducente è " + newId + ". Conservalo con cura.";
        smsService.sendSms(phoneNumber, message);// The method sendSMS(String, String) is undefined for the type
                                                 // SmsServiceJava(67108964)
    }

    public void sendEmail(String recipient, String newId) {
        String subject = "Reset ID Conducente - SafeDriveMonitor";
        String content = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <style>"
                + "        body {"
                + "            font-family: 'Arial', sans-serif;"
                + "            margin: 0;"
                + "            padding: 0;"
                + "            background-color: #f9fafb;"
                + "            color: #333;"
                + "        }"
                + "        .email-container {"
                + "            max-width: 600px;"
                + "            margin: 20px auto;"
                + "            background-color: #ffffff;"
                + "            border: 1px solid #e0e0e0;"
                + "            border-radius: 10px;"
                + "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"
                + "            overflow: hidden;"
                + "        }"
                + "        .header {"
                + "            background-color: #007BFF;"
                + "            color: #ffffff;"
                + "            text-align: center;"
                + "            padding: 20px;"
                + "            font-size: 26px;"
                + "            font-weight: bold;"
                + "        }"
                + "        .header img {"
                + "            max-width: 150px;"
                + "            margin-bottom: 10px;"
                + "        }"
                + "        .content {"
                + "            padding: 20px;"
                + "            line-height: 1.8;"
                + "            font-size: 16px;"
                + "        }"
                + "        .highlight {"
                + "            color: #d32f2f;"
                + "            font-weight: bold;"
                + "            font-size: 18px;"
                + "        }"
                + "        .footer {"
                + "            background-color: #f9fafb;"
                + "            text-align: center;"
                + "            padding: 15px;"
                + "            font-size: 14px;"
                + "            color: #666;"
                + "        }"
                + "        .footer a {"
                + "            color: #007BFF;"
                + "            text-decoration: none;"
                + "        }"
                + "        .footer a:hover {"
                + "            text-decoration: underline;"
                + "        }"
                + "        .cta-button {"
                + "            display: inline-block;"
                + "            margin: 20px auto;"
                + "            padding: 10px 20px;"
                + "            background-color: #007BFF;"
                + "            color: #ffffff;"
                + "            text-decoration: none;"
                + "            border-radius: 5px;"
                + "            font-size: 16px;"
                + "        }"
                + "        .cta-button:hover {"
                + "            background-color: #0056b3;"
                + "        }"
                + "        .disclaimer {"
                + "            font-size: 12px;"
                + "            color: #999;"
                + "            margin-top: 10px;"
                + "        }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class='email-container'>"
                + "        <div class='header'>"
                // + " <img src=\"https://imgur.com/a/RqE8uw1\" alt=\"SafeDriveMonitor Logo\">"
                + "            <div>SafeDriveMonitor</div>"
                + "        </div>"
                + "        <div class='content'>"
                + "            <h1>Caro utente,</h1>"
                + "            <p>"
                + "                Il tuo ID conducente &egrave; stato resettato con successo. Il tuo nuovo ID &egrave;:"
                + "                <span class='highlight'>" + newId + "</span>."
                + "            </p>"
                + "            <p>Ti consigliamo di conservarlo con cura e non condividerlo con nessuno.</p>"
                + "            <p>"
                + "                Grazie per aver scelto SafeDriveMonitor. Ricorda che la sicurezza &egrave; la nostra priorit&agrave;: "
                + "                evita l'uso di alcol e sostanze se devi guidare."
                + "            </p>"
                + "            <a href='https://safedrivemonitor.com/account' class='cta-button'>Accedi al tuo account</a>"
                + "        </div>"
                + "        <div class='footer'>"
                + "            <p>SafeDriveMonitor &copy; 2024</p>"
                + "            <p>"
                + "                Hai domande? <a href='mailto:supporto@safedrivemonitor.com'>Contatta il supporto</a>"
                + "            </p>"
                + "            <p class='disclaimer'>"
                + "                Questa email &egrave; stata inviata automaticamente. Non rispondere a questo indirizzo.<br>"
                + "                Se non riconosci questa operazione, contatta immediatamente il nostro supporto."
                + "            </p>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>";

        emailService.sendEmail(recipient, subject, content);
    }

    private String generateNewId() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // Genera un ID casuale di 6 cifre
        }

        @FXML
        private void onBack() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setTitle("SafeDriveMonitor-Home");
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }

