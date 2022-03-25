package tracker.model;

import java.util.Objects;

public class SubTask extends Task {
    private Long idEpic;

    public SubTask(String name, String description, Long id, TaskStatus status, Long idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    public Long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "\nSubTask{" +
                "name ='" + getName() + '\'' +
                ", description ='" + getDescription() + '\'' +
                ", id ='" + getId() + '\'' +
                ", status ='" + getStatus() + '\'' +
                ", idEpic ='" + idEpic +
                '\'' + '}';
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
                (idEpic == otherSubTask.idEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getStatus(), idEpic);
    }
}
