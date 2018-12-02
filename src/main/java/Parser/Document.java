package Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Document {
    String City;
    String ID;
    String path;
    String Text;
    String filename;


    public Document(String path, String city, String id, String text, String filename) {
        this.City = city.toUpperCase();
        this.path = path;
        this.ID = id;
        this.Text = text;
        this.filename=filename;

    }

    public ArrayList<String> TextToToken() {
        //String[] Tokens = Text.replaceAll("[^A-Za-z0-9$%\\s.\\-\\\\]+","").split("--|\\s+");
        String[] Tokens = replaceFromMap(Text,ReadFile.replace).split("--|\\s+");
        return ClearTokens(Tokens);
    }
    public static String replaceFromMap(String string, Map<String, String> replacements) {
        StringBuilder sb = new StringBuilder(string);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            int start = sb.indexOf(key, 0);
            while (start > -1) {
                int end = start + key.length();
                int nextSearchStart = start + value.length();
                sb.replace(start, end, value);
                start = sb.indexOf(key, nextSearchStart);
            }
        }
        return sb.toString();
    }

    public ArrayList<String> ClearTokens(String[] token) {
        ArrayList<String> TokenArr = new ArrayList<>();
        for (int i = 0; i < token.length; i++) {
            if (!(token[i].equals(""))) {

                while (!token[i].equals("") &&  ( token[i].charAt(0) == '/'  ||token[i].charAt(0) == '\\'  )) {
                    token[i] = token[i].substring(1);
                }
                while (!token[i].equals("") &&  ( token[i].charAt(token[i].length() - 1) == '/'||  token[i].charAt(token[i].length() - 1) == '\\' || token[i].charAt(token[i].length() - 1) == '.'   || token[i].charAt(token[i].length() - 1) == '-')) {
                    token[i] = token[i].substring(0, token[i].length() - 1);
                }
                if (token[i].contains("/") && Character.isLetter(token[i].charAt(0))) {
                    String[] words = token[i].split("/");
                    for (int j = 0; j < words.length; j++) {
                        token[i] = words[j];
                        while (!token[i].equals("") && (token[i].charAt(0) == '/'  ||token[i].charAt(0) == '\\' || token[i].charAt(0) == '.')) {
                            token[i] = token[i].substring(1);
                        }
                        while (!token[i].equals("") && ( token[i].charAt(token[i].length() - 1) == '/'||  token[i].charAt(token[i].length() - 1) == '\\' || token[i].charAt(token[i].length() - 1) == '.'  || token[i].charAt(token[i].length() - 1) == '-')) {
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

