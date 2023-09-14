package manager.impl;

import task.*;
import utils.CSVMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    String pathToFile;

    public FileBackedTasksManager(String pathToFile) {
        this.pathToFile = pathToFile;
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
    static FileBackedTasksManager loadFromFile(File file) {
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
            writer.write("id,type,name,status,description,epic\n");
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
            throw new ManagerSaveException();
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
                switch (task.getClass().getSimpleName()) {
                    case "Task":
                        tasks.put(task.getId(), task);
                        break;
                    case "EpicTask":
                        epicTasks.put(task.getId(), (EpicTask) task);
                        break;
                    case "SubTask":
                        subTasks.put(task.getId(), (SubTask) task);
                        break;
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
     * @param epicTasks
     * @param subTasks
     * @param tasks
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
     * @param epicTasks
     * @param subTasks
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

    public static void main(String[] args) {
        String path = "C:\\Users\\superuser\\IdeaProjects\\java-kanban2\\test.csv";
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);

        EpicTask epicTask1 = new EpicTask("Присед 170 кг", "В  рамках этой задачи мы присядем 170 кг");
        fileBackedTasksManager.addNewEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Присед 120 кг", "В рамках этой задачи мы присядем 120 кг",
                epicTask1.getId());
        fileBackedTasksManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("Присед 140 кг", "В рамках этой задачи мы присядем 140 кг",
                epicTask1.getId());
        fileBackedTasksManager.addNewSubTask(subTask2);

        SubTask subTask3 = new SubTask("Присед 170 кг", "В рамках этой задачи мы присядем 170 кг",
                epicTask1.getId());
        fileBackedTasksManager.addNewSubTask(subTask3);

        EpicTask epicTask2 = new EpicTask("Жим 140 кг", "В рамках этой задачи мы выжмем 140 кг");
        fileBackedTasksManager.addNewEpicTask(epicTask2);

        System.out.println(fileBackedTasksManager.historyManager.getHistory());
        System.out.println(fileBackedTasksManager.historyManager.getHistory().size());

        Optional<EpicTask> ep1 = fileBackedTasksManager.getByIdEpicTask(epicTask1.getId());
        fileBackedTasksManager.save();

        Optional<EpicTask> ep12 = fileBackedTasksManager.getByIdEpicTask(epicTask1.getId());
        fileBackedTasksManager.save();

        Optional<SubTask> st1 = fileBackedTasksManager.getByIdSubTask(subTask1.getId());
        fileBackedTasksManager.save();

        Optional<SubTask> st2 = fileBackedTasksManager.getByIdSubTask(subTask2.getId());
        fileBackedTasksManager.save();

        Optional<SubTask> st3 = fileBackedTasksManager.getByIdSubTask(subTask3.getId());
        fileBackedTasksManager.save();

        Optional<EpicTask> ep2 = fileBackedTasksManager.getByIdEpicTask(epicTask2.getId());
        fileBackedTasksManager.save();

        System.out.println(fileBackedTasksManager.historyManager.getHistory());
        System.out.println(fileBackedTasksManager.historyManager.getHistory().size());

        fileBackedTasksManager.deleteByIdEpicTasks(epicTask1.getId());

        System.out.println(fileBackedTasksManager.historyManager.getHistory());
        System.out.println(fileBackedTasksManager.historyManager.getHistory().size());

        System.out.println(fileBackedTasksManager.getTasks().size());
        System.out.println(fileBackedTasksManager.getEpicTasks().size());
        System.out.println(fileBackedTasksManager.getSubTasks().size());


        FileBackedTasksManager fBTManager = FileBackedTasksManager.loadFromFile(new File(path));

        System.out.println(fBTManager.getTasks().size());
        System.out.println(fBTManager.getEpicTasks().size());
        System.out.println(fBTManager.getSubTasks().size());
        System.out.println(fBTManager.historyManager.getHistory());

    }
}
