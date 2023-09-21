import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PR120ReadFile {
    public static void main(String[] args){
        int lineNumber = 1;
        File text = new File("src/PR120.txt");
        Scanner scn;

        try{
            scn = new Scanner(text);
            while(scn.hasNextLine()){
                String line = scn.nextLine();
                System.out.println(lineNumber + " " + line);
                lineNumber ++;

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
