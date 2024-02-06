import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IssueTypeSchemeFetcher {
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

        // Replace with the actual project key (e.g., SPT3)
        String projectKey = "SPT3";

        // Fetch issue type schemes
        HttpResponse<JsonNode> response = Unirest.get(jiraBaseUrl + "/issuetypescheme")
                .basicAuth(username, apiToken)
                .header("Accept", "application/json")
                .asJson();

        // Handle the response
        if (response.isSuccess()) {
            System.out.println("Issue type schemes:");
            System.out.println(response.getBody());
        } else {
            System.err.println("Failed to fetch issue type schemes. Response Code: " + response.getStatus());
            System.err.println("Response Body: " + response.getBody());
        }
    }
}
