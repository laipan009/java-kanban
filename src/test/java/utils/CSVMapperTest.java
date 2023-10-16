package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.EpicTask;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class CSVMapperTest {
    private Task task;
    private SubTask subTask;
    private EpicTask epicTask;

    private Task creatTask() {
        Task task = new Task("Task1", "This is a task");
        task.setId(1);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDuration(60);
        task.setStartTime(LocalDateTime.now());
        return task;
    }

    private SubTask creatSubTask() {
        SubTask subTask = new SubTask("SubTask1", "This is a subtask", epicTask.getId());
        subTask.setId(3);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        subTask.setDuration(30);
        subTask.setStartTime(LocalDateTime.now());
        return subTask;
    }

    private EpicTask creatEpic() {
        EpicTask epicTask = new EpicTask("EpicTask1", "This is a epic task");
        epicTask.setId(2);
        epicTask.setStatus(TaskStatus.IN_PROGRESS);
        epicTask.setDuration(30);
        epicTask.setStartTime(LocalDateTime.now());
        return epicTask;
    }

    @BeforeEach
    public void setup() {
        task = creatTask();
        epicTask = creatEpic();
        subTask = creatSubTask();
    }

    @Test
    public void taskToString_TaskToStringWhenTaskWithAllFieldsThenReturnCSVString() {
        String result = CSVMapper.taskToString(task);
        assertThat(result).isEqualTo("1,TASK,Task1,IN_PROGRESS,This is a task,60," + task.getStartTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void taskToString_TaskToStringWhenTaskWithNullOptionalFieldsThenReturnCSVString() {
        task.setDuration(0);
        task.setStartTime(null);
        String result = CSVMapper.taskToString(task);
        assertThat(result).isEqualTo("1,TASK,Task1,IN_PROGRESS,This is a task,0, ");
    }

    @Test
    public void taskToString_SubTaskToStringWhenSubTaskWithAllFieldsThenReturnCSVString() {
        String result = CSVMapper.taskToString(subTask);
        assertThat(result).isEqualTo("3,SUBTASK,SubTask1,IN_PROGRESS,This is a subtask,30," + subTask.getStartTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ",2");
    }

    @Test
    public void taskToString_SubTaskToStringWhenSubTaskWithNullOptionalFieldsThenReturnCSVString() {
        subTask.setDuration(0);
        subTask.setStartTime(null);
        String result = CSVMapper.taskToString(subTask);
        assertThat(result).isEqualTo("3,SUBTASK,SubTask1,IN_PROGRESS,This is a subtask,0, ,2");
    }

    @Test
    public void taskToString_EpicTaskToStringWhenSubTaskWithAllFieldsThenReturnCSVString() {
        String result = CSVMapper.taskToString(epicTask);
        assertThat(result).isEqualTo("2,EPICTASK,EpicTask1,IN_PROGRESS,This is a epic task,30," + epicTask.getStartTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void taskToString_EpicTaskToStringWhenSubTaskWithNullOptionalFieldsThenReturnCSVString() {
        epicTask.setDuration(0);
        epicTask.setStartTime(null);
        String result = CSVMapper.taskToString(epicTask);
        assertThat(result).isEqualTo("2,EPICTASK,EpicTask1,IN_PROGRESS,This is a epic task,0, ");
    }
}