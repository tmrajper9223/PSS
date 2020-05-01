import java.beans.Transient;
import java.io.FileReader;
import java.io.IOException;
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
     * Algorithm that checks if a task overlaps with another task.
     * @param inputTask passed in as a Task
     * @return false if inputTask does NOT overlap with any other task.
     */
    private boolean isTaskOverlapping( Task inputTask ) {

        // Identify the type of task:
        if (inputTask instanceof TransientTask) {

            for (Task task : taskList) {

                // Do not check any Anti-Tasks because they should not have an overlap.
                // Skip all Anti-Tasks
                // OR skip if the Task has the same name
                if (task instanceof AntiTask || task.getName().equals(inputTask.getName())) {
                    continue;
                }

                // Checks if Tasks' Dates and Start Times are the same.
                if (task.getDate() == inputTask.getDate()
                        && task.getStartTime() == inputTask.getStartTime()) {
                    return true;
                }

                // Checking if the Task in the list is Transient:
                if ( task instanceof TransientTask && task.getDate() == inputTask.getDate() ) {
                    double currentTaskEndTime = task.getStartTime() + task.getDuration(),
                            inputTaskEndTime = task.getStartTime() + task.getDuration();

                    // Checking WITH midnight exception.

                    // If current Task has midnight exception
                    if (task.getStartTime() + task.getDuration() > 23.75 && inputTask.getStartTime() + inputTask.getDuration() < 23.75) {
                        currentTaskEndTime -= 24;

                        if (currentTaskEndTime == inputTaskEndTime) {
                            return true;
                            // Between current Task's start time and midnight
                        } else if ( task.getStartTime() < inputTask.getStartTime() && inputTask.getStartTime() <= 0 ) {
                            // Checks if inputTask start time is between current Task's start time and midnight.
                            return true;
                        } else if ( task.getStartTime() < inputTaskEndTime && inputTaskEndTime <= 0 ) {
                            // Checks if inputTask end time is between current Task's start time and midnight.
                            return true;
                        }

                        // Between midnight and currentTaskEndTime
                        else if ( 0 <= inputTask.getStartTime() && inputTask.getStartTime() < currentTaskEndTime ) {
                            // Checks if inputTask's start time is between midnight and current task's end time.
                            return true;
                        }

                        // If input Task has midnight exception
                    } else if (inputTask.getStartTime() + inputTask.getDuration() > 23.75 && task.getStartTime() + task.getDuration() < 23.75) {
                        inputTaskEndTime -= 24;

                        if (currentTaskEndTime == inputTaskEndTime) {
                            return true;
                        } else if ( inputTask.getStartTime() < task.getStartTime() && task.getStartTime() <= 0 ) {
                            // Checks if current task start time is between inputTask's start time and midnight.
                            return true;
                        } else if ( inputTask.getStartTime() < currentTaskEndTime && currentTaskEndTime <= 0 ) {
                            // Checks if current Task's end time is between inputTask's start time and midnight.
                            return true;
                        } // Between midnight and inputTaskEndTime
                        else if ( 0 <= task.getStartTime() && task.getStartTime() < inputTaskEndTime ) {
                            // Checks if current task's start time is between midnight and inputTask's end time.
                            return true;
                        }

                        // If both current Task and input Task have midnight exceptions
                    } else if (inputTask.getStartTime() + inputTask.getDuration() > 23.75 && task.getStartTime() + task.getDuration() > 23.75) {
                        // If both go past midnight, they will overlap.
                        return true;
                    } else { // Checks WITHOUT midnight exception.

                        if (currentTaskEndTime == inputTaskEndTime) {
                            return true;
                        } else if (task.getStartTime() < inputTask.getStartTime() && inputTask.getStartTime() < currentTaskEndTime) {
                            // If inputTask's start time is within a Transient.
                            return true;
                        } else if (task.getStartTime() < inputTaskEndTime && inputTaskEndTime < currentTaskEndTime) {
                            // If inputTask's end time is within a Transient.
                            return true;
                        } else if (inputTask.getStartTime() < task.getStartTime() && currentTaskEndTime < inputTaskEndTime) {
                            // If a Transient is within the inputTask.
                            return true;
                        } else if (task.getStartTime() < inputTask.getStartTime() && inputTaskEndTime < currentTaskEndTime) {
                            // If inputTask is within a Transient.
                            return true;
                        }

                    }
                } else { // Checks if the input Transient Tasks overlaps with all Recurring Tasks

                }
            }
        } else { // Checks if inputTask is a Recurring Task

        }

        return false;
    }

    //MAKE METHODS FOR DATE AND TIME CHECKS
}