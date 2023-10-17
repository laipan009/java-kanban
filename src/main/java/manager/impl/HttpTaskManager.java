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

import static manager.impl.KeyServerType.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String serverUrl) {
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
            String tasks = kvTaskClient.load(String.valueOf(TASKS));
            String subtasks = kvTaskClient.load(String.valueOf(SUBTASKS));
            String epics = kvTaskClient.load(String.valueOf(EPICS));
            String history = kvTaskClient.load(String.valueOf(HISTORY));

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
            throw new RuntimeException("Failed to save state " + e.getMessage());
        }
    }

    @Override
    public void save() {
        try {
            kvTaskClient.put(String.valueOf(TASKS), gson.toJson(getTasks().values()));
            kvTaskClient.put(String.valueOf(SUBTASKS), gson.toJson(getSubTasks().values()));
            kvTaskClient.put(String.valueOf(EPICS), gson.toJson(getEpicTasks().values()));
            kvTaskClient.put(String.valueOf(HISTORY), gson.toJson(historyManager.getHistory()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save state " + e.getMessage());
        }
    }
}