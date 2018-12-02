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
    public FileManager fileManager;
    public TreeMap <String,int []> Index;
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
       // TreeSet<String> State=new TreeSet<>();

        File folder = new File(fileManager.postingpath+"\\Cities");
        File[] ListOfFile= folder.listFiles();
        for (int i = 0; i <ListOfFile.length ; i++) {
            String key= ListOfFile[i].getName();
            key=key.substring(0,key.length()-4);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://getcitydetails.geobytes.com/GetCityDetails?fqcn="+key).build();
            String data="";
            try {
                data= client.newCall(request).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONParser jsonParser=new JSONParser();
            String []value=new String[3];
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
                if(jsonObject.get("geobytescountry").equals("")){
                    continue;
                }
                value[0]=(String) jsonObject.get("geobytescountry");
                value[1]=(String) jsonObject.get("geobytescurrency");
                value[2]=GetPopulationSize((Double.parseDouble((String)jsonObject.get("geobytespopulation"))));
               //(String)"M"+ Math.round((Double.parseDouble((String)jsonObject.get("geobytespopulation")))/10000.0)/100;
              //  State.add(value[0]);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            CityIndex.put(key,value);
        }
    //    System.out.println(State.size());
    //    Iterator it = State.iterator();
     //   while ( it.hasNext()){
      //      System.out.println(it.next());
        }

    private String GetPopulationSize(double number)  {

     if (number < 1000) {
        return ""+((Math.round(number*100))/100);
    }else if ((number >= 1000) && (number < 1000000)) {
        double newnum = number / 1000;
        return "" + ((Math.round(newnum*100))/100)+ "K";
    } else if (number >= 1000000 && number < 1000000000) {
        double newnum = number / 1000000;
        return "" + ((Math.round(newnum*100))/100)+ "M";
    } else if (number <= 1000000000) {
        double newnum = number / 1000000000;
        return "" + ((Math.round(newnum*100))/100) + "B";
    }
        return null;
}

    // }
    private void AddTermToCapital(String key, Integer value, String DocID) {
        if(Index.containsKey(key)){
            int [] setvalue=AllCapitalLetterWords.get(key);
            setvalue[0]++;
            setvalue[2]+=value;
            CapitalLetterPosting.put(key,CapitalLetterPosting.get(key)+"|" + DocID + "," + value.toString());
        }
        else {
            int [] setnewvalue={1,linenumber[27],value};
            AllCapitalLetterWords.put(key,setnewvalue);
            CapitalLetterPosting.put(key,"|" + DocID + "," + value.toString());
            linenumber[27]++;
        }
    }

    private void AddTermToDic(String key, Integer value,String DocID) {
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

    public void FinishIndexing() {

        for (Map.Entry<String, int[]> entry : AllCapitalLetterWords.entrySet()) {
            if(Index.containsKey(entry.getKey().toLowerCase())){
                int[] value=Index.get(entry.getKey().toLowerCase());
                value[0]+=AllCapitalLetterWords.get(entry.getKey())[0];
                value[2]+=AllCapitalLetterWords.get(entry.getKey())[2];
                fileManager.SetCapitalToLoweCasePosting(entry.getKey().toLowerCase(),CapitalLetterPosting.get(entry.getKey()),Index.get(entry.getKey().toLowerCase())[1]);
                CapitalLetterPosting.remove(entry.getKey());
            }
            else{
                Index.put(entry.getKey(),entry.getValue());
            }
        }
        fileManager.AddCapitalLettersToCache(CapitalLetterPosting);
        try {
            fileManager.AllTermToDisk();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fileManager.CitiesToDisk();
        IndexCities();
        fileManager.AllDocumentsToDisk();
        Controller.Termunique=Index.size();
        System.out.println("create Dictionary");
        File filedic = new File(fileManager.postingpath + "\\Dictionary.txt");
        StringBuilder term =new StringBuilder("");
        for (Map.Entry<String, int[]> entry : Index.entrySet()) {
            term.append(entry.getKey()+","+entry.getValue()[0]+","+entry.getValue()[1]+","+entry.getValue()[2]+"\n");
        }
        try (FileWriter fw = new FileWriter(filedic,false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(term);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
