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
        this.ID=id;
        this.Text= text;

    }

    public ArrayList<String> GetTokens() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
            String[] text = content.split("<TEXT>");
            String[] Tokens = text[1].split(" ");
            return eliminate(Tokens);
        } catch (IOException e) {
            e.printStackTrace();

        }

        return null;
    }

    public ArrayList<String> eliminate(String[] token) {
        ArrayList<String> TokenArr = new ArrayList<>();
        for (int i = 0; i < token.length; i++) {
            if (!(token[i].equals(""))) {
                while (!token[i].equals("") && (token[i].charAt(0) == '\n' || token[i].charAt(0) == '[' || token[i].charAt(0) == '('))
                {
                    token[i] = token[i].substring(1);
                }
                while (!token[i].equals("") && (token[i].charAt(token[i].length()-1) == '\n' || token[i].charAt(token[i].length()-1) == ']' || token[i].charAt(token[i].length()-1) == ')' || token[i].charAt(token[i].length()-1) == ',' || token[i].charAt(token[i].length()-1) == '.'|| token[i].charAt(token[i].length()-1) == ':'|| token[i].charAt(token[i].length()-1) == '?'|| token[i].charAt(token[i].length()-1) == '\"'))
                {
                    token[i] = token[i].substring(0, token[i].length() - 1);
                }
                if (!token[i].equals("")) {
                    TokenArr.add(token[i]);
                }
            }
                /*if ((token[i].charAt(0)) == '\n'){
                    token[i]=token[i].substring(1,token[i].length());
                    if(token[i].equals("")){
                        continue;
                    }
                }
                if ((token[i].charAt(token[i].length() - 1)) == '.' || (token[i].charAt(token[i].length() - 1)) == ',') {
                   if((token[i].charAt(0)) == '\n'){
                       TokenArr.add(token[i].substring(1, token[i].length() - 1));
                   }else {
                       TokenArr.add(token[i].substring(0, token[i].length() - 1));
                   }
                }
                TokenArr.add(token[i]);
            }*/
        } return TokenArr;
    }
}

