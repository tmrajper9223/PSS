import java.beans.Transient;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import utils.taskutils.Task;
import utils.taskutils.TaskFactory;
import utils.taskutils.TransientTask;
import utils.taskutils.RecurringTask;
import utils.taskutils.AntiTask;

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
        int counter = 0;
        int n = taskList.size();
        List<Task> printList = new ArrayList<>();
        while (counter < n && taskList.get(counter).getDate() <= startDate) {
            counter++;
        }
        Task currentTask = taskList.get(counter);
        while (currentTask.getDate() - startDate <= timePeriod) {
            printList.add(currentTask);
            if (counter < n-1)
                currentTask = taskList.get(++counter);
        }

        // sort by date & starting time
        Comparator<Task> sortByDate = new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (int)(o1.getDate() - o2.getDate());
            }
        };
        Comparator<Task> sortByTime = new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (int)(o1.getStartTime() - o2.getStartTime());
            }
        };
        printList.sort(sortByTime);
        printList.sort(sortByDate);

        for (int i=0; i<printList.size(); i++)
            System.out.println(printList.get(i).toString());
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

    /**
     * Checks if the input of the list of tasks overlap.
     * @param inputList a list of all Tasks that need to be checked for overlaps.
     * @return true if there is an overlap in the list of tasks
     */
    private boolean isTaskOverlapping(List<Task> inputList) {

        // O(n^2) - Iterates over the inputList to a Task with every other Task
        // with exceptions to Anti-Tasks which are skipped.
        for (int i = 0; i < taskList.size(); i++) {

            // If the current Task is Anti-Task, skip it.
            if (inputList.get(i) instanceof AntiTask) {
                continue;
            }

            // For loop will iterate over every OTHER task to check for overlaps.
            for (int j = 1; j < taskList.size(); j++) {

                // Check if the next Task in the list is an Anti-Task OR The names are the same. If so, skip to next Task.
                if (inputList.get(j) instanceof AntiTask || inputList.get(i).getName().equals(inputList.get(j).getName())) {
                    continue;
                }

                // If current Task and next Task have the same Date AND startTime, return true.
                if (inputList.get(i).getDate() == inputList.get(j).getDate()
                        && inputList.get(i).getStartTime() == inputList.get(j).getStartTime()) {
                    return true;
                }

                // Create two doubles to store the end times of the current Task ( 'i' index ) and next Task ( 'j' index )
                double currentTaskEndTime = inputList.get(i).getStartTime() + inputList.get(i).getDuration(),
                        nextTaskEndTime = inputList.get(j).getStartTime() + inputList.get(j).getDuration();

                // If next Task has midnight exception AND current Task does not.
                if (nextTaskEndTime > 23.75 && currentTaskEndTime < 23.75) {

                    // Subtracting 24 from the endTime would correct the endTime.
                    nextTaskEndTime -= 24;

                    // next Task is prominent in two days, so get the next day.
                    String parsedDate = new Schedule().formatLocalDate(inputList.get(j).getDate());
                    LocalDate localDate = LocalDate.parse(parsedDate);
                    localDate = localDate.plusDays(1);
                    String nextTaskNewDate = new Schedule().formatTaskDate(localDate.toString());

                    // Check if both Tasks' Dates match
                    if (inputList.get(i).getDate() == inputList.get(j).getDate()) {
                        // Between current Task's start time and midnight

                        // Checks if current Task's end time is between next Task's start time and midnight.
                        if (inputList.get(j).getStartTime() < currentTaskEndTime && currentTaskEndTime <= 0) {
                            return true;
                        }
                    }
                    // Check if the next Task's next day is the same as current Task's day.
                    else if (inputList.get(i).getDate() == Double.parseDouble(nextTaskNewDate)) {
                        // Between midnight and currentTaskEndTime

                        // Checks if current Task's start time is between midnight and next Task's end time.
                        if (0 <= inputList.get(i).getStartTime() && inputList.get(i).getStartTime() < nextTaskEndTime) {
                            return true;
                        }
                    }

                }

                // If current Task goes past midnight AND next Task does not.
                else if (currentTaskEndTime > 23.75 && nextTaskEndTime < 23.75) {
                    currentTaskEndTime -= 24;

                    // current Task is prominent in two days, so get the next day.
                    String parsedDate = new Schedule().formatLocalDate(inputList.get(i).getDate());
                    LocalDate localDate = LocalDate.parse(parsedDate);
                    localDate = localDate.plusDays(1);
                    String currentTaskNewDate = new Schedule().formatTaskDate(localDate.toString());

                    // Check if both Tasks' Dates match
                    if (inputList.get(i).getDate() == inputList.get(j).getDate()) {
                        // Between next Task's start time and midnight

                        // Checks if next Task's end time is between current Task's start time and midnight.
                        if (inputList.get(i).getStartTime() < nextTaskEndTime && nextTaskEndTime <= 0) {
                            return true;
                        }
                    }
                    // Check if the current Task's next day is the same as next Task's day.
                    else if (inputList.get(j).getDate() == Double.parseDouble(currentTaskNewDate)) {
                        // Between midnight and nextTaskEndTime

                        // Checks if next Task's start time is between midnight and current Task's end time.
                        if (0 <= inputList.get(j).getStartTime() && inputList.get(j).getStartTime() < currentTaskEndTime) {
                            return true;
                        }
                    }

                }
                // If both current Task and next Task have midnight exceptions AND are on the same day, return true
                else if (currentTaskEndTime > 23.75 && nextTaskEndTime > 23.75
                        && inputList.get(i).getDate() == inputList.get(j).getDate()) {
                    return true;
                }
                // Checks WITHOUT midnight exception AND both Tasks are the same day.
                else if ( inputList.get(i).getDate() == inputList.get(j).getDate() ) {
                    // Checks if both end times are the same, return true
                    if (nextTaskEndTime == currentTaskEndTime) {
                        return true;
                    }
                    // If current Task's start time is within next Task.
                    else if (inputList.get(j).getStartTime() < inputList.get(i).getStartTime()
                            && inputList.get(i).getStartTime() < nextTaskEndTime) {
                        return true;
                    }
                    // If current Task's end time is within next Task.
                    else if (inputList.get(j).getStartTime() < currentTaskEndTime
                            && currentTaskEndTime < nextTaskEndTime) {
                        return true;
                    }
                    // If next Task is within the current Task.
                    else if (inputList.get(i).getStartTime() < inputList.get(j).getStartTime()
                            && nextTaskEndTime < currentTaskEndTime) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
