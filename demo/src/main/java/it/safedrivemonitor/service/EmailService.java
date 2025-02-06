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

    private static String apiKey;
    private static String senderEmail;
    private static String senderName;
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    static {
        // Carica l'API Key e le altre configurazioni dal file `config.properties`
        try {
            Properties props = new Properties();
            var inputStream = EmailService.class.getClassLoader().getResourceAsStream("config.properties");
            props.load(inputStream);
            apiKey = props.getProperty("api.key");
            senderEmail = props.getProperty("sender.email");
            senderName = props.getProperty("sender.name");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file config.properties:");
            e.printStackTrace();
        }
    }

    public void sendEmail(String recipientEmail, String subject, String content) {
        // Verifica che le configurazioni siano state caricate correttamente
        if (apiKey == null || senderEmail == null || senderName == null) {
            System.err.println("Configurazione non valida. Controlla il file config.properties.");
            return;
        }

        // Crea il payload JSON
        EmailPayload payload = new EmailPayload();
        payload.setSender(new Sender(senderEmail, senderName));
        payload.addRecipient(new Recipient(recipientEmail));
        payload.setSubject(subject);
        payload.setHtmlContent(content);

        // Riferimento alle proprietà per evitare warning di non utilizzo
        System.out.printf("Preparing to send email from: %s <%s>, to: %s, subject: %s, content: %s%n",
                payload.getSender().getName(),
                payload.getSender().getEmail(),
                payload.getTo()[0].email,
                payload.getSubject(),
                payload.getHtmlContent());

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload);

        // Effettua la chiamata API
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("api-key", apiKey);
            post.setEntity(new StringEntity(jsonPayload));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                System.out.println("Response Body: " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper classes per il payload JSON
    private static class EmailPayload {
        private Sender sender;
        private Recipient[] to;
        private String subject;
        private String htmlContent;

        public void setSender(Sender sender) {
            this.sender = sender;
        }

        public void addRecipient(Recipient recipient) {
            this.to = new Recipient[] { recipient };
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
        }

        public Sender getSender() {
            return sender;
        }

        public Recipient[] getTo() {
            return to;
        }

        public String getSubject() {
            return subject;
        }

        public String getHtmlContent() {
            return htmlContent;
        }
    }

    private static class Sender {
        private final String email;
        private final String name;

        public Sender(String email, String name) {
            this.email = email;
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }

    private static class Recipient {
        private final String email;

        public Recipient(String email) {
            this.email = email;
        }
    }
}
