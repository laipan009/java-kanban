package manager;

import task.EpicTask;
import task.SubTask;
import task.Task;
import taskstatus.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private long idGenerator = 1;
    private Map<Long, Task> tasks = new HashMap<>();
    private Map<Long, EpicTask> epicTasks = new HashMap<>();
    private Map<Long, SubTask> subTasks = new HashMap<>();

    @Override
    public Map<Long, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    @Override
    public void setEpicTasks(Map<Long, EpicTask> epicTasks) {
        this.epicTasks = epicTasks;
    }

    @Override
    public Map<Long, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public void setSubTasks(Map<Long, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public Map<Long, Task> getTasks() {
        return tasks;
    }

    @Override
    public long generateId() {
        return idGenerator++;
    }

    @Override
    public void setTasks(Map<Long, Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Проверяет есть ли в epicTasks задача с epicId, если есть, то ищет в subTasks элементы с epicId и добавляет в result.
     *
     * @param epicId - идентификатор EpicTask по которому нужно вернуть subTasks.
     * @return - список subTasks с полем UUID idEpicTask равным epicId.
     */
    @Override
    public List<SubTask> getSubTasksByEpicId(long epicId) {
        List<SubTask> result = new ArrayList<>();
        if (epicTasks != null && epicTasks.containsKey(epicId)) {
            for (long idSubtask : subTasks.keySet()) {
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
    @Override
    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Задачи удалены");
    }

    /**
     * Удаляет все элементы subTasks.
     */
    @Override
    public void removeAllSubTasks() {
        subTasks.clear();
        System.out.println("Задачи удалены");
    }

    /**
     * Удаляет все элементы epicTask и соответственно subTasks.
     */
    @Override
    public void removeAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
        System.out.println("Задачи удалены");
    }

    /**
     * Ищет по Id объект, при нахождении в tasks возвращает.
     * Вызывает метод add у объекта типа InMemoryHistoryManager.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    @Override
    public Optional<Task> getById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /**
     * Ищет по Id объект, при нахождении в subTasks возвращает.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    @Override
    public Optional<SubTask> getByIdSubTask(long id) {
        return Optional.ofNullable(subTasks.get(id));
    }

    /**
     * Ищет по Id объект, при нахождении в epicTasks возвращает.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    @Override
    public Optional<EpicTask> getByIdEpicTask(long id) {
        return Optional.ofNullable(epicTasks.get(id));
    }

    /**
     * Генерирует для task id и добавляет в tasks значение task по ключу Id.
     *
     * @param task
     */
    @Override
    public void addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    /**
     * Генерирует для epicTask id и добавляет в epicTasks значение task по ключу Id.
     *
     * @param epicTask
     */
    @Override
    public void addNewEpicTask(EpicTask epicTask) {
        epicTask.setId(generateId());
        epicTasks.put(epicTask.getId(), epicTask);
    }

    /**
     * Добавляет в subTasks значение subTask по ключу Id и добавляет Id в список subTasks соответствующего epicTasks.
     *
     * @param subTask
     */
    @Override
    public void addNewSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        epicTasks.get(subTask.getIdEpicTask()).addSubTask(subTask.getId());
    }

    /**
     * Проверяет на null tasks, ищет объект с равным Id и найденный объект заменяет на updatedTask.
     *
     * @param updatedTask - обновленный объект, который нужно вставить при совпадении Id вместо найденного в tasks.
     */
    @Override
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
    @Override
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
    @Override
    public void updateEpicTask(EpicTask updatedTask) {
        if (epicTasks != null && epicTasks.containsKey(updatedTask.getId())) {
            epicTasks.put(updatedTask.getId(), updatedTask);
            System.out.println("Задача обновлена");
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    /**
     * Проверяет на null epicTasks, считает статусы в subTasks, которые соответствуют epicTask и изменяет статус
     * объекта в зависимости от условий.
     *
     * @param epicTask - объект в котором нужно проверить статус subTasks и изменить статус самого объекта.
     */
    @Override
    public void checkStatusEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            List<Long> listSubTasks = epicTask.getSubTasksId();
            int doneStatus = 0;
            int inProgressStatus = 0;
            int newStatus = 0;

            for (Long idSubTask : listSubTasks) {
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
    @Override
    public void deleteByIdTask(long id) {
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
    @Override
    public void deleteByIdSubTask(long id) {
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
    @Override
    public void deleteByIdEpicTasks(long id) {
        if (epicTasks != null && epicTasks.containsKey(id)) {
            List<Long> listSubTasks = epicTasks.get(id).getSubTasksId();
            epicTasks.remove(id);

            if (listSubTasks != null) {
                for (Long idSubTask : listSubTasks) {
                    subTasks.remove(idSubTask);
                }
            }

            System.out.println("Task deleted by id  = " + id);
        } else {
            System.out.println("Такой задачи нет");
        }
    }
}
