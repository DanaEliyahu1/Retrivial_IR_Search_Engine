package FileManager;

import Parser.TermInfo;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileManager {
    TreeMap<String, TreePointerToQ> Cache;
    PriorityQueue<PointerCache> Q;
    String DocId;
    double PriorityAll;
    HashMap<String,String> cities;
    Executor threadpool;

    public FileManager(String docId) {
        DocId = docId;
        Cache=new TreeMap<String,TreePointerToQ>();
        Q=new PriorityQueue<PointerCache>((x,y)->{ return (int) (x.priority-y.priority);});
        cities=new HashMap<String,String>();
        PriorityAll=0;
        threadpool= Executors.newFixedThreadPool(1);
    }


    public void AddTermTofile(String key, TermInfo value) {
        if (Cache.containsKey(key)) {
            Cache.get(key).value=Cache.get(key).value + "|" + DocId + "," + value.toString();
            Cache.get(key).pc.priority++;
            Cache.put(key, Cache.get(key));
        } else {
            PointerCache newpc=new PointerCache(key, PriorityAll);
            Cache.put(key,new TreePointerToQ(newpc, "|" + DocId + "," + value.toString()));
            Q.add(newpc);
        }
        if (Cache.size() > 10000) {
            PointerCache keytofile = Q.poll();
            String Value = Cache.get(keytofile.pointerterm).value;
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
                pointer=pointer.replaceAll("/","-");
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


   return "Indexing\\u-z\\"+pointer+".txt";
    }

    public void setDocId(String docId) {
        DocId = docId;
    }

    public void AllTermToDisk(){

        while (!Q.isEmpty()){
            PointerCache keytofile = Q.poll();
            String Value = Cache.get(keytofile.pointerterm).value;
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

    public void DocPosting(String ID,String City,int maxtf, int uniqueterms){
        AddDocToCityIndex(ID,City);
        File file =new File("Documents\\"+ID+".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter fw = new FileWriter("Documents\\"+ID+".txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print( "|" + City + "," + maxtf+ ","+ uniqueterms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void AddDocToCityIndex(String DocId,String City){
        if (cities.containsKey(City)) {
            cities.put(City,cities.get(City)+ "," + DocId);
        } else {
            cities.put(City, DocId);
        }
    }
    public void CitiesToDisk(){
        Iterator<Map.Entry<String,String>> it= cities.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,String> currCity=it.next();
            File file =new File("Cities\\"+currCity.getKey()+".txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (FileWriter fw = new FileWriter("Cities\\"+currCity.getKey()+".txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(currCity.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void ResultToFile (String DocID,TreeMap<String,TermInfo> SpecialTermsMap,TreeMap <String,TermInfo>TermsMap,String City){
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                int counter=0;
                setDocId(DocID);
                for (Map.Entry<String, TermInfo> entry : SpecialTermsMap.entrySet()) {
                    AddTermTofile(entry.getKey(),entry.getValue());
                    if(counter<entry.getValue().TermCount){
                        counter=entry.getValue().TermCount;
                    }

                }
                for (Map.Entry<String, TermInfo> entry : TermsMap.entrySet()) {
                    AddTermTofile(entry.getKey(), entry.getValue());
                    if (counter < entry.getValue().TermCount) {
                        counter = entry.getValue().TermCount;
                    }
                }
                int uniqueterms=SpecialTermsMap.size() + TermsMap.size();
                DocPosting(DocID,City,counter,uniqueterms);
            }
        });

    }
}

class PointerCache {
    double priority;
    String pointerterm;

    public PointerCache(String pointerterm, double priority) {
        this.pointerterm = pointerterm;
        this.priority = priority;
    }
}
class TreePointerToQ{
    public PointerCache pc;
    public String value;

    public TreePointerToQ(PointerCache pc, String value) {
        this.pc = pc;
        this.value = value;
    }
}
