import java.time.LocalDateTime;
import java.util.Date;

public class Task {
    private String _task;
    private boolean _status;
    private Date _startingDate;
    private Date _endingDate;
    private int _priority;

    public Task(String _task, Date _endingDate, int _priority){
        super();

        this._task = _task;
        this._endingDate = _endingDate;
        this._priority = _priority;
        _status = true;
        _startingDate = new Date();

    }

    public void change_status(){
        if (_status){
            _status = false;
            return;
        }
        _status = true;
    }

    public void change_priority(int _priority){
        this._priority = _priority;
    }

    public String get_task(){
        return _task;
    }

    public Date get_startingDate(){
        return _startingDate;
    }

    public Date get_endingDate(){
        return _endingDate;
    }

    public int get_priority(){
        return _priority;
    }

}
