/**
 * Created by Rocky on 2017/12/17.
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.sandbox.*;
import javax.management.Query;

public class Search {
    public static void main(String args[]) throws IOException {
//        File indexrepository_file = new File("D:/index/");
        File indexrepository_file = new File("D:/indexFromMongoDB/");
        Path path = indexrepository_file.toPath();
        Directory directory = FSDirectory.open(path);
        IndexReader indexReader = DirectoryReader.open(directory);
        // 创建一个IndexSearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 创建一个查询对象
        String field="中国人民银行\n" +
                "\n" +
                " 关于小额贷款公司试点的指导意见";
        QueryParser parser = new QueryParser("name",new StandardAnalyzer());

        try {
//            DuplicateFilter filter=new DuplicateFilter(field);
            System.out.println(field.length());
//            if (field.length()<=7){
//                field = "中华人民共和国"+field;
//
//            }
            org.apache.lucene.search.Query query1 = parser.parse(field);
            System.out.println("");
            TopDocs topDocs1 = indexSearcher.search(query1, 30);
            System.out.println("可能有关联的查询结果数：" + topDocs1.totalHits);
            System.out.println("=======================");
            ScoreDoc[] scoreDocs = topDocs1.scoreDocs;
//            System.out.println( indexSearcher.doc(scoreDocs[0].doc).get("name"));
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 取对象document的对象id
                int docID = scoreDoc.doc;

                // 相关度得分
                float score = scoreDoc.score;

                // 根据ID去document对象
                Document document = indexSearcher.doc(docID);

                System.out.println(document.get("name"));
                System.out.println("");
                System.out.println(document.get("time")+"  所属类别："+document.get("catogery"));
                System.out.println("");
                System.out.println("相关度得分：" + score);
//                System.out.println(document.getField("name").stringValue());
                System.out.println("=======================");
            }

            indexReader.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
