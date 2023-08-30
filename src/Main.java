import manager.impl.InMemoryHistoryManager;
import task.EpicTask;
import manager.impl.InMemoryTaskManager;
import task.SubTask;
import task.TaskStatus;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        EpicTask epicTask1 = new EpicTask("Присед 170 кг", "В рамках этой задачи мы присядем 170 кг");
        inMemoryTaskManager.addNewEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Присед 120 кг", "В рамках этой задачи мы присядем 120 кг",
                epicTask1.getId());
        inMemoryTaskManager.addNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("Присед 140 кг", "В рамках этой задачи мы присядем 140 кг",
                epicTask1.getId());
        inMemoryTaskManager.addNewSubTask(subTask2);

        SubTask subTask3 = new SubTask("Присед 170 кг", "В рамках этой задачи мы присядем 170 кг",
                epicTask1.getId());
        inMemoryTaskManager.addNewSubTask(subTask3);

        EpicTask epicTask2 = new EpicTask("Жим 140 кг", "В рамках этой задачи мы выжмем 140 кг");
        inMemoryTaskManager.addNewEpicTask(epicTask2);

        System.out.println(inMemoryTaskManager.historyManager.getHistory());
        System.out.println(inMemoryTaskManager.historyManager.getHistory().size());

        Optional<EpicTask> ep1 = inMemoryTaskManager.getByIdEpicTask(epicTask1.getId());

        Optional<EpicTask> ep12 = inMemoryTaskManager.getByIdEpicTask(epicTask1.getId());

        Optional<SubTask> st1 = inMemoryTaskManager.getByIdSubTask(subTask1.getId());

        Optional<SubTask> st2 = inMemoryTaskManager.getByIdSubTask(subTask2.getId());

        Optional<SubTask> st3 = inMemoryTaskManager.getByIdSubTask(subTask3.getId());

        Optional<EpicTask> ep2 = inMemoryTaskManager.getByIdEpicTask(epicTask2.getId());

        System.out.println(inMemoryTaskManager.historyManager.getHistory());
        System.out.println(inMemoryTaskManager.historyManager.getHistory().size());

        inMemoryTaskManager.deleteByIdEpicTasks(epicTask1.getId());

        System.out.println(inMemoryTaskManager.historyManager.getHistory());
        System.out.println(inMemoryTaskManager.historyManager.getHistory().size());
    }
}