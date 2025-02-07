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

    // Componenti della UI definite nel file FXML
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

    // Servizi per invio email e sms
    private final EmailService emailService = new EmailService();
    private final SmsService smsService = new SmsService();

    // Metodo di inizializzazione, viene chiamato all'avvio del controller
    @FXML
    private void initialize() {
        // Imposta il ToggleGroup per permettere la selezione esclusiva dei RadioButton
        ToggleGroup toggleGroup = new ToggleGroup();
        emailOption.setToggleGroup(toggleGroup);
        smsOption.setToggleGroup(toggleGroup);
    }

    // Metodo invocato alla pressione del pulsante di invio per resettare l'ID del conducente
    @FXML
    private void onSendReset() {
        // Ottiene i valori inseriti dall'utente e rimuove eventuali spazi
        String name = driverNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        // Verifica che il nome e almeno un contatto siano stati inseriti
        if (name.isEmpty() || (email.isEmpty() && phone.isEmpty())) {
            System.out.println("Inserisci Nome e Cognome e almeno un contatto (Email o Telefono)!");
            return;
        }

        // Genera un nuovo ID casuale per il conducente
        String newId = generateNewId();

        // Resetta l'ID del conducente sul database e procede con l'invio tramite email o sms
        if (resetDriverId(name, email, phone, newId)) {
            // Se l'opzione email è selezionata e il campo email non è vuoto
            if (emailOption.isSelected() && !email.isEmpty()) {
                System.out.println("Invio ID via Email...");
                sendEmail(email, newId);
            } 
            // Se l'opzione sms è selezionata e il campo telefono non è vuoto
            else if (smsOption.isSelected() && !phone.isEmpty()) {
                System.out.println("Invio ID via SMS...");
                sendSms(phone, newId);
            } 
            // Se nessuna opzione valida è stata scelta o il relativo campo è vuoto
            else {
                System.out.println("Seleziona un'opzione valida e compila il campo corrispondente.");
            }
        } else {
            System.out.println("Nome o contatti non trovati. Riprova.");
        }
    }

    // Metodo che esegue l'update dell'ID nel database per il conducente specificato
    private boolean resetDriverId(String name, String email, String newId, String phone) {
        String sql = "UPDATE drivers SET driver_id = ? WHERE driver_name = ? AND (email = ? OR phone = ?)";
        try (Connection conn = new DatabaseManager().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Imposta i parametri della query
            pstmt.setString(1, newId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            // Esegue l'update e ritorna true se almeno una riga è stata aggiornata
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Stampa eventuali errori SQL
            e.printStackTrace();
            return false;
        }
    }

    // Metodo per l'invio di SMS contenente il nuovo ID
    private void sendSms(String phoneNumber, String newId) {
        // Costruisce il messaggio da inviare
        String message = "SafeDriveMonitor: Il tuo nuovo ID conducente è " + newId + ". Conservalo con cura.";
        // Utilizza il servizio SMS per inviare il messaggio
        smsService.sendSms(phoneNumber, message);
    }

    // Metodo per l'invio di email contenente il nuovo ID
    public void sendEmail(String recipient, String newId) {
        // Definisce oggetto e contenuto dell'email (HTML)
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

        // Invia l'email utilizzando il servizio dedicato
        emailService.sendEmail(recipient, subject, content);
    }

    // Metodo per generare un nuovo ID casuale a 6 cifre
    private String generateNewId() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // Metodo per tornare indietro e caricare nuovamente la schermata di login
    @FXML
    private void onBack() {
        try {
            // Ottiene il riferimento alla finestra corrente tramite uno dei componenti della UI
            Stage stage = (Stage) emailField.getScene().getWindow();
            // Carica il layout della schermata precedente (login del conducente)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/conductor_login.fxml"));
            Parent root = loader.load();
            // Imposta la nuova scena con le dimensioni della finestra corrente
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setTitle("SafeDriveMonitor-Home");
            // Mostra la schermata
            stage.show();
        } catch (IOException e) {
            // Gestisce eventuali errori di caricamento del file FXML
            e.printStackTrace();
        }
    }
}
