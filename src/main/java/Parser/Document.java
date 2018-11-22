package Parser;

import javax.print.Doc;
import javax.xml.soap.Text;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Document {
    String City;
    String ID;
    String path;
    String Text;

    public Document(String path, String city, String id, String text) {
        this.City = city;
        this.path = path;
        this.ID = id;
        this.Text = text;

    }

    public ArrayList<String> GetTokens() {
        //String content = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
        //String[] text = content.split("<TEXT>");
        //String cutend=text[1].split("</TEXT>")[0];
        String[] Tokens = Text.split(" |\\--");
        return eliminate(Tokens);
    }

    public ArrayList<String> eliminate(String[] token) {
        ArrayList<String> TokenArr = new ArrayList<>();
        for (int i = 0; i < token.length; i++) {
            token[i] = token[i].replaceAll("\\?|\\*|\\`|\\;|\\?|\\:|\\||\\>|\\<|\\^|\\\"|\\\\||\\\n|\\,|\\+","");
            if (!(token[i].equals(""))) {

                while (!token[i].equals("") && (token[i].charAt(0) == '\n' || token[i].charAt(0) == '[' || token[i].charAt(0) == '(' || token[i].charAt(0) == '\"'  || token[i].charAt(0) == '\'' )) {
                    token[i] = token[i].substring(1);
                }
                while (!token[i].equals("") && (token[i].charAt(token[i].length() - 1) == '\n' || token[i].charAt(token[i].length() - 1) == ']' || token[i].charAt(token[i].length() - 1) == ')' || token[i].charAt(token[i].length() - 1) == ',' || token[i].charAt(token[i].length() - 1) == '.'   || token[i].charAt(token[i].length() - 1) == '\"' || token[i].charAt(token[i].length() - 1) == '\''  || token[i].charAt(token[i].length() - 1) == '-')) {
                    token[i] = token[i].substring(0, token[i].length() - 1);
                }
                if (token[i].contains("/") && Character.isLetter(token[i].charAt(0))) {
                    String[] words = token[i].split("/");
                    for (int j = 0; j < words.length; j++) {
                        token[i] = words[j].replaceAll("\\?|\\*|\\`|\\;|\\?|\\:|\\||\\>|\\<|\\^|\\\"|\\\\||\\\n|\\,|\\+","");
                        while (!token[i].equals("") && (token[i].charAt(0) == '\n' || token[i].charAt(0) == '[' || token[i].charAt(0) == '(' || token[i].charAt(0) == '\"' || token[i].charAt(0) == '\'' || token[i].charAt(0) == '.')) {
                            token[i] = token[i].substring(1);
                        }
                        while (!token[i].equals("") && (token[i].charAt(token[i].length() - 1) == '\n' || token[i].charAt(token[i].length() - 1) == ']' || token[i].charAt(token[i].length() - 1) == ')' || token[i].charAt(token[i].length() - 1) == ',' || token[i].charAt(token[i].length() - 1) == '.'  || token[i].charAt(token[i].length() - 1) == '\"' || token[i].charAt(token[i].length() - 1) == '\'' || token[i].charAt(token[i].length() - 1) == '-')) {
                            token[i] = token[i].substring(0, token[i].length() - 1);
                        }
                        if (!token[i].equals("")) {
                            TokenArr.add(token[i]);
                        }
                    }

                }
                else if (!token[i].equals("")){
                    TokenArr.add(token[i]);
                }
            }
        }
        return TokenArr;
    }
}

