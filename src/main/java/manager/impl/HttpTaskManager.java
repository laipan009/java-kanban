package manager.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.server.KVTaskClient;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;
    private Gson gson;

    public HttpTaskManager(String serverUrl) throws IOException {
        super(serverUrl);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .create();
        this.kvTaskClient = new KVTaskClient(serverUrl);
        restoreState();
    }

    public void restoreState() {
        try {
            String tasks = kvTaskClient.load("tasks");
            String subtasks = kvTaskClient.load("subtasks");
            String epics = kvTaskClient.load("epics");
            String history = kvTaskClient.load("history");

            List<Task> tasksList = gson.fromJson(tasks, new TypeToken<ArrayList<Task>>() {
            }.getType());
            if (tasksList != null) {
                tasksList.forEach(t -> {
                    getTasks().put(t.getId(), t);
                    addTaskToTimeTable(t);
                });
            }

            List<SubTask> subTaskList = gson.fromJson(subtasks, new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            if (subTaskList != null) {
                subTaskList.forEach(st -> {
                    getSubTasks().put(st.getId(), st);
                    addTaskToTimeTable(st);
                });
            }

            List<EpicTask> epicsList = gson.fromJson(epics, new TypeToken<ArrayList<EpicTask>>() {
            }.getType());
            if (epicsList != null) {
                epicsList.forEach(et -> getEpicTasks().put(et.getId(), et));
            }

            setNewIdValue(getEpicTasks(), getSubTasks(), getTasks());
            combineEpicAndSubTasks(getEpicTasks(), getSubTasks());
            if (!history.isBlank()) {
                restoreHistory(gson.fromJson(history, new TypeToken<List<Long>>() {
                }.getType()), this);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save state", e);
        }
    }

    @Override
    public void save() {
        try {
            kvTaskClient.put("tasks", gson.toJson(getTasks().values()));
            kvTaskClient.put("subtasks", gson.toJson(getSubTasks().values()));
            kvTaskClient.put("epics", gson.toJson(getEpicTasks().values()));
            kvTaskClient.put("history", gson.toJson(historyManager.getHistory()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save state", e);
        }
    }
}