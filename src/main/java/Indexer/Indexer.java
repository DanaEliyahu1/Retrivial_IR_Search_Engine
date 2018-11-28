package Indexer;

import GUI.Controller;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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


    public void ResultToFile(String DocID, TreeMap<String, Integer> SpecialTermsMap, TreeMap<String, Integer> TermsMap, String City, TreeMap<String, Integer> capitalLetterWords, String cityplaces, String filename){
                int counter=0;
                String mostTf="";
                for (Map.Entry<String, Integer> entry : SpecialTermsMap.entrySet()) {
                    AddTermToDic(entry.getKey(),entry.getValue(),DocID);
                    if(counter<entry.getValue()){
                        counter=entry.getValue();
                        mostTf=entry.getKey();
                    }
                }
                for (Map.Entry<String, Integer> entry : TermsMap.entrySet()) {
                    AddTermToDic(entry.getKey(), entry.getValue(),DocID);
                    if (counter < entry.getValue()) {
                        counter = entry.getValue();
                        mostTf=entry.getKey();
                    }
                }
                int uniqueterms=SpecialTermsMap.size() + TermsMap.size();

                fileManager.DocPosting(DocID,City,counter,uniqueterms,mostTf,cityplaces,filename);
        for (Map.Entry<String, Integer> entry : capitalLetterWords.entrySet()) {
            if(!Index.containsKey(entry.getKey().toLowerCase())){
               AddTermToCapital(entry.getKey(),entry.getValue(),DocID);
            }
            else{
                AddTermToDic(entry.getKey().toLowerCase(), entry.getValue(),DocID);
            }
        }
    }
    public void IndexCities(){
        File folder = new File(fileManager.postingpath+"\\Cities");
        File[] ListOfFile= folder.listFiles();
        for (int i = 0; i <ListOfFile.length ; i++) {
            String key= ListOfFile[i].getName();
            key=key.substring(0,key.length()-4);
            CityIndex.put(key,null);
        }
        OkHttpClient client = new OkHttpClient();// currencies???
        Request request = new Request.Builder().url("https://restcountries.eu/rest/v2/all?fields=capital;name;currencies;population").build();
        String data="";
        try {
            data= client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser=new JSONParser();
        try {
            JSONArray jsonArray =(JSONArray) jsonParser.parse(data);
            for (int i = 0; i <jsonArray.size() ; i++) {
                JSONObject curr = (JSONObject) jsonArray.get(i);
                String Cityname = ((String) curr.get("capital")).split(" ")[0];
                System.out.println(Cityname);
                if(CityIndex.containsKey(Cityname)){
                    JSONArray curr1= (JSONArray) curr.get("currencies");
                    JSONObject currency = (JSONObject) curr1.get(0);
                    //  String currency =((JSONObject)((JSONArray)curr.get("currencies")).get("code")).toString();
                    double population = ((long)curr.get("population"))/1000000.0;
                    String populations= "M"+Math.round(population*100)/100;
                    String[] CityInfo = {(String)curr.get("name"),(String)currency.get("code"),populations};
                    CityIndex.put(Cityname,CityInfo);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void AddTermToCapital(String key, Integer value, String DocID) {
        if(Index.containsKey(key)){
            int [] setvalue=Index.get(key);
            setvalue[0]++;
            setvalue[2]+=value;
            CapitalLetterPosting.put(key,CapitalLetterPosting.get(key)+"|" + DocID + "," + value.toString());
        }
        else {
            int [] setnewvalue={1,linenumber[27],value};
            Index.put(key,setnewvalue);
            CapitalLetterPosting.put(key,"|" + DocID + "," + value.toString());
            linenumber[27]++;
        }
    }

    private void AddTermToDic(String key, Integer value,String DocID) {
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                if(Index.containsKey(key)){
                    int [] setvalue=Index.get(key);
                    setvalue[0]++;
                    setvalue[2]+=value;
                    fileManager.AddToPosting(key,value,DocID,setvalue[1]);
                }
                else {
                    int line=-1;
                    if(Character.isLetter(key.charAt(0))){
                        line=linenumber[key.charAt(0)-97];
                    }else{
                        line=linenumber[26];
                    }
                    int [] setnewvalue={1,line,value};
                    Index.put(key,setnewvalue);
                    fileManager.AddToPosting(key,value,DocID,line);
                    if(Character.isLetter(key.charAt(0))){
                        linenumber[key.charAt(0)-97]++;
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
        IndexCities();
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
        fileManager.AddCapitalLettersToCache(CapitalLetterPosting);
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
