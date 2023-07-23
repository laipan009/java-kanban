package epic.task;

import task.Task;
import java.util.*;

public class EpicTask extends Task {
    private List<UUID> subTasks = new ArrayList<>();

    public EpicTask() {
    }

    public EpicTask(String name, String description) {
        super(name, description);
    }

    public List<UUID> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<UUID> subTasks) {
        this.subTasks = subTasks;
    }

    public void addSubTask(UUID idSubtask) {
        subTasks.add(idSubtask);
    }


}
