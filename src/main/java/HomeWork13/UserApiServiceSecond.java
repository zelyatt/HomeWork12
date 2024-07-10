package HomeWork13;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class UserApiServiceSecond {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    public static void main(String[] args) throws IOException {

        System.out.println("Get all users: " + getAllUsers());
        System.out.println("_________" );
        System.out.println("Get users by id: " +getUserById(1));
        System.out.println("_________" );
        System.out.println("Ger user by name: " + getUserByUsername("Bret"));
        System.out.println("_________" );
        System.out.println(createUser(new JSONObject("{\"name\": \"New User\", \"username\": \"newuser\", \"email\": \"newuser@example.com\"}")));
        System.out.println("_________" );
        System.out.println(updateUser(1, new JSONObject("{\"name\": \"Updated User\", \"username\": \"updateduser\", \"email\": \"updateduser@example.com\"}")));
        System.out.println("_________" );
        System.out.println("Delete user: " + deleteUser(1));

        getCommentsForLastPost(1);
        getOpenTodos(1);
    }

    public static String getAllUsers() throws IOException {
        return sendRequest(BASE_URL, "GET", null);
    }

    public static String getUserById(int id) throws IOException {
        return sendRequest(BASE_URL + "/" + id, "GET", null);
    }

    public static String getUserByUsername(String username) throws IOException {
        return sendRequest(BASE_URL + "?username=" + username, "GET", null);
    }

    public static String createUser(JSONObject jsonInput) throws IOException {
        return sendRequest(BASE_URL, "POST", jsonInput.toString());
    }
    public static String updateUser(int id, JSONObject jsonInput) throws IOException {
        return sendRequest(BASE_URL + "/" + id, "PUT", jsonInput.toString());
    }

    // Метод для видалення об'єкта
    public static String deleteUser(int id) throws IOException {
        return sendRequest(BASE_URL + "/" + id, "DELETE", null);
    }

    private static String sendRequest(String urlString, String method, String jsonInputString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");

        if (jsonInputString != null && !jsonInputString.isEmpty()) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        int responseCode = connection.getResponseCode();
        InputStream inputStream = (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) ? connection.getInputStream() : connection.getErrorStream();
        try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }

    public static void getCommentsForLastPost(int userId) throws IOException {
        String postsUrl = "https://jsonplaceholder.typicode.com/users/" + userId + "/posts";
        String postsResponse = sendRequest(postsUrl, "GET", null);

        JSONArray posts = new JSONArray(postsResponse);
        JSONObject lastPost = null;
        int maxId = -1;
        for (int i = 0; i < posts.length(); i++) {
            JSONObject post = posts.getJSONObject(i);
            int postId = post.getInt("id");
            if (postId > maxId) {
                maxId = postId;
                lastPost = post;
            }
        }

        if (lastPost == null) {
            System.out.println("No posts found for user " + userId);
            return;
        }

        int lastPostId = lastPost.getInt("id");
        String commentsUrl = "https://jsonplaceholder.typicode.com/posts/" + lastPostId + "/comments";
        String commentsResponse = sendRequest(commentsUrl, "GET", null);

        String fileName = "user-" + userId + "-post-" + lastPostId + "-comments.json";
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(commentsResponse);
        }

        System.out.println("Comments for the last post of user " + userId + " saved to " + fileName);
    }

    public static void getOpenTodos(int userId) throws IOException {
        String todosUrl = "https://jsonplaceholder.typicode.com/users/" + userId + "/todos";
        String todosResponse = sendRequest(todosUrl, "GET", null);

        JSONArray todos = new JSONArray(todosResponse);
        JSONArray openTodos = new JSONArray();
        for (int i = 0; i < todos.length(); i++) {
            JSONObject todo = todos.getJSONObject(i);
            if (!todo.getBoolean("completed")) {
                openTodos.put(todo);
            }
        }

        System.out.println("Open todos for user " + userId + ": " + openTodos.toString(2));
    }
}
