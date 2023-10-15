package manager.impl;

import manager.server.HttpTaskServer;
import manager.server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpTaskManagerTest {

    KVServer kvServer;

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    HttpTaskManager getHTTPTaskManager() throws IOException {
        HttpTaskManager taskManager = new HttpTaskManager("http://localhost/8080");
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);
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
    public void restoreStateAndSave_GivenInitializeServerWhenSaveAndRemoveAllTasksThenRestoreDataEqualBeforeData() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer server = new HttpTaskServer(getHTTPTaskManager());
        Map<Long, Task> tasksBeforeRemove = server.getHttpTaskManager().getTasks();
        Map<Long, EpicTask> epicTasksBefore = server.getHttpTaskManager().getEpicTasks();
        Map<Long, SubTask> subTasksBefore = server.getHttpTaskManager().getSubTasks();

        server.getHttpTaskManager().save();

        server.getHttpTaskManager().removeAllTasks();
        server.getHttpTaskManager().removeAllEpicTasks();
        server.getHttpTaskManager().removeAllSubTasks();

        server.getHttpTaskManager().restoreState();

        Map<Long, Task> tasksAfterRestore = server.getHttpTaskManager().getTasks();
        Map<Long, EpicTask> epicTasksAfter = server.getHttpTaskManager().getEpicTasks();
        Map<Long, SubTask> subTasksAfter = server.getHttpTaskManager().getSubTasks();

        assertThat(tasksBeforeRemove).isEqualTo(tasksAfterRestore);
        assertThat(epicTasksBefore).isEqualTo(epicTasksAfter);
        assertThat(subTasksBefore).isEqualTo(subTasksAfter);

        for (Task task : server.getHttpTaskManager().getOrderedTasksByStartTime()) {
            System.out.println(task);
        }
    }
}