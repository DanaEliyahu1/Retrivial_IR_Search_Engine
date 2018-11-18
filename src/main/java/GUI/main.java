package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Controller controller = new Controller();
        Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("main.fxml").openStream());
        primaryStage.setTitle("Google Demo");
        Scene scene = new Scene(root, 1024, 600);
        primaryStage.setScene(scene);
        controller.setStage(primaryStage);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}