package tracker.controllers;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskStatus;
import tracker.model.TaskType;

import tracker.exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static String fileName = "TasksCSV.csv";
    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    private static final String pathDr = "C://Users//Anar//dev//java-sprint2-hw";
    private static final Path pathTaskManager = Paths.get(pathDr, fileName); // создаем объект типа Path (содержит полный путь к файлу)
    private static final FileBackedTasksManager manager = new FileBackedTasksManager(fileName);

    public static void main(String[] args) { // Может в этом классе не надо все-таки main? Перенести код в main основной
        createAllTasks(manager);
        System.out.println("\n===Печать всех задач/эпиков/подзадач без изменения===");
        printAllTasks(manager);

        // Запросите некоторые из них, чтобы заполнилась история просмотра.
        createHistory(manager);
        System.out.println("\n===Печать истории просмотров===");
        System.out.println(toString(manager));

        // Создайте новый FileBackedTasksManager менеджер из этого же файла.
        System.out.println("\n>>>Проверка как создается Manager из файла<<<");
        FileBackedTasksManager managerLoadFromFile = loadFromFile(pathTaskManager);

        System.out.println("\n===Печать всех задач/эпиков/подзадач из файла===");
        printAllTasks(managerLoadFromFile);

        System.out.println("\n===Печать истории просмотров===");
        System.out.println(toString(managerLoadFromFile));
    }

    // Разделение одной строки на массив:
    // String[] split = value.split(",");
    // split[0] - это id
    // split[1] - это тип задачи
    // split[2] - это name задачи
    // split[3] - это Status задачи
    // split[4] - это описание задачи
    // split[5] - это номер айди эпика (если рассматриваем subtask)

    private static Task fromStringTask(String value) {
        String[] split = value.split(",");
        Task task = new Task(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]));
        if ((!split[5].equals("null")) && (!split[6].equals("null"))) { // поля startTime & duration необязательные
            // если они все-таки есть, то пересоздаем task с учетом этих полей
            task = new Task(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]),
                    LocalDateTime.parse(split[5]), Long.valueOf(split[6]));
        }
        return task;
    }

    private static Epic fromStringEpic(String value) {
        String[] split = value.split(",");
        Epic epic = new Epic(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]), new ArrayList<>());
        return epic;
    }

    private static SubTask fromStringSubTask(String value) {
        String[] split = value.split(",");
        SubTask subTask = new SubTask(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]), Long.valueOf(split[8]));
        if ((!split[5].equals("null")) && (!split[6].equals("null"))) { // поля startTime & duration необязательные
            // если они все-таки есть, то пересоздаем subTask с учетом этих полей
            subTask = new SubTask(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]),
                    LocalDateTime.parse(split[5]), Long.valueOf(split[6]), Long.valueOf(split[8]));
        }
        return subTask;
    }

    private static List<Integer> fromString(String value) {
        String[] split = value.split(",");
        List<Integer> listID = new ArrayList<>();
        for (String id : split) {
            listID.add(Integer.valueOf(id));
        }
        return listID;
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager managerLoadFromFile = new FileBackedTasksManager("TasksCSV.csv");
        try (BufferedReader br = new BufferedReader(new
                FileReader(path.toFile().getName(), StandardCharsets.UTF_8))) {
            br.readLine(); // пропустим первую строчку, где указаны название полей
            while (br.ready()) {
                String value = br.readLine(); // прочитали всю строку

                if (value.isEmpty()) { // если пустая строка, то
                    String value1 = br.readLine(); // значит на следущей строке сохранена история
                    // последнюю строку также разбираем на числа, чтобы заполнить историю historyManager (вызывая эти задачи)
                    List<Integer> listID = fromString(value1);
                    for (Integer taskID : listID) {
                        managerLoadFromFile.loadTaskUserWoSave(Long.valueOf(taskID));
                    }
                    break; // выходим из цикла, т.к. строка с историей просмотров - последняя строка в файле
                }

                String[] split = value.split(","); // value - это одна строка
                switch (TaskType.valueOf(split[1])) {
                    case TASK:
                        Task task = fromStringTask(value);
                        managerLoadFromFile.loadTaskWoSave(task);
                        break;
                    case EPIC:
                        Epic epic = fromStringEpic(value);
                        managerLoadFromFile.loadEpicWoSave(epic);
                        break;
                    case SUBTASK:
                        SubTask subTask = fromStringSubTask(value);
                        managerLoadFromFile.loadSubTaskWoSave(subTask);
                        break;
                    default:
                        System.out.println("Такого типа задач нет");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return managerLoadFromFile;
    }

    private String toString(Task task) {
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription()
                + "," + task.getStartTime() + "," + task.getDuration() + "," + task.getEndTime(task.getStartTime(), task.getDuration());
    }

    private String toString(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription()
                + "," + epic.getStartTime()+ "," + epic.getDuration() + "," + epic.getEndTimeEpic();
    }

    private String toString(SubTask subTask) {
        return subTask.getId() + "," + TaskType.SUBTASK + "," + subTask.getName() + "," + subTask.getStatus() +
                "," + subTask.getDescription() + "," + subTask.getStartTime() + "," + subTask.getDuration() + ","
                + subTask.getEndTime(subTask.getStartTime(), subTask.getDuration())+ "," + subTask.getIdEpic();
    }

    // Напишите статические методы static String toString(HistoryManager manager)
    // для сохранения менеджера истории из CSV.
    private static String toString(FileBackedTasksManager manager) {
        List<Task> taskList = manager.getHistory();
        StringBuilder builder = new StringBuilder();
        for (Task taskE : taskList) {
            builder.append(taskE.getId());
            builder.append(",");
        }
        return builder.toString();
    }

    private void save() {
        // проверка, есть такой файл или нет. Если есть, то удаляем и создаем. Если нет, то просто создаем.
        try {
            // удаление файла, если он уже существует
            if (Files.exists(Paths.get(pathDr, fileName))) {
                Files.delete(pathTaskManager);
            }
            // создание файла
            Files.createFile(pathTaskManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // запись в файл (с помощью try-with-resources (without close()))
        try (BufferedWriter fileWriter = new BufferedWriter(new
                FileWriter(fileName, StandardCharsets.UTF_8, true))) {

            fileWriter.write("id,type,name,status,description,epic" + "\n");

            // запись в файл по очереди сначала все Task, потом Epic, потом все SubTask
            List<Task> taskList = getAllTasks();
            for (Task task : taskList) {
                fileWriter.write(toString(task) + "\n");
            }

            List<Epic> epicList = getAllEpics();
            for (Epic epic : epicList) {
                fileWriter.write(toString(epic) + "\n");
            }

            List<SubTask> subTaskList = getAllSubTasks();
            for (SubTask subTask : subTaskList) {
                fileWriter.write(toString(subTask) + "\n");
            }

            // сохраняем историю
            fileWriter.write("\n"); //пропуск 1-й строки перед сохранением истории - согласно заданию
            fileWriter.write(toString(manager));

        } catch (IOException ex) {
            try {
                throw new ManagerSaveException(ex);
            } catch (ManagerSaveException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createAllTasks (TaskManager manager) {
        // создание 3-х задач
        Task task1 = new Task("1Task", "1TaskDescription", 0L, TaskStatus.NEW);
        manager.createTask(task1);
        Task task2 = new Task("2Task", "2TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 10, 0), 180L);
        manager.createTask(task2);
        Task task3 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 60L);
        manager.createTask(task3);

        // создание Эпика с 3-мя подзадачами
        Epic epic1 = new Epic ("1Epic", "1EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic1);
        SubTask subTask11 = new SubTask("11SubTask", "11SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 0), 30L, 4L);
        manager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("12SubTask", "12SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 2, 10, 0), 90L,4L);
        manager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("13SubTask", "13SubTaskDescription", 0L, TaskStatus.NEW, 4L);
        manager.createSubTask(subTask13);

        // создание Эпика без подзадач
        Epic epic2 = new Epic ("2Epic", "2EpicDescription", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic2);

        // создание задач с пересечением времени
        Task task4 = new Task("3Task", "3TaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 23, 30), 60L);
        manager.createTask(task4);
        SubTask subTask21 = new SubTask("21SubTask", "21SubTaskDescription", 0L, TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 1, 9, 0), 90L,8L);
        manager.createSubTask(subTask21);
    }

    public static void createHistory(TaskManager manager) {
        manager.getSubTaskUser(5L);
        manager.getEpicUser(4L);
        manager.getTaskUser(1L);
        manager.getSubTaskUser(5L);
        manager.getEpicUser(8L);
        manager.getTaskUser(2L);
        manager.getTaskUser(2L);
        manager.getSubTaskUser(6L);
        manager.getEpicUser(8L);
        manager.getSubTaskUser(7L);
        manager.getTaskUser(2L);
        manager.getTaskUser(1L);
        manager.getEpicUser(8L);
    }

    public static void printAllTasks (TaskManager manager) {
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
    }

    // Удаление всех задач.
    @Override
    public void deleteAllTasks() {
        super.getAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    // Создание. Сам объект должен передаваться в качестве параметра - с Сохранением, т.е. нельзя использовать этот метод,
    // когда создаем Task из файла, т.к. будет перезаписывание (вызов метода save()).
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    // Создание. Но уже без вызова save() - эти методы нужны чтобы вызывать их в load()
    // Если использовать стандартные мметоды, то происходит перезаписывание файла - это неверно.
    public void loadTaskWoSave(Task task) {
        super.createTask(task);
    }

    public void loadEpicWoSave(Epic epic) {
        super.createEpic(epic);
    }

    public void loadSubTaskWoSave(SubTask subTask) {
        super.createSubTask(subTask);
    }

    // Обновление. Новая версия объекта с верным идентификатором передаются в виде параметра.
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    // Удаление по идентификатору.
    @Override
    public void deleteTaskByID(Long id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(Long id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteSubTaskByID(Long id) {
        super.deleteSubTaskByID(id);
        save();
    }

    // Получение по идентификатору.
    @Override
    public Task getTaskUser(Long id) { // в history сохраняются только те задачи, вызванные user
        final Task task = super.getTaskUser(id);
        save();
        return task;
    }

    // Получение по идентификатору. Но уже без вызова save() - эти методы нужны чтобы вызывать их в load()
    // Если использовать стандартные мметоды, то происходит перезаписывание файла - это неверно.
    public Task loadTaskUserWoSave(Long id) {
        if (super.getTaskUser(id) != null) {return super.getTaskUser(id);}
        if (super.getEpicUser(id) != null) {return super.getEpicUser(id);}
        if (super.getSubTaskUser(id) != null) {return super.getSubTaskUser(id);}
        return null;
    }

    @Override
    public Epic getEpicUser(Long id) { // в history сохраняются только те задачи, вызванные user
        final Epic epic = super.getEpicUser(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskUser(Long id) { // в history сохраняются только те задачи, вызванные user
        final SubTask subTask = super.getSubTaskUser(id);
        save();
        return subTask;
    }

}
