package Indexer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileManager {

    TreeMap<String, TreeObject> Cache;
    HashMap<String,String> cities;
    public static int DocNum;
    public static String postingpath;
    public StringBuilder DocInfo;
    public ExecutorService threadpool;
    public int chunksize;

    public FileManager(String docId, String path) {
        Cache=new TreeMap<String, TreeObject>();
        cities=new HashMap<String,String>();
        DocInfo=new StringBuilder("");
        postingpath=path;
        threadpool= Executors.newSingleThreadExecutor();
    }

    public synchronized void AddToPosting(String key, Integer value, String docID,int line) {
        if (Cache.containsKey(key)) {
            Cache.put(key,new TreeObject(Cache.get(key).value + "|" + docID + "," + value,line));
        } else {
            Cache.put(key,new TreeObject("|" + docID + "," + value,line));
        }
            if(Cache.size()>chunksize){
                TreeMap<String , TreeObject> TermToFile=Cache;
                PushTermsToDisk(TermToFile);
                Cache=new TreeMap<String,TreeObject>();
                chunksize+=500;
            }
    }

    private void PushTermsToDisk(TreeMap<String , TreeObject> TermToFile) {
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("====DELETING");
                char currletter = '*';
                StringBuilder [] currentfile=null;
                for (Map.Entry<String, TreeObject> entry : TermToFile.entrySet()) {
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
                                bw.close();
                                fw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        currletter=entry.getKey().charAt(0);
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
                    currentfile[entry.getValue().lineNumber].append(entry.getValue().value);


                }
                System.out.println("====STOP- DELETING");
            }
        });

    }

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

    public synchronized void AllTermToDisk() throws InterruptedException {
        TreeMap<String , TreeObject> TermToFile=Cache;
        PushTermsToDisk(TermToFile);
        Cache=new TreeMap<String,TreeObject>();
     }
    public void DocPosting(String ID, String City, int maxtf, int uniqueterms, String mostTf, String cityplaces, String filename){
        DocNum++;
        AddDocToCityIndex(ID,City);
        DocInfo.append("|"+ ID+","+ City + "," + maxtf+ ","+ uniqueterms+ ","+ mostTf+","+cityplaces+","+filename);
        if(DocInfo.length()>200000){
            AllDocumentsToDisk();
        }
       // System.out.println(DocNum);
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
              // e.printStackTrace();
            }
            try (FileWriter fw = new FileWriter(postingpath+"\\Cities\\"+currCity.getKey()+".txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(currCity.getValue());
                bw.close();
                fw.close();

            } catch (IOException e) {
             //   e.printStackTrace();
            }
        }

    }

    public synchronized void SetCapitalToLoweCasePosting(String key, String value,int line) {
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

    public void AddCapitalLettersToCache(TreeMap<String, String> capitalLetterPosting) {
        for (Map.Entry<String, String> entry : capitalLetterPosting.entrySet()) {
            Cache.put(entry.getKey(),new TreeObject(entry.getValue(),0));
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
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DocInfo=new StringBuilder("");
    }
}

class TreeObject {
    public String value;
    public int lineNumber;

    public TreeObject(String value, int lineNumber) {
        this.lineNumber=lineNumber;
        this.value = value;
    }
}
