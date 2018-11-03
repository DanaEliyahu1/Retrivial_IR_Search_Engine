package Parser;

import java.util.*;

public class Parse {

    HashSet StopWord;
    TreeMap TermsMap;
    TreeMap<String, String> SpecialTermsMap;
    Stemmer stemmer;
    HashSet Months;
    HashSet NumberHash;
    HashSet DollarHash;
    ArrayList<String> Tokens;
    int i;

    public Parse(HashSet stopWord, HashSet months, HashSet numberHash, HashSet dollarHash) {
        this.StopWord = stopWord;
        this.Months = months;
        this.NumberHash = numberHash;
        this.DollarHash = dollarHash;
        Stemmer stemmer = new Stemmer();
        TermsMap = new TreeMap();
        SpecialTermsMap = new TreeMap();
    }

    public void parse(Document Doc) {
        Tokens = Doc.GetTokens();
        // Tokens = currline.split(" |\\%|\\$|\\(|\\)\\[|\\\\]|\\\"|\\)|\\:|\\;");
        for (int i = 0; i < Tokens.size(); i++) {
            if (ParseRules()) {
                i++;
            }
        }

        for (Map.Entry<String, String> entry : SpecialTermsMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
        }
    }

    private boolean ParseRules() {
        if (StopWord.contains(Tokens.get(i))) {
            return false;
        }
        if (Tokens.get(i).equals("")) {
            System.out.println();
        }
        if (Character.isDigit(Tokens.get(i).charAt(0))) {
            //Special
            if (Months.contains(Tokens.get(i+1))) {
                SpecialTermsMap.put(DateToTerm(), "");
                return true;
            } else if (NumberHash.contains(Tokens.get(i+1))) {
                SpecialTermsMap.put(NumberToTerm(), "");
                return true;
            } else if (DollarHash.contains(Tokens.get(i+1))) {
                SpecialTermsMap.put(PriceToTerm(), "");
                return true;
            } else if (Tokens.get(i+1).equals("percent") || Tokens.get(i+1).equals("percentage") || Tokens.get(i+1).equals("%")) {
                SpecialTermsMap.put(PercentToTerm(), "");
                return true;
            }
            return false;
        } else if (Character.isUpperCase(Tokens.get(i).charAt(0))) {
            return true;

        } else if (Months.contains(Tokens.get(i))) {
            SpecialTermsMap.put(MonthsToTerm(), "");


        } else if (Tokens.get(i).contains("-")) {
            SpecialTermsMap.put(Tokens.get(i), null);
        }
        return false;
    }

    private String MonthsToTerm() {
        return Tokens.get(i);
    }

    private String PercentToTerm() {
        return Tokens.get(i);
    }

    private String PriceToTerm() {
        return Tokens.get(i);
    }

    private String NumberToTerm() {
        return Tokens.get(i);
    }

    private String DateToTerm() {
        switch (Tokens.get(i+1)) {
            case "Jan":
            case "January":
            case "january":
            case "JANUARY":
                return "01-" + Tokens.get(i);
            case "Feb":
            case "February":
            case "february":
            case "FEBRUARY":
                return "02-" + Tokens.get(i);
            case "Mar":
            case "March":
            case "march":
            case "MARCH":
                return "03-" + Tokens.get(i);
            case "Apr":
            case "April":
            case "april":
            case "APRIL":
                return "04-" + Tokens.get(i);
            case "May":
            case "may":
            case "MAY":
                return "05-" + Tokens.get(i);
            case "Jun":
            case "June":
            case "june":
            case "JUNE":
                return "06-" + Tokens.get(i);
            case "Jul":
            case "July":
            case "july":
            case "JULY":
                return "07-" + Tokens.get(i);
            case "Aug":
            case "August":
            case "august":
            case "AUGUST":
                return "08-" + Tokens.get(i);
            case "Sept":
            case "Sep":
            case "September":
            case "september":
            case "SEPTEMBER":
                return "09-" + Tokens.get(i);
            case "October":
            case "Oct":
            case "october":
            case "OCTOBER":
                return "10-" + Tokens.get(i);
            case "November":
            case "Nov":
            case "november":
            case "NOVEMBER":
                return "11-" + Tokens.get(i);
            case "December":
            case "Dec":
            case "december":
            case "DECEMBER":
                return "12-" + Tokens.get(i);
        }
        return null;
    }

}