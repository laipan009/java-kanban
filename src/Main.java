import manager.impl.FileBackedTasksManager;
import task.EpicTask;
import task.SubTask;

import java.io.File;
import java.util.Optional;

public class Main {
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