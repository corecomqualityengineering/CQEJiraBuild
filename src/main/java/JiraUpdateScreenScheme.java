import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JiraUpdateScreenScheme {

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
            // Read values from JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            File configFile = new File("src/main/data/screenSchemes.json");

            JsonNode screenSchemesNode = objectMapper.readTree(configFile);
            ArrayNode screenSchemesArray = (ArrayNode) screenSchemesNode;

            // Process each entry in the array
            for (JsonNode schemeNode : screenSchemesArray) {
                String name = schemeNode.get("name").asText();
                String screenSchemeId = schemeNode.get("screenSchemeId").asText();
                String defaultScreenId = schemeNode.get("defaultScreenId").asText();
                String description = schemeNode.has("description") ? schemeNode.get("description").asText() : "";

                // Check if createScreenId exists in the JSON and has a value
                String createScreenId = schemeNode.has("createScreenId") && !schemeNode.get("createScreenId").isNull()
                        ? schemeNode.get("createScreenId").asText()
                        : null;

                // The payload definition using the Jackson library
                ObjectNode payload = objectMapper.createObjectNode();
                payload.put("name", name);
                payload.put("description", description);
                ObjectNode screens = payload.putObject("screens");
                screens.put("default", defaultScreenId);

                // Add createScreenId to the payload if it has a value
                if (createScreenId != null && !createScreenId.isEmpty()) {
                    screens.put("create", createScreenId);
                }

                // Replace {screenSchemeId} with the actual ID in the endpoint URL
                String endpoint = jiraBaseUrl + "/screenscheme/{screenSchemeId}";
                String actualEndpoint = endpoint.replace("{screenSchemeId}", screenSchemeId);

                // This code sample uses the 'Unirest' library: http://unirest.io/java.html
                HttpResponse<String> response = Unirest.put(actualEndpoint)
                        .basicAuth(username, apiToken)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .body(payload.toString())
                        .asString();

                System.out.println(response.getBody());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
