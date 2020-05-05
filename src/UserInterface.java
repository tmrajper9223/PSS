import utils.IOUtils.ScannerUtil;
import utils.taskutils.RecurringTask;
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

    private Controller controller;

    private Scanner scan;

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
    private void createTask() {
        try {
            System.out.println("Enter Task Type: ");
            String type = scan.nextLine();
            Task newTask = controller.createTask(type);
            if (newTask == null)
                throw new NullPointerException();
            System.out.println("New Task Has Been Added!\n");
        } catch (NullPointerException ignored) {
            System.out.println("Failed to Add Task\n");
        }
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
     * Prompts User for Task Name and sends name to controller -> controller.getTask(String name)
     * If Task Not Found controller Throws Exception and returns null
     * NullPointerException ignored here
     * If Task Found Prompt User for fields to edit, when finished with edits call -> controller.replace(String name, Task editedTask)
     * String name -> Name of the task prompted for in the beginning of the function
     * If edits are not valid for formatting, exception will be thrown in controller and return null, function will terminate and no changes saved
     */
    private void editTask() {
        System.out.println("Enter Task Name to Edit: ");
        String name = scan.nextLine();
        try{
            Task task = controller.viewTask(name);
            Task editedTask = new Task();
            editedTask.setName(task.getName());
            editedTask.setType(task.getType());
            editedTask.setDate(task.getDate());
            editedTask.setDuration(task.getDate());

            if(task instanceof RecurringTask){
                editedTask = new RecurringTask();
                ((RecurringTask) editedTask).setFrequency(((RecurringTask) task).getFrequency());
                ((RecurringTask) editedTask).setEndDate(((RecurringTask) task).getEndDate());
            }
            System.out.println(task.toString());
            String edit;
            while(true) {
                System.out.println("What Would you like to Edit?\nType q to quit.");
                edit = scan.nextLine();
                if(edit.equals("q")){
                    break;
                }else{
                    switch(edit){
                        case "Name":
                            System.out.println("Please Enter a new Name: ");
                            String editName = scan.nextLine();
                            editedTask.setName(editName);
                            break;
                        case "Date":
                            System.out.println("Please Enter a new Date: ");
                            double editDate = scan.nextDouble();
                            scan.nextLine();
                            editedTask.setDate(editDate);
                            break;
                        case "StartTime":
                            System.out.println("Please Enter a new StartTime: ");
                            double editStartTime = scan.nextDouble();
                            scan.nextLine();
                            editedTask.setStartDate(editStartTime);
                            break;
                        case "Duration":
                            System.out.println("Please Enter a new Duration: ");
                            double editDuration = scan.nextDouble();
                            scan.nextLine();
                            editedTask.setDuration(editDuration);
                            break;
                        case "EndDate":
                            if(task instanceof RecurringTask){
                                System.out.println("Please Enter a new EndDate: ");
                                double editEndDate = scan.nextDouble();
                                scan.nextLine();
                                ((RecurringTask) editedTask).setEndDate(editEndDate);
                            }else{
                                System.out.println("The Current Task Type does not need an EndDate");
                            }
                            break;
                        case "Frequency":
                            if(task instanceof  RecurringTask){
                                System.out.println("Please Enter a new Frequency: ");
                                double editFrequency = scan.nextDouble();
                                ((RecurringTask) editedTask).setFrequency(editFrequency);
                            }else{
                                System.out.println("The Current Task Type does not need a Frequency");
                            }
                            break;
                    }
                    scan.nextLine();
                }
            }
            
            controller.replace(name, task);

        }catch (NullPointerException ignored){
            System.out.println("A Task With that Name Does Not Exist!\n");
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
