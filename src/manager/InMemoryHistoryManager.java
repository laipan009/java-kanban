package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> lastSeenTasks = new ArrayList<>();

    /**
     * @return возвращает поле lastSeenTasks содержащий последние 10 просмотренных задач.
     */
    @Override
    public Optional<List<Task>> getHistory() {
        return Optional.ofNullable(lastSeenTasks);
    }

    /**
     * Если размер lastSeenTasks равен 10, то удаляет первый элемент и добавляет в конец новый по id, либо добавляет
     * в конец списка по id.
     *
     * @param task просмотренная задача.
     */
    @Override
    public void add(Task task) {
        if (lastSeenTasks.size() == 10) {
            lastSeenTasks.remove(0);
        }
        lastSeenTasks.add(task);
    }
}
