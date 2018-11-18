package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Controller {

    public Stage stage;
    public CheckBox checkBox;



    public void StartButton(){
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getClassLoader().getResource("GetPath.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        Scene scene = new Scene(root, 290, 300);
        newStage.setScene(scene);

        GetPathController NewGetPath = fxmlLoader.getController();
        NewGetPath.setStage(newStage);
        NewGetPath.setcheckbox(checkBox.isSelected());
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();


    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}






