package FileManager;

import Parser.TermInfo;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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
        if (Cache.size() > 500) {
            PointerCache keytofile = Q.poll();
            String Value = Cache.get(keytofile.pointer);
            Cache.remove(keytofile.pointer);
            File file =new File("src\\main\\java\\Parser\\Terms\\"+"\\" + keytofile.pointer + ".txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (FileWriter fw = new FileWriter("src\\main\\java\\Parser\\Terms\\" +"\\"+ keytofile.pointer + ".txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(Value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDocId(String docId) {
        DocId = docId;
    }
}

class PointerCache {
    double priority;
    String pointer;


    public PointerCache(String pointer, double priority) {
        this.pointer = pointer;
        this.priority = priority;
    }
}
