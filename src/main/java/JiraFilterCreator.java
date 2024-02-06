import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class JiraFilterCreator {

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
            // Read filter details from JSON file
            List<FilterDetails> filters = readFilterDetailsFromFile("src/main/data/filters.json");

            // Create Jira filters
            createJiraFilters(client, filters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createJiraFilters(CloseableHttpClient client, List<FilterDetails> filters) {
        for (FilterDetails filter : filters) {
            try {
                String jsonPayload = buildJsonPayload(filter);

                HttpPost postRequest = new HttpPost(jiraBaseUrl + "/filter");
                postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                postRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));
                postRequest.setEntity(new StringEntity(jsonPayload));

                HttpResponse response = client.execute(postRequest);

                try {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        System.out.println("Filter created successfully: " + filter.getName());
                    } else {
                        System.out.println("Error creating filter. Status code: " + response.getStatusLine().getStatusCode());
                        printResponseBody(response);
                    }
                } finally {
                    response.getEntity().getContent().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String buildJsonPayload(FilterDetails filterDetails) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.createObjectNode()
                    .put("jql", filterDetails.getJql())
                    .put("name", filterDetails.getName());

            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException("Error building JSON payload", e);
        }
    }

    private static List<FilterDetails> readFilterDetailsFromFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new FileReader(filePath), new TypeReference<List<FilterDetails>>() {});
    }

    private static void printResponseBody(HttpResponse response) throws IOException {
        // Implementation for printing response body
    }

    private static class FilterDetails {
        private String jql;
        private String name;

        public String getJql() {
            return jql;
        }

        public void setJql(String jql) {
            this.jql = jql;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
