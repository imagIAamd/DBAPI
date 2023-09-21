import java.io.File;
import java.io.IOException;

public class PR121Files {
    public static void main(String[] args) throws IOException {
        // Creació de la carpeta
        File myFiles = new File("myFiles");
        if (myFiles.mkdir()){
            System.out.println("Folder created: " + myFiles.getName());
        } else {
            System.out.println("Folder already exists");
        }
        // Creació dels arxius
        File file1 = new File("myFiles/file1.txt");
        File file2 = new File("myFiles/file2.txt");
        if (file1.createNewFile()){
            System.out.println("File created: " + file1.getName());
        } else {
            System.out.println("File " + file1.getName() + " already exists");
        }
        if (file2.createNewFile()){
            System.out.println("File created: " + file2.getName());
        } else {
            System.out.println("File " + file2.getName() + " already exists");
        }
        // Canvi de nom del fitxer file2
        file2.renameTo(new File(myFiles.getPath() + "/renamedFile.txt"));
        // Mostrar els arxius de la carpeta
        printFolderFiles(myFiles);
        // eliminar file1
        file1.delete();
        // Tornar a mostrar els arxius de la carpeta
        printFolderFiles(myFiles);

    }

    public static void printFolderFiles(File folder){
        System.out.println("Els arxius de la carpeta són:");
        for (File file: folder.listFiles()) {
            System.out.println(" - " + file.getName());

        }
    }
}
