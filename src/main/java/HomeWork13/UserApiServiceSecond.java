package HomeWork13;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserApiServiceSecond {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserApiServiceSecond() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<Optional<JsonNode>> createUser(String userJson) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 201) {
                        try {
                            return Optional.of(objectMapper.readTree(response.body()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return Optional.empty();
                });
    }

    public CompletableFuture<Optional<JsonNode>> updateUser(int userId, String userJson) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(userJson))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return Optional.of(objectMapper.readTree(response.body()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return Optional.empty();
                });
    }

    public CompletableFuture<Boolean> deleteUser(int userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .DELETE()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() >= 200 && response.statusCode() < 300);
    }

    public CompletableFuture<Optional<JsonNode>> getAllUsers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return Optional.of(objectMapper.readTree(response.body()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return Optional.empty();
                });
    }

    public CompletableFuture<Optional<JsonNode>> getUserById(int userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return Optional.of(objectMapper.readTree(response.body()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return Optional.empty();
                });
    }

    public CompletableFuture<Optional<JsonNode>> getUserByUsername(String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users?username=" + username))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JsonNode usersNode = objectMapper.readTree(response.body());
                            if (usersNode.isArray() && usersNode.size() > 0) {
                                return Optional.of(usersNode.get(0));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return Optional.empty();
                });
    }

    public CompletableFuture<Void> fetchAndSaveCommentsForLastPost(int userId) {
        return getUserById(userId)
                .thenCompose(userResponse -> getLastPostId(userResponse.get().toString())
                        .thenCompose(lastPostId -> getCommentsForPost(lastPostId)
                                .thenApply(commentsResponse -> {
                                    String fileName = "user-" + userId + "-post-" + lastPostId + "-comments.json";
                                    writeToFile(commentsResponse.body(), fileName);
                                    return null;
                                })
                        )
                );
    }

    public CompletableFuture<Void> fetchAndDisplayOpenTasks(int userId) {
        return getOpenTasksForUser(userId)
                .thenAccept(tasksResponse -> {
                    try {
                        JsonNode tasksNode = objectMapper.readTree(tasksResponse.body());
                        System.out.println("Open tasks for user " + userId + ":");
                        for (JsonNode task : tasksNode) {
                            if (!task.get("completed").asBoolean()) {
                                System.out.println(task);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private CompletableFuture<HttpResponse<String>> getOpenTasksForUser(int userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/" + userId + "/todos"))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private CompletableFuture<Integer> getLastPostId(String userJson) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonNode userNode = objectMapper.readTree(userJson);
                int userId = userNode.get("id").asInt();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/users/" + userId + "/posts"))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode postsNode = objectMapper.readTree(response.body());
                if (postsNode.isArray() && postsNode.size() > 0) {
                    return postsNode.get(postsNode.size() - 1).get("id").asInt();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return -1;
        });
    }

    private CompletableFuture<HttpResponse<String>> getCommentsForPost(int postId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/posts/" + postId + "/comments"))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
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

        userApiService.fetchAndSaveCommentsForLastPost(userId)
                .thenRun(() -> System.out.println("Comments saved to file."))
                .join();

        userApiService.fetchAndDisplayOpenTasks(userId)
                .thenRun(() -> System.out.println("Open tasks displayed."))
                .join();
    }
}
