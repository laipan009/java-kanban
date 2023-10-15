package task;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private long idEpicTask;

    public SubTask(String name, String description, long idEpicTask) {
        super(name, description);
        this.idEpicTask = idEpicTask;
    }

    public SubTask(String name, String description, long duration, LocalDateTime startTime, long idEpicTask) {
        super(name, description, duration, startTime);
        this.idEpicTask = idEpicTask;
    }

    public long getIdEpicTask() {
        return idEpicTask;
    }

    public void setIdEpicTask(long idEpicTask) {
        this.idEpicTask = idEpicTask;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "idEpicTask=" + idEpicTask +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
