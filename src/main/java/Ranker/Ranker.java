package Ranker;


import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Ranker {
    public int b;
    public int k;
    public int avdl;
    public TreeMap<String, Integer> DocLength;
    public TreeSet<RankDoc> SortedDocs;


    public TreeSet<RankDoc> Rank(TreeMap<String, String> DocDictionary, TreeMap<String, int[]> Index) {
        for (Map.Entry<String, String> entry : DocDictionary.entrySet()) {
            double rank = 0;
            String[] terms = entry.getValue().split("\\|");
            for (int i = 0; i < terms.length; i++) {
                String[] termstf = terms[i].split("_");
                rank += BM25(Integer.parseInt(termstf[1]), Index.get(termstf[0])[2], DocLength.get(entry.getKey()));

            }
            SortedDocs.add(new RankDoc(entry.getKey(), rank));
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

    public double BM25(int tf, double idf, int doclength) {
            double numerator = (k + 1) * tf * idf;
            double denominator = tf + k * (1 - b + b * (doclength / avdl));
            return numerator / denominator;
    }


}
