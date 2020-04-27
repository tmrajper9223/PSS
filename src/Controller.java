import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.*;
import org.json.simple.parser.*;

import utils.taskutils.Task;
import utils.taskutils.TaskFactory;

/**
 *
 */
public class Controller {

    private static Controller controllerInstance = null;
    private List<Task> taskList;

    public Controller() {
        taskList = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public static Controller getInstance() {
        return (controllerInstance == null) ? new Controller() : controllerInstance;
    }

    /**
     *
     * @param type
     * @return
     */
    public Task createTask(String type) {
        Task newTask = new TaskFactory().createTask(type);
        try {
            if (doesTaskExist(newTask.getName()))
                throw new InvalidTaskException("A Task With That Name Already Exists!");
        } catch (InvalidTaskException e) {
            System.out.println(e.getMessage());
            return null;
        }
        taskList.add(newTask);
        return newTask;
    }

    /**
     * Iterates Through the Task list and finds task with matching name
     * @param name Name of Task Inputted by the user
     * @return The Task is name matches Task in task list, null if no task found
     */
    public Task viewTask(String name) {
        for (Task task : taskList) {
            if (task.getName().equals(name))
                return task;
        }
        return null;
    }

    /**
     *
     * @param filepath
     */
    public void readScheduleFromFile(String filepath) {
        TaskFactory taskFactory = new TaskFactory();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filepath));
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                Task task = taskFactory.createTask(jsonObject);
                if (task == null)
                    throw new NullPointerException();
                taskList.add(task);
            }
        } catch (NullPointerException ignored) {
            // If Task File Cannot Be Parsed Correctly, Rollback Task List
        }
        catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Iterates through task list and checks is name matches any task name already in the list
     * @param name task name passed in by user/program
     * @return true if task is already in the task list
     */
    private boolean doesTaskExist(String name) {
        for (Task task : taskList)
            if (task.getName().equals(name))
                return true;
        return false;
    }

    private static class InvalidTaskException extends Exception {
        public InvalidTaskException(String errorMessage) {
            super(errorMessage);
        }
    }

}
