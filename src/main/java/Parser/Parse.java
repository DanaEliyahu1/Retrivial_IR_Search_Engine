package Parser;

import java.util.*;

public class Parse {

    HashSet StopWord;
    TreeMap TermsMap;
    TreeMap <String,String> SpicalTermsMap;
    Stemmer stemmer;
    HashSet Months;
    HashSet NumberHash;
    HashSet DollarHash;
    String[] Tokens;
    int i;

    public Parse(HashSet stopWord, HashSet months, HashSet numberHash, HashSet dollarHash) {
        this.StopWord=stopWord;
        this.Months=months;
        this.NumberHash=numberHash;
        this.DollarHash=dollarHash;
        Stemmer stemmer=new Stemmer();
        TermsMap=new TreeMap();
        SpicalTermsMap=new TreeMap();
    }

    public void parse(Document Doc) {
        String currline = Doc.GetNextLine();
        while (!currline.equals("")) {
            Tokens = currline.split(" |\\,|\\%|\\.|\\$|\\(|\\)\\[|\\\\]|\\\"|\\)|\\:|\\;");
            for (int i = 0; i < Tokens.length; i++) {
                if (ParseRules()) {
                    i++;
                }

            }
            currline = Doc.GetNextLine();
        }
        for (Map.Entry<String, String> entry : SpicalTermsMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
        }
    }

    private boolean ParseRules() {
        if (StopWord.contains(Tokens[i])) {
            return false;
        }
        if(Tokens[i].equals("")){
            System.out.println();
        }
        if (Character.isDigit(Tokens[i].charAt(0))) {
            //Special
            if (Months.contains(Tokens[i + 1])) {
                SpicalTermsMap.put(DateToTerm(),"");
                return true;
            } else if (NumberHash.contains(Tokens[i + 1])) {
                SpicalTermsMap.put(NumberToTerm(),"");
                return true;
            } else if (DollarHash.contains(Tokens[i + 1])) {
                SpicalTermsMap.put(PriceToTerm(),"");
                return true;
            } else if (Tokens[i + 1].equals("percent") || Tokens[i + 1].equals("percentage") || Tokens[i + 1].equals("%")) {
                SpicalTermsMap.put(PercentToTerm(),"");
                return true;
            }
            return false;
        } else if (Character.isUpperCase(Tokens[i].charAt(0))) {
            return true;

        }
        else if(Months.contains(Tokens[i])){
            SpicalTermsMap.put(MonthsToTerm(),"");


        }
        else if(Tokens[i].contains("-")){
            SpicalTermsMap.put(Tokens[i],null);
        }
        return false;
    }

    private String MonthsToTerm() {
        return Tokens[i];
    }

    private String PercentToTerm() {
        return Tokens[i];
    }

    private String PriceToTerm() {
        return Tokens[i];
    }

    private String NumberToTerm() {
        return Tokens[i];
    }

    private String DateToTerm() {
        switch (Tokens[i+1]){
            case "Jan":
            case "January":
            case "january":
            case "JANUARY":
                return "01-"+Tokens[i];
            case "Feb":
            case "February":
            case "february":
            case "FEBRUARY":
                return "02-"+Tokens[i];
            case "Mar":
            case "March":
            case "march":
            case "MARCH":
                return "03-"+Tokens[i];
            case "Apr":
            case "April":
            case "april":
            case "APRIL":
                return "04-"+Tokens[i];
            case "May":
            case "may":
            case "MAY":
                return "05-"+Tokens[i];
            case "Jun":
            case "June":
            case "june":
            case "JUNE":
                return "06-"+Tokens[i];
            case "Jul":
            case "July":
            case "july":
            case "JULY":
                return "07-"+Tokens[i];
            case "Aug":
            case "August":
            case "august":
            case "AUGUST":
                return "08-"+Tokens[i];
            case "Sept":
            case "Sep":
            case "September":
            case "september":
            case "SEPTEMBER":
                return "09-"+Tokens[i];
            case "October":
            case "Oct":
            case "october":
            case "OCTOBER":
                return "10-"+Tokens[i];
            case "November":
            case "Nov":
            case "november":
            case "NOVEMBER":
                return "11-"+Tokens[i];
            case "December":
            case "Dec":
            case "december":
            case "DECEMBER":
                return "12-"+Tokens[i];
        }
        return null;
    }

}
