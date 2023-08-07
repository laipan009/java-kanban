package manager;

import task.Task;

import java.util.List;
import java.util.Optional;

public interface HistoryManager {
    void add(Task task);
    Optional<List<Task>> getHistory();
}
