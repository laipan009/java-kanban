package utill;

import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

