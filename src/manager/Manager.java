package manager;

import epic.task.EpicTask;
import subtask.SubTask;
import task.Task;
import taskstatus.TaskStatus;
import java.util.*;

public class Manager {
    private Map<UUID, Task> tasks = new HashMap<>();
    private Map<UUID, EpicTask> epicTasks = new HashMap<>();
    private Map<UUID, SubTask> subTasks = new HashMap<>();


    public Map<UUID, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public void setEpicTasks(Map<UUID, EpicTask> epicTasks) {
        this.epicTasks = epicTasks;
    }

    public Map<UUID, SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Map<UUID, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public Map<UUID, Task> getTasks() {
        return tasks;
    }

    public void setTasks(Map<UUID, Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Проверяет есть ли в epicTasks задача с epicId, если есть, то ищет в subTasks элементы с epicId и добавляет в result.
     *
     * @param epicId - идентификатор EpicTask по которому нужно вернуть subTasks.
     * @return - список subTasks с полем UUID idEpicTask равным epicId.
     */
    public List<SubTask> getSubTasksByEpicId(UUID epicId) {
        List<SubTask> result = new ArrayList<>();
        if (epicTasks != null && epicTasks.containsKey(epicId)) {
            for (UUID idSubtask : subTasks.keySet()) {
                if (subTasks.get(idSubtask).getIdEpicTask() == epicId) {
                    result.add(subTasks.get(idSubtask));
                }
            }
        }
        return result;
    }

    /**
     * Удаляет все элементы tasks.
     */
    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Задачи удалены");
    }

    /**
     * Удаляет все элементы subTasks.
     */
    public void removeAllSubTasks() {
        subTasks.clear();
        System.out.println("Задачи удалены");
    }

    /**
     * Удаляет все элементы epicTask и соответственно subTasks.
     */
    public void removeAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Задачи удалены");
    }

    /**
     * Ищет по Id объект, при нахождении в tasks возвращает.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    public Optional<Task> getByIdTask(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /**
     * Ищет по Id объект, при нахождении в subTasks возвращает.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    public Optional<SubTask> getByIdSubTask(UUID id) {
        return Optional.ofNullable(subTasks.get(id));
    }

    /**
     * Ищет по Id объект, при нахождении в epicTasks возвращает.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    public Optional<EpicTask> getByIdEpicTask(UUID id) {
        return Optional.ofNullable(epicTasks.get(id));
    }

    /**
     * Добавляет в tasks значение task по ключу Id.
     *
     * @param task
     */
    public void addNewTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /**
     * Добавляет в epicTasks значение epicTask по ключу Id.
     *
     * @param epicTask
     */
    public void addNewEpicTask(EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
    }

    /**
     * Добавляет в subTasks значение subTask по ключу Id и добавляет Id в список subTasks соответствующего epicTasks.
     *
     * @param subTask
     */
    public void addNewSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        epicTasks.get(subTask.getIdEpicTask()).addSubTask(subTask.getId());
    }


    /**
     * Проверяет на null tasks, ищет объект с равным Id и найденный объект заменяет на updatedTask.
     *
     * @param updatedTask - обновленный объект, который нужно вставить при совпадении Id вместо найденного в tasks.
     */
    public void updateTask(Task updatedTask) {
        if (tasks != null && tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
            System.out.println("Задача обновлена");
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    /**
     * Проверяет на null subTasks, ищет объект с равным Id и найденный объект заменяет на updatedTask.
     * Вызывает метод checkStatusEpicTask для EpicTask к которому updatedTask является SubTask.
     *
     * @param updatedTask - обновленный объект, который нужно вставить при совпадении Id вместо найденного в tasks.
     */
    public void updateSubTask(SubTask updatedTask) {
        if (subTasks != null && subTasks.containsKey(updatedTask.getId())) {
            subTasks.put(updatedTask.getId(), updatedTask);
            System.out.println("Задача обновлена");
            checkStatusEpicTask(epicTasks.get(updatedTask.getIdEpicTask()));
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    /**
     * Проверяет на null epicTasks, ищет объект с равным Id и найденный объект заменяет на updatedTask.
     *
     * @param updatedTask - обновленный объект, который нужно вставить при совпадении Id вместо найденного в tasks.
     */
    public void updateEpicTask(EpicTask updatedTask) {
        if (epicTasks != null && epicTasks.containsKey(updatedTask.getId())) {
            epicTasks.put(updatedTask.getId(), updatedTask);
            System.out.println("Задача обновлена");
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    public void checkStatusEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            List<UUID> listSubTasks = epicTask.getSubTasks();
            int doneStatus = 0;
            int inProgressStatus = 0;
            int newStatus = 0;

            for (UUID idSubTask : listSubTasks) {
                TaskStatus status = subTasks.get(idSubTask).getStatus();
                if (status == TaskStatus.NEW) {
                    newStatus++;
                } else if (status == TaskStatus.DONE) {
                    doneStatus++;
                } else {
                    inProgressStatus++;
                }
            }
            if (doneStatus == 0 && inProgressStatus == 0 || listSubTasks.isEmpty()) {
                epicTask.setStatus(TaskStatus.NEW);
            } else if (doneStatus > 0 && inProgressStatus == 0 && newStatus == 0) {
                epicTask.setStatus(TaskStatus.DONE);
            } else {
                epicTask.setStatus(TaskStatus.IN_PROGRESS);
            }

        }


    }

    /**
     * Проверяет на null tasks, ищет объект с равным  Id и удаляет из tasks.
     *
     * @param id - объекта, который нужно найти и удалить из tasks.
     */
    public void deleteByIdTask(UUID id) {
        if (tasks != null && tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Task deleted by id  = " + id);
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    /**
     * Проверяет на null subTasks, ищет объект с равным  Id и удаляет из subTasks.
     *
     * @param id - объекта, который нужно найти и удалить из subTasks.
     */
    public void deleteByIdSubTask(UUID id) {
        if (subTasks != null && subTasks.containsKey(id)) {
            subTasks.remove(id);
            System.out.println("Task deleted by id  = " + id);
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    /**
     * Проверяет на null epicTasks, ищет объект с равным Id и удаляет из epicTasks.
     * Также удаляет из subTasks по ключу subTask'и содержащиеся в поле subTasks EpicTask.
     *
     * @param id - объекта, который нужно найти и удалить из epicTasks.
     */
    public void deleteByIdEpicTasks(UUID id) {
        if (epicTasks != null && epicTasks.containsKey(id)) {
            List<UUID> listSubTasks = epicTasks.get(id).getSubTasks();
            epicTasks.remove(id);

            if (listSubTasks != null) {
                for (UUID idSubTask : listSubTasks) {
                    subTasks.remove(idSubTask);
                }
            }

            System.out.println("Task deleted by id  = " + id);
        } else {
            System.out.println("Такой задачи нет");
        }
    }
}
