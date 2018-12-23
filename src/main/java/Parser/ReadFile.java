package Parser;

import GUI.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ReadFile {
    public static HashMap<String,String> replace;
    String corpuspath;
    private String FileName;
    //gets the path to the corpus
    public ReadFile(String corpuspath) {
        this.corpuspath = corpuspath;
    }
//getting the docs from the file in the input
    public Document[] GetDoc(String path,String filename) {
        FileName = path;
        try {
            String content = new String(Files.readAllBytes(Paths.get(corpuspath + "\\" + path)), Charset.defaultCharset());
            String[] document = content.split("<DOC>");
            Document[] Doc = new Document[document.length - 1];
            for (int i = 0; i < Doc.length; i++) {
                Doc[i] = initdoc(document[i + 1],filename);
            }
            return Doc;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
//getting doc and cutting it and only returning the relevant info and tags in an Document Object
    private Document initdoc(String s,String filename) {
        String finalTitle="";
        String City = "";
        //cheak the format
        String[] IdArr = s.split("</DOCNO>");
        String Id = IdArr[0].split("<DOCNO>")[1].replaceAll(" ","");
        if(IdArr[1].contains("<TI>")){
            String title=IdArr[1].split("<TI>")[1];
            String title2=title.split("</TI>")[0];
            finalTitle=" "+title2;
        }
        else if(IdArr[1].contains("<HEADLINE>")){
            String title=IdArr[1].split("<HEADLINE>")[1];
            String title2=title.split("</HEADLINE>")[0];
            finalTitle=" "+title2;
        }

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
        else if(City1.length>=2 && City1[1].contains("F P=104>")){
            String [] City5=City1[1].split("F P=104>");
            String [] City6=City5[1].split("</F>");
            String [] City7=City6[0].split("\n|\\ ");
           // System.out.println(Id);
            for (int i = 0; i <City7.length ; i++) {
                if (!City7[i].equals("")) {
                    City = City7[i];
                    break;
                }
            }

        }
        String Text="";
        try{
           Text = City1[1].split("</TEXT>")[0];

            if (Text.contains("[Text]")) {
                String[] aftersdplit=Text.split("\\[Text\\]");
                Text = aftersdplit[1];
                String [] language=aftersdplit[0].split("<F P=105>" )[1].split("</F>");
                String Language=language[0].replaceAll(" ","");
                Controller.SetLanguages(Language);
                for (int i = 0; i <10 ; i++) {
                    Text+=finalTitle;
                }
                return new Document(corpuspath + File.separator + FileName, City, Id, Text, filename);
            }
        }catch (Exception e){
          //  e.printStackTrace();
        }
        for (int i = 0; i <10 ; i++) {
            Text+=finalTitle;
        }
        return new Document(corpuspath + File.separator + FileName, City.toUpperCase(), Id, Text,filename);
    }
//after getting in constructor the path it starts here and it calls the parser and
// indexer so everything starts and ends here
    public void GetFile() {

        File[] FileList = new File(corpuspath).listFiles();
        File[] CurrFolder=null;
        Document[] CurrDoc=null;
        for (int i = 0; i < FileList.length; i++) {
    //        System.out.println("*********************************" + i);
            try {
                CurrFolder=FileList[i].listFiles();
                for (int j = 0; j < CurrFolder.length; j++) {
                    CurrDoc = GetDoc(FileList[i].getName() + "\\" + CurrFolder[j].getName(),CurrFolder[j].getName());
                    for (int k = 0; k < CurrDoc.length; k++) {
                           new Parse().parse(CurrDoc[k]);
                           Parse.indexer.AddDocTOIndex(CurrDoc[k]);
                    }
                    CurrDoc=null;
                }
            } catch (Exception e) {
                e.printStackTrace();
   //             System.out.println("#");
            }

        }
     //   System.out.println("waiting for finishing");
        Parse.indexer.FinishIndexing();


    }

}


