package utils.taskutils;

public interface TaskInterface {

    public void setName(String name);

    public void setType(String type);

    public void setDate(double date);

    public void setStartDate(double startTime);

    public void setDuration(double duration);

    public String getName();

    public String getType();

    public double getDate();

    public double getStartTime();

    public double getDuration();

}
