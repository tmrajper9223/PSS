import utils.IOUtils.ScannerUtil;
import utils.taskutils.Task;
import utils.taskutils.TaskFactory;

import javax.swing.*;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 */
public class UserInterface {

    private final Controller controller;

    private final Scanner scan;

    public UserInterface() {
        controller = Controller.getInstance();
        scan = ScannerUtil.getInstance();
    }

    /**
     * Displays Choices for user and prompts user for input choice
     */
    public void startPSS() {
        while (true) {
            display();
            int userInput = scan.nextInt();
            scan.nextLine();

            switch (userInput) {
                case 1:
                    createTask();
                    break;
                case 2:
                    viewTask();
                    break;
                case 3:
                    deleteTask();
                    break;
                case 4:
                    editTask();
                    break;
                case 5:
                    writeSchedule();
                    break;
                case 6:
                    readScheduleFromFile();
                    break;
                case 7:
                    viewSchedule();
                    break;
                case 8:
                    writeScheduleForInterval();
                    break;
                case 9:
                    scan.close();
                    System.out.println("Exiting...");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Invalid Input!");
                    break;
            }
        }
    }

    /**
     * User Enters Task Type and sends type to controller, controller handles exceptions
     */
    private boolean createTask() {
        try {
            System.out.println("Enter Task Type: ");
            String type = scan.nextLine();
            Task newTask = controller.createTask(type);
            if (newTask == null)
                throw new NullPointerException();
            System.out.println("New Task Has Been Added!\n");
            return true;
        } catch (NullPointerException ignored) {
            System.out.println("Failed to Add Task\n");
        }
        return false;
    }

    /**
     * User Enters Task Name and passes to controller, if it exists it displays Task, if not NullPointerException is Caught/Ignored and Appropriate Msg is displayed
     */
    private void viewTask() {
        System.out.println("Enter Task Name: ");
        String name = scan.nextLine();
        try {
            Task task = controller.viewTask(name);
            System.out.println(task.toString());
        } catch (NullPointerException ignored) {
            System.out.println("A Task With That Name Does Not Exist!\n");
        }
    }

    /**
     * Prompts User for Task Name and sends to controller, controller will handle exceptions
     */
    private void deleteTask() {
        System.out.println("Enter Task Name To Delete: ");
        String name = scan.nextLine();
        controller.deleteTask(name);
    }

    /**
     * User Enters Task Name, If Exists Prompts the createTask() method
     * Simpler Approach, if user modifies Task Type, much easier to Create Task From scratch
     * If Changes Invalid, Task List/Schedule are Rolled Back
     */
    private void editTask() {
        try {
            System.out.println("Enter Task Name: ");
            String taskName = scan.nextLine();
            if (!controller.doesTaskExist(taskName))
                throw new Exception("A Task With That Name Does Not Exist!\n");

            Task oldTask = controller.getTask(taskName);
            System.out.println(oldTask.toString());
            controller.deleteTask(oldTask.getName());
            System.out.println("Re-Enter Edited Task Information: ");
            if (!createTask())
                controller.addTask(oldTask);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This Writes the Current Schedule To the File
     */
    private void writeSchedule() {
        System.out.println("Dialog May Be Under Your Windows, Alt + Tab to Navigate Windows.");
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new java.io.File("."));
        jfc.setDialogTitle("Select a Directory");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.showDialog(null, "Open");
        try {
            if (jfc.getSelectedFile() == null)
                throw new NullPointerException("No Directory Selected");
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        String filepath = jfc.getSelectedFile().toString();
        System.out.println("Enter Filename: ");
        String filename = scan.nextLine() + ".json";
        controller.writeSchedule(Paths.get(filepath, filename).toString());
    }

    /**
     *
     */
    private void writeScheduleForInterval() {
        System.out.println("Dialog May Be Under Your Windows, Alt + Tab to Navigate Windows.");
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new java.io.File("."));
        jfc.setDialogTitle("Select a Directory");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.showDialog(null, "Open");
        try {
            if (jfc.getSelectedFile() == null)
                throw new NullPointerException("No Directory Selected");

            System.out.println("View Schedule for Day/Week/Month (1/7/30) Respectively: ");
            int timePeriod = scan.nextInt();
            if (timePeriod != 1 && timePeriod != 7 && timePeriod != 30)
                throw new Exception("Time Period Must Be either 1, 7, or 30\n");
            System.out.println("Enter Start Date of View Period: ");
            double date;
            while (!scan.hasNextDouble()) {
                System.out.println("Date Must Be YYYYMMDD");
                scan.next();
            }
            date = scan.nextDouble();
            scan.nextLine();
            if (new TaskFactory().isInvalidDate(date)) {
                throw new Exception("Bad Date Formatting\n");
            }
            String filepath = jfc.getSelectedFile().toString();
            System.out.println("Enter Filename: ");
            String filename = scan.nextLine() + ".json";
            controller.writeSchedule(Paths.get(filepath, filename).toString(), timePeriod, date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prompts User For Filepath, verifies file is a JSON File, passes file to controller for parsing
     */
    private void readScheduleFromFile() {
        System.out.println("Dialog May Be Under Your Windows, Alt + Tab to Navigate Windows.");
        JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
        jfc.setDialogTitle("Please Select a JSON File");
        jfc.showDialog(null, "Open");

        try {
            if (jfc.getSelectedFile() == null)
                throw new NullPointerException("\nNo File Selected\n");
            String filepath = jfc.getSelectedFile().toString();
            if (!verifyFileType(filepath))
                throw new InvalidFileTypeException("File Must Be a JSON File\n");
            controller.readScheduleFromFile(filepath);
        } catch (NullPointerException | InvalidFileTypeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Displays Schedule From the Start Date for that Time Period
     * Prompts User For Time Period, Day/Week/Month -> (1, 7, 30) Respectively
     * Prompts User For the Start Date For that Time Period
     */
    private void viewSchedule() {
        try {
            System.out.println("View Schedule for Day/Week/Month (1/7/30) Respectively: ");
            int timePeriod = scan.nextInt();
            if (timePeriod != 1 && timePeriod != 7 && timePeriod != 30)
                throw new Exception("Time Period Must Be either 1, 7, or 30\n");
            System.out.println("Enter Start Date of View Period: ");
            double date;
            while (!scan.hasNextDouble()) {
                System.out.println("Date Must Be YYYYMMDD");
                scan.next();
            }
            date = scan.nextDouble();
            if (new TaskFactory().isInvalidDate(date)) {
                throw new Exception("Bad Date Formatting\n");
            }
            controller.viewSchedule(timePeriod, date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Verifies the File Type is a JSON
     * @param filepath Path To File
     * @return true if file is JSON File
     */
    private boolean verifyFileType(String filepath) {
        String[] path = filepath.split(Pattern.quote("\\"));
        String[] fileSplit = path[path.length-1].split("\\.");
        return fileSplit[fileSplit.length - 1].equals("json");
    }


    /**
     * Displays Options For User
     */
    private void display() {
        System.out.println("1: Create a Task");
        System.out.println("2: View a Task");
        System.out.println("3: Delete a Task");
        System.out.println("4: Edit a Task");
        System.out.println("5: Write a Schedule to a File");
        System.out.println("6: Read a Schedule from a File");
        System.out.println("7: View Schedule for a Day/Week/Month");
        System.out.println("8: Write Schedule for a Day/Week/Month");
        System.out.println("9: Exit\n");
        System.out.print("Select an Option: ");
    }

    /**
     * Custom Exception if User Does Not Select JSON File
     */
    private static class InvalidFileTypeException extends Exception {
        public InvalidFileTypeException(String errorMessage) {
            super(errorMessage);
        }
    }
}
