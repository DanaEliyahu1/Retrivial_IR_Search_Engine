package FileManager;

import Indexer.Indexer;
import Parser.TermInfo;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileManager {

    TreeMap<String, TreePointerToQ> Cache;
    PriorityQueue<PointerCache> Q;
    int PriorityAll;
    HashMap<String,String> cities;
    public static int DocNum;
    public static String postingpath;
    public String DocInfo;


    public FileManager(String docId, String path) {
        Cache=new TreeMap<String,TreePointerToQ>();
        Q=new PriorityQueue<PointerCache>((x,y)->{ return (int)(x.priority-y.priority);});
        cities=new HashMap<String,String>();
        PriorityAll=0;
        DocInfo="";
        postingpath=path;

    }

    public static String geturl(String pointer){
        char firstLetter=pointer.charAt(0);
        if(Character.isLetter(firstLetter)&&Character.isLowerCase(firstLetter)){
            return postingpath+"\\Indexing\\"+firstLetter+".txt";
        }else if(Character.isDigit(firstLetter)||firstLetter=='$'||firstLetter=='%'){
            return postingpath+"\\Indexing\\Numbers.txt";
        }else if(Character.isUpperCase(firstLetter)){
            return postingpath+"\\Indexing\\CapitalLetters.txt";
        }
        return postingpath+"\\Indexing\\Else.txt";
    }

    public void AllTermToDisk() throws InterruptedException {
       PushTermsToDisk(Cache.size());
     }
    public void DocPosting(String ID, String City, int maxtf, int uniqueterms, String mostTf){
        DocNum++;
        AddDocToCityIndex(ID,City);
        DocInfo+=("|"+ ID+","+ City + "," + maxtf+ ","+ uniqueterms+ ","+ mostTf+"\n");
        if(DocInfo.length()>300000){
            AllDocumentsToDisk();
        }

    }
    void AddDocToCityIndex(String DocId,String City){
        if(City.equals(""))return;
        if (cities.containsKey(City)) {
            cities.put(City,cities.get(City)+ "," + DocId);
        } else {
            cities.put(City, DocId);
        }
    }

    public void CitiesToDisk(){
        System.out.println("cities to disk");
        Iterator<Map.Entry<String,String>> it= cities.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,String> currCity=it.next();
            File file =new File(postingpath+"\\Cities\\"+currCity.getKey()+".txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (FileWriter fw = new FileWriter(postingpath+"\\Cities\\"+currCity.getKey()+".txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(currCity.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void AddToPosting(String key, TermInfo value, String docID, int i) {
        if (Cache.containsKey(key)) {
            Cache.get(key).value=Cache.get(key).value + "|" + docID + "," + value.toString();
            Cache.get(key).pc.priority++;
            Cache.put(key, Cache.get(key));
        } else {
            PointerCache newpc=new PointerCache(key, PriorityAll);
            Cache.put(key,new TreePointerToQ(newpc, "|" + docID + "," + value.toString()));
            Q.add(newpc);
        }
        if(Cache.size()>500000){
            PushTermsToDisk(499000);
        }
    }

    private void PushTermsToDisk(int numOfTerms) {
        System.out.println("====DELETING");
        TreeMap<String ,TreePointerToQ> TermToFile=new TreeMap<String,  TreePointerToQ>();
        for (int j = 0; j <numOfTerms ; j++) {
            TreePointerToQ Value=Cache.remove(Q.poll().pointerterm);
            TermToFile.put(Value.pc.pointerterm,Value);
        }
        char currletter = '*';
        StringBuilder [] currentfile=null;
        for (Map.Entry<String, TreePointerToQ> entry : TermToFile.entrySet()) {
            if(entry.getKey().charAt(0)!=currletter){
                if(currentfile!=null){
                    StringJoiner sj=new StringJoiner("\n");
                    for (int k = 0; k <currentfile.length ;k++) {
                        sj.add(currentfile[k]);
                    }
                    try (FileWriter fw = new FileWriter(geturl(""+currletter), false);
                         BufferedWriter bw = new BufferedWriter(fw);
                         PrintWriter out = new PrintWriter(bw)) {
                        out.print(sj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currletter=entry.getKey().charAt(0);
                try {
                    String[] arrFromFile=new String(Files.readAllBytes(Paths.get(geturl(""+currletter))), Charset.defaultCharset()).split("\n");
                    if(Character.isLetter(currletter)&&Character.isLowerCase(currletter)){
                        currentfile=new StringBuilder[Indexer.linenumber[currletter-97]];
                    }else if(Character.isLetter(currletter)&&Character.isUpperCase(currletter)){
                        currentfile=new StringBuilder[Indexer.linenumber[27]];
                    }
                    else{
                        currentfile=new StringBuilder[Indexer.linenumber[26]];
                    }
                    for (int j = 0; j < arrFromFile.length; j++) {
                        currentfile[j]=new StringBuilder(arrFromFile[j]);
                    }
                    for (int j = arrFromFile.length; j <currentfile.length ; j++) {
                        currentfile[j]=new StringBuilder("");
                    }
                    arrFromFile=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            currentfile[entry.getValue().lineNumber].append(entry.getValue().value);


        }
        System.out.println("====STOP- DELETING");
    }

    public void SetCapitalToLoweCasePosting(String key, String value) {
        if (Cache.containsKey(key)) {
            Cache.get(key).value=Cache.get(key).value +value;
            Cache.get(key).pc.priority++;
            Cache.put(key, Cache.get(key));
        } else {
            PointerCache newpc=new PointerCache(key, PriorityAll);
            Cache.put(key,new TreePointerToQ(newpc, value));
            Q.add(newpc);
        }
        if(Cache.size()>500000){
            PushTermsToDisk(499000);
        }
    }

    public void AddCapitalLettersToDisk(TreeMap<String, String> capitalLetterPosting) {
        for (Map.Entry<String, String> entry : capitalLetterPosting.entrySet()) {
            PointerCache newpc=new PointerCache(entry.getKey(), PriorityAll);
            Cache.put(entry.getKey(),new TreePointerToQ(newpc, entry.getValue()));
            Q.add(newpc);
        }

    }

    public void AllDocumentsToDisk() {
        File file =new File(postingpath+"\\Documents.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter fw = new FileWriter(postingpath+"\\Documents.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print( DocInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DocInfo="";
    }
}

class PointerCache {
    int priority;
    String pointerterm;

    public PointerCache(String pointerterm, int priority) {
        this.pointerterm = pointerterm;
        this.priority = priority;
    }
}
class TreePointerToQ{
    public PointerCache pc;
    public String value;
    public int lineNumber;

    public TreePointerToQ(PointerCache pc, String value) {
        this.pc = pc;
        this.value = value;
    }
}
