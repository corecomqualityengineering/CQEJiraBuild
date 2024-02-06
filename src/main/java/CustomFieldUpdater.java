import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class CustomFieldUpdater {

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
        updateCustomFieldsFromJson("src/main/data/customFields.json");
    }

    public static void updateCustomFieldsFromJson(String jsonFilePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

            // Parse JSON array
            JSONArray fieldsArray = new JSONArray(jsonContent);

            // Update custom fields
            for (int i = 0; i < fieldsArray.length(); i++) {
                JSONObject field = fieldsArray.getJSONObject(i);
                updateCustomFieldName(
                        field.getString("customFieldId"),
                        field.getString("name"),
                        field.getString("description")
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateCustomFieldName(String fieldId, String newName, String newDescription) {
        // The payload definition using the Kong Unirest library
        JSONObject payload = new JSONObject();
        payload.put("description", newDescription);
        payload.put("name", newName);

        // Update custom field name using Kong Unirest
        HttpResponse<JsonNode> response = Unirest.put(jiraBaseUrl + "/field/" + fieldId)
                .basicAuth(username, apiToken)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(payload)
                .asJson();

        System.out.println(newName);
    }
}
