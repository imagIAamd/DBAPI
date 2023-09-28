import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PR125cp {
    public static void main(String[] args){
        String filePath;
        String copyPath;
        File file;
        Scanner scn = new Scanner(System.in);

        System.out.println("-> cp *file path*  *copy path*");
        System.out.println("*file path* :");
        filePath = scn.next();
        System.out.println("*copy path* :");
        copyPath = scn.next();

        file = new File(filePath);
        if (!file.exists()){
            System.out.println("la path no existeix");
        } else {
            try {
                scn = new Scanner(file);
                String text = "";

                while (scn.hasNextLine()){
                    text = text.concat(scn.nextLine() + "\n");
                }

                File newFile = new File(copyPath);
                if(!newFile.exists()){
                    System.out.println("el path de destí no existeix");
                } else if (newFile.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(newFile.getPath());
                    fileWriter.write(text);
                    fileWriter.close();

                    System.out.println("S'ha copiat el fitxer amb path: " + filePath + " a " + copyPath);
                }

            } catch (IOException e) {
                System.out.println("an error happened\n\n");
            }

        }


    }
}
