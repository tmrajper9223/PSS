package utils.taskutils;

import org.json.simple.JSONObject;
import utils.IOUtils.ScannerUtil;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Scanner;

public class TaskFactory {

    private Scanner scan;

    public TaskFactory() {
        scan = ScannerUtil.getInstance();
    }

    /**
     * Checks if Task contains invalid format of fields
     * @param jsonObject JSONObject From JSON File Array
     * @return Task if task has valid formatting of fields, null if invalid format
     */
    public Task createTask(JSONObject jsonObject) {
        try {
            if (isInvalidType(jsonObject.get("Type")))
                throw new InvalidTaskFormatException("Type Must Be a String and Must Be a Valid Category");

            String type = (String) jsonObject.get("Type");

            if (containsType(Task.RECURRING_CATEGORIES, type))
                return createRecurringTask(jsonObject);
            else if (containsType(Task.ANTI_CATEGORIES, type))
                return createAntiTask(jsonObject);
            else if (containsType(Task.TRANSIENT_CATEGORIES, type))
                return createTransientTask(jsonObject);

        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param type
     * @return
     */
    public Task createTask(String type) {
        try {
            if (containsType(Task.RECURRING_CATEGORIES, type))
                return createRecurringTask(type);
            else if (containsType(Task.TRANSIENT_CATEGORIES, type))
                return createTransientTask(type);
            else if (containsType(Task.ANTI_CATEGORIES, type))
                return createAntiTask(type);
            else
                throw new InvalidTaskFormatException("Not a Valid Task Type");
        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Creates Anti Task
     * @param jsonObject JSON Object From JSON Array
     * @return Anti Task if all fields valid, null if not
     */
    private AntiTask createAntiTask(JSONObject jsonObject) {
        if (isInvalidFormat(jsonObject))
            return null;

        try {
            if (isInvalidDate(jsonObject.get("Date")))
                throw new InvalidTaskFormatException("Date Must be a Long or Double Data Type with Format of, YYYYMMDD");
        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
            return null;
        }

        String name, type;
        double date, startTime, duration;
        name = (String) jsonObject.get("Name");
        type = (String) jsonObject.get("Type");
        date = parseToDouble(jsonObject.get("Date"));
        startTime = parseToDouble(jsonObject.get("StartTime"));
        duration = parseToDouble(jsonObject.get("Duration"));

        return new AntiTask(name, type, date, startTime, duration);
    }

    /**
     *
     * @param type
     * @return
     */
    private AntiTask createAntiTask(String type) {
        System.out.println("Enter Task Information (Name,Date,StartTime,Duration):");
        String taskInfo = scan.nextLine();
        taskInfo = taskInfo.replaceAll("\\s+", "");
        String[] splitTaskInfo = taskInfo.split(",");
        if (isInvalidFormat(splitTaskInfo))
            return null;
        String name = splitTaskInfo[0];

        double date, startTime, duration;
        date = Double.parseDouble(splitTaskInfo[1]);
        startTime = Double.parseDouble(splitTaskInfo[2]);
        duration = Double.parseDouble(splitTaskInfo[3]);

        return new AntiTask(name, type, date, startTime, duration);
    }

    /**
     * Creates Transient Task
     * @param jsonObject JSON Object From JSON Array
     * @return Transient Task if all fields valid, null if not
     */
    private TransientTask createTransientTask(JSONObject jsonObject) {
        if (isInvalidFormat(jsonObject))
            return null;

        try {
            if (isInvalidDate(jsonObject.get("Date")))
                throw new InvalidTaskFormatException("Date Must be a Long or Double Data Type with Format of, YYYYMMDD");
        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
            return null;
        }

        String name, type;
        double date, startTime, duration;
        name = (String) jsonObject.get("Name");
        type = (String) jsonObject.get("Type");
        date = parseToDouble(jsonObject.get("Date"));
        startTime = parseToDouble(jsonObject.get("StartTime"));
        duration = parseToDouble(jsonObject.get("Duration"));

        return new TransientTask(name, type, date, startTime, duration);
    }

    /**
     *
     * @param type
     * @return
     */
    private TransientTask createTransientTask(String type) {
        System.out.println("Enter Task Information (Name,Date,StartTime,Duration):");
        String taskInfo = scan.nextLine();
        taskInfo = taskInfo.replaceAll("\\s+", "");
        String[] splitTaskInfo = taskInfo.split(",");
        if (isInvalidFormat(splitTaskInfo))
            return null;
        String name = splitTaskInfo[0];

        double date, startTime, duration;
        date = Double.parseDouble(splitTaskInfo[1]);
        startTime = Double.parseDouble(splitTaskInfo[2]);
        duration = Double.parseDouble(splitTaskInfo[3]);

        return new TransientTask(name, type, date, startTime, duration);
    }

    /**
     * Creates the Recurring Task
     * @param jsonObject JSON Object From JSON Array
     * @return Recurring Task if all fields are valid, null if not
     */
    private RecurringTask createRecurringTask(JSONObject jsonObject) {
        if (isInvalidFormat(jsonObject))
            return null;

        try {
            if (isInvalidDate(jsonObject.get("StartDate")) || isInvalidDate(jsonObject.get("EndDate")))
                throw new InvalidTaskFormatException("Date Must be a Long or Double Data Type with Format of, YYYYMMDD");
        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
            return null;
        }

        String name, type;
        double date, startTime, duration, endDate, frequency;
        name = (String) jsonObject.get("Name");
        type = (String) jsonObject.get("Type");
        date = parseToDouble(jsonObject.get("StartDate"));
        startTime = parseToDouble(jsonObject.get("StartTime"));
        duration = parseToDouble(jsonObject.get("Duration"));

        try {
            if (isInvalidEndDate(date, jsonObject.get("EndDate")))
                throw new InvalidTaskFormatException("EndDate Must be a Long or Double Data Type with Format of, YYYYMMDD, and Later Than the StartDate");
            if (isInvalidFrequency(jsonObject.get("Frequency")))
                throw new InvalidTaskFormatException("Frequency Must be a Number with the Value, 1, 7, 30");
        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
            return null;
        }

        endDate = parseToDouble(jsonObject.get("EndDate"));
        frequency = parseToDouble(jsonObject.get("Frequency"));

        return new RecurringTask(name, type, date, startTime, duration, endDate, frequency);
    }

    /**
     *
     * @param type
     * @return
     */
    private RecurringTask createRecurringTask(String type) {
        System.out.println("Enter Task Information (Name,Date,StartTime,Duration,EndDate,Frequency):");
        String taskInfo = scan.nextLine();
        taskInfo = taskInfo.replaceAll("\\s+", "");
        String[] splitTaskInfo = taskInfo.split(",");
        if (isInvalidFormat(splitTaskInfo))
            return null;
        String name = splitTaskInfo[0];

        double date, startTime, duration, endDate, frequency;
        date = Double.parseDouble(splitTaskInfo[1]);
        startTime = Double.parseDouble(splitTaskInfo[2]);
        duration = Double.parseDouble(splitTaskInfo[3]);

        try {
            endDate = Double.parseDouble(splitTaskInfo[4]);
            frequency = Double.parseDouble(splitTaskInfo[5]);
        } catch (Exception e) {
            System.out.println("Make Sure Fields Are: Name(String); Date(s)(Number:YYYYMMDD); 0 <= StartTime <= 23.75; 0.25 <= Duration <= 23.75; Frequency=1, 7, 30");
            return null;
        }

        try {
            if (isInvalidDate(endDate) || isInvalidEndDate(startTime, endDate))
                throw new InvalidTaskFormatException("End Date Must Be a Number with the format, YYYYMMDD, and must be after the start date");
            if (isInvalidFrequency(frequency))
                throw new InvalidTaskFormatException("Frequency Must Be a Number that is either; 1(Daily), 7(Weekly), or 30(Monthly)");
        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return new RecurringTask(name, type, date, startTime, duration, endDate, frequency);
    }

    /**
     * Checks Shared Fields For Transient and Anti Tasks
     * @param jsonObject Object Parsed From JSONObject
     * @return true if Shared Fields Are Valid Format
     */
    private boolean isInvalidFormat(JSONObject jsonObject) {
        try {
            if (isInvalidName(jsonObject.get("Name")))
                throw new InvalidTaskFormatException("Name Field Must Be a String");

            if (isInvalidStartTime(jsonObject.get("StartTime")))
                throw new InvalidTaskFormatException("StartTime Field Must Be a Positive Long or Double from 0 to 23.75");

            if (isInvalidDuration(jsonObject.get("Duration")))
                throw new InvalidTaskFormatException("Duration Field Must Be a Positive Long or Double from 0.25 to 23.75");

        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
            return true;
        }
        return false;
    }

    /**
     * Parses User Input Task, NOT From File
     * @param splitTaskInfo String Array of the User's Input
     * @return true if Format is Invalid
     */
    private boolean isInvalidFormat(String[] splitTaskInfo) {
        try {
            if (splitTaskInfo.length != 4 && splitTaskInfo.length != 6)
                throw new InvalidTaskFormatException("Enter Task Information in 1 one line separated by commas(Recurring Tasks Must Include End Date and Frequency): name, date, ...");

            double date, startTime, duration;
            String name = splitTaskInfo[0];
            date = Double.parseDouble(splitTaskInfo[1]);
            startTime = Double.parseDouble(splitTaskInfo[2]);
            duration = Double.parseDouble(splitTaskInfo[3]);

            if (isInvalidName(name))
                throw new InvalidTaskFormatException("Name Field Must Be a String");

            if (isInvalidDate(date))
                throw new InvalidTaskFormatException("Date Must be a Long or Double Data Type with Format of, YYYYMMDD");

            if (isInvalidStartTime(startTime))
                throw new InvalidTaskFormatException("StartTime Field Must Be a Positive Long or Double from 0 to 23.75");

            if (isInvalidDuration(duration))
                throw new InvalidTaskFormatException("Duration Field Must Be a Positive Long or Double from 0.25 to 23.75");

        } catch (InvalidTaskFormatException e) {
            System.out.println(e.getMessage());
            return true;
        } catch (Exception e) {
            System.out.println("Make Sure Fields Are: Name(String); Date(Number:YYYYMMDD); 0 <= StartTime <= 23.75; 0.25 <= Duration <= 23.75");
            return true;
        }
        return false;
    }

    /**
     * Checks Data type of the Object instance
     * @param nameObject Object Parsed From JSONObject
     * @return true if Object Type is not a String
     */
    private boolean isInvalidName(Object nameObject) {
        return !(nameObject instanceof String);
    }

    /**
     * Checks Data type of the Object instance and checks category of the object
     * @param typeObject Object Parsed From JSONObject
     * @return true if Object is not a String or if is not a Valid Task Category
     */
    private boolean isInvalidType(Object typeObject) {
        if (!(typeObject instanceof String))
            return false;
        final String taskType = (String) typeObject;

        return !((containsType(Task.ANTI_CATEGORIES, taskType)) ||
                (containsType(Task.TRANSIENT_CATEGORIES, taskType)) ||
                (containsType(Task.RECURRING_CATEGORIES, taskType)));
    }

    /**
     * Iterates through array and compares values to the taskType
     * @param categories Array of task categories
     * @param taskType taskType from JSONObject
     * @return true if array contains taskType
     */
    private boolean containsType(String[] categories, String taskType) {
        for (String category : categories)
            if (category.toLowerCase().equals(taskType.toLowerCase()))
                return true;

        return false;
    }

    /**
     * Checks if Date follows correct format of, YYYYMMDD
     * @param dateObject Object Parsed From JSONObject
     * @return true if neither long or double or does NOT follows correct format of YYYYMMDD, where numOfDays in month matches year
     */
    private boolean isInvalidDate(Object dateObject) {
        double date;
        if (dateObject instanceof Double)
            date = (double) dateObject;
        else if (dateObject instanceof Long)
            date = convertToDouble((Long) dateObject);
        else
            return true;

        String temp = new BigDecimal(date).toString();

        char[] dateAsCharArray = temp.toCharArray();

        if (dateAsCharArray.length != 8)
            return true;

        String monthAsString = String.valueOf(dateAsCharArray[4]) + dateAsCharArray[5];
        int month = Integer.parseInt(monthAsString);
        if (month < 1 || month > 12)
            return true;

        StringBuilder yearAsString = new StringBuilder();
        for (int i = 0; i < 4; i++)
            yearAsString.append(dateAsCharArray[i]);
        int year = Integer.parseInt(String.valueOf(yearAsString));

        String dayAsString = String.valueOf(dateAsCharArray[6]) + dateAsCharArray[7];
        int day = Integer.parseInt(dayAsString);

        YearMonth yearMonth = YearMonth.of(year, month);
        int numOfDays = yearMonth.lengthOfMonth();

        return day < 1 || day > numOfDays;
    }

    /**
     * Checks if the data type is either a long or double and converts to double, and checks if is valid startTime
     * @param startTimeObject Object Parsed From JSONObject
     * @return true if neither long or double or start time is not positive, or between [0, 23.75]
     */
    private boolean isInvalidStartTime(Object startTimeObject) {
        double startTime;
        if (startTimeObject instanceof Double)
            startTime = (double) startTimeObject;
        else if (startTimeObject instanceof Long)
            startTime = convertToDouble((Long) startTimeObject);
         else
            return true;

        return (!(startTime >= 0)) || (!(startTime <= 23.75));
    }

    /**
     * Checks if the data type is either a long or double and converts to double, and checks if is valid duration
     * @param durationObject Object Parsed From JSONObject
     * @return true if neither long or double or start time is not positive, or between [0.25, 23.75]
     */
    private boolean isInvalidDuration(Object durationObject) {
        double duration;
        if (durationObject instanceof Double)
            duration = (Double) durationObject;
        else if (durationObject instanceof Long)
            duration = convertToDouble((Long) durationObject);
        else
            return true;

        return (!(duration >= 0.25)) || (!(duration <= 23.75));
    }

    /**
     * Checks if the endDate and the Start Date are equal or less than each other
     * @param startDate Parsed Double From Previous Function
     * @param endDateObject Object Parsed From JSONObject
     * @return true if endDate is either Long or Double, and if endDate is greater than the startDate, at a later date than the startDate
     */
    private boolean isInvalidEndDate(double startDate, Object endDateObject) {
        double endDate;
        if (endDateObject instanceof Double)
            endDate = (double) endDateObject;
        else if (endDateObject instanceof Long)
            endDate = convertToDouble((Long) endDateObject);
        else
            return true;

        return endDate < startDate;
    }

    /**
     * Checks if the data type is either a long or Double or Integer and converts to double, and checks if is valid frequency
     * @param frequencyObject Object Parsed From JSONObject
     * @return true if neither long or double or Integer and if value is not a valid frequency value, 1 (daily), 7 (Weekly), 30 (Monthly)
     */
    private boolean isInvalidFrequency(Object frequencyObject) {
        double frequency;
        if (frequencyObject instanceof Double || frequencyObject instanceof Integer)
            frequency = (double) frequencyObject;
        else if (frequencyObject instanceof Long)
            frequency = convertToDouble((Long) frequencyObject);
        else
            return true;

        return !((frequency == 1) || (frequency == 7) || (frequency == 30));
    }

    /**
     * Converts Long Object to double primitive type
     * @param longObject Long Object Parsed From JSONObject
     * @return doubleValue of Long Object
     */
    private double convertToDouble(Long longObject) {
        return longObject.doubleValue();
    }

    /**
     * Coverts Object to Double
     * @param obj Object Field From JSONObject
     * @return double Value of Object
     */
    private double parseToDouble(Object obj) {
        if (obj instanceof Double || obj instanceof Integer)
            return (double) obj;
        else
            return convertToDouble((Long) obj);
    }

    private static class InvalidTaskFormatException extends Exception {
        public InvalidTaskFormatException(String errorMessage) {
            super(errorMessage);
        }
    }

}
