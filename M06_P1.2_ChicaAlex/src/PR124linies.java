import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PR124linies {
    public static void main(String[] args){
        File file = new File("numeros.txt");
        FileWriter fileWriter;
        int num;

        try {
            fileWriter = new FileWriter(file.getPath());
            for(int i=0;i<10;i++){
                num = (int) (Math.random() * 100);
                fileWriter.write(num + "\n");
            }
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
