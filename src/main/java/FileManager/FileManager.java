package FileManager;

import Parser.TermInfo;

import java.io.*;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class FileManager {
    TreeMap<String, String> Cache;

    PriorityQueue<PointerCache> Q;
    String DocId;
    double PriorityAll;

    public FileManager(String docId) {
        DocId = docId;
        Cache=new TreeMap<String,String>();
        Q=new PriorityQueue<PointerCache>((x,y)->{ return (int) (x.priority-y.priority);});
    }


    public void AddTermTofile(String key, TermInfo value) {
        if (Cache.containsKey(key)) {
            Cache.put(key, Cache.get(key) + "|" + DocId + "," + value.toString());
        } else {
            Cache.put(key, "|" + DocId + "," + value.toString());
            Q.add(new PointerCache(key, PriorityAll));
        }
        PriorityAll++;
        if (Cache.size() > 10000) {
            PointerCache keytofile = Q.poll();
            String Value = Cache.get(keytofile.pointerterm);
            Cache.remove(keytofile.pointerterm);
            File file =new File(geturl(keytofile.pointerterm));
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (FileWriter fw = new FileWriter(geturl(keytofile.pointerterm), true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(Value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String geturl(String pointer) {
        char first =pointer.charAt(0);
        switch (first){
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '$':
                return "Indexing\\Numbers\\"+pointer+".txt";
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
                return "Indexing\\a-e\\"+pointer+".txt";
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
                return "Indexing\\f-j\\"+pointer+".txt";
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
                return "Indexing\\k-o\\"+pointer+".txt";
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
                return "Indexing\\p-t\\"+pointer+".txt";
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                return "Indexing\\u-z\\"+pointer+".txt";
        }

   return null;
    }

    public void setDocId(String docId) {
        DocId = docId;
    }

    public void AllTermToDisk(){

        while (!Q.isEmpty()){
            PointerCache keytofile = Q.poll();
            String Value = Cache.get(keytofile.pointerterm);
            Cache.remove(keytofile.pointerterm);
            File file =new File(geturl(keytofile.pointerterm));
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (FileWriter fw = new FileWriter(geturl(keytofile.pointerterm), true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(Value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
        //todo


}



class PointerCache {
    double priority;
    String pointerterm;


    public PointerCache(String pointerterm, double priority) {
        this.pointerterm = pointerterm;
        this.priority = priority;
    }
}
