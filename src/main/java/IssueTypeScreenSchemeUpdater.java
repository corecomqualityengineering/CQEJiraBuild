import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IssueTypeScreenSchemeUpdater {

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
        // Replace with the actual issue screen scheme ID
        String issueTypeScreenSchemeId = "10009";

        // Update only the "name" field
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("name", "Software Development Issue Type Screen Scheme");
        payload.put("description", "");

        // This code sample uses the 'Unirest' library:
        // http://unirest.io/java.html
        HttpResponse<kong.unirest.JsonNode> response = Unirest.put(jiraBaseUrl + "/issuetypescreenscheme/" + issueTypeScreenSchemeId)
                .basicAuth(username, apiToken)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(payload.toString())
                .asJson();

        System.out.println(response.getBody());
    }
}
