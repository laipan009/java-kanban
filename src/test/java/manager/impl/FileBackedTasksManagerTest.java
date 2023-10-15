package manager.impl;

import manager.api.TaskManagerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private Path tempFilePath;
    private String path = "C:\\Users\\superuser\\IdeaProjects\\java-kanban2\\test.csv";

    @BeforeEach
    void setUpBackedManager() {
        tempFilePath = Path.of("C:\\Users\\superuser\\IdeaProjects\\java-kanban2\\test.csv");
        taskManager = new FileBackedTasksManager(tempFilePath.toString());
    }

    @Test
    void setPathToFile_ShouldSetNewValueForPathToFileIfNotNull() {
        assertThat(taskManager.getPathToFile()).isEqualTo(path);
        String newPath = "C:\\Users\\superuser\\IdeaProjects\\java-kanban2\\NEWtest.csv";

        taskManager.setPathToFile(newPath);

        assertThat(taskManager.getPathToFile()).isEqualTo(newPath);
    }

    @Test
    void setPathToFile_ShouldNotDoAnythingIfValueForPathToFileIsNull() {
        assertThat(taskManager.getPathToFile()).isEqualTo(path);
        String newPath = null;

        taskManager.setPathToFile(newPath);

        assertThat(taskManager.getPathToFile()).isEqualTo(path);
    }

    @Test
    void save_WhenTasksAddedThenFileContainsCorrectData() throws IOException {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);

        taskManager.save();

        String fileContent = Files.readString(tempFilePath);
        assertThat(fileContent).contains(
                "id,type,name,status,description,duration,start_Time,epic",
                "1,EPICTASK,Test Epic Task,NEW,This is a test epic task,60,2023-09-29 10:00:00",
                "2,SUBTASK,Test SubTask,NEW,This is a test subtask,60,2023-09-29 10:00:00,1"
        );
    }

    @Test
    void loadFromFile_WhenFileContainsValidDataThenStateRestored() throws IOException {
        LocalDateTime startTime = LocalDateTime.of(2023, 10, 1, 10, 0, 0);
        String validData = "id,type,name,status,description,duration,start_Time,epic\n" +
                "1,TASK,Task 1,NEW,Description 1,0, \n" +
                "2,TASK,Task 2,IN_PROGRESS,Description 60,2,2023-10-01T11:00:00\n" + "\n ";

        Files.writeString(tempFilePath, validData, StandardCharsets.UTF_8);

        FileBackedTasksManager restoredManager = taskManager.loadFromFile(tempFilePath.toFile());

        assertThat(restoredManager.getTasks()).hasSize(2);
        assertThat(restoredManager.getTasks().get(1L).getDuration()).isEqualTo(0);
        assertNull(restoredManager.getTasks().get(1L).getStartTime());
        assertThat(restoredManager.getTasks().get(1L).getName()).isEqualTo("Task 1");

        assertThat(restoredManager.getTasks().get(2L).getName()).isEqualTo("Task 2");
    }

    @Test
    void loadFromFile_WhenFileContainsValidDataThenAddTaskWithCrossTimeNotAddedAndAddValidTaskWithValidTime() throws IOException {
        LocalDateTime startTime = LocalDateTime.of(2023, 10, 1, 10, 0, 0);
        String validData = "id,type,name,status,description,duration,start_Time,epic\n" +
                "1,TASK,Task 1,NEW,Description 1,60,2023-10-01T10:00:00\n" +
                "2,TASK,Task 2,IN_PROGRESS,Description 60,2,2023-10-01T11:00:00\n" + "\n ";

        Files.writeString(tempFilePath, validData, StandardCharsets.UTF_8);

        FileBackedTasksManager restoredManager = taskManager.loadFromFile(tempFilePath.toFile());

        assertThat(restoredManager.getTasks()).hasSize(2);
        assertThat(restoredManager.getTasks().get(1L).getName()).isEqualTo("Task 1");
        assertThat(restoredManager.getTasks().get(2L).getName()).isEqualTo("Task 2");
        assertThat(restoredManager.getOrderedTasksByStartTime().get(0)).isEqualTo(restoredManager.getTasks().get(1L));
        Task task = new Task("Test Task", "This is a test epic task", 60, startTime);
        restoredManager.addNewTask(task);
        assertFalse(restoredManager.getTasks().containsKey(task.getId()));
        Task task2 = new Task("Test Task", "This is a test epic task", 60, startTime.plusHours(2));
        restoredManager.addNewTask(task2);
        assertTrue(restoredManager.getTasks().containsKey(task2.getId()));
    }

    @Test
    void loadFromFile_WhenFileIsEmptyThenThrowExceptions() throws IOException {
        Files.writeString(tempFilePath, "", StandardCharsets.UTF_8);

        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> taskManager.loadFromFile(tempFilePath.toFile())
        );

        assertThat(exception.getMessage()).isEqualTo("File is Empty");
    }


    @Test
    void loadFromFile_WhenFileContainsInvalidDataThenThrowExceptions() throws IOException {
        String invalidData = "invalid data";
        Files.writeString(tempFilePath, invalidData, StandardCharsets.UTF_8);

        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> taskManager.loadFromFile(tempFilePath.toFile())
        );

        assertThat(exception.getMessage()).isEqualTo("Not valid file format");
    }
}