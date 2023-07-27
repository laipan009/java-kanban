package task;

import java.util.UUID;

public class SubTask extends Task {

    private long idEpicTask;

    public SubTask(String name, String description, long  idEpicTask) {
        super(name, description);
        this.idEpicTask = idEpicTask;
    }

    public long  getIdEpicTask() {
        return idEpicTask;
    }

    public void setIdEpicTask(long  idEpicTask) {
        this.idEpicTask = idEpicTask;
    }
}
