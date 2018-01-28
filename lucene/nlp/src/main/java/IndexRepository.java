package localLucene; /**
 * Created by Rocky on 2017/12/17.
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import localLucene.Dbuilts;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 索引存储
 */
public class IndexRepository {
    public static void main(String[] args) throws IOException {
        // 指定索引库的存放路径，需要在系统中首先进行索引库的创建
        // 指定索引库存放路径
        File indexrepository_file = new File("D:/index/");
        Path path = indexrepository_file.toPath();
        Directory directory = FSDirectory.open(path);

        // 创建一个分析器对象
        // 使用标准分析器
        Analyzer analyzer = new StandardAnalyzer();
        // 创建一个IndexwriterConfig对象
        // 分析器
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        // 创建一个IndexWriter对象，对于索引库进行写操作
        IndexWriter indexWriter = new IndexWriter(directory, config);
        // 遍历一个数据库
        String sql ="select * from loyal";
        Dbuilts db = new Dbuilts();
        List<Law> result = null;
        try {
            result= db.getList(sql, Law.class, (Object[]) null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Law p :result){
            System.out.println(p.getLcatagory());
        }

        for (Law p :result) {
            // 获得各项属性
            String name  = p.getLname();
            String time = p.getLtime();
            String catogery = p.getLcatagory();

            // 创建一个Document对象
            Document document = new Document();
            // 向Document对象中添加域信息
            // 参数：1、域的名称；2、域的值；3、是否存储；
            Field nameField = new TextField("name", name , Store.YES);
            Field timeField = new TextField("time", time , Store.YES);
            Field catogeryField = new TextField("catogery", catogery , Store.YES);
            // 将域添加到document对象中
            document.add(nameField);
            document.add(timeField);
            document.add(catogeryField);
            // 将信息写入到索引库中
            indexWriter.addDocument(document);

        }

        // 关闭indexWriter
        indexWriter.close();
    }

}