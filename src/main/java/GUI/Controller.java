package GUI;

import Indexer.FileManager;
import Indexer.Indexer;
import Parser.Document;
import Parser.Parse;
import Parser.ReadFile;
import Parser.Stemmer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class Controller {
    public ChoiceBox choiceBox;
    public TreeMap<String,int []> Index;
    public CheckBox checkBox;
    public static int Termunique;
    public String corpusselected;
    public Stage stage;
    public String postingselected;
    public boolean IsStem;
   public String stopwordsselected;
    public TextField Choosecorpus;
    public TextField ChooseStopWords;
    public TextField ChoosePosting;


    public void StartButton() {
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


    }

    public void Reset() {
        new File(postingselected).renameTo(new File(new File(postingselected).getParent() + "\\" + "Deleted"));
        //* new Thread(()->*/new File(NewGetPath.postingselected.getParent()+"\\"+"Deleted").delete()/*).start()*/;
        try {

            FileUtils.deleteDirectory((new File(new File(postingselected).getParent() + "\\" + "Deleted")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void ShowDic() {
        postingselected=ChoosePosting.getText();
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
        if(Index.size()>0){
            StringBuilder contentindex=new StringBuilder("");
            for (Map.Entry<String, int[]> entry : Index.entrySet()) {
                contentindex.append(entry.getKey()+","+entry.getValue()[0]+"\n");
            }
            content=contentindex.toString();
        }
            else {
    LoadDic();
        }
        showd.text.setText(content);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();


    }

    public void LoadDic() {
        postingselected=ChoosePosting.getText();
        IsStem=checkBox.isSelected();
        TreeMap<String, String[]> Dic = new TreeMap<>();
        String content = null;
        if(!new File(postingselected).exists()){
            showAlert("cannot find dictionary. make sure the posting path is right");
            return;
        }
        try {
            if(new File(postingselected+"\\Stemming" + "\\Dictionary.txt").exists() && IsStem){
                content = new String(Files.readAllBytes(Paths.get(postingselected + "\\Stemming\\Dictionary.txt")), Charset.defaultCharset());


            }
            else if((new File(postingselected+"\\NotStemming" + "\\Dictionary.txt").exists())&& !IsStem){
                content = new String(Files.readAllBytes(Paths.get(postingselected + "\\NotStemming\\Dictionary.txt")), Charset.defaultCharset());

            }
           String [] Terms=content.split("\n");
           Index= new TreeMap<>();
            for (int i = 0; i <Terms.length ; i++) {
                String [] termcut=Terms[i].split(",");
                int [] value={Integer.parseInt(termcut[1]),Integer.parseInt(termcut[2]),Integer.parseInt(termcut[3])};
                Index.put(termcut[0],value);

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


    public void choosecorpus() {
        DirectoryChooser dc = new DirectoryChooser();
        File fcorpusselected = dc.showDialog(stage);
        if (fcorpusselected != null) {
            Choosecorpus.setText(fcorpusselected.getPath());
        }

    }

    public void chooseposting() {
        DirectoryChooser dc = new DirectoryChooser();
        File fpostingselected = dc.showDialog(stage);
        if (fpostingselected != null) {
            ChoosePosting.setText(fpostingselected.getPath());
        }
    }

    public void choosestopwords() {
        FileChooser dc = new FileChooser();
        File fstopwordsselected = dc.showOpenDialog(stage);
        if (fstopwordsselected != null) {
            ChooseStopWords.setText(fstopwordsselected.getPath());
        }
    }

    public void StartDictinory() throws Exception {
        IsStem=checkBox.isSelected();
        postingselected=ChoosePosting.getText();
        corpusselected=Choosecorpus.getText();
        stopwordsselected= ChooseStopWords.getText();
        if(postingselected.equals("") || corpusselected.equals("") || stopwordsselected.equals("")){
            showAlert("Choose Folder Again");
            return;
        }
        String[] Letter = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        if (IsStem) {
            if(new File(postingselected+"/Stemming").exists()){
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
            new File(postingselected+ "/Stemming/Cities").mkdir();
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
            if(new File(postingselected+"/NotStemming").exists()) {
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
            new File(postingselected + "/NotStemming/Cities").mkdir();
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
        // String[] stopwordsarr = {"a", "a's", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "ain't", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "aren't", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "b", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "c", "c'mon", "c's", "came", "can", "can't", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldn't", "course", "currently", "d", "definitely", "described", "despite", "did", "didn't", "different", "do", "does", "doesn't", "doing", "don't", "done", "down", "downwards", "during", "e", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "f", "far", "few", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "g", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "h", "had", "hadn't", "happens", "hardly", "has", "hasn't", "have", "haven't", "having", "he", "he's", "hello", "help", "hence", "her", "here", "here's", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "i'd", "i'll", "i'm", "i've", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll", "it's", "its", "itself", "j", "just", "k", "keep", "keeps", "kept", "know", "knows", "known", "l", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "let's", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "m", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "n", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "o", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "p", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "q", "que", "quite", "qv", "r", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "s", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldn't", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "t", "t's", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "that's", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "there's", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "they'd", "they'll", "they're", "they've", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "u", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "uucp", "v", "value", "various", "very", "via", "viz", "vs", "w", "want", "wants", "was", "wasn't", "way", "we", "we'd", "we'll", "we're", "we've", "welcome", "well", "went", "were", "weren't", "what", "what's", "whatever", "when", "whence", "whenever", "where", "where's", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "who's", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "won't", "wonder", "would", "would", "wouldn't", "x", "y", "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "z", "zero",
        //};
        //String[] stopwordsarr={};
        HashSet StopWord = new HashSet();
//        for (int i = 0; i < stopwordsarr.length; i++) {
//            StopWord.add(stopwordsarr[i]);
//        }
        if (stopwordsselected != null) {
            String content = new String(Files.readAllBytes(Paths.get(stopwordsselected)), Charset.defaultCharset());
            String[] contentsw = content.split("\\s+");
            for (int i = 0; i < contentsw.length; i++) {
                if (!contentsw[i].equals("")) {
                    StopWord.add(contentsw[i].toLowerCase());
                }
            }
        }
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
        Parse.DollarHash = DollarHash;
        Parse.NumberHash = NumberHash;
        Parse.Months = Months;
        Parse.StopWord = StopWord;
        Parse.stemmer = new Stemmer();
        Parse.isStemmig = IsStem;
        Parse.indexer = new Indexer(new FileManager("", postingselected));
        Parse.indexer.fileManager.chunksize=60000;

        ReadFile readFile = new ReadFile(corpusselected);
        long start = System.nanoTime();
        readFile.GetFile();
        long end = System.nanoTime();
        long duratation = end - start;
        double minutes = (duratation / 60000000000.0); //change to second
        showAlert("Total Documents indexed " + FileManager.DocNum + ". Total Unique terms indexed " + Controller.Termunique + ". " + " runtime " + minutes + "seconds.");
        FileManager.DocNum = 0;
        Index=Parse.indexer.Index;
        Parse.indexer=null;
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

}








