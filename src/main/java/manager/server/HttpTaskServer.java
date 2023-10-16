package manager.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.impl.HttpTaskManager;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;


public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private final HttpTaskManager httpTaskManager;
    private GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .serializeNulls();
    private final Gson gson;

    public HttpTaskManager getHttpTaskManager() {
        return httpTaskManager;
    }

    public HttpTaskServer(HttpTaskManager httpTaskManager) throws IOException {
        gson = gsonBuilder.create();
        this.httpTaskManager = httpTaskManager;
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/", this::handleTaskRequestsByMethod);
        httpServer.start();
    }

    private void handleTaskRequestsByMethod(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        switch (RequestType.valueOf(requestMethod)) {
            case GET -> handleGetRequestByPath(exchange);
            case POST -> handlePostRequestByPath(exchange);
            case PUT -> handlePutRequestByPath(exchange);
            case DELETE -> handleDeleteRequestByPath(exchange);
            default -> exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
        }
        exchange.close();
    }

    private void handleGetRequestByPath(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        if (path.equals("/tasks/task/") && query != null && query.startsWith("id=")) {
            handleGetTaskById(exchange);
        }

        switch (path) {
            case "/tasks/task" -> handleGetTasks(exchange);
            case "/tasks/subtask" -> handleGetSubTasks(exchange);
            case "/tasks/epic" -> handleGetEpicTasks(exchange);
            case "/tasks" -> handleAllTasksRequests(exchange);
            case "/tasks/history" -> handleHistoryRequests(exchange);

            default -> {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                exchange.close();
            }
        }
    }

    public void handleGetTasks(HttpExchange exchange) throws IOException {
        Map<Long, Task> tasks = httpTaskManager.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
            return;
        }
        String response = gson.toJson(tasks);
        sendText(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        Map<Long, SubTask> subTasks = httpTaskManager.getSubTasks();
        if (subTasks == null || subTasks.isEmpty()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
            return;
        }
        String response = gson.toJson(subTasks);
        sendText(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void handleGetEpicTasks(HttpExchange exchange) throws IOException {
        Map<Long, EpicTask> epicTasks = httpTaskManager.getEpicTasks();
        if (epicTasks == null || epicTasks.isEmpty()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
            return;
        }
        String response = gson.toJson(epicTasks);
        sendText(exchange, response, HttpURLConnection.HTTP_OK);

    }

    private void handleAllTasksRequests(HttpExchange exchange) throws IOException {
        List<Task> tasks = httpTaskManager.getOrderedTasksByStartTime();
        if (tasks == null || tasks.isEmpty()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
            return;
        }
        String response = gson.toJson(tasks);
        sendText(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void handleHistoryRequests(HttpExchange exchange) throws IOException {
        List<Task> history = httpTaskManager.historyManager.getHistory();
        if (history == null || history.isEmpty()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
            return;
        }
        String response = gson.toJson(history);
        sendText(exchange, response, HttpURLConnection.HTTP_OK);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        long taskId = Long.parseLong(exchange.getRequestURI().getQuery().substring(3));
        System.out.println(taskId);
        Optional<? extends Task> task = Optional.empty();
        if (httpTaskManager.getTasks().containsKey(taskId)) {
            task = httpTaskManager.getById(taskId);
        } else if (httpTaskManager.getSubTasks().containsKey(taskId)) {
            task = httpTaskManager.getByIdSubTask(taskId);
        } else if (httpTaskManager.getEpicTasks().containsKey(taskId)) {
            task = httpTaskManager.getByIdEpicTask(taskId);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
        }

        if (task.isPresent()) {
            String response = gson.toJson(task.get());
            sendText(exchange, response, HttpURLConnection.HTTP_OK);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
        }
    }

    private void handlePostRequestByPath(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case "/tasks/task" -> handlePostTask(exchange);
            case "/tasks/subtask" -> handlePostSubTask(exchange);
            case "/tasks/epic" -> handlePostEpicTask(exchange);

            default -> {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                exchange.close();
            }
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String body = reader.lines().collect(Collectors.joining("\n"));

        if (body.isBlank()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
        } else {
            Task task = gson.fromJson(body, Task.class);
            httpTaskManager.addNewTask(task);
            String response = gson.toJson(task);
            sendText(exchange, response, HttpURLConnection.HTTP_CREATED);
        }
    }

    private void handlePostSubTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String body = reader.lines().collect(Collectors.joining("\n"));

        if (body.isBlank()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
        } else {
            SubTask subtask = gson.fromJson(body, SubTask.class);
            httpTaskManager.addNewSubTask(subtask);
            String response = gson.toJson(subtask);
            sendText(exchange, response, HttpURLConnection.HTTP_CREATED);
        }
    }

    private void handlePostEpicTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String body = reader.lines().collect(Collectors.joining("\n"));

        if (body.isBlank()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
        } else {
            EpicTask epicTask = gson.fromJson(body, EpicTask.class);
            httpTaskManager.addNewEpicTask(epicTask);
            String response = gson.toJson(epicTask);
            sendText(exchange, response, HttpURLConnection.HTTP_CREATED);
        }
    }

    private void handlePutRequestByPath(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case "/tasks/task" -> handlePutTask(exchange);
            case "/tasks/subtask" -> handlePutSubTask(exchange);
            case "/tasks/epic" -> handlePutEpicTask(exchange);

            default -> {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                exchange.close();
            }
        }
    }

    private void handlePutTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String body = reader.lines().collect(Collectors.joining("\n"));

        if (body.isBlank()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
        } else {
            try {
                Task task = gson.fromJson(body, Task.class);
                httpTaskManager.updateTask(task);
                String response = gson.toJson(task);
                sendText(exchange, response, HttpURLConnection.HTTP_CREATED);
            } catch (RuntimeException e) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
                System.out.println(e.getMessage());
            }
        }
    }

    private void handlePutSubTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String body = reader.lines().collect(Collectors.joining("\n"));

        if (body.isBlank()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
        } else {
            try {
                SubTask subtask = gson.fromJson(body, SubTask.class);
                httpTaskManager.updateSubTask(subtask);
                String response = gson.toJson(subtask);
                sendText(exchange, response, HttpURLConnection.HTTP_CREATED);
            } catch (RuntimeException e) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
                System.out.println(e.getMessage());
            }
        }
    }

    private void handlePutEpicTask(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String body = reader.lines().collect(Collectors.joining("\n"));

        if (body.isBlank()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, -1);
        } else {
            try {
                EpicTask epicTask = gson.fromJson(body, EpicTask.class);
                httpTaskManager.updateEpicTask(epicTask);
                String response = gson.toJson(epicTask);
                sendText(exchange, response, HttpURLConnection.HTTP_CREATED);
            } catch (RuntimeException e) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleDeleteRequestByPath(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        if (path.equals("/tasks/task/") && query != null && query.startsWith("id=")) {
            handleDeleteTaskById(exchange);
        }

        switch (path) {
            case "/tasks/task" -> handleDeleteTasks(exchange);
            case "/tasks/subtask" -> handleDeleteSubTasks(exchange);
            case "/tasks/epic" -> handleDeleteEpicTasks(exchange);
            case "/tasks" -> handleDeleteAllTasks(exchange);

            default -> {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                exchange.close();
            }
        }
    }

    void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        httpTaskManager.removeAllTasks();
        httpTaskManager.removeAllSubTasks();
        httpTaskManager.removeAllEpicTasks();
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
    }

    void handleDeleteEpicTasks(HttpExchange exchange) throws IOException {
        httpTaskManager.removeAllEpicTasks();
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
    }

    void handleDeleteSubTasks(HttpExchange exchange) throws IOException {
        httpTaskManager.removeAllSubTasks();
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
    }

    void handleDeleteTasks(HttpExchange exchange) throws IOException {
        httpTaskManager.removeAllTasks();
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        long taskId = Long.parseLong(exchange.getRequestURI().getQuery().substring(3));
        System.out.println(taskId);

        if (httpTaskManager.getTasks().containsKey(taskId)) {
            httpTaskManager.deleteByIdTask(taskId);
        } else if (httpTaskManager.getSubTasks().containsKey(taskId)) {
            httpTaskManager.deleteByIdSubTask(taskId);
        } else if (httpTaskManager.getEpicTasks().containsKey(taskId)) {
            httpTaskManager.deleteByIdEpicTasks(taskId);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
        }
    }

    protected void sendText(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(rCode, resp.length);
        h.getResponseBody().write(resp);
    }
}
