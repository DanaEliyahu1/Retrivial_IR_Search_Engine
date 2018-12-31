package Ranker;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Ranker {
    /*
    main function rank will use the ranker's fields and methods to return
    the best rank to the user
     */
    public double b;
    public double k;
    public double avdl;
    public TreeMap<String,DocInfo> DocLength; //length,idf
    public TreeSet<RankDoc> SortedDocs;

    public Ranker(double b, double k, double avdl) {
        SortedDocs = new TreeSet<RankDoc>();
        this.b=b;
        this.k=k;
        this.avdl=avdl;
    }

    /**
     * @param queryLength- the number of words in the query, needed to calculate CosSim
     * @param DocDictionary- a map that hold all relevant docs with tf info for calculations
     * @param Index- a map that knows for each term what is the IDF.
     * @param docLength- with the info on each one (using DocInfo object)
     * @return returns treeset with RankDoc(doc id+rank) to user which is sorted
     * highest to lowest by ranking. returns up to 50 results.
     */
    public TreeSet<RankDoc> Rank(int queryLength,TreeMap<String, String> DocDictionary, TreeMap<String, int[]> Index,TreeMap<String,DocInfo> docLength) {
        DocLength = docLength;
        for (Map.Entry<String, String> entry : DocDictionary.entrySet()) {
            double mechene= DocLength.get(entry.getKey()).maxtf;
            double rank = 0;
            double sum=0;
            String[] terms = entry.getValue().split("\\|");
            for (int i = 0; i < terms.length; i++) {
                String[] termstf = terms[i].split("_");
                double tfidf=((double)Integer.parseInt(termstf[1]))*Math.log10(DocLength.size()/(Index.get(termstf[0])[2]+1));
                sum+=(tfidf/mechene); //this sum in the sigma in the numerator of the CosSim function
                rank += BM25((double)Integer.parseInt(termstf[1]),tfidf, DocLength.get(entry.getKey()).lengthdoc);
            }
            double sigmadoc=DocLength.get(entry.getKey()).sigmatfidf/(mechene)/(mechene); //normalized doc vector length
            double CosSim= (sum)/(Math.sqrt(queryLength)*Math.sqrt(sigmadoc));
           // System.out.println("Doc: " + entry.getKey() + " ,Rank:" + rank+ " ,Cossim:" + CosSim+ ", doc size"+ DocLength.get(entry.getKey()).lengthdoc+" "+entry.getValue());
            SortedDocs.add(new RankDoc(entry.getKey(), (rank+0.1*CosSim)/2));
        }
        TreeSet<RankDoc> FinalDocRank= new TreeSet();
        if(SortedDocs.size()>50){
            Iterator<RankDoc> iterator =SortedDocs.iterator();
            for (int i = 0; i <50 ; i++) {
                FinalDocRank.add(iterator.next());
            }
        }else {
            FinalDocRank=SortedDocs;
        }
    return FinalDocRank;
    }
/*
we included a function that gets all parameters and returns BM25 value for a term in a document
 */
    public double BM25(double tf,double tfidf, int doclength) {
            double numerator = (k + 1) * tfidf;
            double denominator = tf + k * (1 - b + b * (doclength / avdl));
            return numerator / denominator;
    }
}
