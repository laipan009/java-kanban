package main;

import epic.task.EpicTask;
import manager.Manager;
import subtask.SubTask;
import taskstatus.TaskStatus;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        EpicTask epicTask1 = new EpicTask("Присед 170 кг", "В рамках этой задачи мы присядем 170 кг");
        manager.addNewEpicTask(epicTask1);

        SubTask subTask1 = new SubTask("Присед 120 кг", "В рамках этой задачи мы присядем 120 кг", epicTask1.getId());
        manager.addNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("Присед 140 кг", "В рамках этой задачи мы присядем 140 кг", epicTask1.getId());
        manager.addNewSubTask(subTask2);

        EpicTask epicTask2 = new EpicTask("Жим 140 кг", "В рамках этой задачи мы выжмем 140 кг");
        manager.addNewEpicTask(epicTask2);
        SubTask subTask3 = new SubTask("Жим 110 кг", "В рамках этой задачи мы выжмем 140 кг", epicTask2.getId());
        manager.addNewSubTask(subTask3);

        System.out.println(manager.getEpicTasks());

        subTask3.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask3);

        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask1);

        System.out.println(manager.getEpicTasks());
        System.out.println(manager.getSubTasks());
        manager.deleteByIdEpicTasks(epicTask1.getId());
        System.out.println(manager.getEpicTasks());
        System.out.println(manager.getSubTasks());
    }
}