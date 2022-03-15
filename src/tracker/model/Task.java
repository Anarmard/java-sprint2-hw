package tracker.model;
import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private Long id;
    private String status;

    public Task(String name, String description, Long id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "tracker.model.Task{" +
                "name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", id ='" + id + '\'' +
                ", status ='" + status +
                '\'' + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) &&
                Objects.equals(description, otherTask.description) &&
                Objects.equals(status, otherTask.status); // id не сравниваю т.к. они уникальны у каждой задачи
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }
}
