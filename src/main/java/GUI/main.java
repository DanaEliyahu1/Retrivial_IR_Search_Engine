package GUI;
import Parser.Parse;
import Parser.ReadFile;
import Parser.Stemmer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
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
       Controller.langoptions =FXCollections.observableArrayList();
        controller.choiceBoxlang.setItems(Controller.langoptions);
        Controller.cityoptions =FXCollections.observableArrayList();
        Controller.entities =FXCollections.observableArrayList();
       controller.City.getItems().addAll(Controller.cityoptions);
        controller.Index =new TreeMap<>();
        primaryStage.show();

        InitStaticFunc();
    }
// we must run this before running a query or indexing a corpus
    private void InitStaticFunc() {
        String[] Monthsarr = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Sept", "Oct", "Nov", "Dec", "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
        HashSet Months = new HashSet();
        for (int i = 0; i < Monthsarr.length; i++) {
            Months.add(Monthsarr[i]);
        }
        HashSet NumberHash = new HashSet();
        NumberHash.add("Thousand");
        NumberHash.add("Million");
        NumberHash.add("Billion");
        NumberHash.add("Trillion");
        String[] dollararr = {"dollars", "Dollars", "$", "m", "bn", "U.S.", "million", "billion", "trillion"};
        HashSet DollarHash = new HashSet();
        for (int i = 0; i < dollararr.length; i++) {
            DollarHash.add(dollararr[i]);
        }
        HashMap<String, String> replace = new HashMap<>();
        replace.put("?", "");
        replace.put("*", "");
        replace.put(",", "");
        replace.put(";", "");
        replace.put(":", "");
        replace.put("<", "");
        replace.put(">", "");
        replace.put("|", "");
        replace.put("^", "");
        replace.put("\"", "");
        replace.put("\'", "");
        replace.put("(", "");
        replace.put(")", "");
        replace.put("[", "");
        replace.put("]", "");
        replace.put("{", "");
        replace.put("}", "");
        replace.put("!", "");
        replace.put("+", "");
        //puting the lists in the right places:
        ReadFile.replace = replace;
        Parse.DollarHash = DollarHash;
        Parse.NumberHash = NumberHash;
        Parse.Months = Months;
        Parse.stemmer = new Stemmer();

    }


    public static void main(String[] args) {
        launch(args);
    }

}