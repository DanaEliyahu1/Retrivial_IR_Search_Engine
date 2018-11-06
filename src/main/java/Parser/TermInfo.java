package Parser;

public class TermInfo {

    int TermCount;
    double priority;
    String key;

    public TermInfo() {

        this.TermCount=1;
    }

    @Override
    public String toString() {
        return ""+TermCount;
    }
}
