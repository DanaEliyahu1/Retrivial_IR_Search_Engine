package Ranker;

public class RankDoc implements Comparable {


    public double rank;
    public String docid;
/*
RankDoc will contain a result(=a dcodument) of a query with his docId and
rank according to our ranking methods.
 */
    public RankDoc(String docid, double rank) {
        this.docid=docid;
        this.rank=rank;
    }
/*
 a function that return which result is better by checking the rank value.
 This is needed for the tree-set which holds the results sorted by value using this function.
 */
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
