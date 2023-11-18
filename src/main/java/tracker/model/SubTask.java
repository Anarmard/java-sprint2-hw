package tracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final Long idEpic;

    public SubTask(String name, String description, Long id, TaskStatus status, LocalDateTime startTime, Long duration, Long idEpic) {
        super(name, description, id, status, startTime, duration);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Long id, TaskStatus status, Long idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    public Long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.SUBTASK + "," + getName() + "," + getStatus()
                + "," + getStartTime() + "," + getDuration() + "," + getEndTime(getStartTime(), getDuration()) + ","
                + getDescription() + "," + idEpic;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        SubTask otherSubTask = (SubTask) obj;
        return Objects.equals(getName(), otherSubTask.getName()) &&
                Objects.equals(getDescription(), otherSubTask.getDescription()) &&
                Objects.equals(getStatus(), otherSubTask.getStatus()) &&
                (Objects.equals(idEpic, otherSubTask.idEpic));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getStatus(), idEpic);
    }
}
