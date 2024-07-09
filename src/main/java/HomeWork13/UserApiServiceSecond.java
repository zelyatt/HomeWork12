package HomeWork13;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public JsonNode createUser(ObjectNode user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(user.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            return null;
        }

        return objectMapper.readTree(response.body());
    }

    public JsonNode updateUser(int userId, ObjectNode user) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(user.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        return objectMapper.readTree(response.body());
    }

    public boolean deleteUser(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() / 100 == 2;
    }

    public JsonNode getAllUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        return objectMapper.readTree(response.body());
    }

    public JsonNode getUserByUsername(String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users?username=" + username))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        return objectMapper.readTree(response.body());
    }

    public JsonNode getUserById(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        return objectMapper.readTree(response.body());
    }

    public JsonNode getPostsByUser(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId + "/posts"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        return objectMapper.readTree(response.body());
    }

    public JsonNode getCommentsByPostId(int postId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/posts/" + postId + "/comments"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        return objectMapper.readTree(response.body());
    }

    public JsonNode getOpenTasksForUser(int userId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId + "/todos"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }

        JsonNode tasksNode = objectMapper.readTree(response.body());
        ArrayNode openTasks = objectMapper.createArrayNode();

        tasksNode.forEach(task -> {
            if (!task.get("completed").asBoolean()) {
                openTasks.add(task);
            }
        });

        return openTasks;
    }

    public boolean fetchAndSaveCommentsForLastPost(int userId) {
        try {
            JsonNode userNode = getUserById(userId);
            if (userNode == null) {
                return false;
            }

            JsonNode postsNode = getPostsByUser(userId);
            if (postsNode == null || !postsNode.isArray() || postsNode.size() == 0) {
                return false;
            }

            int lastPostId = postsNode.get(postsNode.size() - 1).get("id").asInt();
            JsonNode commentsNode = getCommentsByPostId(lastPostId);
            if (commentsNode == null) {
                return false;
            }

            String fileName = "user-" + userId + "-post-" + lastPostId + "-comments.json";
            writeToFile(commentsNode.toString(), fileName);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean fetchAndDisplayOpenTasks(int userId) {
        try {
            JsonNode tasksNode = getOpenTasksForUser(userId);
            if (tasksNode == null) {
                return false;
            }

            System.out.println("Open tasks for user " + userId + ":");
            System.out.println(tasksNode.toPrettyString());
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
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

        try {
            JsonNode userNode = userApiService.getUserById(userId);
            if (userNode != null) {
                System.out.println("User details:");
                System.out.println(userNode.toPrettyString());
            } else {
                System.out.println("Failed to fetch user details.");
            }

            JsonNode postsNode = userApiService.getPostsByUser(userId);
            if (postsNode != null) {
                System.out.println("User posts:");
                System.out.println(postsNode.toPrettyString());
            } else {
                System.out.println("Failed to fetch user posts.");
            }

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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
