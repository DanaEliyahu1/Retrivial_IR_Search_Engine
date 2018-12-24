package Ranker;


import javafx.util.Pair;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Ranker {
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
                sum+=(tfidf/mechene);
                rank += BM25((double)Integer.parseInt(termstf[1]),tfidf, DocLength.get(entry.getKey()).lengthdoc);
            }
            double sigmadoc=DocLength.get(entry.getKey()).sigmatfidf/(mechene)/(mechene);
            double CosSim= (sum)/(Math.sqrt(queryLength)*Math.sqrt(sigmadoc));
            System.out.println("Doc: " + entry.getKey() + " ,Rank:" + rank+ " ,Cossim:" + CosSim+ ", doc size"+ DocLength.get(entry.getKey()).lengthdoc+" "+entry.getValue());
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

    public double BM25(double tf,double tfidf, int doclength) {
            double numerator = (k + 1) * tfidf;
            double denominator = tf + k * (1 - b + b * (doclength / avdl));
            return numerator / denominator;
    }
}
