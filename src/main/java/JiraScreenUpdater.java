import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class JiraScreenUpdater {
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

        // Read data from JSON file
        String filePath = "src/main/data/screens.json";
        List<ScreenData> screenDataList = readScreenDataListFromFile(filePath);

        if (screenDataList != null && !screenDataList.isEmpty()) {
            for (ScreenData screenData : screenDataList) {
                updateScreen(username, apiToken, screenData);
            }
        } else {
            System.out.println("Failed to read data from screens.json");
        }
    }

    public static void updateScreen(String email, String apiToken, ScreenData screenData) {
        String payload = "{\"name\":\"" + screenData.getScreenName() + "\",\"description\":\"" + screenData.getDescription() + "\"}";

        HttpResponse<JsonNode> response = Unirest.put("https://psinghjiracloud.atlassian.net/rest/api/3/screens/{screenId}")
                .basicAuth(email, apiToken)
                .routeParam("screenId", String.valueOf(screenData.getScreenId()))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(payload)
                .asJson();

        // Check the response
        if (response.isSuccess()) {
            System.out.println("Screen updated successfully. Screen ID: " + screenData.getScreenId());
        } else {
            System.out.println("Failed to update screen. Status code: " + response.getStatus());
            System.out.println(response.getBody());
        }
    }

    private static List<ScreenData> readScreenDataListFromFile(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return Arrays.asList(objectMapper.readValue(new File(filePath), ScreenData[].class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class ScreenData {
        private int screenId;
        private String screenName;
        private String description;

        // getters and setters

        public int getScreenId() {
            return screenId;
        }

        public void setScreenId(int screenId) {
            this.screenId = screenId;
        }

        public String getScreenName() {
            return screenName;
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
