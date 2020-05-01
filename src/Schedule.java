import utils.taskutils.Task;

import java.util.*;

/**
 * Schedule is Generated Here, Data Structure for Schedule To Be Determined
 */
public class Schedule {

    Map<Double, List<Task>> schedule;

    /**
     * Creates a new Schedule object with an empty HashMap
     */
    public Schedule() {
        schedule = new HashMap<>();
    }


    /**
     * Creates a new Schedule object using a list of tasks. Each list in the schedule is sorted by time.
     * Key: Date
     * Value: List of tasks for that date
     * @param tasks List of tasks to sort into a schedule.
     */
    public Schedule(List<Task> tasks) {
        schedule = new HashMap<>();
        Task currentTask;
        for (int i=0; i<tasks.size(); i++) {
            currentTask = tasks.get(i);
            schedule.putIfAbsent(currentTask.getDate(), new ArrayList<Task>());
            schedule.get(currentTask.getDate()).add(currentTask);
        }
        for (double date : schedule.keySet()) {
            schedule.get(date).sort(sortByTime);
        }
    }

    /**
     * Displays Schedule for a particular timePeriod(Day/Week/Month) on a specific startDate
     * @param timePeriod Day/Week/Month(1/7/30 Respectively) Inputted by User in the UI
     * @param startDate Date To start Viewing from based on time period, Inputted by User in the UI
     */
    public void view(int timePeriod, double startDate) {
        for (int i=0; i<timePeriod; i++) {
            double currentDate = startDate + i;
            if (schedule.containsKey(currentDate)) {
                for (int j = 0; j < schedule.get(currentDate).size(); j++)
                    System.out.println(schedule.get(currentDate).get(j).toString());
            }
        }
    }

    /**
     * Unclear will ask professor -> Tarik
     * @param timePeriod Day/Week/Month to Write Schedule for
     * @param startDate Start Date for specific time period
     */
    public void write(int timePeriod, double startDate) {

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
}
