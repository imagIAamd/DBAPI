import java.io.File;
import java.util.Scanner;

public class PR122cat {
    public static void main(String[] args){
        Scanner scn;

        try{
            scn = new Scanner(System.in);
            System.out.println("Ruta de l'arxiu: ");
            String path = scn.nextLine();
            File file = new File(path);

            if (file.exists() && file.isDirectory()) {
                System.out.println("El path no correspon a un arxiu, sinó a una carpeta");

            } else if (file.exists() && file.isFile()) {
                scn = new Scanner(file);
                while (scn.hasNextLine()){
                    String line = scn.nextLine();
                    System.out.println(line);
                }

            } else {
                System.out.println("El fitxer amb path: " + file.getPath() + " no existeix");

            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
