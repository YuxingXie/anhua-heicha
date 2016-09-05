package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.entity.TestAuthors;
import com.lingyun.entity.TestPosts;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TestPostsDao extends BaseMongoDao<TestPosts> {
    private static Logger logger = LogManager.getLogger();
    public void insertDBRef(TestPosts testPosts) {
        List<TestAuthors> authors=testPosts.getAuthors();
        List<DBRef> listDBRef = new ArrayList<DBRef>();
        DBObject testPostsDBObject =new BasicDBObject();
        testPostsDBObject.put("title",testPosts.getTitle());
        for (TestAuthors author:authors){
            //一个DBRef是这样子的：{"$ref" : "testAuthors","$id" : ObjectId("5645a4a86f87bce59c62a7ad")}

            String ns="testAuthors" ;//ns变量值和$ref值相同
            Object id=new ObjectId(author.getId());
            DBRef dbRefAuthor=new DBRef(ns,id);
            listDBRef.add(dbRefAuthor);
        }
        testPostsDBObject.put("authors",listDBRef);
//        WriteResult writeResult=db.getCollection("testPosts").insert(testPostsDBObject);
        getMongoTemplate().insert(testPostsDBObject,"testPosts");
        testPosts.setId(testPostsDBObject.get("_id").toString());
        return;
    }
}
