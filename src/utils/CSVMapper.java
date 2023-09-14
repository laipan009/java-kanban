package utils;

import task.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CSVMapper {
    private CSVMapper() {
    }

    /**
     * Преобразует объект типа Task в строку в формате CSV.
     *
     * @param task
     * @return
     */
    public static String taskToString(Task task) {
        String id = String.valueOf(task.getId());
        String type = task.getClass().getSimpleName().toUpperCase();
        String status = task.getStatus().toString();

        if (task instanceof SubTask) {
            String idEpic = String.valueOf(((SubTask) task).getIdEpicTask());
            return String.join(",", id, type, task.getName(), status, task.getDescription(), idEpic);
        } else {
            return String.join(",", id, type, task.getName(), status, task.getDescription());
        }
    }

    /**
     * Преобразует историю просмотра задач в строку в формате CSV.
     *
     * @param historyTasks лист содержащий историю просмотренных задач.
     * @return
     */
    public static String getHistoryToString(List<Task> historyTasks) {
        StringBuilder resultString = new StringBuilder();
        for (Task task : historyTasks) {
            resultString.append(task.getId());
            resultString.append(",");
        }
        return resultString.toString();
    }

    /**
     * Возвращает из файла формата CSV List с элементами String содержащими поля объектов типа Task для восстановления.
     * Первый(индекс 0) элемент содержит название столбцов, предпоследний пустой, последний историю просмотров задач.
     *
     * @param path к файлу содержащий поля объектов типа Task в CSV.
     * @return Лист, каждый элемент которого содержит поля объекта типа Task.
     */
    public static List<String> getLinesFromFile(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Парсит строку формата CSV и в зависимости от type возвращает соответствующий объект.
     *
     * @param value элемент содержащий поля объекта типа Task.
     * @return объект типа Task, EpicTask или SubTask.
     */
    public static Optional<Task> fromString(String value) {
        if (value != null) {
            String[] taskAttributes = value.split(",");
            long id = Long.parseLong(taskAttributes[0]);
            TaskType type = TaskType.valueOf(taskAttributes[1]);
            String name = taskAttributes[2];
            TaskStatus status = TaskStatus.valueOf(taskAttributes[3]);
            String description = taskAttributes[4];
            Task task;
            switch (type) {
                case EPICTASK:
                    task = new EpicTask(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    return Optional.ofNullable(task);
                case TASK:
                    task = new Task(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    return Optional.ofNullable(task);
                case SUBTASK:
                    long idEpic = Long.parseLong(taskAttributes[5]);
                    task = new SubTask(name, description, idEpic);
                    task.setId(id);
                    task.setStatus(status);
                    return Optional.ofNullable(task);
            }
        }
        return Optional.empty();
    }

    /**
     * tasksInlines содержит историю в последнем элементе, поэтому метод парсит в тип Long именно lastLineIsHistory.
     *
     * @param tasksInlines Лист полученный из метода getLinesFromFile.
     * @return Лист Id объектов содержащихся в истории просмотра задач.
     */
    public static List<Long> historyFromString(List<String> tasksInlines) {
        String lastLineIsHistory = tasksInlines.get(tasksInlines.size() - 1);
        String[] history = lastLineIsHistory.split(",");
        List<Long> historyList = new ArrayList<>();

        for (String idTask : history) {
            historyList.add(Long.parseLong(idTask));
        }
        return historyList;
    }
}
