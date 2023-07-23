package subtask;

import task.Task;
import java.util.UUID;

public class SubTask extends Task {

    private UUID idEpicTask;

    public SubTask() {
    }

    public SubTask(String name, String description, UUID idEpicTask) {
        super(name, description);
        this.idEpicTask = idEpicTask;
    }

    public UUID getIdEpicTask() {
        return idEpicTask;
    }

    public void setIdEpicTask(UUID idEpicTask) {
        this.idEpicTask = idEpicTask;
    }
}
