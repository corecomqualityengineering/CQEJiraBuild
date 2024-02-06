import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FieldConfigurationRetriever {

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
        getFieldConfigurations();
    }

    public static void getFieldConfigurations() {
        HttpResponse<JsonNode> response = Unirest.get(jiraBaseUrl)
                .basicAuth(username, apiToken)
                .header("Accept", "application/json")
                .asJson();

        if (response.isSuccess()) {
            System.out.println(response.getBody());
            // Parse and process the JSON response as needed
        } else {
            System.err.println("Failed to retrieve field configurations. Status code: " + response.getStatus());
            System.err.println("Error message: " + response.getStatusText());
        }
    }
}
