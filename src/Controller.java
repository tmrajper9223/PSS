import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import utils.taskutils.*;

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
        List<Task> temp = new ArrayList<>(taskList);
        try {
            if (newTask == null)
                return null;

            if (doesTaskExist(newTask.getName()))
                throw new InvalidTaskException("A Task With That Name Already Exists!\n");
            temp.add(newTask);
            Schedule s = new Schedule();
            if (s.getSchedule(temp) == null && s.hasOverlap)
                throw new InvalidTaskException("New Task(s) Were Not Added To Schedule, Overlaps Detected in Schedule\n");

            if (s.getSchedule(temp) == null && s.isInvalidAntiTask)
                throw new InvalidTaskException("New Task(s) Were Not Added To Schedule, No Matching Recurring Task Found For Anti Task\n");
        } catch (InvalidTaskException | NullPointerException e) {
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
        if (taskList.isEmpty())
            return null;
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
        try {
            if (taskList.isEmpty())
                throw new InvalidTaskException("Task List is Empty\n");

            for (Task task : taskList) {
                if (task.getName().equals(name)) {
                  taskList.remove(task);
                  System.out.println("Task Successfully Removed\n");
                  return;
                }
            }
            throw new InvalidTaskException("A Task With That Name Does Not Exist!\n");
        } catch (InvalidTaskException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Finds Task By Name in taskList and returns it, if not found throw InvalidTaskException here with appropriate msg
     * @param name Name of Task to be Edited
     * @return Task if found, null if not
     */
    public Task getTask(String name) {
        return null;
    }

    /**
     * Finds the Task By name and replaces that task with the edited task
     * @param name name of the Task to Edit
     * @param editedTask New Edited Task to replace the old task
     */
    public void replace(String name, Task editedTask) {

    }

    /**
     * Takes Task List and Passes it to Schedule Class to generate Schedule
     * Once Schedule is generated, it is returned here and written out in JSON format to a file
     * @param filename Path to JSON File to write schedule to
     */
    public void writeSchedule(String filename) {
        try {
            if (taskList.isEmpty())
                throw new InvalidTaskException("No Tasks To Write to File\n");

            List<Task> schedule = new Schedule().getSchedule(taskList);
            if (schedule == null)
                throw new InvalidTaskException("Schedule Error, Cannot Generate Schedule\n");

            writeJSON(filename, schedule);

        } catch (InvalidTaskException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Writes Schedule for Time Period Starting From Start Date
     * @param filename Path to Directory and Filename
     * @param frequency Time Period -> 1/7/30 -> Day/Week/Month
     * @param date Start Date
     */
    public void writeSchedule(String filename, int frequency, double date) {
        try {
            if (taskList.isEmpty())
                throw new InvalidTaskException("No Tasks To Write To File\n");

            List<String> dates = new Schedule().generateDates(date, frequency);
            Schedule schedule = new Schedule();
            if (schedule.getSchedule(taskList) == null || schedule.getSchedule(taskList).isEmpty())
                throw new InvalidTaskException("No Schedule Can Be Generated, No Tasks or Invalid Schedule\n");

            List<Task> tasks = schedule.viewSchedule(dates);
            writeJSON(filename, tasks);
        } catch (InvalidTaskException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void writeJSON(String filename, List<Task> schedule) {
        JSONArray list = new JSONArray();
        for (Task task : schedule) {
            JSONObject obj = new JSONObject();
            obj.put("Name", task.getName());
            obj.put("Type", task.getType());
            obj.put("StartTime", task.getStartTime());
            obj.put("Duration", task.getDuration());
            if (task instanceof RecurringTask) {
                obj.put("StartDate", (int) task.getDate());
                obj.put("EndDate", (int) ((RecurringTask) task).getEndDate());
                obj.put("Frequency", ((RecurringTask) task).getFrequency());
            } else {
                obj.put("Date", (int) task.getDate());
            }
            list.add(obj);
        }
        try (FileWriter file = new FileWriter(filename)) {
            file.write(list.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the file from the filepath and uses JSON Library To Parse JSON Array and Pass Each Object To TaskFactory
     * @param filepath Path to JSON File
     */
    public void readScheduleFromFile(String filepath) {
        List<Task> newTasks = new ArrayList<>(taskList);
        TaskFactory taskFactory = new TaskFactory();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filepath));
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                Task task = taskFactory.createTask(jsonObject);
                if (task == null)
                    throw new NullPointerException("New Tasks Were Not Added To Schedule, Bad Formatting Detected\n");
                newTasks.add(task);
            }
            if (new Schedule().getSchedule(newTasks) == null)
                throw new NullPointerException("New Task(s) Were Not Added To Schedule, Overlaps Detected in Schedule\n");
            taskList.addAll(newTasks);
        } catch (NullPointerException e) {
            // If Task File Cannot Be Parsed Correctly, Rollback Task List
            newTasks.clear();
            System.out.println(e.getMessage());
        }
        catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays Schedule for a Specific Time Period
     * @param frequency Day/Week/Month Input -> 1/7/30
     * @param date Start Date for Viewing Tasks
     */
    public void viewSchedule(int frequency, double date) {
        try {
            List<String> dates = new Schedule().generateDates(date, frequency);
            Schedule scheduleObject = new Schedule();
            List<Task> schedule = scheduleObject.getSchedule(taskList);
            if (schedule == null || schedule.isEmpty())
                throw new InvalidTaskException("No Schedule Can Be Generated, No Tasks or Invalid Schedule\n");

            List<Task> tasks = scheduleObject.viewSchedule(dates);
            System.out.println(tasks.size());
            for (Task task : tasks)
                System.out.println(task.toString());
        } catch (InvalidTaskException e) {
            System.out.println(e.getMessage());
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

    /**
     * Called When Task Cannot Be Found in Task List
     */
    private static class InvalidTaskException extends Exception {
        public InvalidTaskException(String errorMessage) {
            super(errorMessage);
        }
    }

}
