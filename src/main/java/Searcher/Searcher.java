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

/**
 * the searcher will get the query, parse it,find in the posting docs with one or more terms
 * and will return a map with docId and tf info about the query terms
 * we make it optional to use semantic search here.
 */
public class Searcher {
    public TreeMap<String, int[]> indexer;

    /**
     *
     * @param Query the query that the user got as a string
     * @param indexer a map with info about each term's posting storage, needed to find
     *               list of docs for the term wanted
     * @param IsSemanticSelected a boolean field which will find synonyms to words in the
     *                           query to expand the wuery with accurate results
     * @return treemap with docId of (optional) relevant docs and their tf for query terms
     */
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

    /**
     * getting a query we will connect to the web and get the best word to describe the
     * input word
     * @param query a string with the query from the text field
     * @return a query with added synonym words
     */
    private String semanticTreatment(String query) {
        String text=query;
        String[] terms=text.split(" ");
        for (int i = 0; i <terms.length ; i++) {  //foreach term in the query
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
                    for (int j = 0; j <jsonArr.size() && j<1 ; j++) { //getting one word out of the api
                        text+=(" "+((JSONObject)jsonArr.get(j)).get("word").toString() );
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

    /**
     * getting the the posting data for each term
     * this function is the slowest function because it opens a lot of posting files
     * which are big.
     * @param termsMap a tree with all strings after parse. (the integer value isnt used)
     * @return a map with docId and value has info on tf of words from query
     */
    private TreeMap<String, String> GetDataFromPosting(TreeMap<String, Integer> termsMap) {
        TreeMap<String, String> result = new TreeMap<>();
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
                String currposting = arrFromFile[linenumber];  //this is the posting line of the wanted term
                String[] doclist = currposting.split("\\|");
                for (int i = 1; i < doclist.length; i++) {  //adding each doc and expanding tf data if already exists
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

}
