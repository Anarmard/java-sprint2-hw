public class Task {
    protected String name;
    protected String description;
    protected Long id;
    protected String status;

    public Task(String name, String description, Long id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", id ='" + id + '\'' +
                ", status ='" + status +
                '\'' + '}';
    }

}
