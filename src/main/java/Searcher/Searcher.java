package Searcher;

import Indexer.FileManager;
import Parser.Document;
import Parser.Parse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Searcher {
    public TreeMap<String, int[]> indexer;

    public TreeMap<String, String> Searcher(String Query, TreeMap<String, int[]> indexer, boolean IsSemanticSelected) {
        this.indexer = indexer;
        Parse parse = new Parse();
        Document doc = new Document(null, "", null, Query, null);
        if(IsSemanticSelected){
            String newQ=semanticTreatment(Query);
            doc.Text=newQ;
        }
        parse.parse(doc);
        doc.TermsMap.putAll(doc.CapitalLetterWords);// only for the query
        TreeMap<String, String> DocDictionary = GetDataFromPosting(doc.TermsMap);
        //String s="";
        for (Map.Entry<String, String> entry : DocDictionary.entrySet()) {
          // System.out.println("Doc: " + entry.getKey() + " ,each term's tf:" + entry.getValue());
         //  s+=(351+" 0 "+entry.getKey()+" 1 42.38 mt\n");
        }
       /* try (FileWriter fw = new FileWriter("results.txt", false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(s);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return DocDictionary;
    }

    private String semanticTreatment(String query) {
        String text=query;
        String[] terms=text.split(" ");
        for (int i = 0; i <terms.length ; i++) {
            if(!terms[i].equals("")){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.datamuse.com/words?ml="+terms[i]).build();
                String data = "";
                try {
                    data = client.newCall(request).execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONParser jsonParser = new JSONParser();
                try {
                    JSONArray jsonArr = (JSONArray) jsonParser.parse(data);
                    for (int j = 0; j <jsonArr.size() && j<1 ; j++) {
                        text+=(" "+((JSONObject)jsonArr.get(j)).get("word").toString() );
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }


    private TreeMap<String, String> GetDataFromPosting(TreeMap<String, Integer> termsMap) {
        TreeMap<String, String> result = new TreeMap<>();
        TreeSet<String> ranked=GetRankedtreesettest();
        for (Map.Entry<String, Integer> entry : termsMap.entrySet()) {
            String[] arrFromFile = new String[0];
            try {
                String Token = entry.getKey();
                if (indexer.containsKey(Token.toLowerCase())) {
                    Token = Token.toLowerCase();
                } else if (indexer.containsKey(Token.toUpperCase())) {
                    Token = Token.toUpperCase();
                } else continue;

                arrFromFile = new String(Files.readAllBytes(Paths.get(FileManager.geturl("" + Token.charAt(0)))), Charset.defaultCharset()).split("\n");
                int linenumber = indexer.get(Token)[1];
                String currposting = arrFromFile[linenumber];
                String[] doclist = currposting.split("\\|");
                for (int i = 1; i < doclist.length; i++) {
                    String[] KeyValue = doclist[i].split(",");
                    if (result.containsKey(KeyValue[0])) {
                        result.put(KeyValue[0], result.get(KeyValue[0]) + "|" + Token + "_" + KeyValue[1]);
                    } else {
                        result.put(KeyValue[0], Token + "_" + KeyValue[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return result;
    }

    private TreeSet<String> GetRankedtreesettest() {
        TreeSet<String> tree=new TreeSet<>();
        String s="FBIS3-10551\n" +
                "FBIS3-10646\n" +
                "FBIS3-10697\n" +
                "FBIS3-11107\n" +
                "FBIS3-19947\n" +
                "FBIS3-33035\n" +
                "FBIS3-33505\n" +
                "FBIS3-50570\n" +
                "FBIS3-59016\n" +
                "FBIS4-10762\n" +
                "FBIS4-11114\n" +
                "FBIS4-34579\n" +
                "FBIS4-34996\n" +
                "FBIS4-35048\n" +
                "FBIS4-56243\n" +
                "FBIS4-56741\n" +
                "FBIS4-57354\n" +
                "FBIS4-64976\n" +
                "FBIS4-9937\n" +
                "FT921-2097\n" +
                "FT921-6272\n" +
                "FT921-6603\n" +
                "FT921-8458\n" +
                "FT922-14936\n" +
                "FT922-15099\n" +
                "FT922-3165\n" +
                "FT922-8324\n" +
                "FT923-11890\n" +
                "FT923-1456\n" +
                "FT924-1564\n" +
                "FT931-10913\n" +
                "FT931-16617\n" +
                "FT931-932\n" +
                "FT932-16710\n" +
                "FT932-6577\n" +
                "FT934-13429\n" +
                "FT934-13954\n" +
                "FT934-4629\n" +
                "FT934-4848\n" +
                "FT934-4856\n" +
                "FT941-13429\n" +
                "FT941-7250\n" +
                "FT941-9999\n" +
                "FT942-12805\n" +
                "FT943-14758\n" +
                "FT943-15117\n" +
                "FT944-15849\n" +
                "LA111290-0139";
        String[] docs= s.split("\n");
        for (int i = 0; i <docs.length ; i++) {
            tree.add(docs[i]);
        }
        return tree;
    }


}
