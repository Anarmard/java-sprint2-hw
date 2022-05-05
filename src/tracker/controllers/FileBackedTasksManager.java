package tracker.controllers;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;
import tracker.model.TaskStatus;
import tracker.model.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private String fileName = "TasksCSV.csv";
    private String pathDr = "C://Users//Anar//dev//java-sprint2-hw";
    private Path pathTaskManager = Paths.get(pathDr, fileName); // создаем объект типа Path (содержит полный путь к файлу)
    public static FileBackedTasksManager manager = new FileBackedTasksManager("TasksCSV.csv");

    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    public static void main(String[] args) {
        // создание 2-х задач
        Task task1 = new Task("Продукты", "Заехать в магазин", 0L, TaskStatus.NEW);
        manager.createTask(task1);
        Task task2 = new Task("Ремонт", "Покрасить стену", 0L, TaskStatus.NEW);
        manager.createTask(task2);

        // создание Эпика с 3-мя подзадачами
        Epic epic1 = new Epic ("Уборка дома", "Еженедельная процедура", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic1);
        SubTask subTask11 = new SubTask("Пыль", "Протереть пыль", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask11);
        SubTask subTask12 = new SubTask("Стирка", "Постирать вещи", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask12);
        SubTask subTask13 = new SubTask("Посуда", "Помыть посуду", 0L, TaskStatus.NEW, 3L);
        manager.createSubTask(subTask13);

        // создание Эпика без подзадач
        Epic epic2 = new Epic ("Спорт", "Улучшить здоровье", 0L, TaskStatus.NEW, new ArrayList<>());
        manager.createEpic(epic2);

        // печать всех типов задач
        System.out.println("\nПечать всех задач/эпиков/подзадач без изменения:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        // Запросите некоторые из них, чтобы заполнилась история просмотра.
        manager.getSubTaskUser(4L);
        manager.getEpicUser(3L);
        manager.getTaskUser(2L);
        manager.getTaskUser(1L);
        manager.getSubTaskUser(4L);

        System.out.println("\nПечать истории просмотров:");
        System.out.println(toString(manager));

        // Проверка как создается Manager из файла
        // Создайте новый FileBackedTasksManager менеджер из этого же файла.
        String fileName = "TasksCSV.csv";
        String pathDr = "C://Users//Anar//dev//java-sprint2-hw";
        Path pathTaskManager = Paths.get(pathDr, fileName);
        FileBackedTasksManager managerLoadFromFile = loadFromFile(pathTaskManager);

        // !!! происходит перезаписывание файла - возможная причина ошибок (?)
        // печать всех типов задач
        System.out.println("\nПечать всех задач/эпиков/подзадач из файла:");
        System.out.println(managerLoadFromFile.getAllTasks());
        System.out.println(managerLoadFromFile.getAllEpics());
        System.out.println(managerLoadFromFile.getAllSubTasks());

        System.out.println("\nПечать истории просмотров:");
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

    public static Task fromStringTask(String value) {
        String[] split = value.split(",");
        Task task = new Task(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]));
        return task;
    }

    public static Epic fromStringEpic(String value) {
        String[] split = value.split(",");
        Epic epic = new Epic(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]), null);
        return epic;
    }

    public static SubTask fromStringSubTask(String value) {
        String[] split = value.split(",");
        SubTask subTask = new SubTask(split[2], split[4], Long.valueOf(split[0]), TaskStatus.valueOf(split[3]), Long.getLong(split[5]));
        return subTask;
    }

    static List<Integer> fromString(String value) {
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
                String value = br.readLine();

                if (value.isEmpty()) { // если пустая строка, то
                    String value1 = br.readLine(); // значит на следущей строке сохранена история
                    // последнюю строку также разбираем на числа, чтобы заполнить историю historyManager (вызывая эти задачи)
                    List<Integer> listID = fromString(value1);
                    for (Integer taskID : listID) {
                        managerLoadFromFile.getTaskUser(Long.valueOf(taskID));
                    }
                    break;
                }

                String[] split = value.split(","); // value - это 1 строка
                switch (TaskType.valueOf(split[1])) {
                    case TASK:
                        Task task = fromStringTask(value);
                        managerLoadFromFile.createTask(task);
                        break;
                    case EPIC:
                        Epic epic = fromStringEpic(value);
                        managerLoadFromFile.createEpic(epic);
                        break;
                    case SUBTASK:
                        SubTask subTask = fromStringSubTask(value);
                        managerLoadFromFile.createSubTask(subTask);
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

    public String toString(Task task) {
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
    }

    public String toString(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription();
    }

    public String toString(SubTask subTask) {
        return subTask.getId() + "," + TaskType.SUBTASK + "," + subTask.getName() + "," + subTask.getStatus() +
                "," + subTask.getDescription() + "," + subTask.getIdEpic();
    }

    // Напишите статические методы static String toString(HistoryManager manager)
    // для сохранения менеджера истории из CSV.
    public static String toString(FileBackedTasksManager manager) {
        List<Task> taskList = manager.getHistory();
        StringBuilder builder = new StringBuilder();
        for (Task taskE : taskList) {
            builder.append(taskE.getId());
            builder.append(",");
        }
        return builder.toString();
    }

    public void save() {
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

        // непосредственно сама запись в файл
        try (BufferedWriter fileWriter = new BufferedWriter(new
                FileWriter(fileName, StandardCharsets.UTF_8, true))) {

            fileWriter.write("id,type,name,status,description,epic" + "\n");

            // запись в файл по очереди сначала все Task, потом Epic, потом все SubTask
            List<Task> taskList = getAllTasks();
            for (int i = 0; i < taskList.size(); i++) {
                fileWriter.write(toString(taskList.get(i)) + "\n");
            }

            List<Epic> epicList = getAllEpics();
            for (int i = 0; i < epicList.size(); i++) {
                fileWriter.write(toString(epicList.get(i)) + "\n");
            }

            List<SubTask> subTaskList = getAllSubTasks();
            for (int i = 0; i < subTaskList.size(); i++) {
                fileWriter.write(toString(subTaskList.get(i)) + "\n");
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

    private class ManagerSaveException extends Throwable {
        public ManagerSaveException(IOException e) {
        }
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

    // Создание. Сам объект должен передаваться в качестве параметра.
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
