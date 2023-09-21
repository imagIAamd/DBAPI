import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PR123append {
    public static void main(String[] args){
        File file = new File("frasesMatrix.txt");
        FileWriter fileWriter;

        try{
            fileWriter = new FileWriter(file.getPath(), true);
            fileWriter.append("Yo sólo puedo mostrarte la puerta\n");
            fileWriter.append("Tú eres quien la tiene que atravesar\n");
            fileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
