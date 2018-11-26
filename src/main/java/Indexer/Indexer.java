package Indexer;

import FileManager.FileManager;
import GUI.Controller;
import Parser.TermInfo;

import java.io.*;
import java.util.*;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Indexer {
    public static TreeMap<String,int []> AllCapitalLetterWords;
    private ExecutorService threadpool;
    FileManager fileManager;
    TreeMap <String,int []> Index;
    TreeMap <String,String[]> CityIndex;
    TreeMap<String,String> CapitalLetterPosting;
    public static int [] linenumber; //27-numbers 28-capitalLetters

    public Indexer(FileManager fileManager) {
        AllCapitalLetterWords =new TreeMap<>();
        Index = new TreeMap<> ();
        CityIndex=new TreeMap<>();
        this.fileManager=fileManager;
         linenumber=new  int[28];
         CapitalLetterPosting=new TreeMap<>();
        this.threadpool= Executors.newSingleThreadExecutor();
    }

    public void IndexCities(){
        File folder = new File(fileManager.postingpath+"\\Cities");
        File[] ListOfFile= folder.listFiles();
        for (int i = 0; i <ListOfFile.length ; i++) {
            String key= ListOfFile[i].getName();
            key=key.substring(0,key.length()-3);
            CityIndex.put(key,null);
        }
    }

    public void ResultToFile(String DocID, TreeMap<String, TermInfo> SpecialTermsMap, TreeMap<String, TermInfo> TermsMap, String City, TreeMap<String, TermInfo> capitalLetterWords){
                int counter=0;
                String mostTf="";
                for (Map.Entry<String, TermInfo> entry : SpecialTermsMap.entrySet()) {
                    AddTermToDic(entry.getKey(),entry.getValue(),DocID);
                    if(counter<entry.getValue().TermCount){
                        counter=entry.getValue().TermCount;
                        mostTf=entry.getKey();
                    }
                }
                for (Map.Entry<String, TermInfo> entry : TermsMap.entrySet()) {
                    AddTermToDic(entry.getKey(), entry.getValue(),DocID);
                    if (counter < entry.getValue().TermCount) {
                        counter = entry.getValue().TermCount;
                        mostTf=entry.getKey();
                    }
                }
                int uniqueterms=SpecialTermsMap.size() + TermsMap.size();

                fileManager.DocPosting(DocID,City,counter,uniqueterms,mostTf);
        for (Map.Entry<String, TermInfo> entry : capitalLetterWords.entrySet()) {
            if(!Index.containsKey(entry.getKey().toLowerCase())){
               AddTermToCapital(entry.getKey(),entry.getValue(),DocID);
            }
            else{
                AddTermToDic(entry.getKey().toLowerCase(), entry.getValue(),DocID);
            }
        }
    }

    private void AddTermToCapital(String key, TermInfo value, String DocID) {
        if(Index.containsKey(key)){
            int [] setvalue=Index.get(key);
            setvalue[0]++;
            setvalue[2]+=value.TermCount;
            CapitalLetterPosting.put(key,CapitalLetterPosting.get(key)+"|" + DocID + "," + value.toString());
        }
        else {
            int [] setnewvalue={1,linenumber[28],value.TermCount};
            Index.put(key,setnewvalue);
            CapitalLetterPosting.put(key,"|" + DocID + "," + value.toString());
            linenumber[28]++;
        }
    }

    private void AddTermToDic(String key, TermInfo value,String DocID) {
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                if(Index.containsKey(key)){
                    int [] setvalue=Index.get(key);
                    setvalue[0]++;
                    setvalue[2]+=value.TermCount;
                    fileManager.AddToPosting(key,value,DocID,setvalue[1]);
                }
                else {
                    int line=-1;
                    if(Character.isLetter(key.charAt(0))){
                        line=linenumber[key.toLowerCase().charAt(0)-97];
                    }else{
                        line=linenumber[26];
                    }
                    int [] setnewvalue={1,line,value.TermCount};
                    Index.put(key,setnewvalue);
                    fileManager.AddToPosting(key,value,DocID,line);
                    if(Character.isLetter(key.charAt(0))){
                        linenumber[key.toLowerCase().charAt(0)-97]++;
                    }else{
                        linenumber[26]++;
                    }
                }

            }
        });

    }

    public void FinishIndexing() {
        System.out.println("waiting for finish");
        try {
            threadpool.shutdown();
            threadpool.awaitTermination(40,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finish & start finish to disk");
        fileManager.CitiesToDisk();
        System.out.println(" start capital letters to disk");
        for (Map.Entry<String, int[]> entry : AllCapitalLetterWords.entrySet()) {
            if(Index.containsKey(entry.getKey().toLowerCase())){
                int[] value=Index.get(entry.getKey().toLowerCase());
                value[0]+=AllCapitalLetterWords.get(entry.getKey())[0];
                value[2]+=AllCapitalLetterWords.get(entry.getKey())[2];
                fileManager.SetCapitalToLoweCasePosting(entry.getKey().toLowerCase(),CapitalLetterPosting.get(entry.getKey()));
                AllCapitalLetterWords.remove(entry);
            }
        }
        fileManager.AddCapitalLettersToDisk(CapitalLetterPosting);
        try {
            fileManager.AllTermToDisk();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fileManager.AllDocumentsToDisk();
        Controller.Termunique=Index.size();
        System.out.println("create Dictionary");
        File filedic = new File(fileManager.postingpath + "\\Dictionary.txt");
        StringBuilder term =new StringBuilder("");
        for (Map.Entry<String, int[]> entry : Index.entrySet()) {
            term.append(entry.getKey()+","+entry.getValue()[0]+"\n");
        }
        try (FileWriter fw = new FileWriter(filedic,false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(term);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
