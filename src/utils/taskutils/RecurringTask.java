package utils.taskutils;

public class RecurringTask extends Task implements RecurringTaskInterface {

    private double endDate, frequency;

    public RecurringTask() {

    }

    public RecurringTask(String name, String type, double date, double startTime, double duration, double endDate, double frequency) {
        super(name, type, date, startTime, duration);
        this.frequency = frequency;
        this.endDate = endDate;
    }

    @Override
    public void setEndDate(double endDate) {
        this.endDate = endDate;
    }

    @Override
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    @Override
    public double getEndDate() {
        return endDate;
    }

    @Override
    public double getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "\n{" +
                "\n\tname='" + getName() + '\'' +
                ", \n\ttype='" + getType() + '\'' +
                ", \n\tdate=" + getDate() +
                ", \n\tstartTime=" + getStartTime() +
                ", \n\tduration=" + getDuration() +
                ", \n\tendDate=" + endDate +
                ", \n\tfrequency=" + frequency +
                "\n}";
    }

}
