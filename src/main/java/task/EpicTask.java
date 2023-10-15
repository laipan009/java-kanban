package task;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private List<Long> subTasksId = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description);
    }

    public List<Long> getSubTasksId() {
        return subTasksId;
    }

    public void setSubTasksId(List<Long> subTasksId) {
        this.subTasksId = subTasksId;
    }

    public void addSubTask(Long idSubtask) {
        subTasksId.add(idSubtask);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPICTASK;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
