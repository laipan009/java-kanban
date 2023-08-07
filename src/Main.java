import manager.InMemoryHistoryManager;
import task.EpicTask;
import manager.InMemoryTaskManager;
import task.SubTask;
import taskstatus.TaskStatus;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        EpicTask epicTask1 = new EpicTask("Присед 170 кг", "В рамках этой задачи мы присядем 170 кг");
        inMemoryTaskManager.addNewEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Присед 120 кг", "В рамках этой задачи мы присядем 120 кг", epicTask1.getId());
        inMemoryTaskManager.addNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("Присед 140 кг", "В рамках этой задачи мы присядем 140 кг", epicTask1.getId());
        inMemoryTaskManager.addNewSubTask(subTask2);

        EpicTask epicTask2 = new EpicTask("Жим 140 кг", "В рамках этой задачи мы выжмем 140 кг");
        inMemoryTaskManager.addNewEpicTask(epicTask2);
        SubTask subTask3 = new SubTask("Жим 110 кг", "В рамках этой задачи мы выжмем 140 кг", epicTask2.getId());
        inMemoryTaskManager.addNewSubTask(subTask3);

        System.out.println(inMemoryTaskManager.getEpicTasks());

        subTask3.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubTask(subTask3);

        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubTask(subTask1);

        System.out.println(inMemoryTaskManager.getEpicTasks());
        System.out.println(inMemoryTaskManager.getSubTasks());
        inMemoryTaskManager.deleteByIdEpicTasks(epicTask1.getId());
        System.out.println(inMemoryTaskManager.getEpicTasks());
        System.out.println(inMemoryTaskManager.getSubTasks());

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        System.out.println(historyManager.getHistory().get());

        Optional<EpicTask> epicTask22 = inMemoryTaskManager.getByIdEpicTask(epicTask2.getId());
        historyManager.add(epicTask2);
        Optional<EpicTask> epicTask = inMemoryTaskManager.getByIdEpicTask(epicTask1.getId());
        historyManager.add(epicTask1);
        Optional<SubTask> subTaskTask = inMemoryTaskManager.getByIdSubTask(subTask1.getId());
        historyManager.add(subTask1);
        Optional<SubTask> subTaskTaskD = inMemoryTaskManager.getByIdSubTask(subTask1.getId());
        historyManager.add(subTask1);
        Optional<SubTask> subTaskTask22 = inMemoryTaskManager.getByIdSubTask(subTask2.getId());
        historyManager.add(subTask2);

        System.out.println(historyManager.getHistory().get());
    }
}