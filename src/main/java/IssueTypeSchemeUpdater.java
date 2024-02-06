import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class IssueTypeSchemeUpdater {
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
        // Replace with your Jira domain, email, and API token

        // Replace with the actual issue type scheme ID
        String issueTypeSchemeId = "10213";

        // Fetch the existing scheme
        // Fetch the existing scheme
        HttpResponse<kong.unirest.JsonNode> fetchResponse = Unirest.get(jiraBaseUrl + "/issuetypescheme/" + issueTypeSchemeId)
                .basicAuth(username, apiToken)
                .header("Accept", "application/json")
                .asJson();


        // Handle the fetch response
        if (fetchResponse.isSuccess()) {
            // Convert the received JSON to Jackson's JsonNode
            kong.unirest.JsonNode existingScheme = fetchResponse.getBody();

            // Debugging statements
            System.out.println("Existing Scheme JSON: " + existingScheme.toString());

            // Modify the description in the existing scheme
            existingScheme.getObject().put("defaultIssueTypeId", "10011");
            existingScheme.getObject().put("description", "Software Development Issue Type Scheme");
            existingScheme.getObject().put("name", "Software Development Issue Type Scheme");

            // Debugging statements
            System.out.println("Update Payload JSON: " + existingScheme.toString());

            // Update the issue type scheme using PUT
            HttpResponse<JsonNode> updateResponse = Unirest.put(jiraBaseUrl + "/issuetypescheme/" + issueTypeSchemeId)
                    .basicAuth(username, apiToken)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(existingScheme)
                    .asJson();

            // Debugging statement for update response
            System.out.println("Update Response: " + updateResponse.getBody());

            // Handle the update response
            if (updateResponse.isSuccess()) {
                System.out.println("Issue type scheme description updated successfully!");
            } else {
                System.err.println("Failed to update issue type scheme description. Response Code: " + updateResponse.getStatus());
                System.err.println("Response Body: " + updateResponse.getBody());
            }
        } else {
            System.err.println("Failed to fetch existing issue type scheme. Response Code: " + fetchResponse.getStatus());
            System.err.println("Response Body: " + fetchResponse.getBody());
        }
    }
}
