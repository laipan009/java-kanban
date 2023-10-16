package utils;

import task.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CSVMapper {
    public static final String COLUMN_HEADER = "id,type,name,status,description,duration,start_Time,epic";

    private CSVMapper() {
    }

    /**
     * Преобразует объект типа Task в строку в формате CSV.
     *
     * @param task объект, который будет преобразовываться в строку.
     * @return строку в формате CSV.
     */
    public static String taskToString(Task task) {
        String id = String.valueOf(task.getId());
        String type = task.getTaskType().toString();
        String status = task.getStatus().toString();
        String duration = String.valueOf(task.getDuration());
        String startTime = Optional.ofNullable(task.getStartTime())
                .map(time -> time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(" ");

        if (task.getTaskType() == TaskType.SUBTASK) {
            String idEpic = String.valueOf(((SubTask) task).getIdEpicTask());
            return String.join(",", id, type, task.getName(), status, task.getDescription(), duration,
                    startTime, idEpic);
        } else {
            return String.join(",", id, type, task.getName(), status, task.getDescription(), duration,
                    startTime);
        }
    }

    /**
     * Преобразует историю просмотра задач в строку в формате CSV.
     *
     * @param historyTasks лист содержащий историю просмотренных задач.
     * @return строку в формате CSV.
     */
    public static String getHistoryToString(List<Task> historyTasks) {
        if (historyTasks.isEmpty()) {
            return " ";
        }
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
            List<String> lines = Files.readAllLines(Paths.get(path));
            if (lines.isEmpty()) {
                throw new NotValidFileException("File is Empty");
            } else if (lines.get(0).equals(COLUMN_HEADER)) {
                return lines;
            } else {
                throw new NotValidFileException("Not valid file format");
            }
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
            long duration = Long.parseLong(taskAttributes[5]);
            LocalDateTime startTime = taskAttributes[6].isBlank() ? null : LocalDateTime.parse(taskAttributes[6]);
            Task task;
            switch (type) {
                case EPICTASK:
                    task = new EpicTask(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    task.setDuration(duration);
                    task.setStartTime(startTime);
                    return Optional.of(task);
                case TASK:
                    task = new Task(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    task.setDuration(duration);
                    task.setStartTime(startTime);
                    return Optional.of(task);
                case SUBTASK:
                    long idEpic = Long.parseLong(taskAttributes[7]);
                    task = new SubTask(name, description, idEpic);
                    task.setId(id);
                    task.setStatus(status);
                    task.setDuration(duration);
                    task.setStartTime(startTime);
                    return Optional.of(task);
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
        List<Long> historyList = new ArrayList<>();
        if (tasksInlines.isEmpty() || tasksInlines.get(tasksInlines.size() - 1).isBlank()) {
            return historyList;
        }
        String lastLineIsHistory = tasksInlines.get(tasksInlines.size() - 1);
        String[] history = lastLineIsHistory.split(",");
        historyList = new ArrayList<>();

        for (String idTask : history) {
            historyList.add(Long.parseLong(idTask));
        }
        return historyList;
    }
}
