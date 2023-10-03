package manager.impl;

import task.EpicTask;
import task.SubTask;
import task.Task;
import utils.CSVMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private String pathToFile;

    public FileBackedTasksManager(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        if (pathToFile != null) {
            this.pathToFile = pathToFile;
        }
    }

    public String getPathToFile() {
        return pathToFile;
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы.
     *
     * @param file содержащий данные для восстановления.
     * @return восстановленный из файла объект FileBackedTasksManager.
     */
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fBTManager = new FileBackedTasksManager(file.getPath());
        List<String> list = CSVMapper.getLinesFromFile(fBTManager.getPathToFile());
        List<Long> historyList = CSVMapper.historyFromString(list);

        fBTManager.restoreTasksByType(list);
        fBTManager.setNewIdValue(fBTManager.getEpicTasks(), fBTManager.getSubTasks(), fBTManager.getTasks());
        combineEpicAndSubTasks(fBTManager.getEpicTasks(), fBTManager.getSubTasks());
        restoreHistory(historyList, fBTManager);

        return fBTManager;
    }

    /**
     * Сохраняет текущее состояние менеджера в файл указанный в поле pathToFile. Вызывается в модифицирующих методах.
     * В методах getById, getByIdEpicTask, getByIdSubTask после их вызова в main.
     * Первая строка содержит название столбцов, предпоследняя пустая, последняя историю просмотров задач.
     */
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
            writer.write("id,type,name,status,description,duration,start_Time,epic\n");
            Map<Long, ? extends Task> tasks = getTasks();
            for (Long id : tasks.keySet()) {
                String taskInString = CSVMapper.taskToString(tasks.get(id));
                writer.write(taskInString + "\n");
            }
            tasks = getEpicTasks();
            for (Long id : tasks.keySet()) {
                String taskInString = CSVMapper.taskToString(tasks.get(id));
                writer.write(taskInString + "\n");
            }
            tasks = getSubTasks();
            for (Long id : tasks.keySet()) {
                String taskInString = CSVMapper.taskToString(tasks.get(id));
                writer.write(taskInString + "\n");
            }
            writer.write("\n" + CSVMapper.getHistoryToString(historyManager.getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
    }

    /**
     * Восстанавливает объекты типа Task из строк типа CSV.
     * В зависимости от типа объекта добавляет объект в поля tasks, epicTasks, subTasks.
     * Цикл начинается со второго элемента и заканчивается на предпоследнем не включительно.
     *
     * @param tasksInlines Лист с элементами содержащими поля объектов типа Task из метода getLinesFromFile.
     *                     Первый(индекс 0) элемент содержит название столбцов, предпоследний пустой, последний историю просмотров задач.
     */
    public void restoreTasksByType(List<String> tasksInlines) {
        int countLastEmptyLines = 2;
        int countColumnNameLines = 1;
        Map<Long, Task> tasks = new HashMap<>();
        Map<Long, EpicTask> epicTasks = new HashMap<>();
        Map<Long, SubTask> subTasks = new HashMap<>();

        for (int i = countColumnNameLines; i < tasksInlines.size() - countLastEmptyLines; i++) {
            Optional<Task> taskInOpt = CSVMapper.fromString(tasksInlines.get(i));
            if (taskInOpt.isEmpty()) {
                System.out.println("Object might not find");
            } else {
                var task = taskInOpt.get();
                addTaskToTimeTable(task);
                switch (task.getTaskType()) {
                    case TASK -> tasks.put(task.getId(), task);
                    case EPICTASK -> epicTasks.put(task.getId(), (EpicTask) task);
                    case SUBTASK -> subTasks.put(task.getId(), (SubTask) task);
                }
            }
        }
        setTasks(tasks);
        setEpicTasks(epicTasks);
        setSubTasks(subTasks);
    }

    /**
     * Мёрджит Set'ы с Id задач, находит максимальную и устанавливает в поле idGenerator.
     * Вызывается после restoreTasksByType, что бы генерация Id продолжилась с последней.
     *
     * @param epicTasks поле содержащее задачи типа EpicTask.
     * @param subTasks  поле содержащее задачи типа SubTask.
     * @param tasks     поле содержащее задачи типа Task.
     */
    private void setNewIdValue(Map<Long, EpicTask> epicTasks, Map<Long, SubTask> subTasks, Map<Long, Task> tasks) {
        TreeSet<Long> tasksIds = new TreeSet<>(tasks.keySet());
        long maxId;

        tasksIds.addAll(epicTasks.keySet());
        tasksIds.addAll(subTasks.keySet());
        if (!tasksIds.isEmpty()) {
            maxId = tasksIds.last();
            setIdGenerator(maxId);
        }
    }

    /**
     * Заполняет поле idSubTask у epicTask. Вызывается после метода restoreTasksByType.
     *
     * @param epicTasks поле содержащее задачи типа EpicTask.
     * @param subTasks  поле содержащее задачи типа SubTask.
     */
    public static void combineEpicAndSubTasks(Map<Long, EpicTask> epicTasks, Map<Long, SubTask> subTasks) {
        for (Long idSubTask : subTasks.keySet()) {
            long idEpic = subTasks.get(idSubTask).getIdEpicTask();
            if (epicTasks.containsKey(idEpic)) {
                epicTasks.get(idEpic).addSubTask(idSubTask);
            }
        }
    }

    /**
     * Восстанавливает историю просмотра задач.
     *
     * @param historyList       лист задач полученный из метода CSVLoader.historyFromString.
     * @param fileBackedManager объект полученный из метода loadFromFile.
     */
    public static void restoreHistory(List<Long> historyList, FileBackedTasksManager fileBackedManager) {
        for (Long idTask : historyList) {
            if (fileBackedManager.getTasks().containsKey(idTask)) {
                fileBackedManager.historyManager.add(fileBackedManager.getTasks().get(idTask));
            } else if (fileBackedManager.getEpicTasks().containsKey(idTask)) {
                fileBackedManager.historyManager.add(fileBackedManager.getEpicTasks().get(idTask));
            } else {
                fileBackedManager.historyManager.add(fileBackedManager.getSubTasks().get(idTask));
            }
        }
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpicTask(EpicTask epicTask) {
        super.addNewEpicTask(epicTask);
        save();
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask updatedTask) {
        super.updateSubTask(updatedTask);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask updatedTask) {
        super.updateEpicTask(updatedTask);
        save();
    }

    @Override
    public void checkStatusEpicTask(EpicTask epicTask) {
        super.checkStatusEpicTask(epicTask);
        save();
    }

    @Override
    public void deleteByIdTask(long id) {
        super.deleteByIdTask(id);
        save();
    }

    @Override
    public void deleteByIdSubTask(long id) {
        super.deleteByIdSubTask(id);
        save();
    }

    @Override
    public void deleteByIdEpicTasks(long id) {
        super.deleteByIdEpicTasks(id);
        save();
    }
}
