package Parser;

import javax.print.Doc;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Document {

int place;
Scanner scanner;

    public Document(File file, int place) {
        this.place = place;
        try {
            this.scanner = new Scanner(file).useDelimiter("\\s*.\\s*");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String GetNextLine(){
        if(scanner.hasNext())
            return scanner.next();
        scanner.close();
        return "";



}

}
