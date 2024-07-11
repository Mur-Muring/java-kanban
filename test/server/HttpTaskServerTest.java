package server;

import com.google.gson.*;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Duration.parse(json.getAsString());
    }
}

class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}

public class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private TaskManager manager;
    private static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Response code should be 201");

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Tasks should not be null");
        assertEquals(1, tasksFromManager.size(), "Tasks size should be 1");
        assertEquals("Test Task", tasksFromManager.get(0).getName(), "Task name should match");
    }


    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getIdTask());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getIdTask(), taskFromResponse.getIdTask(), "Task ID should match");
        assertEquals(task.getName(), taskFromResponse.getName(), "Task name should match");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task 1", "Task description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test Task 2", "Task description 2", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(10));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), List.class);
        assertEquals(2, tasksFromResponse.size(), "Tasks size should be 2");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Task description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getIdTask());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertTrue(tasksFromManager.isEmpty(), "Tasks should be empty");
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task 1", "Task description 1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test Task 2", "Task description 2", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(10));
        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();
        assertTrue(tasksFromManager.isEmpty(), "Tasks should be empty");
    }
}
