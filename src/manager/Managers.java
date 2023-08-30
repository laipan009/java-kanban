package manager;

import manager.api.TaskManager;
import manager.impl.InMemoryHistoryManager;
import manager.impl.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

