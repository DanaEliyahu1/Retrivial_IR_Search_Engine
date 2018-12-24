package Parser;

import Indexer.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Parse {
    private String City;
    private Document d;
    public static Indexer indexer;
    public static HashSet StopWord;
    private TreeMap<String, Integer> TermsMap;
    public static Stemmer stemmer;
    public static HashSet Months;
    public static HashSet NumberHash;
    public static HashSet DollarHash;
    public static boolean isStemmig;
    private TreeMap<String, Integer> CapitalLetterWords;
    private ArrayList<String> Tokens;
    private int i;
    private String Cityplaces;
    public static TreeMap<String,Double>  Idf;


    //each document needs its own tree map of terms
    public Parse() {
        TermsMap = new TreeMap<String, Integer>();
        CapitalLetterWords = new TreeMap<>();
    }

    //getting the doc and its tokens. iterating i (a field)
// and it is possible to skips a term but if it is merged in parsing
    public void parse(Document Doc) {
        this.City = Doc.City;
        this.d = Doc;
        Cityplaces = "";
        Tokens = Doc.TextToToken();
        int size = Tokens.size();
        for (i = 0; i < size; i++) {
            try {
                //in case of skipping:
                if (ParseRules()) {
                    i++;
                }
            } catch (Exception e) {
               // e.printStackTrace();
             //  System.out.println("@");
            }
        }
       /* if(DocID.equals("FBIS3-3366")){       //for the report
            TreeMap<String,Integer> alltermsdoc=new TreeMap<>();
            alltermsdoc.putAll(TermsMap);
            alltermsdoc.putAll(SpecialTermsMap);
            alltermsdoc.putAll(CapitalLetterWords);
        for (Map.Entry<String, Integer> entry : alltermsdoc.entrySet()) {
            System.out.println("Term: "+entry.getKey()+",Tf:"+entry.getValue());
        }
        }*/

        for (Map.Entry<String, Integer> entry : new ArrayList<>(CapitalLetterWords.entrySet())) {
            if(TermsMap.containsKey(entry.getKey().toLowerCase())){
                TermsMap.put(entry.getKey().toLowerCase(), TermsMap.get(entry.getKey().toLowerCase()) + CapitalLetterWords.get(entry.getKey()));
                CapitalLetterWords.remove(entry.getKey());
            }
        }
       // AddDocumentCosSimMechana();
        d.SetDoc(TermsMap,CapitalLetterWords,Cityplaces,Tokens.size());

    }

    // the actual parse rule each term goes through
    private boolean ParseRules() {
        //is it "between X and Y"  ?
        if ((i+2)<Tokens.size() &&Tokens.get(i).toLowerCase().equals("between") && Tokens.get(i + 2).toLowerCase().equals("and")) {
            AddTermToTree(false, Tokens.get(i).toLowerCase() + " " + Tokens.get(i + 1) + " and " + Tokens.get(i + 3));

        }
        //is it a stop word?
        if (StopWord.contains(Tokens.get(i).toLowerCase())) {
            return false;
        }
        //is it the city the document tagged? if so gets its place index
        if (Tokens.get(i).toLowerCase().equals(this.City.toLowerCase())) {
            Cityplaces += ("-" + i);
        }
        //is it a day in a month?
        if ((i+1)<Tokens.size()&& Months.contains(Tokens.get(i)) && Character.isDigit(Tokens.get(i + 1).charAt(0))) {

            AddTermToTree(false, Tokens.get(i + 1) + "-" + TranslateMonths(i));
            return true;
            //our parse rule: does it end with 's?
        } else if (Tokens.get(i).contains("'s")) {
            String newToken = Tokens.get(i).substring(0, Tokens.get(i).length() - 2);
            if (StopWord.contains(newToken)) {
                return false;
            }
            if (Character.isUpperCase(newToken.charAt(0))) {
                if (CapitalLetterWords.containsKey(newToken)) {
                    CapitalLetterWords.put(newToken.toUpperCase(), CapitalLetterWords.get(newToken.toUpperCase()) + 1);
                    return false;
                } else {
                    CapitalLetterWords.put(newToken.toUpperCase(), 1);
                    return false;
                }
            }
            AddTermToTree(false, newToken);
            return false;
        }
        //is it uppercase or a name which starts with capital letter?
        else if (Character.isUpperCase(Tokens.get(i).charAt(0))) {

            if (CapitalLetterWords.containsKey(Tokens.get(i).toUpperCase())) {
                CapitalLetterWords.put(Tokens.get(i).toUpperCase(), CapitalLetterWords.get(Tokens.get(i).toUpperCase()) + 1);
            } else {
                CapitalLetterWords.put(Tokens.get(i).toUpperCase(), 1);
            }
            return false;
        }
        //is it uppercase or a name which starts with capital letter?
        if (Tokens.get(i).contains("-")) {

            AddTermToTree(false, Tokens.get(i).toLowerCase());
            return false;
            //is it a number
        } else if (Character.isDigit(Tokens.get(i).charAt(0)) && Tokens.get(i).matches("[0-9.$%\\\\]+")) {
            //is the number percent?
            if (Tokens.get(i).charAt(Tokens.get(i).length() - 1) == '%') {

                AddTermToTree(false, Tokens.get(i));

                return true;
            } else if (Tokens.get(i + 1).equals("percent") || Tokens.get(i + 1).equals("percentage") ||((i+1)<Tokens.size()&&Tokens.get(i + 1).equals("%")) ) {

                AddTermToTree(false, Tokens.get(i) + "%");
                return true;
                //is the number of time ?
            } else if ((i+1)<Tokens.size()&&Months.contains(Tokens.get(i + 1))) {
                AddTermToTree(false, TranslateMonths(i + 1) + "-" + Tokens.get(i));
                return true;
                //is the number counting hundreds, thausands ...
            } else if ((i+1)<Tokens.size()&&NumberHash.contains(Tokens.get(i + 1))) {
                AddTermToTree(false, NumberToTerm());
                return true;
                //is the number counting dollars?
            } else if ((i+1)<Tokens.size()&&DollarHash.contains(Tokens.get(i + 1))) {

                AddTermToTree(false, PriceToTerm());
                return true;
                //our 2nd parse rule: counting length in feet as a term?
            } else if ((i+1)<Tokens.size()&&(Tokens.get(i + 1).equals("feet") || Tokens.get(i + 1).equals("Feet") || Tokens.get(i + 1).equals("FEET") || Tokens.get(i + 1).equals("FOOT") || Tokens.get(i + 1).equals("foot"))) {
                AddTermToTree(false, Tokens.get(i) + " feet");
                return true;
            }
            AddTermToTree(false, TokenToNum());
            return false;
            //does it start with $ and might be a price
        } else if (Tokens.get(i).charAt(0) == '$') {
            AddTermToTree(false, PriceToTerm());
            return false;
            //all others are reuglar and if needed its getting stemmed
        } else {
            if (isStemmig) {
                AddTermToTree(true, stemmer.StemToken(Tokens.get(i).toLowerCase()));
                return false;
            } else {
                AddTermToTree(true, Tokens.get(i));
                return false;
            }
        }
    }
//getting words like million to a number
    private String TokenToNum() {
        if (Tokens.get(i).contains("/")) {
            return Tokens.get(i);
        }
        String tokenWithoutCommas = Tokens.get(i);
        double number = -1;
        if (tokenWithoutCommas.contains(".") || tokenWithoutCommas.length() > 9) {
            number = Double.parseDouble(tokenWithoutCommas);
        } else {
            number = (double) Integer.parseInt(tokenWithoutCommas);
        }
        if ((number < 1000) && Tokens.get(i + 1).contains("/")) {
            return Tokens.get(i) + " " + Tokens.get(i + 1);
        } else if (number < 1000) {
            return Tokens.get(i);
        } else if ((number >= 1000) && (number < 1000000)) {
            double newnum = number / 1000;
            return "" + newnum + "K";
        } else if (number >= 1000000 && number < 1000000000) {
            double newnum = number / 1000000;
            return "" + newnum + "M";
        } else if (number <= 1000000000) {
            double newnum = number / 1000000000;
            return "" + newnum + "B";
        }
        return null;
    }
//getting a price into the right format asked
    private String PriceToTerm() {
        String tokenWithoutCommas = Tokens.get(i).replaceAll("\\$", "");
        double number = -1;
        if (Tokens.get(i).contains("/")) {
            return Tokens.get(i);
        }
        if (tokenWithoutCommas.contains("m")) {
            return tokenWithoutCommas.substring(0, tokenWithoutCommas.length() - 1) + " M Dollars";

        }
        if (tokenWithoutCommas.equals("")) {
            return "";
        }
        if (tokenWithoutCommas.contains(".") || tokenWithoutCommas.length() > 9) {
            number = Double.parseDouble(tokenWithoutCommas);
        } else {
            number = (double) Integer.parseInt(tokenWithoutCommas);
        }
        double newnumber = number;
        switch (Tokens.get(i + 1)) {
            case "Dollars":
            case "dollars":
                newnumber = number;
                break;
            case "m":
            case "million":
                newnumber = number * 1000000;
                break;
            case "bn":
            case "billion":
                newnumber = number * 1000000000;
                break;
            case "trillion":
                newnumber = number * 1000000000000.0;
                break;
        }
        if (newnumber >= 1000000) {
            newnumber = newnumber / 1000000;
            if (newnumber == Math.floor(newnumber)) {
                return ((int) newnumber) + " M Dollars";
            }
            return newnumber + " M Dollars";
        }
        if (newnumber == Math.floor(newnumber)) {
            return ((int) newnumber) + " Dollars";
        }
        return newnumber + " Dollars";
    }
//getting a number with right letter to describe it. like K/M/B/T
    public String NumberToTerm() {
        switch (Tokens.get(i + 1)) {
            case "Thousand":
                return Tokens.get(i) + "K";
            case "Million":
                return Tokens.get(i) + "M";
            case "Billion":
                return Tokens.get(i) + "B";
            case "Trillion":
                return Tokens.get(i) + "000B";
        }
        return null;
    }
//month translator
    private String TranslateMonths(int token) {
        switch (Tokens.get(token)) {
            case "Jan":
            case "January":
            case "january":
            case "JANUARY":
                return "01";
            case "Feb":
            case "February":
            case "february":
            case "FEBRUARY":
                return "02";
            case "Mar":
            case "March":
            case "march":
            case "MARCH":
                return "03";
            case "Apr":
            case "April":
            case "april":
            case "APRIL":
                return "04";
            case "May":
            case "may":
            case "MAY":
                return "05";
            case "Jun":
            case "June":
            case "june":
            case "JUNE":
                return "06";
            case "Jul":
            case "July":
            case "july":
            case "JULY":
                return "07";
            case "Aug":
            case "August":
            case "august":
            case "AUGUST":
                return "08";
            case "Sept":
            case "Sep":
            case "September":
            case "september":
            case "SEPTEMBER":
                return "09";
            case "October":
            case "Oct":
            case "october":
            case "OCTOBER":
                return "10";
            case "November":
            case "Nov":
            case "november":
            case "NOVEMBER":
                return "11";
            case "December":
            case "Dec":
            case "december":
            case "DECEMBER":
                return "12";
        }
        return null;
    }
//adding term to the right tree is decided using boolean and its generic for all maps
    public void AddTermToTree(boolean IsWord, String Token) {
        //true = termtree
        // false= specialtreemap
        String newtoken=Token;
        if(IsWord){
            newtoken = Token.toLowerCase().replaceAll("[^a-z0-9.-]", "");
        }
        if (newtoken.equals("")) return;
            if (TermsMap.containsKey(newtoken)) {
                TermsMap.put(newtoken, TermsMap.get(newtoken) + 1);
            } else {
                TermsMap.put(newtoken, 1);
            }
    }
    public void AddDocumentCosSimMechana(){
        TreeMap<String,Integer> alltermsdoc=new TreeMap<>();
        alltermsdoc.putAll(TermsMap);
        alltermsdoc.putAll(CapitalLetterWords);
        double sum=0;
        for (Map.Entry<String, Integer> entry : alltermsdoc.entrySet()) {
         double a=-1;
            if(Idf.get(entry.getKey())==null){
                a=Idf.get(entry.getKey().toLowerCase());
            }
            else{
                a=Idf.get(entry.getKey());
            }
         double b=((double) entry.getValue());
          sum+=(Math.pow(b*a,2));
        }
        try (FileWriter fw = new FileWriter(FileManager.postingpath + "\\CosSimMechana", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(d.ID+","+sum+"\n");
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void GetIdf(){
        Idf=new TreeMap<String,Double>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(FileManager.postingpath + "\\IDF")), Charset.defaultCharset());
            String[] terms=content.split("\n");
            for (int j = 0; j <terms.length ; j++) {
                String[] KeyValues=terms[j].split(",");
                Idf.put(KeyValues[0],Double.parseDouble(KeyValues[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}