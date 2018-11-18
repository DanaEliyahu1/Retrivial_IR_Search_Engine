package GUI;

import FileManager.FileManager;
import Parser.Parse;
import Parser.ReadFile;
import Parser.Stemmer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashSet;

public class GetPathController {
    public File corpusselected;
    public Stage stage;
    public File postingselected;
    public boolean IsStem;

    public void choosecorpus(){
        FileChooser fc = new FileChooser();
        corpusselected = fc.showOpenDialog(stage);
    }

    public void chooseposting(){
        FileChooser fc = new FileChooser();
        postingselected = fc.showOpenDialog(stage);
    }


    public void StartDictinory(){
        if(IsStem){
            new File(postingselected.getPath()+"/Stemming/Indexing").mkdir();
            new File(postingselected.getPath()+"/Stemming/Indexing/a-e").mkdir();
            new File(postingselected.getPath()+"/Stemming/Indexing/f-j").mkdir();
            new File(postingselected.getPath()+"/Stemming/Indexing/k-o").mkdir();
            new File(postingselected.getPath()+"/Stemming/Indexing/p-t").mkdir();
            new File(postingselected.getPath()+"/Stemming/Indexing/u-z").mkdir();
            new File(postingselected.getPath()+"/Stemming/Indexing/Numbers").mkdir();
            new File(postingselected.getPath()+"/Stemming/Documents").mkdir();
            new File(postingselected.getPath()+"/Stemming/Cities").mkdir();
        }
        else {  new File(postingselected.getPath()+"/NotStemming/Indexing").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Indexing/a-e").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Indexing/f-j").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Indexing/k-o").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Indexing/p-t").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Indexing/u-z").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Indexing/Numbers").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Documents").mkdir();
            new File(postingselected.getPath()+"/NotStemming/Cities").mkdir();

        }
        String[] stopwordsarr = {"a", "a's", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "ain't", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "aren't", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "b", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "c", "c'mon", "c's", "came", "can", "can't", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldn't", "course", "currently", "d", "definitely", "described", "despite", "did", "didn't", "different", "do", "does", "doesn't", "doing", "don't", "done", "down", "downwards", "during", "e", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "f", "far", "few", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "g", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "h", "had", "hadn't", "happens", "hardly", "has", "hasn't", "have", "haven't", "having", "he", "he's", "hello", "help", "hence", "her", "here", "here's", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "i'd", "i'll", "i'm", "i've", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll", "it's", "its", "itself", "j", "just", "k", "keep", "keeps", "kept", "know", "knows", "known", "l", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "let's", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "m", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "n", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "o", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "p", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "q", "que", "quite", "qv", "r", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "s", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldn't", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "t", "t's", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "that's", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "there's", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "they'd", "they'll", "they're", "they've", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "u", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "uucp", "v", "value", "various", "very", "via", "viz", "vs", "w", "want", "wants", "was", "wasn't", "way", "we", "we'd", "we'll", "we're", "we've", "welcome", "well", "went", "were", "weren't", "what", "what's", "whatever", "when", "whence", "whenever", "where", "where's", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "who's", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "won't", "wonder", "would", "would", "wouldn't", "x", "y", "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "z", "zero",
        };
        HashSet StopWord = new HashSet();
        for (int i = 0; i < stopwordsarr.length; i++) {
            StopWord.add(stopwordsarr[i]);
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
        Parse.DollarHash=DollarHash;
        Parse.NumberHash=NumberHash;
        Parse.Months=Months;
        Parse.StopWord=StopWord;
        Parse.stemmer=new Stemmer();
        Parse.fileManager=new FileManager("",postingselected.getPath());


        ReadFile readFile = new ReadFile(corpusselected.getPath());
        readFile.GetFile();
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setcheckbox(boolean checkbox) {
        this.IsStem = checkbox;
    }
}