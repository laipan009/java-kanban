package manager;

import task.EpicTask;
import task.SubTask;
import task.Task;

import java.util.*;

public interface TaskManager {

    Map<Long, EpicTask> getEpicTasks();

    void setEpicTasks(Map<Long, EpicTask> epicTasks);

    Map<Long, SubTask> getSubTasks();

    void setSubTasks(Map<Long, SubTask> subTasks);

    Map<Long, Task> getTasks();

    long generateId();

    void setTasks(Map<Long, Task> tasks);

    List<SubTask> getSubTasksByEpicId(long epicId);

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpicTasks();

    Optional<Task> getById(long id);

    Optional<SubTask> getByIdSubTask(long id);

    Optional<EpicTask> getByIdEpicTask(long id);

    void addNewTask(Task task);

    void addNewEpicTask(EpicTask epicTask);

    void addNewSubTask(SubTask subTask);

    void updateTask(Task updatedTask);

    void updateSubTask(SubTask updatedTask);

    void updateEpicTask(EpicTask updatedTask);

    void checkStatusEpicTask(EpicTask epicTask);

    void deleteByIdTask(long id);

    void deleteByIdSubTask(long id);

    void deleteByIdEpicTasks(long id);

}


