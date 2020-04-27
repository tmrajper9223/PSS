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
     *
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
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    readScheduleFromFile();
                    break;
                case 7:
                    break;
                case 8:
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
     * User Enters Task Type and TaskFactory Prompts User for appropriate fields, if null it is ignored
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
     *
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
     *
     */
    private void deleteTask() {

    }

    /**
     *
     */
    private void editTask() {

    }

    /**
     *
     */
    private void writeScheduleToFile() {

    }

    /**
     *
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
     *
     * @param filepath
     * @return
     */
    private boolean verifyFileType(String filepath) {
        String[] path = filepath.split(Pattern.quote("\\"));
        String[] fileSplit = path[path.length-1].split("\\.");
        return fileSplit[fileSplit.length - 1].equals("json");
    }

    /**
     *
     * @param viewPeriod
     */
    private void viewSchedule(int viewPeriod) {

    }

    /**
     *
     * @param writePeriod
     */
    private void writeSchedule(int writePeriod) {

    }

    /**
     *
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

    private static class InvalidFileTypeException extends Exception {
        public InvalidFileTypeException(String errorMessage) {
            super(errorMessage);
        }
    }
}
