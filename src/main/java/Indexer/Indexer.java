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
        this.threadpool= Executors.newFixedThreadPool(1);
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
        System.out.println("end index");

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

        fileManager.CitiesToDisk();
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
        File filedic = new File(fileManager.postingpath + "\\Dictionary.txt");
        String term = "";
        for (Map.Entry<String, int[]> entry : Index.entrySet()) {
            term+=(entry.getKey()+","+entry.getValue()[0]+"\n");
        }
        try (FileWriter fw = new FileWriter(filedic,false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(term);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Java program for implementation of QuickSort
        /* This function takes last element as pivot,
        places the pivot element at its correct
        position in sorted array, and places all
        smaller (smaller than pivot) to left of
        pivot and all greater elements to right
        of pivot *//*
         int partition(String arr[], int low, int high)
        {
            int pivot =Integer.parseInt(arr[high].split(",")[1]);
            int i = (low-1); // index of smaller element
            for (int j=low; j<=high-1; j++)
            {
                // If current element is smaller than or
                // equal to pivot
                if (Integer.parseInt(arr[j].split(",")[1]) <= pivot)
                {
                    i++;

                    // swap arr[i] and arr[j]
                    String temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }

            // swap arr[i+1] and arr[high] (or pivot)
            String temp = arr[i+1];
            arr[i+1] = arr[high];
            arr[high] = temp;

            return i+1;
        }*/

        /* The main function that implements QuickSort()
        arr[] --> Array to be sorted,
        low --> Starting index,
        high --> Ending index */
     /*   void qSort(String arr[], int low, int high)
        {
            if (low < high)
            {

                int pi = partition(arr, low, high);

                // Recursively sort elements before
                // partition and after partition
                qSort(arr, low, pi-1);
                qSort(arr, pi+1, high);
            }
        }*/
    }
