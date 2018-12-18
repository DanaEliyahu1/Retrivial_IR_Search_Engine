package Ranker;

public class RankDoc implements Comparable {


    double rank;
    String docid;

    public RankDoc(String docid, double rank) {
        this.docid=docid;
        this.rank=rank;
    }

    @Override
    public int compareTo(Object o) {
        if(((RankDoc)o).rank>this.rank){
          return 1;
        }
        else if(((RankDoc)o).rank<this.rank){
            return -1;
        }
        return 0;
    }
}
