package Parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReadFile {

    String corpuspath;
    private String FileName;
    private ExecutorService threadpool;
    public ReadFile(String corpuspath) {
        this.corpuspath = corpuspath;
        this.threadpool= Executors.newSingleThreadExecutor();
    }

    public Document[] GetDoc(String filename) {
        FileName = filename;
        try {
            String content = new String(Files.readAllBytes(Paths.get(corpuspath + "\\" + filename)), Charset.defaultCharset());
            String[] document = content.split("<DOC>");
            Document[] Doc = new Document[document.length - 1];
            for (int i = 0; i < Doc.length; i++) {
                Doc[i] = initdoc(document[i + 1]);
            }
            return Doc;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document initdoc(String s) {
        String City = "";
        //cheak the format
        String[] IdArr = s.split("</DOCNO>");
        String Id = IdArr[0].substring(9);
        String[] City1 = IdArr[1].split("<TEXT>");
        String[] City2 = City1[0].split("<F P=104>");
        if (City2.length != 1) {
            String[] City3 = City2[1].split("</F>");
            String[] City4 = City3[0].split("\n|\\ ");
            for (int i = 0; i <City4.length ; i++) {
                if(!City4[i].equals("")){
                    City=City4[i];
                    break;
                }
            }
        }
        String Text="";
        try{
           Text = City1[1].split("</TEXT>")[0];
            if (Text.contains("[Text]")) {
                Text = Text.split("[Text]")[1];
                return new Document(corpuspath + File.separator + FileName, City, Id, Text);
            }
        }catch (Exception e){
          //  e.printStackTrace();
        }

        return new Document(corpuspath + File.separator + FileName, City, Id, Text);
    }

    public void GetFile() {

        File[] FileList = new File(corpuspath).listFiles();
        File[] CurrFolder=null;
        Document[] CurrDoc=null;
        for (int i = 0; i < FileList.length; i++) {
           // System.out.println("*********************************" + i);
            try {
                CurrFolder=FileList[i].listFiles();
                for (int j = 0; j < CurrFolder.length; j++) {
                    CurrDoc = GetDoc(FileList[i].getName() + "\\" + CurrFolder[j].getName());
                    for (int k = 0; k < CurrDoc.length; k++) {
                       if(CurrDoc[k].Text.length()>50)
                        threadpool.execute(new Parse(CurrDoc[k]));
                    }
                    CurrDoc=null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("#");
            }

        }
        System.out.println("waiting for finishing");
        try {
            threadpool.shutdown();
            threadpool.awaitTermination(40, TimeUnit.MINUTES);
            System.out.println("finished");
        } catch (InterruptedException e) {
            System.out.println("time out~~");
        }

        Parse.indexer.FinishIndexing();


    }

}


