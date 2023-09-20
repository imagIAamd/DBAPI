import java.util.ArrayList;
import java.util.Date;

public class TaskManager {
    private ArrayList<Task> _tasks;

    public TaskManager(){
        super();

    }

    public void add_task(String task, Date endingDate, int priority){
        _tasks.add(new Task(task, endingDate, priority));

    }

}
