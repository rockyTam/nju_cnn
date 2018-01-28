import com.google.gson.Gson;
import com.mongodb.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Time;

/**
 * Created by Rocky on 2017/12/25.
 */

public class SerachFromLucene {

    public static String getLim(String s){
        return s.replace("<", "").replace("《", "").replace("《", "").replace("》", "").replace("﹥", "").replace("＜", "").replace("》", "").replace("﹤", "").replace("〉", "").replace("〈", "")
                .replace("〉", "").replace("〈", "").replace("<", "").replace(">", "").replace("﹥", "").replace("＜", "").replace("＞", "");

    }

    public static void main(String args[]) throws IOException {
//        MongoClient mc= new MongoClient(); //local
        MongoClient mc= new MongoClient("192.168.68.11",20000); //连接学院数据库
        DB tset = mc.getDB("lawCase");
        DBCollection law = tset.getCollection("lawreference");
        DBCursor dbCursor = law.find().addOption(Bytes.QUERYOPTION_NOTIMEOUT);
        Gson gson=new Gson();

        MongoClient lawClient= new MongoClient();
        DB loyalDB = lawClient.getDB("loyalDB");
        DBCollection compareLaw = loyalDB.getCollection("loyalUpdata");
        int count = 0;

        File indexrepository_file = new File("D:/indexFromMongoDB/");
        Path path = indexrepository_file.toPath();
        Directory directory = FSDirectory.open(path);
        IndexReader indexReader = DirectoryReader.open(directory);
        // 创建一个IndexSearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 创建一个查询对象
        QueryParser parser = new QueryParser("name",new StandardAnalyzer());

        //记录当前时间
        long time =System.currentTimeMillis();
        //遍历整个数据库
        while (dbCursor.hasNext()) {
            count++;
            if (count %1000 ==0){
                long usetime = System.currentTimeMillis()-time;
                //输出所用时间
                System.out.println("遍历了"+count+"条数据 "+"用时:"+(usetime%(1000*60*60*24)/(1000*60*60))+"时"+(usetime%(1000*60*60))/(1000*60)+"分"+(usetime%(1000*60)/1000)+"秒");
            }
            DBObject dbObject = dbCursor.next();
            LawList law1 = gson.fromJson(dbObject.toString(), LawList.class);
            int countforlaw = 1;
            boolean isModify = false;
            DBObject value = new BasicDBObject();
            DBCollection result = loyalDB.getCollection("resultTest18/1/24");

            for (LawForMongo lawForMongo : law1.getReferences()){
                String realName = lawForMongo.getName();
                String realNameLim = realName;
                if (realName.charAt(0)=='《'){

                    realNameLim = realName.substring(1,realName.length()-1);
//                    System.out.println(r);
                }
                BasicDBObject queryObject = new BasicDBObject("lname",realNameLim);
                DBObject dbObject1 = compareLaw.findOne(queryObject);
                if (dbObject1!=null){

                }
                else {
                    try {

                        org.apache.lucene.search.Query query1 = parser.parse(realName);
                        TopDocs topDocs1 = indexSearcher.search(query1, 3);
//                        System.out.println("可能有关联的查询结果数：" + topDocs1.totalHits);
//                        System.out.println("=======================");
                        ScoreDoc[] scoreDocs = topDocs1.scoreDocs;
                        String correctResult = indexSearcher.doc(scoreDocs[0].doc).get("name");
                        String correctResult2 = indexSearcher.doc(scoreDocs[1].doc).get("name");
                        String correctResult3 = indexSearcher.doc(scoreDocs[2].doc).get("name");
//                        System.out.println(realName+"最相近的结果是"+correctResult );
//                        System.out.println("=======================");;
                        //写入结果至result数据库
                        String in = realName.replace("<", "").replace("《", "").replace("》", "").replace("﹥", "").replace("＜", "").replace("﹤", "").replace("〉", "").replace("〈", "");
                        String out1 = correctResult.replace("〉", "").replace("〈", "").replace("<", "").replace("《", "").replace(">", "").replace("》", "").replace("﹥", "").replace("＜", "").replace("＞", "");
//                        System.out.println(out);
                        String out2 =correctResult2.replace("〉", "").replace("〈", "").replace("<", "").replace("《", "").replace(">", "").replace("》", "").replace("﹥", "").replace("＜", "").replace("＞", "");
                        String out3 =correctResult3.replace("〉", "").replace("〈", "").replace("<", "").replace("《", "").replace(">", "").replace("》", "").replace("﹥", "").replace("＜", "").replace("＞", "");


                        if (!in.equals(out1)&&!in.equals(out2)&&!in.equals(out3)){
                            //判断从lucene得到的结果是否完全匹配原名，不匹配则记录下来，并推荐给用户
                            value.put("fullTextId", law1.getFullTextId());
                            if (!isModify)
                                result.insert(value);
                            isModify = true;
                            if (realNameLim.length()<=7){ //若字符数小于7则在其前面加上中华人民共和国，以提升搜索准确度
                                String filed2 = "中华人民共和国"+realName;
                                org.apache.lucene.search.Query query2 = parser.parse(filed2);
                                TopDocs topDocs2 = indexSearcher.search(query2, 1);
                                ScoreDoc[] scoreDocs2 = topDocs2.scoreDocs;

                                correctResult3 = indexSearcher.doc(scoreDocs2[0].doc).get("name");

                            }
                            if (realName.length()>3 && realName.substring(0,3).equals("国务院")){ //去掉之前的不必要的字符，以提升准确度
                                String filed = realName.substring(3,realName.length());
                                org.apache.lucene.search.Query query2 = parser.parse(filed);
                                TopDocs topDocs2 = indexSearcher.search(query2, 1);
                                ScoreDoc[] scoreDocs2 = topDocs2.scoreDocs;

                                correctResult3 =indexSearcher.doc(scoreDocs2[0].doc).get("name");
                            }
                            DBObject insertValue = new BasicDBObject(); //将存在问题的法条数据插入mongodb
                            insertValue.put("number", countforlaw);
                            insertValue.put("oldName", realName);
                            insertValue.put("correctName1", correctResult);
                            insertValue.put("correctName2", correctResult2);
                            insertValue.put("correctName3", correctResult3);

                            BasicDBObject newLaw= new BasicDBObject().append("$push",new BasicDBObject().append("references",insertValue));
                            result.update(value,newLaw);
                    }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                countforlaw++;
            }


        }
        System.out.println("All done");
        indexReader.close();
    }
}
