package Indexer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileManager {
/*
A class we added to work with files so the logic can be kept in the indexer
 */
    TreeMap<String, TreeObject> Cache; //new terms are added here
     public HashMap<String,StringBuilder> cities; //new cities and their documents
    public static int DocNum; //counter for later use
    public static String postingpath; //where to read and write
    public StringBuilder DocInfo; //info of all documents is stored here
    public int chunksize; //can be dynamic
    public FileManager(String docId, String path) {
        Cache=new TreeMap<String, TreeObject>();
        cities=new HashMap<String,StringBuilder>();
        DocInfo=new StringBuilder("");
        postingpath=path;
    }
//adding new term with its posting information or adding it to an existing key
    public void AddToPosting(String key, Integer value, String docID,int line) {
        if (Cache.containsKey(key)) {
            Cache.put(key,new TreeObject(Cache.get(key).value + "|" + docID + "," + value,line));
        } else {
            Cache.put(key,new TreeObject("|" + docID + "," + value,line));
        }
        //if the cache is too big we empty it
            if(Cache.size()>chunksize){
                TreeMap<String , TreeObject> TermToFile=Cache;
                PushTermsToDisk(TermToFile);
                Cache=new TreeMap<String,TreeObject>();
                chunksize+=0; //zeros? can be changed to 500
            }
    }
//the actual writing to disk.
    /*
    because treemap is ordered we write each file in the alphabet in order
    and we get to a new letter we change file
     */
    private void PushTermsToDisk(TreeMap<String , TreeObject> TermToFile) {
               // System.out.println("====DELETING");
                char currletter = '*';
                StringBuilder [] currentfile=null;
                for (Map.Entry<String, TreeObject> entry : TermToFile.entrySet()) {
                    if(entry.getKey().charAt(0)!=currletter){
                        //writing when finished with letter
                        if(currentfile!=null){
                            StringJoiner sj=new StringJoiner("\n");
                            for (int k = 0; k <currentfile.length ;k++) {
                                sj.add(currentfile[k]);
                            }
                            try (FileWriter fw = new FileWriter(geturl(""+currletter), false);
                                 BufferedWriter bw = new BufferedWriter(fw);
                                 PrintWriter out = new PrintWriter(bw)) {
                                out.print(sj);
                                bw.close();
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        currletter=entry.getKey().charAt(0);
                        //reading the file of new letter
                        try {
                            String[] arrFromFile=new String(Files.readAllBytes(Paths.get(geturl(""+currletter))), Charset.defaultCharset()).split("\n");
                            if(Character.isLetter(currletter)&&Character.isLowerCase(currletter)){
                                currentfile=new StringBuilder[Indexer.linenumber[currletter-97]+1];
                            }else if(Character.isLetter(currletter)&&Character.isUpperCase(currletter)){
                                currentfile=new StringBuilder[Indexer.linenumber[27]+1];
                            }
                            else{
                                currentfile=new StringBuilder[Indexer.linenumber[26]+1];
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
                    //actually appending each term
                    currentfile[entry.getValue().lineNumber].append(entry.getValue().value);


                }
             //   System.out.println("====STOP- DELETING");

               }
//classifies each term to file
    public static String geturl(String Term){
        char firstLetter=Term.charAt(0);
        if(Character.isLetter(firstLetter)&&Character.isLowerCase(firstLetter)){
            return postingpath+"\\Indexing\\"+firstLetter+".txt";
        }else if(Character.isDigit(firstLetter)||firstLetter=='$'||firstLetter=='%'){
            return postingpath+"\\Indexing\\Numbers.txt";
        }else if(Character.isUpperCase(firstLetter)){
            return postingpath+"\\Indexing\\CapitalLetters.txt";
        }
        return postingpath+"\\Indexing\\Else.txt";
    }
//getting cache values that we didnt get to
    public void AllTermToDisk() throws InterruptedException {
        TreeMap<String , TreeObject> TermToFile=Cache;
        PushTermsToDisk(TermToFile);
        Cache=new TreeMap<String,TreeObject>();
     }
     //adding information of new documents;
    public void DocPosting(String ID, String City, int maxtf, int uniqueterms, String mostTf, String cityplaces, String filename){
        DocNum++;
        AddDocToCityIndex(ID,City);
        DocInfo.append("|"+ ID+","+ City + "," + maxtf+ ","+ uniqueterms+ ","+ mostTf+","+cityplaces+","+filename);
        if(DocInfo.length()>1500000){
            AllDocumentsToDisk();
        }
       // System.out.println(DocNum);
    }
    //information on each city is updated when finished every document
    void AddDocToCityIndex(String DocId,String City){
        if(City.equals(""))return;
        if (cities.containsKey(City)) {
            cities.put(City,cities.get(City).append("," + DocId) );
        } else {
            cities.put(City, new StringBuilder(DocId));
        }
    }
//writing all cities we got Disk
    public void CitiesToDisk(TreeMap<String, String[]> cityIndex){
        File file =new File(postingpath+"\\Cities.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        StringBuilder content=new StringBuilder("");
           Iterator<Map.Entry<String,StringBuilder>> it= cities.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,StringBuilder> currCity=it.next();
            content.append(currCity.getKey()+"|"+cityIndex.get(currCity.getKey())[0]+"|"+cityIndex.get(currCity.getKey())[1]+"|"+cityIndex.get(currCity.getKey())[2]+"|"+currCity.getValue().toString()+"\n");

        }
        try (FileWriter fw = new FileWriter(postingpath+"\\Cities.txt", false);
               BufferedWriter bw = new BufferedWriter(fw);
               PrintWriter out = new PrintWriter(bw)) {
            out.print(content.toString());
            bw.close();
            fw.close();

        } catch (IOException e) {
            //   e.printStackTrace();
        }
        cities=new HashMap<>();

    }
//adding terms they started as capital letters but are saved lowercase in lowercase format
    public  void SetCapitalToLoweCasePosting(String key, String value,int line) {
        if (Cache.containsKey(key)) {
            Cache.get(key).value=Cache.get(key).value +value;
            Cache.put(key, Cache.get(key));
        } else {
            Cache.put(key,new TreeObject(value,line));
        }
        if(Cache.size()>50000){
            TreeMap<String , TreeObject> TermToFile=Cache;
            PushTermsToDisk(TermToFile);
            Cache=new TreeMap<String,TreeObject>();
        }
    }
//adding capital letters to the cache and files
    public void AddCapitalLettersToCache(TreeMap<String, StringBuilder> capitalLetterPosting, TreeMap<String, int[]> allCapitalLetterWords) {
        for (Map.Entry<String, StringBuilder> entry : capitalLetterPosting.entrySet()) {
            Cache.put(entry.getKey(),new TreeObject(entry.getValue().toString(),allCapitalLetterWords.get(entry.getKey())[1]));
        }
    }
//documents left are now written to disk with their posting info
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
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DocInfo=new StringBuilder("");
    }
}
// a SubClass which is juat a container for the tree
class TreeObject {
    public String value;
    public int lineNumber;
    public TreeObject(String value, int lineNumber) {
        this.lineNumber=lineNumber;
        this.value = value;
    }
}
