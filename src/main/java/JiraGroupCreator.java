import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Properties;

public class JiraGroupCreator {

    private static String jiraBaseUrl;
    private static String username;
    private static String apiToken;

    static {
        // Load configuration from the properties file
        try (InputStream input = CustomFieldUpdater.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            jiraBaseUrl = prop.getProperty("jiraBaseUrl");
            username = prop.getProperty("username");
            apiToken = prop.getProperty("apiToken");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            createGroup(client, "Ceridian-Team");
            createGroup(client, "Client-Team");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createGroup(CloseableHttpClient client, String groupName) {
        try {
            HttpPost postRequest = new HttpPost(jiraBaseUrl + "/group");
            postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            postRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));

            String jsonPayload = "{\"name\":\"" + groupName + "\"}";
            postRequest.setEntity(new StringEntity(jsonPayload));

            HttpResponse response = client.execute(postRequest);

            if (response.getStatusLine().getStatusCode() == 201) {
                System.out.println("Group created successfully: " + groupName);
            } else {
                System.out.println("Error creating group. Status code: " + response.getStatusLine().getStatusCode());
                printResponseBody(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printResponseBody(HttpResponse response) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder responseContent = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        System.out.println("Response body: " + responseContent.toString());
    }
}
