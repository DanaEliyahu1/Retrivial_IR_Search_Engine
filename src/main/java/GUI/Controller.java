package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class Controller {

    public Stage stage;
    public CheckBox checkBox;
    public GetPathController NewGetPath;
    public static int Termunique;




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

        NewGetPath = fxmlLoader.getController();
        NewGetPath.setStage(newStage);
        NewGetPath.setcheckbox(checkBox.isSelected());
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();


    }
    public void Reset(){
        NewGetPath.postingselected.renameTo(new File(NewGetPath.postingselected.getParent()+"\\"+"Deleted"));
       //* new Thread(()->*/new File(NewGetPath.postingselected.getParent()+"\\"+"Deleted").delete()/*).start()*/;
        try {

            FileUtils.deleteDirectory((new File(NewGetPath.postingselected.getParent()+"\\"+"Deleted")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void ShowDic (){
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getClassLoader().getResource("ShowDictionary.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        Scene scene = new Scene(root, 290, 300);
        newStage.setScene(scene);

        ShowDictionary showd = fxmlLoader.getController();
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(NewGetPath.postingselected.getPath()+"\\Dictionary.txt")), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        showd.text.setText(content);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();


    }
    public void LoadDic(){
        TreeMap<String,String[]> Dic=new TreeMap<>();
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(NewGetPath.postingselected.getPath()+"\\Dictionary.txt")), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String [] Terms =content.split("\n");
        for (int i = 0; i <Terms.length ; i++) {
            String[] keyvalue=Terms[i].split(",");
            String[] value={keyvalue[1]};
            Dic.put(keyvalue[0],value);
        }

        for (Map.Entry<String, String[]> entry : Dic.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue()[0]);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}






