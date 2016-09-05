package com.lingyun.common.util;

import com.mongodb.*;

import java.util.Date;

public class MongoTest {
    public static void out(Object o) {
        System.err.println(o);
    }

    public static void out() {
        System.err.println();
    }

    public static void main(String[] args) throws Exception {
        //Mongo是与java.sql.Connection同等级别的概念，
        //默认是链接到127.0.0.1:27017/test，
        //见getConnectPoint()的输出结果，其中27017是端口号，test是数据库名
        Mongo mongo = new Mongo();
        out("mongo.getConnectPoint() = " + mongo.getConnectPoint());

        //查看所有的数据库名
        for (String dbName : mongo.getDatabaseNames()) {
            out("dbName = " + dbName);
        }

        //DB类用来表示一个数据库，如果数据库不存在则创建一个
        DB db = mongo.getDB("test");

        //查看所有的Collection名
        for (String collectionName : db.getCollectionNames()) {
            out("collectionName = " + collectionName);
        }

        out();

        //DBCollection类用来存放对象，类似数据库表的概念
        DBCollection coll = db.getCollection("testCollection");

        //插入10个User对象到coll
        //插入DBCollection中的对象必需实现DBObject接口(见User类的注释)
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setName("user " + (10 + i));
            user.setAge(10 + i);
            user.setDate(new Date());

            coll.insert(user);
        }

        findAll(coll); //查找出coll中的所有对象并打印输出

        //查找出第一条记录，在内部会自动转换成User类型
        coll.setObjectClass(User.class);
        User user = (User) coll.findOne();
        out("user.name=" + user.getName());
        out("user.age=" + user.getAge());
        out("user.getDate()=" + user.getDate());
        coll.setObjectClass(BasicDBObject.class); //还原最初的对象类型

        out();

        user.setAge(100);
        coll.save(user); //保存更新
        findAll(coll); //确认第一条记录的"Age"是否变成100了?

        coll.remove(user); //删除第一条记录

        findAll(coll);

        //查找年纪大于等于15小于等于18的记录(15<=age<=18)
        //(查询条件的组合方式很啰嗦，这方面就没sql方便)
        BasicDBObject query = new BasicDBObject();
        //注意这里的"Age"第一个字母要大写，如果把User类替换成UserNoReflection类就用小写，
        //这是因为ReflectionDBObject类的内部实现在截取setter和getter方法的"set"和"get"前缀后
        //没有进行大小写转换，比如"setAge"得到的key值就是"Age"
        query.put("Age", new BasicDBObject("$gte", 15).append("$lte", 18));


        DBCursor cur = coll.find(query);
        while (cur.hasNext()) {
            out(cur.next());
        }

        out();

        //跳过前面3条记录，然后显示总共5条记录
        cur = coll.find().skip(3).limit(5);
        while (cur.hasNext()) {
            out(cur.next());
        }

        coll.drop(); //删除coll这个DBCollection
    }

    public static void findAll(DBCollection coll) {
        DBCursor cur = coll.find();
        while (cur.hasNext()) {
            out(cur.next());
        }
        out();
    }

    //插入DBCollection中的java对象必需实现DBObject接口，
    //ReflectionDBObject实现了DBObject接口，在内部是采用反射来完成的
    public static class User extends ReflectionDBObject {
        private String name;
        private int age;
        private Date date;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }
    }

    //如果不想用反射的方式，又不想手工实现DBObject接口的方法，
    //可以直接继承BasicDBObject，但是需要做额外的工作，
    //就像下面这样:
    public static class UserNoReflection extends BasicDBObject {
        private String name;
        private int age;
        private Date date;

        public void setName(String name) {
            this.name = name;

            super.put("name", name);
        }

        public String getName() {
            return name;
        }

        public void setAge(int age) {
            this.age = age;

            super.put("age", age);
        }

        public int getAge() {
            return age;
        }

        public void setDate(Date date) {
            this.date = date;

            super.put("date", date);
        }

        public Date getDate() {
            return date;
        }

        public Object put(String key, Object v) {
            if (key.equals("name")) name = (String) v;
            else if (key.equals("age")) age = (Integer) v;
            else if (key.equals("date")) date = (Date) v;

            return super.put(key, v);
        }
    }
}
