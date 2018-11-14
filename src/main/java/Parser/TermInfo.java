package Parser;

public class TermInfo {

   public int TermCount;
   // public double priority;
   // public String key;

    public TermInfo() {

        this.TermCount=1;
    }

    @Override
    public String toString() {
        return ""+TermCount;
    }
}
