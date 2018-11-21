package FileManager;

import Indexer.Indexer;
import Parser.TermInfo;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileManager {

    TreeMap<String, TreePointerToQ> Cache;
    PriorityQueue<PointerCache> Q;
    int PriorityAll;
    HashMap<String,String> cities;
    public static int DocNum;
    public static String postingpath;


    public FileManager(String docId, String path) {
        Cache=new TreeMap<String,TreePointerToQ>();
        Q=new PriorityQueue<PointerCache>((x,y)->{ return (int)(x.priority-y.priority);});
        cities=new HashMap<String,String>();
        PriorityAll=0;

        postingpath=path;

    }

/*
    public void AddTermTofile(String key, TermInfo value) {

        if (Cache.size() > 10000) {

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
*/
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
                return postingpath+ "\\Indexing\\Numbers\\"+pointer+".txt";
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
                return postingpath+"\\Indexing\\a-e\\"+pointer+".txt";
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
                return postingpath+"\\Indexing\\f-j\\"+pointer+".txt";
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
                return postingpath+"\\Indexing\\k-o\\"+pointer+".txt";
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
                return postingpath+"\\Indexing\\p-t\\"+pointer+".txt";
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                return postingpath+"\\Indexing\\u-z\\"+pointer+".txt";
        }


   return postingpath+"\\Indexing\\u-z\\"+pointer+".txt";
    }


    public void AllTermToDisk() throws InterruptedException {
        System.out.println("writing to disk");
        System.out.println("finished to disk");
        while (!Q.isEmpty()){
            System.out.println(Q.size());
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
        DocNum++;
        AddDocToCityIndex(ID,City);
        File file =new File(postingpath+"\\Documents\\"+ID+".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter fw = new FileWriter(postingpath+"\\Documents\\"+ID+".txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print( "|" + City + "," + maxtf+ ","+ uniqueterms);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                e.printStackTrace();
            }
            try (FileWriter fw = new FileWriter(postingpath+"\\Cities\\"+currCity.getKey()+".txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.print(currCity.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void AddToPosting(String key, TermInfo value, String docID, int i) {
        if (Cache.containsKey(key)) {
            Cache.get(key).value=Cache.get(key).value + "|" + docID + "," + value.toString();
            Cache.get(key).pc.priority++;
            Cache.put(key, Cache.get(key));
        } else {
            PointerCache newpc=new PointerCache(key, PriorityAll);
            Cache.put(key,new TreePointerToQ(newpc, "|" + docID + "," + value.toString()));
            Q.add(newpc);
        }
        if(Cache.size()>1500){
            TreeMap<String ,TreePointerToQ> TermToFile=new TreeMap<String,  TreePointerToQ>();
            for (int j = 0; j <1000 ; j++) {
                TreePointerToQ Value=Cache.remove(Q.poll().pointerterm);
                TermToFile.put(Value.pc.pointerterm,Value);
            }
            char currletter = '*';
            String [] currentfile=null;
            for (Map.Entry<String, TreePointerToQ> entry : TermToFile.entrySet()) {
                if(Character.isLowerCase(entry.getKey().charAt(0))){
                    if(entry.getKey().charAt(0)!=currletter){
                        if(currentfile!=null){
                            StringJoiner sj=new StringJoiner("|");
                            for (int k = 0; k <currentfile.length ;k++) {
                                sj.add(currentfile[k]);
                            }
                            try (FileWriter fw = new FileWriter(postingpath+"\\Indexing\\"+currletter+".txt", true);
                                 BufferedWriter bw = new BufferedWriter(fw);
                                 PrintWriter out = new PrintWriter(bw)) {
                                out.print(sj);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        currletter=entry.getKey().charAt(0);
                        try {
                            String[] arrFromFile=new String(Files.readAllBytes(Paths.get(postingpath +"\\Indexing\\"+ currletter+".txt")), Charset.defaultCharset()).split("\n");
                            if(Character.isLetter(key.charAt(0))){
                                currentfile=new String[Indexer.linenumber[currletter-97]];
                            }else{
                                currentfile=new String[Indexer.linenumber[0]];
                            }
                            for (int j = 0; j < arrFromFile.length; j++) {
                                currentfile[j]=arrFromFile[j];
                            }
                            arrFromFile=null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    currentfile[entry.getValue().lineNumber]+=entry.getValue().value;
                }

            }

        }
    }
}

class PointerCache {
    int priority;
    String pointerterm;

    public PointerCache(String pointerterm, int priority) {
        this.pointerterm = pointerterm;
        this.priority = priority;
    }
}
class TreePointerToQ{
    public PointerCache pc;
    public String value;
    public int lineNumber;

    public TreePointerToQ(PointerCache pc, String value) {
        this.pc = pc;
        this.value = value;
    }
}
