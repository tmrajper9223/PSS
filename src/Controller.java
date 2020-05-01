import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import utils.taskutils.Task;
import utils.taskutils.TaskFactory;

/**
 * Singleton Design of the Controller Class handles interaction between UI and TaskFactory/Schedule
 */
public class Controller {

    private static Controller controllerInstance = null;
    private List<Task> taskList;

    public Controller() {
        taskList = new ArrayList<>();
    }

    /**
     * Singleton Design of the Controller Class
     * @return The Instance of the Controller Class
     */
    public static Controller getInstance() {
        return (controllerInstance == null) ? new Controller() : controllerInstance;
    }

    /**
     * Passes the User Inputted type to the TaskFactory and checks if name already exists in Task List
     * @param type User Inputted Task Type
     * @return New Task if Task Name does not already Exist, null if it does
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
     * Finds Task in Task List and Removes it from list, if not found throw InvalidTaskException with appropriate exception msg
     * @param name Name of Task to be deleted
     */
    public void deleteTask(String name) {

    }

    /**
     * Finds Task By Name in taskList and returns it, if not found throw InvalidTaskException here with appropriate msg
     * @param name Name of Task to be Edited
     * @return Task if found, null if not
     */
//    public Task getTask(String name) {return null;}
//    Edited out because redundant

    /**
     * Finds the Task By name and replaces that task with the edited task
     * @param name name of the Task to Edit
     * @param editedTask New Edited Task to replace the old task
     */
    public void replace(String name, Task editedTask) {
//        for(Task task : taskList){
//            if(task.getName().equals(name)){
//                if(!doesTaskExist(editedTask.getName())){
//                    task.setName(editedTask.getName());
//                }else{System.out.println("Cannot Change Task Name, Task Already exists");}
//
//
//            }else{
//            System.out.println("A Task With that Name Does Not Exist!\n");
//        }
//     }



    }

    /**
     * Takes Task List and Passes it to Schedule Class to generate Schedule
     * Once Schedule is generated, it is returned here and written out in JSON format to a file
     * @param filepath Path to JSON File to write schedule to
     */
    public void writeScheduleToFile(String filepath) {

    }

    /**
     * Reads the file from the filepath and uses JSON Library To Parse JSON Array and Pass Each Object To TaskFactory
     * @param filepath Path to JSON File
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
     * Displays Schedule for a particular timePeriod(Day/Week/Month) on a specific startDate
     * @param timePeriod Day/Week/Month(1/7/30 Respectively) Inputted by User in the UI
     * @param startDate Date To start Viewing from based on time period, Inputted by User in the UI
     */
    public void viewSchedule(int timePeriod, double startDate) {
        Schedule s = new Schedule(taskList);
        s.view(timePeriod,startDate);
    }

    /**
     * Unclear will ask professor -> Tarik
     * @param timePeriod Day/Week/Month to Write Schedule for
     * @param startDate Start Date for specific time period
     */
    public void writeSchedule(int timePeriod, double startDate) {

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

    /**
     * Called When Task Cannot Be Found in Task List
     */
    private static class InvalidTaskException extends Exception {
        public InvalidTaskException(String errorMessage) {
            super(errorMessage);
        }
    }

}
