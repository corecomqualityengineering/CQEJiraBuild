import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JiraFieldConfigurationUpdater {

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
        try {
            String fieldConfigurationId = "10007";


            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("name", "Default Field Layout");
            payload.put("description", "");

            // Print the full endpoint
            String fullEndpoint = jiraBaseUrl + "/fieldconfiguration/" + fieldConfigurationId;
            System.out.println("Full Endpoint: " + fullEndpoint);

            // Print the request payload as a JSON string
            String requestPayloadString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            System.out.println("Request Payload: " + requestPayloadString);

            HttpResponse<ObjectNode> response = Unirest.put(fullEndpoint)
                    .basicAuth(username, apiToken)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .asObject(ObjectNode.class);

            // Handle the response
            int statusCode = response.getStatus();
            System.out.println("Response Status: " + statusCode);

            if (statusCode != 200) {
                ObjectNode responseBody = response.getBody();
                if (responseBody != null) {
                    String responseBodyString = responseBody.textValue() != null ? responseBody.toString() : "null";
                    System.err.println("Error updating field configuration. Response body: " + responseBodyString);
                } else {
                    System.err.println("Error updating field configuration. Response body is null.");
                }
            } else {
                System.out.println("Field configuration updated successfully");
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Additional exception handling to capture more details
            e.printStackTrace();
            System.err.println("Exception details: " + e.getMessage());
            System.err.println("Exception cause: " + e.getCause());

            // Check if it's a JsonMappingException
            if (e instanceof JsonMappingException) {
                JsonMappingException jsonMappingException = (JsonMappingException) e;
                System.err.println("JsonMappingException details: " + jsonMappingException.getMessage());
                System.err.println("JsonMappingException location: " + jsonMappingException.getLocation());
                System.err.println("JsonMappingException path: " + jsonMappingException.getPath());
            }

            // Additional logging or handling as needed
        }
    }
}
