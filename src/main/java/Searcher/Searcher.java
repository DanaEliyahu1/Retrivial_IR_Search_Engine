package Searcher;

import Indexer.Indexer;
import Indexer.FileManager;
import Parser.Document;
import Parser.Parse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class Searcher {
    public TreeMap<String, int[]> indexer;

    public TreeMap<String, String> Searcher(String Query, String City, TreeMap<String, int[]> indexer) {
        this.indexer = indexer;
        Parse parse = new Parse();
        if (City == null) {
            City = "";
        }
        Document doc = new Document(null, City, null, Query, null);
        parse.parse(doc);
        doc.TermsMap.putAll(doc.CapitalLetterWords);// only for the query
        TreeMap<String, String> DocDictionary = GetDataFromPosting(doc.TermsMap);
        String s="";
        for (Map.Entry<String, String> entry : DocDictionary.entrySet()) {
           System.out.println("Doc: " + entry.getKey() + " ,each term's tf:" + entry.getValue());
           s+=(352+" 0 "+entry.getKey()+" 1 42.38 mt\n");
        }
        try (FileWriter fw = new FileWriter("results.txt", false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(s);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DocDictionary;
    }


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


}
