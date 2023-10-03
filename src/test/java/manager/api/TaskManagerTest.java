package manager.api;

import manager.impl.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest {
    protected InMemoryTaskManager inMemoryTaskManager;

    InMemoryTaskManager getInMemoryTaskManager() {
        Map<Long, EpicTask> epicTasks = new HashMap<>();
        Map<Long, SubTask> subTasks = new HashMap<>();
        Map<Long, Task> tasks = new HashMap<>();

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        epicTask.setId(1L);
        epicTasks.put(1L, epicTask);
        EpicTask epicTask2 = new EpicTask("Test Epic Task2", "This is a test epic task2");
        epicTask2.setId(2);
        epicTasks.put(2L, epicTask2);
        EpicTask epicTask3 = new EpicTask("Test Epic Task3", "This is a test epic task3");
        epicTask3.setId(3);
        epicTasks.put(3L, epicTask3);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 1L);
        subTask.setId(4);
        subTasks.put(4L, subTask);
        SubTask subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", 1L);
        subTask2.setId(5);
        subTasks.put(5L, subTask2);
        SubTask subTask3 = new SubTask("Test SubTask3", "This is a test subtask3", 1L);
        subTask3.setId(6);
        subTasks.put(6L, subTask3);

        Task task = new Task("Test Task", "This is a test task");
        task.setId(7);
        tasks.put(7L, task);
        Task task2 = new Task("Test Task2", "This is a test task2");
        task2.setId(8);
        tasks.put(8L, task2);
        Task task3 = new Task("Test Task3", "This is a test task3");
        task3.setId(9);
        tasks.put(9L, task3);

        inMemoryTaskManager.setEpicTasks(epicTasks);
        inMemoryTaskManager.setSubTasks(subTasks);
        inMemoryTaskManager.setTasks(tasks);
        return inMemoryTaskManager;
    }

    @BeforeEach
    void setUp() {
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    void getEpicTasks_ShouldReturnEpicTasks() {
        Map<Long, EpicTask> epicTasks = getInMemoryTaskManager().getEpicTasks();

        assertThat(inMemoryTaskManager.getEpicTasks()).isEqualTo(epicTasks);
    }

    @Test
    void setEpicTasks_ShouldSetEpicTasks() {
        Map<Long, EpicTask> epicTasks = new HashMap<>();
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        epicTasks.put(1L, epicTask);

        inMemoryTaskManager.setEpicTasks(epicTasks);

        assertThat(inMemoryTaskManager.getEpicTasks()).isEqualTo(epicTasks);
    }

    @Test
    void getSubTasks_ShouldReturnSubTasks() {
        Map<Long, SubTask> subTasks = getInMemoryTaskManager().getSubTasks();

        assertThat(inMemoryTaskManager.getSubTasks()).isEqualTo(subTasks);
    }

    @Test
    void setSubTasks_ShouldSetSubTasks() {
        Map<Long, SubTask> subTasks = new HashMap<>();
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 1L);
        subTasks.put(1L, subTask);

        inMemoryTaskManager.setSubTasks(subTasks);

        assertThat(inMemoryTaskManager.getSubTasks()).isEqualTo(subTasks);
    }

    @Test
    void getTasks_ShouldReturnTasks() {
        Map<Long, Task> tasks = getInMemoryTaskManager().getTasks();

        assertThat(inMemoryTaskManager.getTasks()).isEqualTo(tasks);
    }

    @Test
    void setTasks_ShouldSetTasks() {
        Map<Long, Task> tasks = new HashMap<>();
        Task task = new Task("Test Task", "This is a test task");
        tasks.put(1L, task);

        inMemoryTaskManager.setTasks(tasks);

        assertThat(inMemoryTaskManager.getTasks()).isEqualTo(tasks);
    }

    @Test
    void getSubTasksByEpicId_ShouldReturnSubTasksWithMatchingEpicId() {
        inMemoryTaskManager = getInMemoryTaskManager();
        SubTask subTask = inMemoryTaskManager.getSubTasks().get(4L);
        SubTask subTask2 = inMemoryTaskManager.getSubTasks().get(5L);
        SubTask subTask3 = inMemoryTaskManager.getSubTasks().get(6L);

        List<SubTask> actualSubTasks = inMemoryTaskManager.getSubTasksByEpicId(1L);

        assertAll("Success",
                () -> assertThat(actualSubTasks).contains(subTask),
                () -> assertThat(actualSubTasks).contains(subTask2),
                () -> assertThat(actualSubTasks).contains(subTask3)
        );
    }

    @Test
    void getSubTasksByEpicId_ShouldReturnEmptyListSubTasksIfEpicIdNotFound() {
        inMemoryTaskManager = getInMemoryTaskManager();
        long notExistId = 1111;

        List<SubTask> actualSubTasks = inMemoryTaskManager.getSubTasksByEpicId(notExistId);

        assertThat(actualSubTasks).isEmpty();
    }

    @Test
    void getSubTasksByEpicId_ShouldReturnEmptyListSubTasksIfEpicsIsNull() {
        Map<Long, EpicTask> epicTasks = null;

        Map<Long, SubTask> subTasks = new HashMap<>();
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 2L);
        subTasks.put(3L, subTask);
        inMemoryTaskManager.setSubTasks(subTasks);

        List<SubTask> actualSubTasks = inMemoryTaskManager.getSubTasksByEpicId(1L);

        assertThat(actualSubTasks).isEmpty();
    }

    @Test
    void removeAllTasks_ShouldRemoveAllTasks() {
        inMemoryTaskManager = getInMemoryTaskManager();

        inMemoryTaskManager.removeAllTasks();

        assertThat(inMemoryTaskManager.getTasks()).isEmpty();
    }

    @Test
    void removeAllTasks_ShouldRemoveAllTasksEvenIsEmpty() {
        Map<Long, Task> tasks = null;
        inMemoryTaskManager.setTasks(tasks);

        inMemoryTaskManager.removeAllTasks();

        assertThat(inMemoryTaskManager.getTasks()).isEmpty();
    }

    @Test
    void removeAllSubTasks_ShouldRemoveAllSubTasks() {
        inMemoryTaskManager = getInMemoryTaskManager();

        inMemoryTaskManager.removeAllSubTasks();

        assertThat(inMemoryTaskManager.getSubTasks()).isEmpty();
    }

    @Test
    void removeAllSubTasks_ShouldRemoveAllSubTasksEvenIsEmpty() {
        Map<Long, SubTask> subtasks = null;
        inMemoryTaskManager.setSubTasks(subtasks);

        inMemoryTaskManager.removeAllSubTasks();

        assertThat(inMemoryTaskManager.getSubTasks()).isEmpty();
    }

    @Test
    void removeAllEpicTasks_ShouldRemoveAllEpicTasksAndSubTasks() {
        inMemoryTaskManager = getInMemoryTaskManager();

        inMemoryTaskManager.removeAllEpicTasks();

        assertAll("Success",
                () -> assertThat(inMemoryTaskManager.getEpicTasks()).isEmpty(),
                () -> assertThat(inMemoryTaskManager.getSubTasks()).isEmpty()
        );
    }

    @Test
    void removeAllEpicTasks_ShouldRemoveAllEpicTasksEvenIsEmpty() {
        Map<Long, EpicTask> epictasks = null;
        inMemoryTaskManager.setEpicTasks(epictasks);

        inMemoryTaskManager.removeAllEpicTasks();

        assertThat(inMemoryTaskManager.getEpicTasks()).isEmpty();
    }

    @Test
    void getById_ShouldReturnTaskWithMatchingId() {
        inMemoryTaskManager = getInMemoryTaskManager();
        Task task = inMemoryTaskManager.getTasks().get(7L);

        Optional<Task> actualTask = inMemoryTaskManager.getById(7);

        assertThat(actualTask.get()).isEqualTo(task);
    }

    @Test
    void getById_ShouldReturnOptionalWithNullIfTasksIsEmpty() {
        Map<Long, Task> tasks = null;
        inMemoryTaskManager.setTasks(tasks);

        Optional<Task> actualTask = inMemoryTaskManager.getById(7);

        assertThat(actualTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnOptionalWithNullIfIdNotExist() {
        inMemoryTaskManager = getInMemoryTaskManager();
        long notExistId = 111;

        Optional<Task> actualTask = inMemoryTaskManager.getById(notExistId);

        assertThat(actualTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnSubTaskWithMatchingId() {
        inMemoryTaskManager = getInMemoryTaskManager();
        SubTask subTask = inMemoryTaskManager.getSubTasks().get(4L);

        Optional<SubTask> actualSubTask = inMemoryTaskManager.getByIdSubTask(4);

        assertThat(actualSubTask.get()).isEqualTo(subTask);
    }

    @Test
    void getById_ShouldReturnOptionalSubTaskWithNullIfSubTasksIsEmpty() {
        Map<Long, SubTask> subTasks = null;
        inMemoryTaskManager.setSubTasks(subTasks);

        Optional<SubTask> actualSubTask = inMemoryTaskManager.getByIdSubTask(7);

        assertThat(actualSubTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnOptionalSubTaskWithNullIfIdNotExist() {
        inMemoryTaskManager = getInMemoryTaskManager();
        long notExistId = 111;

        Optional<SubTask> actualSubTask = inMemoryTaskManager.getByIdSubTask(notExistId);

        assertThat(actualSubTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnEpicTaskWithMatchingId() {
        inMemoryTaskManager = getInMemoryTaskManager();
        EpicTask epicTask = inMemoryTaskManager.getEpicTasks().get(1L);

        Optional<EpicTask> actualEpicTask = inMemoryTaskManager.getByIdEpicTask(1);

        assertThat(actualEpicTask.get()).isEqualTo(epicTask);
    }

    @Test
    void getById_ShouldReturnOptionalEpicTaskWithNullIfSubTasksIsEmpty() {
        Map<Long, EpicTask> epicTasks = null;
        inMemoryTaskManager.setEpicTasks(epicTasks);

        Optional<EpicTask> actualEpicTask = inMemoryTaskManager.getByIdEpicTask(1);

        assertThat(actualEpicTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnOptionalEpicTaskWithNullIfIdNotExist() {
        inMemoryTaskManager = getInMemoryTaskManager();
        long notExistId = 111;

        Optional<EpicTask> actualEpicTask = inMemoryTaskManager.getByIdEpicTask(notExistId);

        assertThat(actualEpicTask).isEmpty();
    }

    @Test
    void addNewTask_ShouldAddNewTask() {
        Task task = new Task("Test Task", "This is a test task");

        inMemoryTaskManager.addNewTask(task);

        assertThat(inMemoryTaskManager.getTasks()).containsValue(task);
    }

    @Test
    void addNewTask_ShouldThrowExceptionIfPassNull() {
        Task task = null;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.addNewTask(task)
        );

        assertThat(exception).hasMessageMatching("Empty value passed");
    }

    @Test
    void addNewEpicTask_ShouldAddNewEpicTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");

        inMemoryTaskManager.addNewEpicTask(epicTask);

        assertThat(inMemoryTaskManager.getEpicTasks()).containsValue(epicTask);
    }

    @Test
    void addNewEpicTask_ShouldThrowExceptionIfPassNull() {
        EpicTask epicTask = null;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.addNewTask(epicTask)
        );

        assertThat(exception).hasMessageMatching("Empty value passed");
    }

    @Test
    void addNewSubTask_ShouldAddNewSubTasksAndAssociateWithEpicTask() {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask", "This is a test subtask", 60,
                startTime.plusMinutes(61), epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask2);

        assertThat(inMemoryTaskManager.getSubTasks()).containsValue(subTask);
        assertThat(inMemoryTaskManager.getEpicTasks().get(epicTask.getId()).getSubTasksId()).contains(subTask.getId());
        assertThat(epicTask.getEndTime()).isEqualTo(subTask2.getEndTime());
    }

    @Test
    void addNewSubTask_ShouldAddOnlyFirstSubTaskAndDeclineSecondSubTaskWithCrossTimeParameters() {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask", "This is a test subtask", 60,
                startTime.plusMinutes(60), epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask2);

        assertThat(inMemoryTaskManager.getSubTasks()).containsValue(subTask);
        assertFalse(inMemoryTaskManager.getSubTasks().containsValue(subTask2));
        assertThat(inMemoryTaskManager.getEpicTasks().get(epicTask.getId()).getSubTasksId()).contains(subTask.getId());
        assertThat(epicTask.getEndTime()).isEqualTo(subTask.getEndTime());
    }

    @Test
    void addNewSubTask_ShouldThrowExceptionIfPassNull() {
        SubTask subTask = null;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.addNewSubTask(subTask)
        );

        assertThat(exception).hasMessageMatching("Empty value passed");
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        Task task = new Task("Test Task", "This is a test task");
        inMemoryTaskManager.addNewTask(task);

        Task updatedTask = new Task("Updated Task", "This is an updated task");
        updatedTask.setId(task.getId());

        inMemoryTaskManager.updateTask(updatedTask);

        assertThat(inMemoryTaskManager.getTasks()).containsValue(updatedTask);
    }

    @Test
    void updateTask_ShouldThrowExceptionIfTasksIsEmpty() {
        Map<Long, Task> tasks = null;
        inMemoryTaskManager.setTasks(tasks);
        Task updatedTask = new Task("Updated Task", "This is an updated task");

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.updateTask(updatedTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateTask_ShouldThrowExceptionIfTasksIsNotExistInMap() {
        Task task = new Task("Test Task", "This is a test task");
        inMemoryTaskManager.addNewTask(task);

        Task updatedTask = new Task("Updated Task", "This is an updated task");
        updatedTask.setId(111);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.updateTask(updatedTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateSubTask_ShouldUpdateExistingSubTaskAndCheckEpicTaskStatus() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);

        SubTask updatedSubTask = new SubTask("Updated SubTask", "This is an updated subtask", epicTask.getId());
        updatedSubTask.setId(subTask.getId());
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);

        inMemoryTaskManager.updateSubTask(updatedSubTask);

        assertThat(inMemoryTaskManager.getSubTasks()).containsValue(updatedSubTask);
        assertThat(inMemoryTaskManager.getEpicTasks().get(epicTask.getId()).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateSubTask_ShouldThrowExceptionIfSubTasksIsEmpty() {
        SubTask updatedSubTask = new SubTask("Updated SubTask", "This is an updated subtask", 5);
        updatedSubTask.setId(22);
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.updateSubTask(updatedSubTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateSubTask_ShouldThrowExceptionIfSubTasksIsNotExistInMap() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);

        SubTask updatedSubTask = new SubTask("Updated SubTask", "This is an updated subtask", epicTask.getId());
        updatedSubTask.setId(1111);
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.updateSubTask(updatedSubTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateEpicTask_ShouldUpdateExistingEpicTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        EpicTask updatedEpicTask = new EpicTask("Updated Epic Task", "This is an updated epic task");
        updatedEpicTask.setId(epicTask.getId());

        inMemoryTaskManager.updateEpicTask(updatedEpicTask);

        assertThat(inMemoryTaskManager.getEpicTasks()).containsValue(updatedEpicTask);
    }

    @Test
    void updateEpicTask_ShouldThrowExceptionIfEpicTasksIsEmpty() {
        EpicTask updatedEpicTask = new EpicTask("Updated Epic Task", "This is an updated epic task");
        updatedEpicTask.setId(22);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.updateEpicTask(updatedEpicTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateEpicTask_ShouldThrowExceptionIfEpicTasksIsNotExistInMap() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        EpicTask updatedEpicTask = new EpicTask("Updated Epic Task", "This is an updated epic task");
        updatedEpicTask.setId(1111);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.updateEpicTask(updatedEpicTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToNew_WhenNoSubTasks() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");

        inMemoryTaskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToDone_WhenAllSubTasksAreDone() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.addNewSubTask(subTask2);

        inMemoryTaskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToInProgress_WhenSomeSubTasksAreInProgress() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.addNewSubTask(subTask2);

        inMemoryTaskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToInProgress_WhenSomeSubTasksAreNew() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.addNewSubTask(subTask2);

        inMemoryTaskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToInProgress_WhenSomeSubTasksAreNewAndInProgress() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.addNewSubTask(subTask2);

        inMemoryTaskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void deleteByIdTask_ShouldRemoveTaskWithMatchingId() {
        Task task = new Task("Test Task", "This is a test task");
        inMemoryTaskManager.addNewTask(task);

        inMemoryTaskManager.deleteByIdTask(task.getId());

        assertThat(inMemoryTaskManager.getTasks()).doesNotContainKey(task.getId());
    }

    @Test
    void deleteByIdTask_ShouldThrowExceptionIfTasksIsEmpty() {
        Task task = new Task("Test Task", "This is a test task");

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.deleteByIdTask(task.getId())
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdTask_ShouldThrowExceptionIfTasksIsNotExistTask() {
        Task task = new Task("Test Task", "This is a test task");
        inMemoryTaskManager.addNewTask(task);
        long notExistId = 111;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.deleteByIdTask(notExistId)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdSubTask_ShouldRemoveSubTaskWithMatchingId() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);

        inMemoryTaskManager.deleteByIdSubTask(subTask.getId());

        assertThat(inMemoryTaskManager.getSubTasks()).doesNotContainKey(subTask.getId());
        assertThat(inMemoryTaskManager.getEpicTasks().get(epicTask.getId()).getSubTasksId()).doesNotContain(subTask.getId());
    }

    @Test
    void deleteByIdSubTask_ShouldThrowExceptionIfSubTasksIsEmpty() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.deleteByIdSubTask(subTask.getId())
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdSubTask_ShouldThrowExceptionIfSubTasksIsNotExistSubTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);
        long notExistId = 111;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.deleteByIdSubTask(notExistId)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdEpicTasks_ShouldRemoveEpicTaskWithMatchingIdAndAssociatedSubTasks() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);

        inMemoryTaskManager.deleteByIdEpicTasks(epicTask.getId());

        assertThat(inMemoryTaskManager.getEpicTasks()).doesNotContainKey(epicTask.getId());
        assertThat(inMemoryTaskManager.getSubTasks()).doesNotContainKey(subTask.getId());
    }

    @Test
    void deleteByIdEpicTasks_ShouldThrowExceptionIfEpicTasksIsEmpty() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.deleteByIdEpicTasks(epicTask.getId())
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdEpicTasks_ShouldThrowExceptionIfSubTasksIsNotExistEpicTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        inMemoryTaskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        inMemoryTaskManager.addNewSubTask(subTask);
        long notExistId = 111;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> inMemoryTaskManager.deleteByIdEpicTasks(notExistId)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

}