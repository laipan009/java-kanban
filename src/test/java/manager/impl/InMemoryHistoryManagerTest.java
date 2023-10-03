package manager.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task;
    EpicTask epicTask;
    SubTask subTask;
    SubTask subTask2;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Test Epic Task", "This is a test epic task");
        task.setId(1);
        epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        epicTask.setId(2);
        subTask = new SubTask("Test SubTask", "This is a test subtask", epicTask.getId());
        subTask.setId(3);
        subTask2 = new SubTask("Test SubTask2", "This is a test subtask2", epicTask.getId());
        subTask2.setId(4);
    }

    @Test
    void add_WhenTaskNotInHistoryThenTaskAdded() {
        setUp();
        assertFalse(historyManager.getHistory().contains(task));

        historyManager.add(task);

        assertTrue(historyManager.getHistory().contains(task));
    }

    @Test
    void add_WhenTaskInHistoryThenTaskRemovedAndAddedAgain() {
        setUp();
        historyManager.add(task);
        List<Task> historyBeforeSecondAdd = historyManager.getHistory();

        historyManager.add(task);
        List<Task> historyAfterSecondAdd = historyManager.getHistory();

        assertEquals(historyBeforeSecondAdd, historyAfterSecondAdd);
        assertEquals(historyBeforeSecondAdd.size(), historyAfterSecondAdd.size());
        assertTrue(historyAfterSecondAdd.contains(task));
    }

    @Test
    void add_WhenNullTaskThenNoModification() {
        setUp();
        List<Task> historyBeforeAdd = historyManager.getHistory();

        historyManager.add(null);
        List<Task> historyAfterAdd = historyManager.getHistory();

        assertEquals(historyBeforeAdd, historyAfterAdd);
    }

    @Test
    void getHistory_ShouldReturnListTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        epicTask.setId(1L);
        inMemoryTaskManager.getEpicTasks().put(1L, epicTask);
        Optional<EpicTask> ep = inMemoryTaskManager.getByIdEpicTask(epicTask.getId());

        SubTask subTask = new SubTask("Test SubTask", "This is a test subtask", 1L);
        subTask.setId(4);
        inMemoryTaskManager.getSubTasks().put(4L, subTask);
        Optional<SubTask> st = inMemoryTaskManager.getByIdSubTask(subTask.getId());

        Task task = new Task("Test Task", "This is a test task");
        task.setId(7);
        inMemoryTaskManager.getTasks().put(7L, task);
        Optional<Task> ts = inMemoryTaskManager.getById(task.getId());

        List<Task> historyList = inMemoryTaskManager.historyManager.getHistory();
        Task lastTask = historyList.get(0);
        Task firstTask = historyList.get(2);

        assertThat(lastTask).isEqualTo(epicTask);
        assertThat(firstTask).isEqualTo(task);
    }

    @Test
    void getHistory_ShouldReturnEmptyListIfHistoryEmpty() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        List<Task> historyList = taskManager.historyManager.getHistory();

        assertThat(historyList).isEmpty();
    }

    @Test
    void remove_ShouldReturnEmptyListAfterRemoveElement() {
        setUp();
        historyManager.add(task);
        List<Task> historyBeforeRemove = historyManager.getHistory();
        assertTrue(historyBeforeRemove.contains(task));

        historyManager.remove(task.getId());
        List<Task> historyAfterRemove = historyManager.getHistory();

        assertFalse(historyAfterRemove.contains(task));
    }

    @Test
    void remove_NoModificationIfTaskIdIsNotExist() {
        setUp();
        historyManager.add(task);
        List<Task> historyBeforeRemove = historyManager.getHistory();
        long notExistId = 111;

        historyManager.remove(notExistId);
        List<Task> historyAfterRemove = historyManager.getHistory();

        assertThat(historyBeforeRemove).containsAnyElementsOf(historyAfterRemove);
    }

    @Test
    void remove_AddToHistoryAndRemoveEdgePositions() {
        setUp();
        historyManager.add(task);
        historyManager.add(epicTask);
        historyManager.add(subTask);
        historyManager.add(subTask2);
        List<Task> historyBeforeRemove = historyManager.getHistory();
        assertThat(historyBeforeRemove).contains(task);
        assertThat(historyBeforeRemove).contains(subTask2);
        assertThat(historyBeforeRemove).contains(subTask);

        historyManager.remove(task.getId());
        historyManager.remove(subTask2.getId());
        historyManager.remove(subTask.getId());
        List<Task> historyAfterRemove = historyManager.getHistory();
        assertFalse(historyAfterRemove.contains(task));
        assertFalse(historyAfterRemove.contains(subTask2));
        assertFalse(historyAfterRemove.contains(subTask));
    }
}