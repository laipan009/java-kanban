package task;

import java.util.*;

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
    public String toString() {
        return "EpicTask{" +
                "subTasksId=" + subTasksId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
