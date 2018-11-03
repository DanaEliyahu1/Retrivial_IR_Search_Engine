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
        for (i = 0; i < Tokens.size(); i++) {
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
        if (Tokens.get(i).equals("November")) {
            System.out.println();
        }
        if (Character.isDigit(Tokens.get(i).charAt(0))) {
            //Special
            if (Months.contains(Tokens.get(i+1))) {
                SpecialTermsMap.put(TranslateMonths(i+1)+"-" + Tokens.get(i), "");
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
        }else if (Months.contains(Tokens.get(i)) &&Character.isDigit(Tokens.get(i+1).charAt(0))) {
            SpecialTermsMap.put(Tokens.get(i+1)+"-"+TranslateMonths(i), "");


        } else if (Character.isUpperCase(Tokens.get(i).charAt(0))) {
            return true;

        }  else if (Tokens.get(i).contains("-")) {
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

}