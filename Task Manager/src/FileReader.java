import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader {

    // File connection sample code
    /*
    public static void main(String[] args) {
        try (FileInputStream fileInputStream = new FileInputStream("./src/example.txt")) {
            int data;
            while ((data = fileInputStream.read()) != -1) {
                System.out.print((char) data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */
    private String filePath;

    public FileReader(){
        super();
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getFileText(){
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            int data;

        }
    }
}
