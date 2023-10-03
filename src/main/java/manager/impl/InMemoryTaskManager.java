package manager.impl;

import manager.api.TaskManager;
import task.EpicTask;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private long id = 1;
    private Map<Long, Task> tasks = new HashMap<>();
    private Map<Long, EpicTask> epicTasks = new HashMap<>();
    private Map<Long, SubTask> subTasks = new HashMap<>();
    protected LocalDateTime startYear = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
    protected LocalDateTime endYear = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
    private TreeSet<Task> orderTasksByStartTime = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null) {
            return 1;
        } else if (o2.getStartTime() == null) {
            return -1;
        } else if (o1.getStartTime().equals(o2.getStartTime())) {
            return 0;
        } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else {
            return 1;
        }
    });
    protected TreeMap<Long, Long> timeTableByYear = new TreeMap<>(generateTimeTable());
    public InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    private long generateId() {
        return id++;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    protected TreeMap<Long, Long> generateTimeTable() {
        TreeMap<Long, Long> timeTableByYearPer15Min = new TreeMap<>();
        long pointFromStartYear = 0;
        LocalDateTime startOfYear = startYear;
        LocalDateTime endOfYear = endYear;
        Duration durationForYear = Duration.between(startOfYear, endOfYear);

        while (pointFromStartYear <= durationForYear.toMinutes()) {
            timeTableByYearPer15Min.put(pointFromStartYear, 0L);
            pointFromStartYear += 15;
        }

        return timeTableByYearPer15Min;
    }

    protected Boolean checkIntersections(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        LocalDateTime startTask = task.getStartTime();
        LocalDateTime endTask = task.getEndTime();

        long startPoint = timeTableByYear.ceilingKey(Duration.between(startYear, startTask).toMinutes());
        long endPoint = timeTableByYear.floorKey(Duration.between(startYear, endTask).toMinutes());

        return timeTableByYear.get(startPoint) == 0 && timeTableByYear.get(endPoint) == 0;
    }

    protected void removeTaskFromTimeTable(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        long sizeOfIntervalInMinBetweenCells = 15;
        LocalDateTime startTask = task.getStartTime();
        LocalDateTime endTask = task.getEndTime();

        long startPoint = timeTableByYear.ceilingKey(Duration.between(startYear, startTask).toMinutes());
        long endPoint = timeTableByYear.floorKey(Duration.between(startYear, endTask).toMinutes());

        while (startPoint <= endPoint) {
            timeTableByYear.put(startPoint, 0L);
            startPoint += sizeOfIntervalInMinBetweenCells;
        }
    }

    protected void addTaskToTimeTable(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        long sizeOfIntervalInMinBetweenCells = 15;
        LocalDateTime startTask = task.getStartTime();
        LocalDateTime endTask = task.getEndTime();

        long startPoint = timeTableByYear.ceilingKey(Duration.between(startYear, startTask).toMinutes());
        long endPoint = timeTableByYear.floorKey(Duration.between(startYear, endTask).toMinutes());

        while (startPoint <= endPoint) {
            timeTableByYear.put(startPoint, task.getId());
            startPoint += sizeOfIntervalInMinBetweenCells;
        }
    }

    private void deleteSubTaskFromEpic(SubTask subTask) {
        EpicTask epicTask = epicTasks.get(subTask.getIdEpicTask());
        List<Long> subtaskId = epicTask.getSubTasksId();
        subtaskId.remove(subTask.getId());
        epicTask.setSubTasksId(subtaskId);
        checkStatusEpicTask(epicTask);
        updateEpicTimeParameters(epicTask);
        epicTasks.put(epicTask.getId(), epicTask);
    }

    private void updateEpicTimeParameters(EpicTask epicTask) {
        Optional<SubTask> firstTime = epicTask.getSubTasksId().stream()
                .map(subtaskId -> subTasks.get(subtaskId))
                .filter(subTask -> subTask.getStartTime() != null)
                .min(Comparator.comparing(Task::getStartTime));

        firstTime.ifPresent(subTask -> epicTask.setStartTime(subTask.getStartTime()));

        Optional<SubTask> lastSubtask = epicTask.getSubTasksId().stream()
                .map(subtaskId -> subTasks.get(subtaskId))
                .filter(subTask -> subTask.getStartTime() != null)
                .max(Comparator.comparing(Task::getStartTime));

        Optional<Duration> sumDuration = lastSubtask.stream()
                .map(Task::getEndTime)
                .map(endTime1 -> Duration.between(firstTime.get().getStartTime(), endTime1))
                .findFirst();

        sumDuration.ifPresent(duration -> epicTask.setDuration(duration.toMinutes()));
    }

    public ArrayList<Task> getOrderedTasksByStartTime() {
        orderTasksByStartTime.addAll(tasks.values());
        orderTasksByStartTime.addAll(epicTasks.values());
        orderTasksByStartTime.addAll(subTasks.values());

        return new ArrayList<>(orderTasksByStartTime);
    }

    @Override
    public Map<Long, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    @Override
    public void setEpicTasks(Map<Long, EpicTask> epicTasks) {
        if (epicTasks != null) {
            this.epicTasks = epicTasks;
        }
    }

    @Override
    public Map<Long, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public void setSubTasks(Map<Long, SubTask> subTasks) {
        if (subTasks != null) {
            this.subTasks = subTasks;
        }
    }

    @Override
    public Map<Long, Task> getTasks() {
        return tasks;
    }

    @Override
    public void setTasks(Map<Long, Task> tasks) {
        if (tasks != null) {
            this.tasks = tasks;
        }
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
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
        }

        return Optional.ofNullable(tasks.get(id));
    }

    /**
     * Ищет по Id объект, при нахождении в subTasks возвращает.
     * Вызывает метод add у объекта типа InMemoryHistoryManager.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    @Override
    public Optional<SubTask> getByIdSubTask(long id) {
        if (subTasks.get(id) != null) {
            historyManager.add(subTasks.get(id));
        }
        return Optional.ofNullable(subTasks.get(id));
    }

    /**
     * Ищет по Id объект, при нахождении в epicTasks возвращает.
     * Вызывает метод add у объекта типа InMemoryHistoryManager.
     *
     * @param id - объекта, который нужно найти и вернуть.
     */
    @Override
    public Optional<EpicTask> getByIdEpicTask(long id) {
        if (epicTasks.get(id) != null) {
            historyManager.add(epicTasks.get(id));
        }
        return Optional.ofNullable(epicTasks.get(id));
    }

    /**
     * Генерирует для task id и добавляет в tasks значение task по ключу Id.
     *
     * @param task
     */
    @Override
    public void addNewTask(Task task) {
        if (task == null) {
            throw new RuntimeException("Empty value passed");
        }
        if (checkIntersections(task)) {
            task.setId(generateId());
            addTaskToTimeTable(task);
            tasks.put(task.getId(), task);
        }
    }

    /**
     * Генерирует для epicTask id и добавляет в epicTasks значение task по ключу Id.
     *
     * @param epicTask
     */
    @Override
    public void addNewEpicTask(EpicTask epicTask) {
        if (epicTask == null) {
            throw new RuntimeException("Empty value passed");
        }
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
        if (subTask == null) {
            throw new RuntimeException("Empty value passed");
        }
        if (checkIntersections(subTask)) {
            long idEpicTask = subTask.getIdEpicTask();

            subTask.setId(generateId());
            addTaskToTimeTable(subTask);
            subTasks.put(subTask.getId(), subTask);
            epicTasks.get(idEpicTask).addSubTask(subTask.getId());
            updateEpicTimeParameters(epicTasks.get(idEpicTask));
            checkStatusEpicTask(epicTasks.get(idEpicTask));
        }
    }

    /**
     * Проверяет на null tasks, ищет объект с равным Id и найденный объект заменяет на updatedTask.
     *
     * @param updatedTask - обновленный объект, который нужно вставить при совпадении Id вместо найденного в tasks.
     */
    @Override
    public void updateTask(Task updatedTask) {
        if (tasks != null && tasks.containsKey(updatedTask.getId())) {
            Task oldTask = tasks.get(updatedTask.getId());
            removeTaskFromTimeTable(oldTask);
            if (checkIntersections(updatedTask)) {
                tasks.put(updatedTask.getId(), updatedTask);
                addTaskToTimeTable(updatedTask);
                System.out.println("Задача обновлена");
            } else {
                addTaskToTimeTable(oldTask);
                throw new RuntimeException("Task time overlaps with an existing task");
            }

        } else {
            throw new RuntimeException("Task not found");
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
            SubTask oldTask = subTasks.get(updatedTask.getId());
            removeTaskFromTimeTable(oldTask);
            if (checkIntersections(updatedTask)) {
                EpicTask epicTask = epicTasks.get(updatedTask.getIdEpicTask());

                addTaskToTimeTable(updatedTask);
                subTasks.put(updatedTask.getId(), updatedTask);
                checkStatusEpicTask(epicTask);
                updateEpicTimeParameters(epicTask);
                System.out.println("Задача обновлена");
            } else {
                addTaskToTimeTable(oldTask);
                throw new RuntimeException("Task time overlaps with an existing task");
            }
        } else {
            throw new RuntimeException("Task not found");
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
            List<Long> subTasksId = updatedTask.getSubTasksId();

            if (!subTasksId.isEmpty()) {
                for (Long idSubtask : subTasksId) {
                    updateSubTask(subTasks.get(idSubtask));
                }
            }

            epicTasks.put(updatedTask.getId(), updatedTask);
            System.out.println("Задача обновлена");
        } else {
            throw new RuntimeException("Task not found");
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
            removeTaskFromTimeTable(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
            System.out.println("Task deleted by id  = " + id);
        } else {
            throw new RuntimeException("Task not found");
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
            removeTaskFromTimeTable(subTasks.get(id));
            deleteSubTaskFromEpic(subTasks.get(id));
            subTasks.remove(id);
            historyManager.remove(id);
            System.out.println("Task deleted by id  = " + id);
        } else {
            throw new RuntimeException("Task not found");
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
            historyManager.remove(id);

            if (!listSubTasks.isEmpty()) {
                for (Long idSubtask : listSubTasks) {
                    removeTaskFromTimeTable(subTasks.get(idSubtask));
                    subTasks.remove(idSubtask);
                    historyManager.remove(idSubtask);
                }
            }

            System.out.println("Task deleted by id  = " + id);
        } else {
            throw new RuntimeException("Task not found");
        }
    }
}
