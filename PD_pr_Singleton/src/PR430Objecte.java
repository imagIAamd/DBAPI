import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public final class PR430Objecte extends Thread{

    private static PR430Objecte instance;
    private int _valor;
    private int _cops;

    private PR430Objecte(int _valor, int _cops){
        this._valor = _valor;
        this._cops = _cops;
    }

    public static PR430Objecte getInstance(int _valor, int _cops){
        if (instance == null){
            instance = new PR430Objecte(_valor, _cops);
        }
        return instance;
    }

    public int get_valor(){
        return _valor;
    }

    public void run(){
        for (int i = 0; i < _cops; i++) {
            _valor += 2;
            System.out.println(this.toString());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        PR430Objecte instanceOne = PR430Objecte.getInstance(0, 10);
        instanceOne.start();
    }
}