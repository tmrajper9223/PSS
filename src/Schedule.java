import utils.taskutils.AntiTask;
import utils.taskutils.RecurringTask;
import utils.taskutils.Task;
import utils.taskutils.TransientTask;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Schedule is Generated Here, Data Structure for Schedule To Be Determined
 */
public class Schedule {

    protected boolean isValid, hasOverlap, isInvalidAntiTask;

    TreeMap<Double, List<Task>> mappedSchedule;
    private List<Task> schedule;

    public Schedule() {
        isValid = true;
        hasOverlap = false;
        isInvalidAntiTask = false;
        schedule = new ArrayList<>();
        mappedSchedule = new TreeMap<>();
    }

    /**
     * Calls generateSchedule and  returns result, if no errors, errors should have been checked in controller ->
     * Conflicting Names, Overlapping Times, etc...
     * @param taskList List of tasks from Controller
     * @return Generated Schedule
     */
    public List<Task> getSchedule(List<Task> taskList) {
        generateSchedule(taskList);
        if (!isValid) {
            mappedSchedule.clear();
            return null;
        }
        for (Double date : mappedSchedule.keySet()) {
            List<Task> tasks = mappedSchedule.get(date);
            schedule.addAll(tasks);
        }
        return schedule;
    }

    /**
     * Gets Tasks for given dates
     * @param dates List of Dates
     * @return List of Tasks from list of dates
     */
    public List<Task> viewSchedule(List<String> dates) {
        List<Task> viewTasksForPeriod = new ArrayList<>();
        for (String date : dates) {
            for (Double day : mappedSchedule.keySet()) {
                Double parsedDate = Double.parseDouble(date);
                if (parsedDate.equals(day)) {
                    List<Task> tasksForDate = mappedSchedule.get(day);
                    viewTasksForPeriod.addAll(tasksForDate);
                }
            }
        }
        return viewTasksForPeriod;
    }

    /**
     * Takes in the list of individual tasks and generates the entire schedule
     * @param taskList List of tasks from Controller
     */
    private void generateSchedule(List<Task> taskList) {
        List<AntiTask> antiTasks = new ArrayList<>();
        for (Task task : taskList) {
            if (task instanceof AntiTask) {
                antiTasks.add((AntiTask) task);
                continue;
            }
            if (task instanceof RecurringTask) {
                RecurringTask recurringTask = (RecurringTask) task;
                String formattedStartDate = formatLocalDate(recurringTask.getDate());
                String formattedEndDate = formatLocalDate(recurringTask.getEndDate());
                List<String> recurringTaskDates = generateDates(formattedStartDate, formattedEndDate, (int) recurringTask.getFrequency());
                for (String date : recurringTaskDates) {
                    List<Task> taskForDate = new ArrayList<>();
                    mappedSchedule.putIfAbsent(Double.parseDouble(date), taskForDate);
                }
            } else if (task instanceof TransientTask) {
                List<Task> taskForDate = new ArrayList<>();
                mappedSchedule.putIfAbsent(task.getDate(), taskForDate);
            }
        }
        addTasksToDate(taskList, mappedSchedule);

        isValid = canAddAntiTask(antiTasks, mappedSchedule);

        if (!isValid)
            return;

        isValid = isValidSchedule(mappedSchedule);
    }

    /**
     * Checks if any tasks overlap each other
     * @param mappedSchedule Schedule Mapped to Specific Date
     * @return true if overlap
     */
    private boolean isValidSchedule(TreeMap<Double, List<Task>> mappedSchedule) {
        for (Double date : mappedSchedule.keySet()) {
            List<Task> tasks = mappedSchedule.get(date);
            if (doesOverlap(tasks)) {
                hasOverlap = true;
                return false;
            }
        }
        return true;
    }

    /**
     * Iterates over taskList and adds task to corresponding date
     * If Recurring Task, generates dates for time period and adds all recurring task instances in
     * @param taskList List of unique individual tasks
     * @param mappedSchedule Dates are mapped to list of tasks for that day
     */
    private void addTasksToDate(List<Task> taskList, TreeMap<Double, List<Task>> mappedSchedule) {
        for (Task task : taskList) {
            if (task instanceof AntiTask)
                continue;

            if (task instanceof RecurringTask) {
                RecurringTask recurringTask = (RecurringTask) task;
                String formattedStartDate = formatLocalDate(recurringTask.getDate());
                String formattedEndDate = formatLocalDate(recurringTask.getEndDate());
                List<String> recurringTaskDates = generateDates(formattedStartDate, formattedEndDate, (int) recurringTask.getFrequency());
                for (String date : recurringTaskDates) {
                    double taskDate = Double.parseDouble(date);
                    RecurringTask taskInstance = new RecurringTask(
                            task.getName(),
                            task.getType(),
                            taskDate,
                            task.getStartTime(),
                            task.getDuration(),
                            ((RecurringTask) task).getEndDate(),
                            ((RecurringTask) task).getFrequency());

                    boolean containsTask = false;

                    for (Task taskName : mappedSchedule.get(Double.parseDouble(date))) {
                        if (taskName.getName().equals(taskInstance.getName())) {
                            containsTask = true;
                            break;
                        }
                    }

                    if (containsTask)
                        continue;

                    mappedSchedule.get(Double.parseDouble(date)).add(taskInstance);
                    // Sorts the Tasks
                    mappedSchedule.get(Double.parseDouble(date)).sort((o1, o2) -> (int) (o1.getStartTime() - o2.getStartTime()));
                }
                continue;
            }
            boolean containsTask = false;

            for (Task taskName : mappedSchedule.get(task.getDate())) {
                if (taskName.getName().equals(task.getName())) {
                    containsTask = true;
                    break;
                }
            }

            if (containsTask)
                continue;

            mappedSchedule.get(task.getDate()).add(task);
            mappedSchedule.get(task.getDate()).sort((o1, o2) -> (int) (o1.getStartTime() - o2.getStartTime()));
        }
    }

    /**
     * Adds Anti Task if possible, can only replace a recurring task
     * @param antiTasks List of anti tasks from task list
     * @param mappedSchedule Dates are mapped to list of tasks for that day
     */
    private boolean canAddAntiTask(List<AntiTask> antiTasks, TreeMap<Double, List<Task>> mappedSchedule) {
        boolean addedTask = false;

        if (antiTasks.isEmpty())
            return true;

        for (AntiTask antiTask : antiTasks) {
            List<Task> tasks = mappedSchedule.get(antiTask.getDate());
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i) instanceof RecurringTask &&
                    tasks.get(i).getStartTime() == antiTask.getStartTime() &&
                    tasks.get(i).getDuration() == antiTask.getDuration()) {
                    tasks.set(i, antiTask);
                    addedTask = true;
                }
            }
        }
        if (!addedTask) {
            isInvalidAntiTask = true;
        }
        return addedTask;
    }

    /**
     * Generates A List of Dates from the Start Date to End Date, used for generating recurring task instances
     * @param startDate Start Date fo Recurring Task
     * @param endDate End Date of Recurring Task
     * @param frequency Day/Week/Month -> 1/7/30
     * @return List of Dates For Specified Time Period
     */
    private List<String> generateDates(String startDate, String endDate, int frequency) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<String> allDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            allDates.add(formatTaskDate(start.toString()));
            if (frequency == 1) {
                start = start.plusDays(1);
            } else if (frequency == 7) {
                start = start.plusWeeks(1);
            } else if (frequency == 30) {
                start = start.plusMonths(1);
            }
        }
        return allDates;
    }

    /**
     * Generates Dates For Time Period For View Schedule, No End Date is Specified Here
     * @param date Start Date of View Period
     * @param frequency Day/Week/Month -> 1/7/30
     * @return A list of dates for the time period
     */
    public List<String> generateDates(double date, int frequency) {
        LocalDate start = LocalDate.parse(formatLocalDate(date));
        List<String> allDates = new ArrayList<>();
        if (frequency == 1) {
            allDates.add(formatTaskDate(start.toString()));
        } else if (frequency == 7) {
            allDates.add(formatTaskDate(start.toString()));
            LocalDate end = start.plusWeeks(1);
            while (!start.isEqual(end)) {
                start = start.plusDays(1);
                allDates.add(formatTaskDate(start.toString()));
            }
        } else if (frequency == 30) {
            allDates.add(formatTaskDate(start.toString()));
            LocalDate end = start.plusMonths(1);
            while (!start.isAfter(end)) {
                start = start.plusDays(1);
                allDates.add(formatTaskDate(start.toString()));
            }
        }
        return allDates;
    }

    /**
     * Formats Our Current Date, YYYMMDD to YYYY-MM-DD, used for LocalDat Library
     * @param date Date in the format of YYYYMMDD
     * @return Date in the format of YYYY-MM-DD
     */
    public String formatLocalDate(double date) {
        BigDecimal temp = new BigDecimal(date);
        char[] charDateArray = new char[temp.toString().length() + 2];
        for (int i = 0, j = 0; i < charDateArray.length; i++) {
            if (i == 4 || i == 7) {
                continue;
            }
            charDateArray[i] = temp.toString().toCharArray()[j++];
        }
        charDateArray[4] = '-';
        charDateArray[7] = '-';
        return String.valueOf(charDateArray);
    }

    /**
     * Formats LocalDate String to our task formatted date, YYYY-MM-DD -> YYYYMMDD
     * @param date Date in the format of YYYY-MM-DD
     * @return Date in the format of YYYYMMDD
     */
    public String formatTaskDate(String date) {
        String[] temp = date.split("-");
        StringBuilder formatTaskDate = new StringBuilder();
        for (String s : temp)
            formatTaskDate.append(s);
        return formatTaskDate.toString();
    }

    /**
     * Checks for Overlaps in Schedule
     * @param tasks List of Tasks for that specific Day
     * @return True if there is an overlap, false if not
     */
    private boolean doesOverlap(List<Task> tasks) {
        if (tasks.size() == 1)
            return false;
        // Check Each Task against all other tasks that will occur after
        for (int i = 0; i < tasks.size()-1; i++) {
            if (tasks.get(i) instanceof AntiTask)
                continue;
            for (int j = i + 1; j < tasks.size(); j++) {
                // Do Not Check Anti Tasks
                if (tasks.get(j) instanceof AntiTask)
                    continue;
                // If Start Times Are Equal, overlap true
                if (tasks.get(i).getStartTime() == tasks.get(j).getStartTime()) {
                    return true;
                }
                // If End time of task one is grater than or equal to start time of any other tasks occurring after, overlap true
                if ((tasks.get(i).getStartTime() + tasks.get(i).getDuration()) >= tasks.get(j).getStartTime()) {
                    return true;
                }
            }
        }
        return false;
    }

}
