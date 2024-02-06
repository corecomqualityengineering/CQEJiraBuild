import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class JiraCustomFieldConfiguration {

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
            // Read configurations from JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode configArray = objectMapper.readTree(Files.newBufferedReader(Paths.get("src/main/data/mandatoryFields.json")));

            // Process each configuration
            for (JsonNode configNode : configArray) {
                String fieldConfigId = configNode.get("fieldConfigId").asText();
                String customFieldId = configNode.get("customFieldId").asText();
                String description = configNode.get("description").asText();

                updateFieldConfiguration(jiraBaseUrl, username, apiToken, fieldConfigId, customFieldId, description);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateFieldConfiguration(String baseUrl, String email, String apiToken, String fieldConfigId, String customFieldId, String description) {
        try {
            JsonNodeFactory jnf = JsonNodeFactory.instance;
            ObjectNode payload = jnf.objectNode();
            ArrayNode fieldConfigurationItems = payload.putArray("fieldConfigurationItems");

            ObjectNode fieldConfigurationItem = fieldConfigurationItems.addObject();
            fieldConfigurationItem.put("id", customFieldId);
            fieldConfigurationItem.put("description", description);
            fieldConfigurationItem.put("isRequired", true);

            // Construct the URL
            URL url = new URL(baseUrl + "/rest/api/3/fieldconfiguration/" + fieldConfigId + "/fields");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method and headers
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + getBase64Credentials(email, apiToken));

            // Enable input/output streams for sending the request and reading the response
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(payload.toString().getBytes());
            os.flush();

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Assuming you want to print the entire JSON response
            System.out.println("Field: " + description);
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response: " + response.toString());

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getBase64Credentials(String username, String token) {
        String credentials = username + ":" + token;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
