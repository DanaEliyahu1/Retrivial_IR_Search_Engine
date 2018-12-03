package GUI;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.TreeMap;

public class main extends Application {
//opening the java fx app
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("main.fxml").openStream());
        primaryStage.setTitle("Google Demo");
        Scene scene = new Scene(root, 1024, 600);
        primaryStage.setScene(scene);
        Controller controller=fxmlLoader.getController();
        controller.setStage(primaryStage);
       Controller.options=FXCollections.observableArrayList();
        controller.choiceBox.setItems(Controller.options);
        controller.Index =new TreeMap<>();
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}