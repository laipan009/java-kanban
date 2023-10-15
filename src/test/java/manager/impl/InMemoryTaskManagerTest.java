package manager.impl;

import manager.api.TaskManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUpManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void generateTimeTable_WhenCalledThenReturnCorrectMap() {
        Map<Long, Long> timeTable = taskManager.generateTimeTable();
        long last15MinPoint = 525585;
        System.out.println(timeTable.size());

        assertThat(timeTable).containsKey(0L);
        assertThat(timeTable).containsKey(last15MinPoint);
    }

    @Test
    void setIdGenerator_ShouldSetIdGeneratorTo2() {
        taskManager.setId(2);

        assertThat(taskManager.getId()).isEqualTo(2);
    }

    @Test
    void generateId_ShouldIncrementIdGenerator() {
        long idGeneratorBefore = taskManager.getId();
        long idGeneratorExpected = idGeneratorBefore + 1;
        Task task = new Task("Test Epic Task", "This is a test epic task");

        taskManager.addNewTask(task);

        assertThat(taskManager.getId()).isEqualTo(idGeneratorExpected);
    }

    @Test
    void checkIntersections_ShouldCheckIntersectionTaskByStartTimeAndEndTimeAndAddTaskToMap() {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime.plusMinutes(61), epicTask.getId());
        taskManager.addNewSubTask(subTask2);

        SubTask subTask3 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime.minusMinutes(61), epicTask.getId());
        taskManager.addNewSubTask(subTask3);

        assertTrue(taskManager.getSubTasks().containsKey(subTask2.getId()));
        assertTrue(taskManager.getSubTasks().containsKey(subTask3.getId()));
    }

    @Test
    void checkIntersections_ShouldCheckIntersectionTaskByStartTimeAndEndTimeAndReturnFalseAndNotAddToMap() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 2, 0, 0);

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime, epicTask.getId());
        taskManager.addNewSubTask(subTask2);

        assertFalse(taskManager.getSubTasks().containsKey(subTask2.getId()));
    }

    @Test
    void checkIntersections_ShouldCheckIntersectionTaskByStartTimeAndEndTimeAndReturnFalseIfRuntimesCrossOnEdgeValue() {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime.plusMinutes(1), epicTask.getId());
        taskManager.addNewSubTask(subTask2);

        SubTask subTask3 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime.plusMinutes(59), epicTask.getId());
        taskManager.addNewSubTask(subTask3);

        assertFalse(taskManager.getSubTasks().containsKey(subTask2.getId()));
        assertFalse(taskManager.getSubTasks().containsKey(subTask3.getId()));
    }

    @Test
    void getOrderTasksByStartTime_ShouldReturnSetOrderingTasksWhereFirstIsUpcomingTimeTask() {
        LocalDateTime startTime = LocalDateTime.of(2023, 9, 29, 10, 0, 0);

        Task task = new Task("Test Task1", "This is a test  task1");
        taskManager.addNewTask(task);

        Task task2 = new Task("Test Task2", "This is a test task2");
        taskManager.addNewTask(task2);

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        taskManager.addNewEpicTask(epicTask);

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 60, startTime,
                epicTask.getId());
        taskManager.addNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", 60,
                startTime.plusMinutes(61), epicTask.getId());
        taskManager.addNewSubTask(subTask2);

        SubTask subTask3 = new SubTask("Test SubTask3", "This is a test subtask2", 60,
                startTime.plusMinutes(121), epicTask.getId());
        taskManager.addNewSubTask(subTask3);

        assertTrue(taskManager.getSubTasks().containsKey(subTask.getId()));
        assertTrue(taskManager.getEpicTasks().containsKey(epicTask.getId()));
        assertTrue(taskManager.getSubTasks().containsKey(subTask2.getId()));
        assertTrue(taskManager.getSubTasks().containsKey(subTask3.getId()));
        assertTrue(taskManager.getTasks().containsKey(task.getId()));
        assertTrue(taskManager.getTasks().containsKey(task2.getId()));

        List<Task> orderedTasks = taskManager.getOrderedTasksByStartTime();
        orderedTasks.forEach(System.out::println);

        SubTask subTask4 = new SubTask("Test SubTask4", "This is a test subtask2", 60,
                startTime.plusHours(5), epicTask.getId());
        taskManager.addNewSubTask(subTask4); /// протестить обновление

        Task task3 = new Task("Test SubTask", "This is a test subtask", 60, startTime);
        taskManager.addNewTask(task3);

        System.out.println();

        List<Task> orderedTasks2 = taskManager.getOrderedTasksByStartTime();
        orderedTasks2.forEach(System.out::println);

        assertThat(orderedTasks.get(orderedTasks.size()-1)).isEqualTo(task2);
        //System.out.println(orderedTasks.get(orderedTasks.size()-1));

        assertThat(orderedTasks.get(0)).isEqualTo(epicTask);
        //System.out.println(orderedTasks.get(0));
    }
}