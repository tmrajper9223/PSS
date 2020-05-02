import utils.IOUtils.ScannerUtil;
import utils.taskutils.RecurringTask;
import utils.taskutils.Task;

import javax.swing.*;
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
                    writeScheduleToFile();
                    break;
                case 6:
                    readScheduleFromFile();
                    break;
                case 7:
                    viewSchedule();
                    break;
                case 8:
                    writeSchedule();
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
            System.out.println(newTask.toString());
        } catch (NullPointerException ignored) { }
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
        System.out.println("Enter Task Name: ");
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
            String edit = "";
            while(!edit.equals("q")) {
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
                                ((RecurringTask)editedTask).setEndDate(editEndDate);
                            }else{
                                System.out.println("The Current Task Type does not need an EndDate");
                            }
                            break;
                        case "Frequency":
                            if(task instanceof  RecurringTask){
                                System.out.println("Please Enter a new Frequency: ");
                                double editFrequency = scan.nextDouble();
                                scan.nextLine();
                                ((RecurringTask) editedTask).setFrequency(editFrequency);
                            }else{
                                System.out.println("The Current Task Type does not need a Frequency");
                            }
                            break;
                    }
                }
            }
            controller.replace(name, editedTask);

        }catch (NullPointerException ignored){
            System.out.println("A Task With that Name Does Not Exist!\n");
        }
    }

    /**
     * This Writes the Current Schedule To the File
     */
    private void writeScheduleToFile() {

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
                throw new InvalidFileTypeException("File Must Be a JSON File");
            controller.readScheduleFromFile(filepath);
        } catch (NullPointerException | InvalidFileTypeException e) {
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
     * Displays Schedule From the Start Date for that Time Period
     * Prompts User For Time Period, Day/Week/Month -> (1, 7, 30) Respectively
     * Prompts User For the Start Date For that Time Period
     */
    private void viewSchedule() {
        System.out.println("View schedule for how many days? (1, 7, 30)");
        int timePeriod = scan.nextInt();
        while (!(timePeriod == 1 || timePeriod == 7 || timePeriod == 30) ) {
            System.out.print("Invalid time period. Please reenter time period: ");
            timePeriod = scan.nextInt();
        }
        System.out.print("Enter Start Date (yyyyMMdd): ");
        double startDate = scan.nextDouble();
        while (!checkDate(startDate)) {
            System.out.print("Invalid date. Enter Start Date (yyyyMMdd): ");
            startDate = scan.nextInt();
        }
        controller.viewSchedule(timePeriod, startDate);
    }

    private boolean checkDate(double date) {
        int month = (int)date % 10000;
        month /= 100;
        int day = (int)date % 100;
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                if (day <= 31) return true; break;
            case 4: case 6: case 9: case 11:
                if (day <= 30) return true; break;
            case 2:
                if (day <= 28 || (checkLeapYear(date) && day <= 29)) return true; break;
            default: return false;
        }
        return false;
    }
    private boolean checkLeapYear(double date) {
        int year = (int)date/10000;
        if (year%4==0) {
            if (year%400==0)
                return true;
            else if (year%100==0)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Unclear, Will Ask Professor About This -> Tarik
     * Writes Schedule From the Start Date for that Time Period
     * Prompts User For Time Period, Day/Week/Month -> (1, 7, 30) Respectively
     * Prompts User For the Start Date For that Time Period
     */
    private void writeSchedule() {

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
