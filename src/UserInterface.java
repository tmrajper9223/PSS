import utils.IOUtils.ScannerUtil;
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
