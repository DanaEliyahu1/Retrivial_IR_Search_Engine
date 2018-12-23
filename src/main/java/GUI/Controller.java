package GUI;

import Indexer.FileManager;
import Indexer.Indexer;
import Parser.Parse;
import Parser.ReadFile;
import Ranker.Ranker;
import Ranker.RankDoc;
import Searcher.Searcher;
import javafx.collections.ObservableList;
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
    public static ObservableList<String> langoptions;
    public static ObservableList<MenuItem> cityoptions;

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

    public void choosersulttarget(){
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
    public void run(){
        SendToParse();
        if(Query.getText().equals("")){
            QueryFromFile();
        }else{

            RunQuery(Query.getText(),"351");
        }

    }

    private String[] getcitiesarr(ObservableList<MenuItem> items) {
    String [] choosecity = new String[items.size()];
    Iterator<MenuItem> it=items.iterator();
    int i=0;
       while (it.hasNext()){
           CustomMenuItem curr=(CustomMenuItem)it.next();
           choosecity[i]=curr.getText();
           i++;
       }
       return choosecity;
    }

    private TreeMap<String,Integer> getDocLength(int[] avdl) {
        int sum=0;
        TreeMap<String,Integer> DocsLength=new TreeMap<>();
        try {
            String[] arrFromFile = new String(Files.readAllBytes(Paths.get(FileManager.postingpath+"/UsefulDocuments.txt")), Charset.defaultCharset()).split("\\|");
            for (int i = 1; i < arrFromFile.length; i++) {
                String [] curr=arrFromFile[i].split(",");
                sum+=Integer.parseInt(curr[1]);
                DocsLength.put(curr[0],Integer.parseInt(curr[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        avdl[0]=(sum/DocsLength.size());
        return DocsLength;
    }
    public void QueryFromFile(){
        String path=queryselected.getText();
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] query = content.split("<top>");
        for (int i = 1; i <query.length ; i++) {
            String number=query[i].split("Number: ")[1];
            String QueryNum = number.split("\n")[0].replace(" ","");
            String befortitle=number.split("<title>")[1];
            String Query=befortitle.split("\n")[0];
            RunQuery(Query,QueryNum);

        }

    }

    public void SendToParse(){
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
        if(checkBoxstem.isSelected()){
            FileManager.postingpath=postingselected+ "/Stemming";
        }else{
            FileManager.postingpath=postingselected+ "/NotStemming";
        }
    }
    public void RunQuery(String Query,String Qnumber){
        Searcher searcher=new Searcher();
        TreeMap<String,String> Searchresults = searcher.Searcher(Query,getcitiesarr(City.getItems()),Index,checksemantica.isSelected());
        int[] avdl=new int[1];
        TreeMap<String,Integer> docLengthTree=getDocLength(avdl);
        if (checkBoxcity.isSelected()){

        }
        Ranker ranker=new Ranker(0.75,2,avdl[0]);
        TreeSet<RankDoc> Rankedresults=ranker.Rank(Query.split(" ").length,Searchresults,Index,docLengthTree);
        Iterator<RankDoc> iterator=Rankedresults.iterator();
        String s="";
        while (iterator.hasNext()){
            RankDoc curr=iterator.next();
            System.out.println("key: " + curr.docid + " ,value:" + curr.rank);
            s+=(Qnumber+" 0 "+curr.docid+" 1 42.38 mt\n");
        }
        try (FileWriter fw = new FileWriter(resultpathselected.getText()+"\\results.txt", true);
                   BufferedWriter bw = new BufferedWriter(fw);
                   PrintWriter out = new PrintWriter(bw)) {
            out.print(s);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}








