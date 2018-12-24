package Ranker;

public class DocInfo {

    public int lengthdoc;
    public double sigmatfidf;
    public int maxtf;

    public DocInfo(int lengthdoc, double sigmatfidf, int maxtf) {
        this.lengthdoc = lengthdoc;
        this.sigmatfidf = sigmatfidf;
        this.maxtf = maxtf;
    }
}
