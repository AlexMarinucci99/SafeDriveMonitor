package it.safedrivemonitor.service;

import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Properties;

public class EmailService {

    // Variabili per le configurazioni, come API key e dati del mittente.
    private static String apiKey;
    private static String senderEmail;
    private static String senderName;
    
    // URL dell'API per l'invio delle email tramite Brevo
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    static {
        // Blocco statico per caricare le configurazioni dal file config.properties
        try {
            // Creazione di un oggetto Properties per leggere il file di configurazione
            Properties props = new Properties();
            // Ottieni lo stream del file config.properties presente nel classpath
            var inputStream = EmailService.class.getClassLoader().getResourceAsStream("config.properties");
            
            // Carica le proprietà dal file
            props.load(inputStream);
            // Imposta le variabili di configurazione
            apiKey = props.getProperty("api.key");
            senderEmail = props.getProperty("sender.email");
            senderName = props.getProperty("sender.name");
        } catch (IOException e) {
            // In caso di errore, stampiamo un messaggio e la traccia dello stack
            System.err.println("Errore nel caricamento del file config.properties:");
            e.printStackTrace();
        }
    }

    // Metodo per inviare una email
    public void sendEmail(String recipientEmail, String subject, String content) {
        // Verifica che la configurazione sia stata caricata correttamente
        if (apiKey == null || senderEmail == null || senderName == null) {
            System.err.println("Configurazione non valida. Controlla il file config.properties.");
            return;
        }

        // Crea l'oggetto payload che verrà convertito in JSON per l'API
        EmailPayload payload = new EmailPayload();
        payload.setSender(new Sender(senderEmail, senderName));
        payload.addRecipient(new Recipient(recipientEmail));
        payload.setSubject(subject);
        payload.setHtmlContent(content);

        // Log per mostrare i dettagli della email in uscita
        System.out.printf("Preparing to send email from: %s <%s>, to: %s, subject: %s, content: %s%n",
                payload.getSender().getName(),
                payload.getSender().getEmail(),
                payload.getTo()[0].email,
                payload.getSubject(),
                payload.getHtmlContent());

        // Converti l'oggetto payload in una stringa JSON usando Gson
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload);

        // Esegue la chiamata HTTP per inviare la email
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Crea una richiesta POST verso l'API
            HttpPost post = new HttpPost(API_URL);
            // Imposta gli header necessari, compreso il contenuto in formato JSON e l'API key
            post.setHeader("Content-Type", "application/json");
            post.setHeader("api-key", apiKey);
            // Imposta il payload JSON come entità della richiesta
            post.setEntity(new StringEntity(jsonPayload));

            // Esegue la richiesta e ottiene la risposta
            try (CloseableHttpResponse response = client.execute(post)) {
                // Converte la risposta in una stringa per loggare il risultato
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                System.out.println("Response Body: " + responseBody);
            }
        } catch (Exception e) {
            // Se c'è un'eccezione, stampane la traccia
            e.printStackTrace();
        }
    }

    // Classe helper per rappresentare il payload JSON dell'email
    private static class EmailPayload {
        private Sender sender;
        private Recipient[] to;
        private String subject;
        private String htmlContent;

        // Imposta il mittente dell'email
        public void setSender(Sender sender) {
            this.sender = sender;
        }

        // Aggiunge un destinatario all'email
        public void addRecipient(Recipient recipient) {
            this.to = new Recipient[] { recipient };
        }

        // Imposta l'oggetto (subject) dell'email
        public void setSubject(String subject) {
            this.subject = subject;
        }

        // Imposta il contenuto HTML dell'email
        public void setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
        }

        // Metodo getter per il mittente
        public Sender getSender() {
            return sender;
        }

        // Metodo getter per i destinatari
        public Recipient[] getTo() {
            return to;
        }

        // Metodo getter per l'oggetto
        public String getSubject() {
            return subject;
        }

        // Metodo getter per il contenuto HTML
        public String getHtmlContent() {
            return htmlContent;
        }
    }

    // Classe per rappresentare il mittente della email
    private static class Sender {
        private final String email;
        private final String name;

        public Sender(String email, String name) {
            this.email = email;
            this.name = name;
        }

        // Restituisce l'email del mittente
        public String getEmail() {
            return email;
        }

        // Restituisce il nome del mittente
        public String getName() {
            return name;
        }
    }

    // Classe per rappresentare un destinatario (recipient) della email
    private static class Recipient {
        private final String email;

        public Recipient(String email) {
            this.email = email;
        }
    }
}
