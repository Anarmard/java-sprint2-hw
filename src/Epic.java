import java.util.ArrayList;

public class Epic extends Task {
    ArrayList <Long> idListSubTask;

    public Epic(String name, String description, Long id, String status, ArrayList<Long> idListSubTask) {
        super(name, description, id, status);
        this.idListSubTask = idListSubTask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", id ='" + id + '\'' +
                ", status ='" + status + '\'' +
                ", idListSubTask ='" + idListSubTask +
                '\'' + '}';
    }
}
