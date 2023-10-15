package manager.server;

import com.google.gson.*;
import manager.impl.HttpTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {
    static HttpTaskServer server;
    static KVServer kvServer;
    static HttpRequest request;
    static HttpClient client;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .serializeNulls().create();
    private static LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);

    @BeforeAll
    public static void runServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer(getHTTPTaskManager());
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    public static void stopServer() {
        kvServer.stop();
    }

    static HttpTaskManager getHTTPTaskManager() throws IOException {
        HttpTaskManager taskManager = new HttpTaskManager("http://localhost:8080");

        Map<Long, EpicTask> epicTasks = new HashMap<>();
        Map<Long, SubTask> subTasks = new HashMap<>();
        Map<Long, Task> tasks = new HashMap<>();

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        epicTask.setId(1L);
        epicTasks.put(1L, epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        subTask.setId(2);
        subTasks.put(2L, subTask);

        SubTask subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime.plusMinutes(61), epicTask.getId());
        subTask2.setId(3);
        subTasks.put(3L, subTask2);

        SubTask subTask3 = new SubTask("Test SubTask3", "This is a test subtask2", 60,
                startTime.plusMinutes(121), epicTask.getId());
        subTask3.setId(4);
        subTasks.put(4L, subTask3);

        Task task = new Task("Test Task", "This is a test task", 60, startTime.plusHours(4));
        task.setId(5);
        tasks.put(5L, task);
        Task task2 = new Task("Test Task2", "This is a test task2", 60, startTime.plusHours(6));
        task2.setId(6);
        tasks.put(6L, task2);

        taskManager.setEpicTasks(epicTasks);
        taskManager.setSubTasks(subTasks);
        taskManager.setTasks(tasks);
        return taskManager;
    }

    @Test
    public void handleGetTasks_GivenRequestGetTasksWhenSendRequestThenReturnResponseCode200() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void handleGetTasks_GivenRequestToEmptyMapGetTasksWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        server.getHttpTaskManager().removeAllTasks();
        assertThat(server.getHttpTaskManager().getTasks()).isEmpty();

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handleGetSubTasks_GivenRequestGetTasksWhenSendRequestThenReturnResponseCode200() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void handleGetSubTasks_GivenRequestToEmptyMapGetTasksWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        server.getHttpTaskManager().removeAllSubTasks();
        assertThat(server.getHttpTaskManager().getSubTasks()).isEmpty();

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handleGetEpicTasks_GivenRequestGetTasksWhenSendRequestThenReturnResponseCode200() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void handleGetEpicTasks_GivenRequestToEmptyMapGetTasksWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        server.getHttpTaskManager().removeAllEpicTasks();
        assertThat(server.getHttpTaskManager().getSubTasks()).isEmpty();

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handleAllTasksRequests_GivenRequestToGetAllTaskMapsTasksWhenSendRequestThenReturnResponseCode200() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void handleAllTasksRequests_GivenRequestToGetAllEmptyTaskMapsTasksWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        server.getHttpTaskManager().removeAllEpicTasks();
        server.getHttpTaskManager().removeAllSubTasks();
        server.getHttpTaskManager().removeAllTasks();
        assertThat(server.getHttpTaskManager().getSubTasks()).isEmpty();
        assertThat(server.getHttpTaskManager().getSubTasks()).isEmpty();
        assertThat(server.getHttpTaskManager().getTasks()).isEmpty();

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handleHistoryRequests_GivenRequestGetHistoryWhenSendRequestThenReturnResponseCode200() throws IOException, InterruptedException {
        server.getHttpTaskManager().getById(5);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void handleHistoryRequests_GivenGetRequestToEmptyHistoryWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        assertThat(server.getHttpTaskManager().historyManager.getHistory()).isEmpty();

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handleGetTaskById_GivenRequestToGetExistTaskByIdWhenSendRequestThenReturnResponseCode200() throws IOException, InterruptedException {
        EpicTask epicTask = server.getHttpTaskManager().getEpicTasks().get(1L);

        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        EpicTask epicFromResponse = gson.fromJson(response.body(), EpicTask.class);

        assertThat(epicTask).isEqualTo(epicFromResponse);
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void handleGetTaskById_GivenRequestToGetNotExistTaskByIdWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=666"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handlePostTask_GivenRequestToPostNotExistTaskByIdWhenSendRequestThenReturnResponseCode201() throws IOException, InterruptedException {
        Task newTask = new Task("New Task", "This is a NEW task", 60, startTime.plusHours(8));
        String jsonTask = gson.toJson(newTask);

        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task = gson.fromJson(response.body(), Task.class);

        assertTrue(server.getHttpTaskManager().getTasks().containsKey(task.getId()));
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    public void handlePostTask_GivenRequestToPostNullBodyWhenSendRequestThenReturnResponseCode400() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    public void handlePostSubTask_GivenRequestToPostNotExistTaskByIdWhenSendRequestThenReturnResponseCode201() throws IOException, InterruptedException {
        SubTask newTask = new SubTask("New SubTask", "This is a NEW Subtask", 60, startTime.plusHours(12), 1);
        String jsonTask = gson.toJson(newTask);

        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask task = gson.fromJson(response.body(), SubTask.class);

        assertTrue(server.getHttpTaskManager().getSubTasks().containsKey(task.getId()));
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    public void handlePostSubTask_GivenRequestToPostNullBodyWhenSendRequestThenReturnResponseCode400() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    public void handlePostEpicTask_GivenRequestToPostNotExistTaskByIdWhenSendRequestThenReturnResponseCode201() throws IOException, InterruptedException {
        EpicTask newTask = new EpicTask("New EpicTask", "This is a NEW EpicTask");
        String jsonTask = gson.toJson(newTask);

        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        EpicTask task = gson.fromJson(response.body(), EpicTask.class);

        assertTrue(server.getHttpTaskManager().getEpicTasks().containsKey(task.getId()));
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    public void handlePostEpicTask_GivenRequestToPostNullBodyWhenSendRequestThenReturnResponseCode400() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    public void handlePutTask_GivenRequestToPutExistTaskByIdWhenSendRequestThenReturnResponseCode201() throws IOException, InterruptedException {
        Task updateTask = new Task("Updated Task", "This is a Updated task", 30, startTime.plusHours(6));
        updateTask.setId(6);
        String jsonTask = gson.toJson(updateTask);

        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task = gson.fromJson(response.body(), Task.class);

        assertTrue(server.getHttpTaskManager().getTasks().containsKey(task.getId()));
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    public void handlePutTask_GivenRequestToPutNotExistTaskByIdWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        Task updateTask = new Task("Updated Task", "This is a Updated task", 30, startTime.plusHours(6));
        updateTask.setId(666);
        String jsonTask = gson.toJson(updateTask);

        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handlePutTask_GivenRequestToPutNullBodyWhenSendRequestThenReturnResponseCode400() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    public void handlePutSubTask_GivenRequestToPutExistSubTaskByIdWhenSendRequestThenReturnResponseCode201() throws IOException, InterruptedException {
        SubTask updateTask = new SubTask("Updated Task", "This is a Updated task", 30, startTime, 1);
        updateTask.setId(2);
        String jsonTask = gson.toJson(updateTask);

        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask task = gson.fromJson(response.body(), SubTask.class);

        assertTrue(server.getHttpTaskManager().getSubTasks().containsKey(task.getId()));
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    public void handlePutSubTask_GivenRequestToPutNotExistTaskByIdWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        SubTask updateTask = new SubTask("Updated Task", "This is a Updated task", 30, startTime, 1);
        updateTask.setId(666);
        String jsonTask = gson.toJson(updateTask);

        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handlePutSubTask_GivenRequestToPutNullBodyWhenSendRequestThenReturnResponseCode400() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    public void handlePutEpicTask_GivenRequestToPutExistEpicTaskByIdWhenSendRequestThenReturnResponseCode201() throws IOException, InterruptedException {
        EpicTask updateTask = new EpicTask("Test Epic Task", "This is a test epic task");
        updateTask.setId(1L);

        String jsonTask = gson.toJson(updateTask);

        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        EpicTask task = gson.fromJson(response.body(), EpicTask.class);

        assertTrue(server.getHttpTaskManager().getEpicTasks().containsKey(task.getId()));
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    public void handlePutEpicTask_GivenRequestToPutNotExistTaskByIdWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        EpicTask updateTask = new EpicTask("Test Epic Task", "This is a test epic task");
        updateTask.setId(666);
        String jsonTask = gson.toJson(updateTask);

        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handlePutEpicTask_GivenRequestToPutNullBodyWhenSendRequestThenReturnResponseCode400() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    public void handleDeleteTasks_GivenRequestToDeleteTasksWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/task"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handleDeleteEpicTasks_GivenRequestToDeleteEpicTasksWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/epic"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    public void handleDeleteSubTasks_GivenRequestToDeleteSubTasksWhenSendRequestThenReturnResponseCode204() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/subtask"))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }
}
