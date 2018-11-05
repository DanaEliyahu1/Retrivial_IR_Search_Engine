package Parser;

import FileManager.FileManager;

import java.text.NumberFormat;
import java.util.*;

public class Parse {
    public Parse() {
        TermsMap=new TreeMap<String, TermInfo>() ;
         SpecialTermsMap=new TreeMap<String, TermInfo>();
    }

    static HashSet StopWord;
    TreeMap<String, TermInfo> TermsMap;
    TreeMap<String, TermInfo> SpecialTermsMap;
    static Stemmer stemmer;
    static HashSet Months;
    static HashSet NumberHash;
    static HashSet DollarHash;
    static TreeSet<String> TermCollection ;
    ArrayList<String> Tokens;
    int i;

    public Parse(HashSet stopWord, HashSet months, HashSet numberHash, HashSet dollarHash) {
        this.StopWord = stopWord;
        this.Months = months;
        this.NumberHash = numberHash;
        this.DollarHash = dollarHash;
        stemmer = new Stemmer();
        TermsMap = new TreeMap();
        SpecialTermsMap = new TreeMap();
    }

    public void parse(Document Doc) {
        Tokens = Doc.GetTokens();
        for (i = 0; i < Tokens.size(); i++) {
            if (ParseRules()) {
                i++;
            }
        }

        for (Map.Entry<String, TermInfo> entry : SpecialTermsMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
        }
        for (Map.Entry<String, TermInfo> entry : TermsMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
        }
    }

    private boolean ParseRules() {
        if (StopWord.contains(Tokens.get(i))) {
            return false;
        }
        if (Tokens.get(i).contains("-")) {
            AddTermToTree(false,Tokens.get(i));
            return false;
        } else if (Character.isDigit(Tokens.get(i).charAt(0))&&Tokens.get(i).matches("^[0-9]+([,.][0-9]?)?$")) {
            //Special
            if (Months.contains(Tokens.get(i + 1))) {
                AddTermToTree(false,TranslateMonths(i + 1) + "-" + Tokens.get(i));
                return true;
            } else if (NumberHash.contains(Tokens.get(i + 1))) {
                AddTermToTree(false,NumberToTerm());
                return true;
            } else if (DollarHash.contains(Tokens.get(i + 1))) {
                AddTermToTree(false,PriceToTerm());
                return true;
            } else if (Tokens.get(i).charAt(Tokens.get(i).length() - 1) == '%') {
                AddTermToTree(false,Tokens.get(i));
                return true;
            } else if (Tokens.get(i + 1).equals("percent") || Tokens.get(i + 1).equals("percentage") || Tokens.get(i + 1).equals("%")) {
                AddTermToTree(false,Tokens.get(i) + "%");
                return true;
            }
            AddTermToTree(false,TokenToNum());
        } else if (Months.contains(Tokens.get(i)) && Character.isDigit(Tokens.get(i + 1).charAt(0))) {
            AddTermToTree(false,Tokens.get(i + 1) + "-" + TranslateMonths(i));
        } else if (Character.isUpperCase(Tokens.get(i).charAt(0))) {
            return true;
        }
        else if(Tokens.get(i).charAt(0)=='$'){
            AddTermToTree(false,PriceToTerm());
        }
        else{
            AddTermToTree(true,stemmer.StemToken(Tokens.get(i)));
        }

        return false;
    }

    private String TokenToNum() {
        if (Tokens.get(i).contains("/")) {
            return Tokens.get(i);
        }
        String tokenWithoutCommas = Tokens.get(i).replaceAll(",", "");
        double number = -1;
        if (tokenWithoutCommas.contains(".")) {
            number = Double.parseDouble(tokenWithoutCommas);
        } else {
            number = (double) Integer.parseInt(tokenWithoutCommas);
        }
        if ((number < 1000) && Tokens.get(i + 1).contains("/")) {
            return Tokens.get(i) + " " + Tokens.get(i + 1);
        } else if (number < 1000) {
            return Tokens.get(i);
        }else if ((number >= 1000) && (number < 1000000)) {
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

    private String PriceToTerm() {
        String tokenWithoutCommas = Tokens.get(i).replaceAll(",", "");
        tokenWithoutCommas=tokenWithoutCommas.replaceAll("\\$","");
        double number = -1;
        if (tokenWithoutCommas.contains(".")) {
            number = Double.parseDouble(tokenWithoutCommas);
        } else {
            number = (double) Integer.parseInt(tokenWithoutCommas);
        }
        double newnumber=number;
        switch (Tokens.get(i+1)){
            case "Dollars":
            case "dollars":
                newnumber=number;
                break;
            case "m":
            case "million":
                newnumber=number*1000000;
                break;
            case "bn":
            case "billion":
                newnumber=number*1000000000;
                break;
            case "trillion":
                newnumber=number*1000000000000.0;
                break;
        } if(newnumber>=1000000){
            newnumber=newnumber/1000000;
            if( newnumber==Math.floor(newnumber)){
                return ((int)newnumber)+" M Dollars";
            }
            return newnumber+" M Dollars";
        }
        if( newnumber==Math.floor(newnumber)){
            return ((int)newnumber)+" Dollars";
        }
        return newnumber+" Dollars";
    }

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

    public void AddTermToTree(boolean TreeMap, String Token){
    //true = termtree
    // false= specialtreemap
        if(TreeMap){
        if(TermsMap.containsKey(Token)){
            TermsMap.get(Token).TermCount++;
        }
        else {
            TermsMap.put(Token,new TermInfo());
        }
        }
        else {
            if(SpecialTermsMap.containsKey(Token)){
                SpecialTermsMap.get(Token).TermCount++;
            }
            else {
                SpecialTermsMap.put(Token,new TermInfo());
            }

        }
    }
    public void ResultToFile (){
         FileManager fileManager=new FileManager();
        for (Map.Entry<String, TermInfo> entry : SpecialTermsMap.entrySet()) {
           fileManager.AddTermTofile(entry.getKey(),entry.getValue());
        }
        for (Map.Entry<String, TermInfo> entry : TermsMap.entrySet()) {
            fileManager.AddTermTofile(entry.getKey(),entry.getValue());
        }


    }
}