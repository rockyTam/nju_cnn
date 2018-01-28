package localLucene;

import com.mongodb.*;
import com.google.gson.Gson;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Rocky on 2017/12/24.
 */
public class IndexRepositoryForMongo {

    public static boolean isRepeat(String name){
        MongoClient mc= new MongoClient();
        DB tset = mc.getDB("loyalDB");
        DBCollection law = tset.getCollection("loyalUpdata");
        BasicDBObject queryObject = new BasicDBObject("lname",name);
        DBObject dbObject = law.findOne(queryObject);
//        Gson gson=new Gson();
//        Law law1= gson.fromJson(dbObject.toString(), Law.class);
        if (dbObject!=null){
            return true;
        }
        else return false;
    }
    public static void main(String args[]) throws IOException {
        //连接mongodb
        MongoClient mc= new MongoClient();
        DB tset = mc.getDB("loyalDB");
        DBCollection law = tset.getCollection("loyalUpdata");
        DBCursor dbCursor = law.find();
        Gson gson=new Gson();
        // 指定索引库的存放路径，需要在系统中首先进行索引库的创建
        // 指定索引库存放路径
        File indexrepository_file = new File("D:/indexFromMongoDB/");
        Path path = indexrepository_file.toPath();
        Directory directory = FSDirectory.open(path);

        // 创建一个分析器对象
        // 使用标准分析器
        Analyzer analyzer = new StandardAnalyzer();
        // 创建一个IndexwriterConfig对象
        // 分析器
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
//        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // 创建一个IndexWriter对象，对于索引库进行写操作
        IndexWriter indexWriter = new IndexWriter(directory,config);

        // 遍历一个数据库
        while (dbCursor.hasNext()){

            DBObject dbObject =  dbCursor.next();
            Law law1= gson.fromJson(dbObject.toString(), Law.class);
            String name  = law1.getLname();
            String time = law1.getLtime();
            String catogery = law1.getLcatagory();
            // 创建一个Document对象
            Document document = new Document();
                // 向Document对象中添加域信息
                // 参数：1、域的名称；2、域的值；3、是否存储；
            System.out.println(name);
            Field nameField = new TextField("name", name , Field.Store.YES);
            Field timeField = new TextField("time", time , Field.Store.YES);
            Field catogeryField = new TextField("catogery", catogery , Field.Store.YES);

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
