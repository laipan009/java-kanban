package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> lastSeenTasks = new ArrayList<>();

    /**
     * @return возвращает поле lastSeenTasks содержащий последние 10 просмотренных задач.
     */
    @Override
    public List<Task> getHistory() {
        return lastSeenTasks;
    }

    /**
     * Если размер lastSeenTasks равен 10, то удаляет первый элемент и добавляет в конец новый по id, либо добавляет
     * в конец списка по id.
     *
     * @param task просмотренная задача.
     */
    @Override
    public void add(Task task) {
        int countLastSeenTasks = 10;
        if (lastSeenTasks.size() == countLastSeenTasks) {
            lastSeenTasks.remove(0);
        }
        lastSeenTasks.add(task);
    }
}
