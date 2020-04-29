package utils.taskutils;

public class Task implements TaskInterface {

    public final static String[] TRANSIENT_CATEGORIES = {"Visit", "Shopping", "Appointment"};
    public final static String[] RECURRING_CATEGORIES = {"Class", "Study", "Sleep", "Exercise", "Meal", "Work"};
    public final static String[] ANTI_CATEGORIES = {"Cancellation"};

    private String name, type;
    private double date, startTime, duration;

    public Task() {

    }

    public Task(String name, String type, double date, double startTime, double duration) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setDate(double date) {
        this.date = date;
    }

    @Override
    public void setStartDate(double startTime) {
        this.startTime = startTime;
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public double getDate() {
        return date;
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\t\"Name\" : \"" + name + '\"' +
                ", \n\t\"Type\" : \"" + type + '\"' +
                ", \n\t\"Date\" : \"" + (int)date +
                ", \n\t\"StartTime\" : " + startTime +
                ", \n\t\"Duration\" : " + duration +
                "\n}";
    }
}
