package manager.api;

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

public abstract class TaskManagerTest <T extends TaskManager>{
    protected T taskManager;

    T getTaskManager() {
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

        taskManager.setEpicTasks(epicTasks);
        taskManager.setSubTasks(subTasks);
        taskManager.setTasks(tasks);
        return taskManager;
    }

    @Test
    void getEpicTasks_ShouldReturnEpicTasks() {
        Map<Long, EpicTask> epicTasks = getTaskManager().getEpicTasks();

        assertThat(taskManager.getEpicTasks()).isEqualTo(epicTasks);
    }

    @Test
    void setEpicTasks_ShouldSetEpicTasks() {
        Map<Long, EpicTask> epicTasks = new HashMap<>();
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        epicTasks.put(1L, epicTask);

        taskManager.setEpicTasks(epicTasks);

        assertThat(taskManager.getEpicTasks()).isEqualTo(epicTasks);
    }

    @Test
    void getSubTasks_ShouldReturnSubTasks() {
        Map<Long, SubTask> subTasks = getTaskManager().getSubTasks();

        assertThat(taskManager.getSubTasks()).isEqualTo(subTasks);
    }

    @Test
    void setSubTasks_ShouldSetSubTasks() {
        Map<Long, SubTask> subTasks = new HashMap<>();
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 1L);
        subTasks.put(1L, subTask);

        taskManager.setSubTasks(subTasks);

        assertThat(taskManager.getSubTasks()).isEqualTo(subTasks);
    }

    @Test
    void getTasks_ShouldReturnTasks() {
        Map<Long, Task> tasks = getTaskManager().getTasks();

        assertThat(taskManager.getTasks()).isEqualTo(tasks);
    }

    @Test
    void setTasks_ShouldSetTasks() {
        Map<Long, Task> tasks = new HashMap<>();
        Task task = new Task("Test Task", "This is a test task");
        tasks.put(1L, task);

        taskManager.setTasks(tasks);

        assertThat(taskManager.getTasks()).isEqualTo(tasks);
    }

    @Test
    void getSubTasksByEpicId_ShouldReturnSubTasksWithMatchingEpicId() {
        taskManager = getTaskManager();
        SubTask subTask = taskManager.getSubTasks().get(4L);
        SubTask subTask2 = taskManager.getSubTasks().get(5L);
        SubTask subTask3 = taskManager.getSubTasks().get(6L);

        List<SubTask> actualSubTasks = taskManager.getSubTasksByEpicId(1L);

        assertAll("Success",
                () -> assertThat(actualSubTasks).contains(subTask),
                () -> assertThat(actualSubTasks).contains(subTask2),
                () -> assertThat(actualSubTasks).contains(subTask3)
        );
    }

    @Test
    void getSubTasksByEpicId_ShouldReturnEmptyListSubTasksIfEpicIdNotFound() {
        taskManager = getTaskManager();
        long notExistId = 1111;

        List<SubTask> actualSubTasks = taskManager.getSubTasksByEpicId(notExistId);

        assertThat(actualSubTasks).isEmpty();
    }

    @Test
    void getSubTasksByEpicId_ShouldReturnEmptyListSubTasksIfEpicsIsNull() {
        Map<Long, EpicTask> epicTasks = null;

        Map<Long, SubTask> subTasks = new HashMap<>();
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 2L);
        subTasks.put(3L, subTask);
        taskManager.setSubTasks(subTasks);

        List<SubTask> actualSubTasks = taskManager.getSubTasksByEpicId(1L);

        assertThat(actualSubTasks).isEmpty();
    }

    @Test
    void removeAllTasks_ShouldRemoveAllTasks() {
        taskManager = getTaskManager();

        taskManager.removeAllTasks();

        assertThat(taskManager.getTasks()).isEmpty();
    }

    @Test
    void removeAllTasks_ShouldRemoveAllTasksEvenIsEmpty() {
        Map<Long, Task> tasks = null;
        taskManager.setTasks(tasks);

        taskManager.removeAllTasks();

        assertThat(taskManager.getTasks()).isEmpty();
    }

    @Test
    void removeAllSubTasks_ShouldRemoveAllSubTasks() {
        taskManager = getTaskManager();

        taskManager.removeAllSubTasks();

        assertThat(taskManager.getSubTasks()).isEmpty();
    }

    @Test
    void removeAllSubTasks_ShouldRemoveAllSubTasksEvenIsEmpty() {
        Map<Long, SubTask> subtasks = null;
        taskManager.setSubTasks(subtasks);

        taskManager.removeAllSubTasks();

        assertThat(taskManager.getSubTasks()).isEmpty();
    }

    @Test
    void removeAllEpicTasks_ShouldRemoveAllEpicTasksAndSubTasks() {
        taskManager = getTaskManager();

        taskManager.removeAllEpicTasks();

        assertAll("Success",
                () -> assertThat(taskManager.getEpicTasks()).isEmpty(),
                () -> assertThat(taskManager.getSubTasks()).isEmpty()
        );
    }

    @Test
    void removeAllEpicTasks_ShouldRemoveAllEpicTasksEvenIsEmpty() {
        Map<Long, EpicTask> epictasks = null;
        taskManager.setEpicTasks(epictasks);

        taskManager.removeAllEpicTasks();

        assertThat(taskManager.getEpicTasks()).isEmpty();
    }

    @Test
    void getById_ShouldReturnTaskWithMatchingId() {
        taskManager = getTaskManager();
        Task task = taskManager.getTasks().get(7L);

        Optional<Task> actualTask = taskManager.getById(7);

        assertThat(actualTask.get()).isEqualTo(task);
    }

    @Test
    void getById_ShouldReturnOptionalWithNullIfTasksIsEmpty() {
        Map<Long, Task> tasks = null;
        taskManager.setTasks(tasks);

        Optional<Task> actualTask = taskManager.getById(7);

        assertThat(actualTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnOptionalWithNullIfIdNotExist() {
        taskManager = getTaskManager();
        long notExistId = 111;

        Optional<Task> actualTask = taskManager.getById(notExistId);

        assertThat(actualTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnSubTaskWithMatchingId() {
        taskManager = getTaskManager();
        SubTask subTask = taskManager.getSubTasks().get(4L);

        Optional<SubTask> actualSubTask = taskManager.getByIdSubTask(4);

        assertThat(actualSubTask.get()).isEqualTo(subTask);
    }

    @Test
    void getById_ShouldReturnOptionalSubTaskWithNullIfSubTasksIsEmpty() {
        Map<Long, SubTask> subTasks = null;
        taskManager.setSubTasks(subTasks);

        Optional<SubTask> actualSubTask = taskManager.getByIdSubTask(7);

        assertThat(actualSubTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnOptionalSubTaskWithNullIfIdNotExist() {
        taskManager = getTaskManager();
        long notExistId = 111;

        Optional<SubTask> actualSubTask = taskManager.getByIdSubTask(notExistId);

        assertThat(actualSubTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnEpicTaskWithMatchingId() {
        taskManager = getTaskManager();
        EpicTask epicTask = taskManager.getEpicTasks().get(1L);

        Optional<EpicTask> actualEpicTask = taskManager.getByIdEpicTask(1);

        assertThat(actualEpicTask.get()).isEqualTo(epicTask);
    }

    @Test
    void getById_ShouldReturnOptionalEpicTaskWithNullIfSubTasksIsEmpty() {
        Map<Long, EpicTask> epicTasks = null;
        taskManager.setEpicTasks(epicTasks);

        Optional<EpicTask> actualEpicTask = taskManager.getByIdEpicTask(1);

        assertThat(actualEpicTask).isEmpty();
    }

    @Test
    void getById_ShouldReturnOptionalEpicTaskWithNullIfIdNotExist() {
        taskManager = getTaskManager();
        long notExistId = 111;

        Optional<EpicTask> actualEpicTask = taskManager.getByIdEpicTask(notExistId);

        assertThat(actualEpicTask).isEmpty();
    }

    @Test
    void addNewTask_ShouldAddNewTask() {
        Task task = new Task("Test Task", "This is a test task");

        taskManager.addNewTask(task);

        assertThat(taskManager.getTasks()).containsValue(task);
    }

    @Test
    void addNewTask_ShouldThrowExceptionIfPassNull() {
        Task task = null;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.addNewTask(task)
        );

        assertThat(exception).hasMessageMatching("Empty value passed");
    }

    @Test
    void addNewEpicTask_ShouldAddNewEpicTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");

        taskManager.addNewEpicTask(epicTask);

        assertThat(taskManager.getEpicTasks()).containsValue(epicTask);
    }

    @Test
    void addNewEpicTask_ShouldThrowExceptionIfPassNull() {
        EpicTask epicTask = null;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.addNewTask(epicTask)
        );

        assertThat(exception).hasMessageMatching("Empty value passed");
    }

    @Test
    void addNewSubTask_ShouldAddNewSubTasksAndAssociateWithEpicTask() {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask", "This is a test subtask", 60,
                startTime.plusMinutes(61), epicTask.getId());
        taskManager.addNewSubTask(subTask2);

        assertThat(taskManager.getSubTasks()).containsValue(subTask);
        assertThat(taskManager.getEpicTasks().get(epicTask.getId()).getSubTasksId()).contains(subTask.getId());
        assertThat(epicTask.getEndTime()).isEqualTo(subTask2.getEndTime());
    }

    @Test
    void addNewSubTask_ShouldAddOnlyFirstSubTaskAndDeclineSecondSubTaskWithCrossTimeParameters() {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask", "This is a test subtask", 60,
                startTime.plusMinutes(60), epicTask.getId());
        taskManager.addNewSubTask(subTask2);

        assertThat(taskManager.getSubTasks()).containsValue(subTask);
        assertFalse(taskManager.getSubTasks().containsValue(subTask2));
        assertThat(taskManager.getEpicTasks().get(epicTask.getId()).getSubTasksId()).contains(subTask.getId());
        assertThat(epicTask.getEndTime()).isEqualTo(subTask.getEndTime());
    }

    @Test
    void addNewSubTask_ShouldThrowExceptionIfPassNull() {
        SubTask subTask = null;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.addNewSubTask(subTask)
        );

        assertThat(exception).hasMessageMatching("Empty value passed");
    }

    @Test
    void addNewTask_ShouldAddNewTaskBetweenTwoEpicSubTasks() {
        LocalDateTime startTime = LocalDateTime.of(2023, 10, 6, 10, 0, 0);
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime.plusHours(4), epicTask.getId());
        taskManager.addNewSubTask(subTask2);

        Task task = new Task("Test Task", "This is a test task", 60, startTime.plusHours(2));
        taskManager.addNewTask(task);

        assertThat(taskManager.getTasks()).containsValue(task);
        assertThat(taskManager.getEpicTasks()).containsValue(epicTask);
        assertThat(taskManager.getSubTasks()).containsValue(subTask);
        assertThat(taskManager.getSubTasks()).containsValue(subTask2);
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        Task task = new Task("Test Task", "This is a test task");
        taskManager.addNewTask(task);

        Task updatedTask = new Task("Updated Task", "This is an updated task");
        updatedTask.setId(task.getId());

        taskManager.updateTask(updatedTask);

        assertThat(taskManager.getTasks()).containsValue(updatedTask);
    }

    @Test
    void updateTask_ShouldThrowExceptionIfTasksIsEmpty() {
        Map<Long, Task> tasks = null;
        taskManager.setTasks(tasks);
        Task updatedTask = new Task("Updated Task", "This is an updated task");

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateTask(updatedTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateTask_ShouldThrowExceptionIfTasksIsNotExistInMap() {
        Task task = new Task("Test Task", "This is a test task");
        taskManager.addNewTask(task);

        Task updatedTask = new Task("Updated Task", "This is an updated task");
        updatedTask.setId(111);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateTask(updatedTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateSubTask_ShouldUpdateExistingSubTaskAndCheckEpicTaskStatus() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask updatedSubTask = new SubTask("Updated SubTask", "This is an updated subtask", epicTask.getId());
        updatedSubTask.setId(subTask.getId());
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateSubTask(updatedSubTask);

        assertThat(taskManager.getSubTasks()).containsValue(updatedSubTask);
        assertThat(taskManager.getEpicTasks().get(epicTask.getId()).getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void updateSubTask_ShouldThrowExceptionIfSubTasksIsEmpty() {
        SubTask updatedSubTask = new SubTask("Updated SubTask", "This is an updated subtask", 5);
        updatedSubTask.setId(22);
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateSubTask(updatedSubTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateSubTask_ShouldThrowExceptionIfSubTasksIsNotExistInMap() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask updatedSubTask = new SubTask("Updated SubTask", "This is an updated subtask", epicTask.getId());
        updatedSubTask.setId(1111);
        updatedSubTask.setStatus(TaskStatus.IN_PROGRESS);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateSubTask(updatedSubTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateEpicTask_ShouldUpdateExistingEpicTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        EpicTask updatedEpicTask = new EpicTask("Updated Epic Task", "This is an updated epic task");
        updatedEpicTask.setId(epicTask.getId());

        taskManager.updateEpicTask(updatedEpicTask);

        assertThat(taskManager.getEpicTasks()).containsValue(updatedEpicTask);
    }

    @Test
    void updateEpicTask_ShouldThrowExceptionIfEpicTasksIsEmpty() {
        EpicTask updatedEpicTask = new EpicTask("Updated Epic Task", "This is an updated epic task");
        updatedEpicTask.setId(22);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateEpicTask(updatedEpicTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void updateEpicTask_ShouldThrowExceptionIfEpicTasksIsNotExistInMap() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        EpicTask updatedEpicTask = new EpicTask("Updated Epic Task", "This is an updated epic task");
        updatedEpicTask.setId(1111);

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.updateEpicTask(updatedEpicTask)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToNew_WhenNoSubTasks() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");

        taskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToDone_WhenAllSubTasksAreDone() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.addNewSubTask(subTask2);

        taskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToInProgress_WhenSomeSubTasksAreInProgress() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addNewSubTask(subTask2);

        taskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToInProgress_WhenSomeSubTasksAreNew() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.addNewSubTask(subTask2);

        taskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void checkStatusEpicTask_ShouldSetStatusToInProgress_WhenSomeSubTasksAreNewAndInProgress() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask1 = new SubTask("SubTask 1", "This is subtask 1", epicTask.getId());
        subTask1.setStatus(TaskStatus.NEW);
        taskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "This is subtask 2", epicTask.getId());
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addNewSubTask(subTask2);

        taskManager.checkStatusEpicTask(epicTask);

        assertThat(epicTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void deleteByIdTask_ShouldRemoveTaskWithMatchingId() {
        Task task = new Task("Test Task", "This is a test task");
        taskManager.addNewTask(task);

        taskManager.deleteByIdTask(task.getId());

        assertThat(taskManager.getTasks()).doesNotContainKey(task.getId());
    }

    @Test
    void deleteByIdTask_ShouldThrowExceptionIfTasksIsEmpty() {
        Task task = new Task("Test Task", "This is a test task");

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteByIdTask(task.getId())
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdTask_ShouldThrowExceptionIfTasksIsNotExistTask() {
        Task task = new Task("Test Task", "This is a test task");
        taskManager.addNewTask(task);
        long notExistId = 111;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteByIdTask(notExistId)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdSubTask_ShouldRemoveSubTaskWithMatchingId() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        taskManager.addNewSubTask(subTask);

        taskManager.deleteByIdSubTask(subTask.getId());

        assertThat(taskManager.getSubTasks()).doesNotContainKey(subTask.getId());
        assertThat(taskManager.getEpicTasks().get(epicTask.getId()).getSubTasksId()).doesNotContain(subTask.getId());
    }

    @Test
    void deleteByIdSubTask_ShouldThrowExceptionIfSubTasksIsEmpty() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteByIdSubTask(subTask.getId())
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdSubTask_ShouldThrowExceptionIfSubTasksIsNotExistSubTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        taskManager.addNewSubTask(subTask);
        long notExistId = 111;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteByIdSubTask(notExistId)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdEpicTasks_ShouldRemoveEpicTaskWithMatchingIdAndAssociatedSubTasks() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        taskManager.addNewSubTask(subTask);

        taskManager.deleteByIdEpicTasks(epicTask.getId());

        assertThat(taskManager.getEpicTasks()).doesNotContainKey(epicTask.getId());
        assertThat(taskManager.getSubTasks()).doesNotContainKey(subTask.getId());
    }

    @Test
    void deleteByIdEpicTasks_ShouldThrowExceptionIfEpicTasksIsEmpty() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteByIdEpicTasks(epicTask.getId())
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

    @Test
    void deleteByIdEpicTasks_ShouldThrowExceptionIfSubTasksIsNotExistEpicTask() {
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        taskManager.addNewSubTask(subTask);
        long notExistId = 111;

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> taskManager.deleteByIdEpicTasks(notExistId)
        );

        assertThat(exception).hasMessageMatching("Task not found");
    }

}