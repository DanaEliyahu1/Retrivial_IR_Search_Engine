package GUI;

import Indexer.FileManager;
import Indexer.Indexer;
import Parser.Parse;
import Parser.ReadFile;
import Ranker.*;
import Ranker.RankDoc;
import Searcher.Searcher;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Controller {
    public ChoiceBox choiceBoxlang;
    public TreeMap<String, int[]> Index;
    public CheckBox checkBoxstem;
    public static int Termunique;
    public String corpusselected;
    public Stage stage;
    public String postingselected;
    public boolean IsStem;
    public String stopwordsselected;
    public TextField Choosecorpus;
    public TextField ChooseStopWords;
    public TextField ChoosePosting;
    public TextField resultpathselected;
    public TextField queryselected;
    public TextField Query;
    public MenuButton City;
    public CheckBox checkBoxcity;
    public CheckBox checksemantica;
    public CheckBox checkBoxentities;
    public Label ShowEntities;
    public ChoiceBox ChooseDocForEntities;
    public static ObservableList<String> langoptions;
    public static ObservableList<MenuItem> cityoptions;
    public static ObservableList<String> entities;

    // deleting all files created while indexing
    public void Reset() {
        new File(postingselected).renameTo(new File(new File(postingselected).getParent() + "\\" + "Deleted"));
        try {

            FileUtils.deleteDirectory((new File(new File(postingselected).getParent() + "\\" + "Deleted")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //showing the dictionary terms and frequencies
    public void ShowDic() {
        postingselected = ChoosePosting.getText();
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

        ShowDictionaryController showd = fxmlLoader.getController();
        String content = null;
        if (Index.size() > 0) {
            StringBuilder contentindex = new StringBuilder("");
            for (Map.Entry<String, int[]> entry : Index.entrySet()) {
                contentindex.append(entry.getKey() + "," + entry.getValue()[0] + "\n");
            }
            content = contentindex.toString();
        } else {
            LoadDic();
        }
        showd.text.setText(content);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();


    }

    //by sending the same path used for indexing, the user can load the dictionary created
    public void LoadDic() {
        postingselected = ChoosePosting.getText();
        IsStem = checkBoxstem.isSelected();
        TreeMap<String, String[]> Dic = new TreeMap<>();
        String content = null;
        if (!new File(postingselected).exists()) {
            showAlert("cannot find dictionary. make sure the posting path is right");
            return;
        }
        try {
            if (new File(postingselected + "\\Stemming" + "\\Dictionary.txt").exists() && IsStem) {
                content = new String(Files.readAllBytes(Paths.get(postingselected + "\\Stemming\\Dictionary.txt")), Charset.defaultCharset());


            } else if ((new File(postingselected + "\\NotStemming" + "\\Dictionary.txt").exists()) && !IsStem) {
                content = new String(Files.readAllBytes(Paths.get(postingselected + "\\NotStemming\\Dictionary.txt")), Charset.defaultCharset());

            }
            String[] Terms = content.split("\n");
            Index = new TreeMap<>();
            for (int i = 0; i < Terms.length; i++) {
                String[] termcut = Terms[i].split(",");
                int[] value = {Integer.parseInt(termcut[1]), Integer.parseInt(termcut[2]), Integer.parseInt(termcut[3])};
                Index.put(termcut[0], value);

            }

        } catch (IOException e) {
            showAlert("Start Indexing Or choose another folder");
        }
        String[] Terms = content.split("\n");
        for (int i = 0; i < Terms.length; i++) {
            String[] keyvalue = Terms[i].split(",");
            String[] value = {keyvalue[1]};
            Dic.put(keyvalue[0], value);
        }
        try {
            String cities = "";
            if (new File(postingselected + "\\Stemming" + "\\Cities.txt").exists() && IsStem) {
                cities = new String(Files.readAllBytes(Paths.get(postingselected + "\\Stemming\\Cities.txt")), Charset.defaultCharset());


            } else if ((new File(postingselected + "\\NotStemming" + "\\Cities.txt").exists()) && !IsStem) {
                cities = new String(Files.readAllBytes(Paths.get(postingselected + "\\NotStemming\\Cities.txt")), Charset.defaultCharset());

            }
            if(City.getItems().size()==0){
            String[] allCities=cities.split("\n");
            for (int i = 0; i < allCities.length; i++) {
                SetCities(allCities[i].split("\\|")[0]);
            }

                City.getItems().addAll(Controller.cityoptions);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        showAlert("Load dictionary is finished");
    }

    //choose path of corpus
    public void choosecorpus() {
        DirectoryChooser dc = new DirectoryChooser();
        File fcorpusselected = dc.showDialog(stage);
        if (fcorpusselected != null) {
            Choosecorpus.setText(fcorpusselected.getPath());
        }

    }

    //choose path of posting files
    public void chooseposting() {
        DirectoryChooser dc = new DirectoryChooser();
        File fpostingselected = dc.showDialog(stage);
        if (fpostingselected != null) {
            ChoosePosting.setText(fpostingselected.getPath());
        }
    }

    //choose the stop_words.txt file
    public void choosestopwords() {
        FileChooser dc = new FileChooser();
        File fstopwordsselected = dc.showOpenDialog(stage);
        if (fstopwordsselected != null) {
            ChooseStopWords.setText(fstopwordsselected.getPath());
        }
    }

    public void choosequery() {
        FileChooser fc = new FileChooser();
        File Qselected = fc.showOpenDialog(stage);
        if (Qselected != null) {
            queryselected.setText(Qselected.getPath());
        }
    }

    public void choosersulttarget() {
        DirectoryChooser dc = new DirectoryChooser();
        File fqueryselected = dc.showDialog(stage);
        if (fqueryselected != null) {
            resultpathselected.setText(fqueryselected.getPath());
        }
    }

    /*the main method- creating posting folders where needed, configuring stemming if needed
     getting the stop-words and starting the creation of the dictionary*/
    public void StartDictinory() throws Exception {
        IsStem = checkBoxstem.isSelected();
        postingselected = ChoosePosting.getText();
        corpusselected = Choosecorpus.getText();
        stopwordsselected = ChooseStopWords.getText();
        if (postingselected.equals("") || corpusselected.equals("") || stopwordsselected.equals("")) {
            showAlert("Choose Folder Again");
            return;
        }
        //letters are needed to create the posting files
        String[] Letter = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        if (IsStem) {
            //stemming files
            if (new File(postingselected + "/Stemming").exists()) {
                FileUtils.deleteDirectory((new File(postingselected + "/Stemming")));
            }
            new File(postingselected + "/Stemming").mkdir();
            new File(postingselected + "/Stemming/Indexing").mkdir();
            for (int i = 0; i < Letter.length; i++) {
                File file = new File(postingselected + "/Stemming/Indexing/" + Letter[i] + ".txt");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            File file = new File(postingselected + "/Stemming/Indexing/Numbers.txt");
            File file1 = new File(postingselected + "/Stemming/Indexing/CapitalLetters.txt");
            File file2 = new File(postingselected + "/Stemming/Indexing/Else.txt");
            try {
                file.createNewFile();
                file1.createNewFile();
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            postingselected = postingselected + "/Stemming";
        } else {
            //NotStemming files
            if (new File(postingselected + "/NotStemming").exists()) {
                FileUtils.deleteDirectory((new File(postingselected + "/NotStemming")));
            }
            new File(postingselected + "/NotStemming").mkdir();
            new File(postingselected + "/NotStemming/Indexing").mkdir();
            for (int i = 0; i < Letter.length; i++) {
                File file = new File(postingselected + "/NotStemming/Indexing/" + Letter[i] + ".txt");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            File file = new File(postingselected + "/NotStemming/Indexing/Numbers.txt");
            File file1 = new File(postingselected + "/NotStemming/Indexing/CapitalLetters.txt");
            File file2 = new File(postingselected + "/NotStemming/Indexing/Else.txt");
            try {
                file.createNewFile();
                file1.createNewFile();
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            postingselected = postingselected + "/NotStemming";

        }
        //creating stopwords list and other lists necessey for the run
        HashSet StopWord = new HashSet();
        if (stopwordsselected != null) {
            String content = new String(Files.readAllBytes(Paths.get(stopwordsselected)), Charset.defaultCharset());
            String[] contentsw = content.split("\\s+");
            for (int i = 0; i < contentsw.length; i++) {
                if (!contentsw[i].equals("")) {
                    StopWord.add(contentsw[i].toLowerCase());
                }
            }
            if (StopWord.contains("may")) {
                StopWord.remove("may");
            }
        }
        Parse.StopWord = StopWord;
        Parse.isStemmig = IsStem;

        Parse.indexer = new Indexer(new FileManager("", postingselected));
        Parse.indexer.fileManager.chunksize = 60000;
        ReadFile readFile = new ReadFile(corpusselected);
        long start = System.nanoTime();
        //the actual process
        readFile.GetFile();
        //
        long end = System.nanoTime();
        long duratation = end - start;
        double minutes = (duratation / 1000000000.0); //change to second
        showAlert("Total Documents indexed " + FileManager.DocNum + ". Total Unique terms indexed " + Controller.Termunique + ". " + " runtime " + minutes + "seconds.");
        FileManager.DocNum = 0;
        City.getItems().addAll(Controller.cityoptions);
        Index = Parse.indexer.Index;
        Parse.indexer = null;


    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    //can be called from the readFile to add languages
    public static void SetLanguages(String languages) {
        if (!langoptions.contains(languages)) {
            langoptions.add(languages);
        }
    }

    public static void SetCities(String cities) {
        CheckBox cb0 = new CheckBox(cities);
        CustomMenuItem item0 = new CustomMenuItem(cb0);
        item0.setHideOnClick(false);
        cityoptions.addAll(item0);
    }

    public void run() {
        if(Index==null || Index.size()==0){
            showAlert("Please load dictionary");
            return;
        }
        else if(resultpathselected.getText().equals("")){
            showAlert("Please insert result path");
            return;
        }
        if(new File(resultpathselected.getText()+"\\results.txt").exists()&&
                new File(resultpathselected.getText()+"\\withoutentities.txt").exists()
                ){
               new File(resultpathselected.getText()+"\\results.txt").delete();

                new File(resultpathselected.getText()+"\\withoutentities.txt").delete();
        }
        SendToParse();
        if (Query.getText().equals("")) {
            QueryFromFile();
        } else {
            if(queryselected.getText().equals("")){
                showAlert("Please choose query file");
                return;
            }
            RunQuery(Query.getText(), "351");
        }
        showAlert("Retrieval finished");

    }

    private String[] getcitiesarr(ObservableList<MenuItem> items) {
        ArrayList<String> CityByuser=new ArrayList<>();
        Iterator<MenuItem> it = items.iterator();
        int i = 0;
         while (it.hasNext()) {
            CustomMenuItem curr = (CustomMenuItem) it.next();
            if(((CheckBox)curr.getContent()).isSelected()){
                CityByuser.add(((CheckBox)curr.getContent()).getText());
            }
        }

      /*  ObservableList<MenuItem> s= City.getItems();
        for (int j = 0; j <s.size() ; j++) {
            String curr=(String) s.get(j);
            CityByuser.add( curr);
        }*/
            String[] res=new String[CityByuser.size()];
        for (int j = 0; j <res.length ; j++) {
            res[j]=CityByuser.get(j);
        }
        return res;
    }

    private TreeMap<String, DocInfo> getDocLength(int[] avdl) {
        int sum = 0;
        TreeMap<String, DocInfo> DocsLength = new TreeMap<>();
        //docLength
        try {
            String[] arrFromFile = new String(Files.readAllBytes(Paths.get(FileManager.postingpath + "/UsefulDocuments.txt")), Charset.defaultCharset()).split("\\|");
            for (int i = 1; i < arrFromFile.length; i++) {
                String[] curr = arrFromFile[i].split(",");
                sum += Integer.parseInt(curr[1]);
                DocsLength.put(curr[0], new DocInfo(Integer.parseInt(curr[1]),1,Integer.parseInt(curr[3])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TF-IDF
        try {
            String[] arrFromFile = new String(Files.readAllBytes(Paths.get(FileManager.postingpath + "/CosSimMechana")), Charset.defaultCharset()).split("\n");
            for (int i = 1; i < arrFromFile.length; i++) {
                String[] curr = arrFromFile[i].split(",");
                int Doclanght=DocsLength.get(curr[0]).lengthdoc;
                double tfidf=Double.parseDouble(curr[1]);
                DocsLength.get(curr[0]).sigmatfidf=tfidf;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        avdl[0] = (sum / DocsLength.size());
        return DocsLength;
    }

    public void QueryFromFile() {
        String path = queryselected.getText();
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] query = content.split("<top>");
        for (int i = 1; i < query.length; i++) {
            String number = query[i].split("Number: ")[1];
            String QueryNum = number.split("\n")[0].replace(" ", "");
            String befortitle = number.split("<title>")[1];
            String Query = befortitle.split("\n")[0];
            RunQuery(Query, QueryNum);

        }

    }

    public void SendToParse() {
        Parse.isStemmig = checkBoxstem.isSelected();
        HashSet StopWord = new HashSet();
        if (stopwordsselected != null) {
            String content = null;
            try {
                content = new String(Files.readAllBytes(Paths.get(stopwordsselected)), Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] contentsw = content.split("\\s+");
            for (int i = 0; i < contentsw.length; i++) {
                if (!contentsw[i].equals("")) {
                    StopWord.add(contentsw[i].toLowerCase());
                }
            }
            if (StopWord.contains("may")) {
                StopWord.remove("may");
            }
        }
        Parse.StopWord = StopWord;
        if (checkBoxstem.isSelected()) {
            FileManager.postingpath = postingselected + "/Stemming";
        } else {
            FileManager.postingpath = postingselected + "/NotStemming";
        }
    }

    public void RunQuery(String Query, String Qnumber) {
        Searcher searcher = new Searcher();
        TreeMap<String, String> Searchresults = searcher.Searcher(Query, Index, checksemantica.isSelected());
        int[] avdl = new int[1];
        TreeMap<String, DocInfo> docLengthTree = getDocLength(avdl);
        if (checkBoxcity.isSelected()) {
            Searchresults = filterBycities(getcitiesarr(City.getItems()), Searchresults);
        }
        Ranker ranker = new Ranker(0.5, 2, avdl[0]);
        TreeSet<RankDoc> Rankedresults = ranker.Rank(Query.split(" ").length, Searchresults, Index, docLengthTree);
        ResultToUser(Rankedresults, Qnumber,Query);

    }

    private void ResultToUser(TreeSet<RankDoc> Rankedresults, String Qnumber, String query) {
        Iterator<RankDoc> iterator = Rankedresults.iterator();
        String trecevalresult = "";
        String NoEntities ="===="+ query+"====\n";
       // String WithEntities="===="+query+"====\n";

        while (iterator.hasNext()) {
            RankDoc curr = iterator.next();
            entities.add(curr.docid);
            System.out.println("key: " + curr.docid + " ,value:" + curr.rank);
            trecevalresult += (Qnumber + " 0 " + curr.docid + " 1 42.38 mt\n");
            NoEntities+=(curr.docid+"\n");
         //   WithEntities+=(curr.docid+"\t"+Entities.get(curr.docid)+"\n");
        }
        ChooseDocForEntities.setItems(entities);
      /*  try (FileWriter fw = new FileWriter(resultpathselected.getText() + "\\withentities.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(WithEntities);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try (FileWriter fw = new FileWriter(resultpathselected.getText() + "\\results.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(trecevalresult);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter fw = new FileWriter(resultpathselected.getText() + "\\withoutentities.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(NoEntities);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private TreeMap<String, String> filterBycities(String[] getcitiesarr, TreeMap<String, String> searchResults) {
        TreeMap<String, String> filteredResults = new TreeMap<>();  //docs to return
        HashSet<String> docsFromCities = new HashSet<String>();     //docs from cities the user selecte4d
        TreeMap<String, String> citiesFromPosting = new TreeMap<>(); // cities from posting

        try {
            String content = new String(Files.readAllBytes(Paths.get(FileManager.postingpath + "\\Cities.txt")), Charset.defaultCharset());
            String[] cities = content.split("\n");

            for (int i = 0; i < cities.length; i++) {
                String[] cityInfo = cities[i].split("\\|");
                citiesFromPosting.put(cityInfo[0], cityInfo[4]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //filter docs from city
        for (int i = 0; i < getcitiesarr.length; i++) {
            String postingFromCurrentCity = citiesFromPosting.get(getcitiesarr[i]);
            String[] docsFromCurrentCity = postingFromCurrentCity.split(",");
            for (int j = 0; j < docsFromCurrentCity.length; j++) {
                docsFromCities.add(docsFromCurrentCity[j]);
            }
        }
        for (Map.Entry<String, String> entry : searchResults.entrySet()) {
            if (docsFromCities.contains(entry.getKey())) {
                filteredResults.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredResults;
    }
    public void ShowResultToUser(){
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

        ShowDictionaryController showd = fxmlLoader.getController();
        String content = null;
        try {
            if(checkBoxentities.isSelected()){
                content = new String(Files.readAllBytes(Paths.get(resultpathselected.getText() + "\\withentities.txt")), Charset.defaultCharset());
        }
        else {
                content = new String(Files.readAllBytes(Paths.get(resultpathselected.getText() + "\\withoutentities.txt")), Charset.defaultCharset());
            }
        } catch (IOException e) {
          //  e.printStackTrace();
            showAlert("Please Run Query ");
            return;
        }
        showd.text.setText(content);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();

    }

    public void ShowEntities(ActionEvent actionEvent) {
        String docId=(String) ((ChoiceBox)actionEvent.getSource()).getValue();
        try {
            String content = new String(Files.readAllBytes(Paths.get(FileManager.postingpath + "\\UsefulDocuments.txt")), Charset.defaultCharset());
            String[] DocEntities = content.split("\\|");
            for (int i = 1; i < DocEntities.length; i++) {
                String [] Entiite=DocEntities[i].split(",");
                if(docId.equals(Entiite[0])){
                    if(Entiite.length==4){
                        ShowEntities.setText(Entiite[2].replaceAll("\\*","\n"));
                        ShowEntities.setText(Entiite[2].replaceAll("\\_",","));
                        //Entities.put(Entiite[0],Entiite[2]);
                    }else {
                        ShowEntities.setText("");
                        //Entities.put(Entiite[0],"");
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
   public void OpenChoiseEntities(){
        if(checkBoxentities.isSelected()){
            ChooseDocForEntities.setDisable(false);
        }
        else{
            ChooseDocForEntities.setDisable(true);
        }
   }
}








