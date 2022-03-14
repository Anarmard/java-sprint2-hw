public class SubTask extends Task {
    protected Long idEpic;

    public SubTask(String name, String description, Long id, String status, Long idEpic) {
        super(name, description, id, status);
        this.idEpic = idEpic;
    }

    public Long getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name ='" + name + '\'' +
                ", description ='" + description + '\'' +
                ", id ='" + id + '\'' +
                ", status ='" + status + '\'' +
                ", idEpic ='" + idEpic +
                '\'' + '}';
    }
}
