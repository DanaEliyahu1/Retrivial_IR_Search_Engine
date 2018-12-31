package Ranker;

public class DocInfo {

    public int lengthdoc;
    public double sigmatfidf;
    public int maxtf;
/*
we would like to keep info on each doc that is semi relevant to the query
therefore we will get all of it from the disk and in order to put it in a map
we will create an object with all of this.
 */
    public DocInfo(int lengthdoc, double sigmatfidf, int maxtf) {
        this.lengthdoc = lengthdoc;
        this.sigmatfidf = sigmatfidf;
        this.maxtf = maxtf;
    }
}
