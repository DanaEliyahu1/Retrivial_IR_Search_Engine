package GUI;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class GetPathController {
    public File corpusselected;
    public Stage stage;
    private File postingselected;

    public void choosecorpus(){
        FileChooser fc = new FileChooser();
        corpusselected = fc.showOpenDialog(stage);
    }

    public void chooseposting(){
        FileChooser fc = new FileChooser();
        postingselected = fc.showOpenDialog(stage);
    }



}
