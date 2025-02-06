package it.safedrivemonitor.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Properties;

public class SmsService {

    private static String smsApiKey;

    static {
        try {
            Properties props = new Properties();
            props.load(SmsService.class.getClassLoader().getResourceAsStream("config.properties"));
            smsApiKey = props.getProperty("api.key.sms");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della configurazione degli SMS:");
            e.printStackTrace();
        }
    }

    public void sendSms(String recipientPhone, String message) {
        if (smsApiKey == null || smsApiKey.isEmpty()) {
            System.err.println("Chiave API per gli SMS non trovata. Controlla il file di configurazione.");
            return;
        }

        String smsApiUrl = "https://api.brevo.com/v3/transactionalSMS/sms";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(smsApiUrl);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("api-key", smsApiKey);

            String payload = String.format(
                    "{ \"sender\": \"SafeDrive\", \"recipient\": \"%s\", \"content\": \"%s\" }",
                    recipientPhone, message);

            post.setEntity(new StringEntity(payload));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                System.out.println("Response Body: " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
