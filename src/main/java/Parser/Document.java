package Parser;

import java.util.ArrayList;

public class Document {
    String City;
    String ID;
    String path;
    String Text;
    String filename;

    public Document(String path, String city, String id, String text, String filename) {
        this.City = city;
        this.path = path;
        this.ID = id;
        this.Text = text;
        this.filename=filename;

    }

    public ArrayList<String> TextToToken() {
        String[] Tokens = Text.split("--|\\s+");
        return ClearTokens(Tokens);
    }

    public ArrayList<String> ClearTokens(String[] token) {
        ArrayList<String> TokenArr = new ArrayList<>();
        for (int i = 0; i < token.length; i++) {
            token[i] = token[i].replaceAll("\\?|\\*|\\,|\\`|\\;|\\?|\\:|\\||\\>|\\<|\\^|\\\"|\\\\||\\|\\,|\\(|\\[|\\)|\\]|\\!|\\+","");
            if (!(token[i].equals(""))) {

                while (!token[i].equals("") &&  (  token[i].charAt(0) == '\"'  || token[i].charAt(0) == '\'' )) {
                    token[i] = token[i].substring(1);
                }
                while (!token[i].equals("") &&  (  token[i].charAt(token[i].length() - 1) == ',' || token[i].charAt(token[i].length() - 1) == '.'   || token[i].charAt(token[i].length() - 1) == '\"' || token[i].charAt(token[i].length() - 1) == '\''  || token[i].charAt(token[i].length() - 1) == '-')) {
                    token[i] = token[i].substring(0, token[i].length() - 1);
                }
                if (token[i].contains("/") && Character.isLetter(token[i].charAt(0))) {
                    String[] words = token[i].split("/");
                    for (int j = 0; j < words.length; j++) {
                        token[i] = words[j].replaceAll("\\?|\\*|\\`|\\;|\\?|\\:|\\||\\>|\\<|\\^|\\\"|\\\\||\\|\\,|\\+|\\(|\\[|\\)|\\]|\\=","");
                        while (!token[i].equals("") && ( token[i].charAt(0) == '\"' || token[i].charAt(0) == '\'' || token[i].charAt(0) == '.')) {
                            token[i] = token[i].substring(1);
                        }
                        while (!token[i].equals("") && (  token[i].charAt(token[i].length() - 1) == ',' || token[i].charAt(token[i].length() - 1) == '.'  || token[i].charAt(token[i].length() - 1) == '\"' || token[i].charAt(token[i].length() - 1) == '\'' || token[i].charAt(token[i].length() - 1) == '-')) {
                            token[i] = token[i].substring(0, token[i].length() - 1);
                        }
                        if (token[i].length()>1) {
                            TokenArr.add(token[i]);
                        }
                    }

                }
                else if (token[i].length()>1){
                    TokenArr.add(token[i]);
                }
            }
        }
        return TokenArr;
    }
}

