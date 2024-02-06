import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class JiraDashboardCreator {

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
            List<DashboardDetails> dashboards = readDashboardDetailsFromFile("src/main/data/dashboards.json");

            for (DashboardDetails dashboard : dashboards) {
                createJiraDashboard(client, dashboard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createJiraDashboard(CloseableHttpClient client, DashboardDetails dashboardDetails) throws IOException {
        String jsonPayload = buildJsonPayload(dashboardDetails);

        HttpPost postRequest = new HttpPost(jiraBaseUrl + "/dashboard");
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        postRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));
        postRequest.setEntity(new StringEntity(jsonPayload));

        HttpResponse response = client.execute(postRequest);

        try {
            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println("Dashboard created successfully: " + dashboardDetails.getName());
            } else {
                System.out.println("Error creating dashboard. Status code: " + response.getStatusLine().getStatusCode());
                printResponseBody(response);
            }
        } finally {
            EntityUtils.consume(response.getEntity());
        }
    }

    private static String buildJsonPayload(DashboardDetails dashboardDetails) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(dashboardDetails);
        } catch (IOException e) {
            throw new RuntimeException("Error building JSON payload", e);
        }
    }

    private static List<DashboardDetails> readDashboardDetailsFromFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new FileReader(filePath), new com.fasterxml.jackson.core.type.TypeReference<List<DashboardDetails>>() {});
    }

    private static void printResponseBody(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            System.out.println(EntityUtils.toString(entity));
        }
    }

    private static class DashboardDetails {
        private String name;

        public String getName() {
            return name;
        }
    }
}
