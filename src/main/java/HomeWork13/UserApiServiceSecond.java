package HomeWork13;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserApiServiceSecond {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserApiServiceSecond() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public boolean fetchAndSaveCommentsForLastPost(int userId) {
        try {
            HttpResponse<String> userResponse = getUserById(userId);
            if (userResponse.statusCode() != 200) {
                return false;
            }

            int lastPostId = getLastPostId(userResponse.body());
            if (lastPostId == -1) {
                return false;
            }

            HttpResponse<String> commentsResponse = getCommentsForPost(lastPostId);
            if (commentsResponse.statusCode() != 200) {
                return false;
            }

            String fileName = "user-" + userId + "-post-" + lastPostId + "-comments.json";
            writeToFile(commentsResponse.body(), fileName);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean fetchAndDisplayOpenTasks(int userId) {
        try {
            HttpResponse<String> tasksResponse = getOpenTasksForUser(userId);
            if (tasksResponse.statusCode() != 200) {
                return false;
            }

            System.out.println("Open tasks for user " + userId + ":");
            System.out.println(tasksResponse.body());
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private HttpResponse<String> getOpenTasksForUser(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId + "/todos"))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getUserById(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private int getLastPostId(String userJson) {
        try {
            JsonNode userNode = objectMapper.readTree(userJson);
            int userId = userNode.get("id").asInt();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/" + userId + "/posts"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return -1;
            }

            JsonNode postsNode = objectMapper.readTree(response.body());
            if (postsNode.isArray() && postsNode.size() > 0) {
                return postsNode.get(postsNode.size() - 1).get("id").asInt();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private HttpResponse<String> getCommentsForPost(int postId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/posts/" + postId + "/comments"))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void writeToFile(String content, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int userId = 1;

        UserApiServiceSecond userApiService = new UserApiServiceSecond();

        if (userApiService.fetchAndSaveCommentsForLastPost(userId)) {
            System.out.println("Comments saved to file.");
        } else {
            System.out.println("Failed to save comments.");
        }

        if (userApiService.fetchAndDisplayOpenTasks(userId)) {
            System.out.println("Displayed open tasks.");
        } else {
            System.out.println("Failed to display open tasks.");
        }
    }
}
