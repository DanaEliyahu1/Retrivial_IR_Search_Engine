package Indexer;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import static FileManager.FileManager.geturl;

public class Indexer {

    TreeMap <String,String []> Index;
    TreeMap <String,String[]> CityIndex;

    public Indexer() {
        Index = new TreeMap<> ();
        CityIndex=new TreeMap<>();
    }

    public void Index(){
        File folder = new File("Indexing");
        File[] ListOfFile= folder.listFiles();
        for (int i = 0; i <ListOfFile.length ; i++) {
            File[] CurrFolder= ListOfFile[i].listFiles();
            for (int j = 0; j <CurrFolder.length; j++) {
                try{
                    String key= CurrFolder[j].getName();
                    System.out.println(key);
                    key=key.substring(0,key.length()-4);
                    String [] values =IndexFile(CurrFolder[j]);
                    Index.put(key,values);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
        IndexCities();
    }
    public void IndexCities(){
        File folder = new File("Cities");
        File[] ListOfFile= folder.listFiles();
        for (int i = 0; i <ListOfFile.length ; i++) {
            String key= ListOfFile[i].getName();
            key=key.substring(0,key.length()-3);
            CityIndex.put(key,null);
        }
    }

    private String[] IndexFile(File file) {
        String [] values= new String[3];
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            String [] Parmeters= content.substring(1).split("\\|");
            qSort(Parmeters,0,Parmeters.length-1);
            StringJoiner sj=new StringJoiner("|");
            for (int i = 0; i <Parmeters.length ; i++) {
                sj.add(Parmeters[i]);
            }
            values[0]=""+Parmeters.length;
            values[1]=file.getName();
            //todo more parmaters
            try (FileWriter fw = new FileWriter(geturl(file.getName()), false);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(sj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return values;
    }



    // Java program for implementation of QuickSort
        /* This function takes last element as pivot,
        places the pivot element at its correct
        position in sorted array, and places all
        smaller (smaller than pivot) to left of
        pivot and all greater elements to right
        of pivot */
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
        }

        /* The main function that implements QuickSort()
        arr[] --> Array to be sorted,
        low --> Starting index,
        high --> Ending index */
        void qSort(String arr[], int low, int high)
        {
            if (low < high)
            {
            /* pi is partitioning index, arr[pi] is
            now at right place */
                int pi = partition(arr, low, high);

                // Recursively sort elements before
                // partition and after partition
                qSort(arr, low, pi-1);
                qSort(arr, pi+1, high);
            }
        }
    }
