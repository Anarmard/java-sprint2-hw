package tracker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList <Long> idListSubTask;
    private LocalDateTime endTimeEpic;

    public Epic(String name, String description, Long id, TaskStatus status, LocalDateTime startTime, Long duration,
                LocalDateTime endTimeEpic, ArrayList<Long> idListSubTask) {
        super(name, description, id, status, startTime, duration);
        this.idListSubTask = idListSubTask;
        this.endTimeEpic = endTimeEpic;
    }

    public Epic(String name, String description, Long id, TaskStatus status, ArrayList<Long> idListSubTask) {
        super(name, description, id, status);
        this.idListSubTask = idListSubTask;
    }

    public void setIdListSubTask(ArrayList<Long> idListSubTask) {
        this.idListSubTask = idListSubTask;
    }

    public ArrayList<Long> getIdListSubTask() {
        return idListSubTask;
    }

    public LocalDateTime getEndTimeEpic() {
        return endTimeEpic;
    }

    public void setEndTimeEpic(LocalDateTime endTimeEpic) {
        this.endTimeEpic = endTimeEpic;
    }

    @Override
    public String toString() {
        return getId() + "," + TaskType.EPIC + "," + getName() + "," + getStatus()
                + "," + getStartTime() + "," + getDuration() +  "," + getEndTimeEpic() +  ","
                + getDescription() + "," + getIdListSubTask();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic otherEpic = (Epic) obj;
        return Objects.equals(getName(), otherEpic.getName()) &&
                Objects.equals(getDescription(), otherEpic.getDescription()) &&
                Objects.equals(getStatus(), otherEpic.getStatus()) &&
                Objects.equals(idListSubTask, otherEpic.idListSubTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getStatus(), idListSubTask);
    }
}
