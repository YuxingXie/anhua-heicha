package com.lingyun.common.base;

import com.lingyun.common.util.MongoDbUtil;
import com.lingyun.common.util.ReflectUtil;
import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Administrator on 2015/5/22.
 */
public abstract class BaseMongoDao<E> implements EntityDao<E> {
    private static Logger logger= LogManager.getLogger();
    @Resource
    private MongoTemplate mongoTemplate;
    private Class<E> collectionClass;

    public BaseMongoDao() {
        Class typeCls = getClass();
        Type genType = typeCls.getGenericSuperclass();
        while (true) {
            if (!(genType instanceof ParameterizedType)) {
                typeCls = typeCls.getSuperclass();
                genType = typeCls.getGenericSuperclass();
            } else {
                break;
            }
        }
        this.collectionClass = (Class<E>) ((ParameterizedType) genType).getActualTypeArguments()[0];
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    public long count(DBObject dbObject) {
        return getMongoTemplate().count(new BasicQuery(dbObject),collectionClass);
    }

    public String saveFile(String fileName, byte[] file) {
        GridFS fs = new GridFS(mongoTemplate.getDb());
        GridFSInputFile fsInputFile = fs.createFile(file);
        fsInputFile.put("uploadDate", new Date());
        fsInputFile.put("filename", fileName);
        fsInputFile.save();
        return fsInputFile.get("_id") == null ? null : fsInputFile.get("_id").toString();
    }
    public void deleteFile(String id) {
        GridFS fs = new GridFS(mongoTemplate.getDb());
        fs.remove(new ObjectId(id));
    }
    public String saveFile(String fileName, File file) throws IOException {
        GridFS fs = new GridFS(mongoTemplate.getDb());
        GridFSInputFile fsInputFile = fs.createFile(file);
        fsInputFile.put("uploadDate", new Date());
        fsInputFile.put("filename", fileName);
        fsInputFile.save();
        return fsInputFile.get("_id") == null ? null : fsInputFile.get("_id").toString();
    }
    public static void main(String[] args){
    }


    public GridFSDBFile findFileById(String id) {
        GridFS fs = new GridFS(mongoTemplate.getDb());
        GridFSDBFile file = fs.find(new ObjectId(id));
        return file;
    }

    @Override
    public void insert(E e) {
        //TODO
//        System.out.println("invoke insert method......");
        mongoTemplate.insert(e);
//        DB db = mongoTemplate.getDb();
//        DBCollection collection = db.getCollection(getCollectionName());
//        collection.insert(e)
//        return e;
    }
    @Override
    public void updateByIds(String[] ids,String field,Object value){
        if (ids==null) return;
        updateByIds(Arrays.asList(ids),field,value);
    }
    @Override
    public void updateByIds(List<String> ids,String field,Object value){
        if (ids==null||ids.size()==0) return;
        if (field==null||field.trim().equals("")) return;
        Update update = new Update();
        update.set(field, value);
        DBObject dbObject=new BasicDBObject();
        dbObject.put("id",new BasicDBObject("$in",ids));
        Query query=new BasicQuery(dbObject);
        mongoTemplate.updateMulti(query, update, collectionClass);
    }
    public void insertDBRef(E e) {
        DB db = mongoTemplate.getDb();
        DBCollection collection = db.getCollection(getCollectionName());
        for (java.lang.reflect.Field field:collectionClass.getDeclaredFields()){
            if (field.isAnnotationPresent(DBRef.class)){
                String refDB= field.getAnnotation(DBRef.class).db();
                if (refDB==null){
                   if (field.getType().isAnnotationPresent(Document.class)){
                       if (field.getType().getAnnotation(Document.class).collection()==null ||field.getType().getAnnotation(Document.class).collection().equals("")){
                           refDB=field.getType().getName();
                       }else{
                           refDB=field.getType().getAnnotation(Document.class).collection();
                       }
                   }else{
                       refDB=field.getType().getName();
                   }

                }

            }
        }
        mongoTemplate.insert(e);

    }
    @Override
    public E findById(String id) {
        if (id == null) return null;
        return findById(new ObjectId(id));
    }

    @Override
    public List<E> findEquals(E e) {
        Query query = getEqualsQuery(e);
        if (!collectionExists()) return null;
        if (query == null) return findAll();
        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public List<E> textQuery(String keyWord) {
        Query textQuery = new TextQuery(keyWord);
        return mongoTemplate.findAll(collectionClass);
//        return mongoTemplate.find(textQuery,collectionClass);
    }

    @Override
    public E findOne(DBObject queryCondition){
//        DB db = mongoTemplate.getDb();
       return mongoTemplate.findOne(new BasicQuery(queryCondition),collectionClass);

    }
    @Override
    public Page<E> findPage(DBObject condition,int currentPage,int pageSize){
        return findPage(condition,currentPage,pageSize,null,null);
    }
    @Override
    public Page<E> findPage(DBObject condition,Integer currentPage,Integer pageSize,String sortField,Boolean asc){
        Sort sort=null;
        if (sortField!=null&&asc!=null){
            Sort.Direction direction=asc?Sort.Direction.ASC:Sort.Direction.DESC;
            sort=new Sort(direction,sortField);
        }
        Pageable pageable = new PageRequest(currentPage-1, pageSize);
        Long count = mongoTemplate.count(new BasicQuery(condition),collectionClass);
        Query q=null;
        if(sort==null)
            q=new BasicQuery(condition).limit(pageSize).skip((currentPage - 1) * pageSize);
        else
            q=new BasicQuery(condition).with(sort).limit(pageSize).skip((currentPage - 1) * pageSize);
        List<E> list = mongoTemplate.find(q,collectionClass);

        return new PageImpl<E>(list, pageable, count);
    }
    @Override
    public Page<E> findPage(DBObject dbObject, Integer page) {
        return findPage(dbObject,page,6);
    }

    public List<E> findAll(DBObject condition){
        return mongoTemplate.find(new BasicQuery(condition),collectionClass);
    }


    public List<E> findAll() {
        return mongoTemplate.find(new BasicQuery(new BasicDBList()),collectionClass);
    }
    public List<E> findAll(Integer limit) {
        List<E>list=limit==null||limit.intValue()==0? mongoTemplate.find(new BasicQuery(new BasicDBList()),collectionClass): mongoTemplate.find(new BasicQuery(new BasicDBList()).limit(limit),collectionClass);;
        return list;
    }
    public List<E> findAll(Query query) {
        return mongoTemplate.find(query,collectionClass);
    }
    public void removeAll(List<E> list){
        for(E e:list){
            removeById(MongoDbUtil.getId(e));
        }
    }
    public void insertAll(List<E> list){
        getMongoTemplate().insertAll(list);
    }

    public E findById(ObjectId id) {
        return mongoTemplate.findById(id, collectionClass);
    }

    @Override
    public E findOne(E condition) {
        return mongoTemplate.findOne(getEqualsQuery(condition), collectionClass);
    }

    @Override
    public List<E> findRange(String key, Object min, Object max) {
        if (!MongoDbUtil.isKeyExists(collectionClass, key)) return null;
        Criteria criteria = Criteria.where(key);
        if (min != null) criteria.gte(min);
        if (max != null) criteria.lte(max);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public List<E> findNotEquals(E e) {
        if (e == null) return findAll();
        Query query = getNotEqualsQuery(e);
//        System.out.println(query);
        CommandResult commandResult = mongoTemplate.executeCommand("");
        commandResult.toString();
        return mongoTemplate.find(query, collectionClass);
    }


    public List<E> findKeyIn(String key, Collection collection) {
        Criteria criteria = Criteria.where(key).in(collection);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public List<E> findKeyNotIn(String key, Collection collection) {
        Criteria criteria = Criteria.where(key).nin(collection);
        Query query = Query.query(criteria);
//        System.out.println(query);

        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public List<E> findKeyIsNull(String key) {
        Criteria criteria = Criteria.where(key).is(null);
        Query query = Query.query(criteria);
//        System.out.println(query);
        return mongoTemplate.find(query, collectionClass);
    }

    public List<E> findByRegex(String key, String regex) {
        Criteria criteria = Criteria.where(key).regex(regex);
        Query query = Query.query(criteria);
//        System.out.println(query);
        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public List<E> findInArray(String key, Object[] values) {
        Criteria criteria = Criteria.where(key).all(values);
        Query query = Query.query(criteria);
//        System.out.println(query);
        return mongoTemplate.find(query, collectionClass);
    }

    @Override
    public List<E> findRef(String refKey, Object value) {
//        Criteria criteria=Criteria.where("address.country").is("China").and("address.provence").is("Hunan");
        Criteria criteria = Criteria.where("address").elemMatch(Criteria.where("country").is("China").and("provence").is("Hunan"));
        Query query = Query.query(criteria);
//        System.out.println(query);
        //why result is empty???
        return mongoTemplate.find(query, collectionClass);
    }

    public Page<E> findPage(int pageIndex) {
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        Long count = mongoTemplate.count(query, collectionClass);
        int pageSize = 5;
        Pageable pageable = new PageRequest(pageIndex, pageSize);
        query = query.limit(pageSize).skip((pageIndex - 1) * pageSize);
//        System.out.println(query);
        List<E> list = mongoTemplate.find(query, collectionClass);
        Page<E> page = new PageImpl<E>(list, pageable, count);
        return page;
    }

    public void upsert(E e) {
        String id = MongoDbUtil.getId(e);
        if (null == id || "".equals(id.trim())) {
            //如果主键为空,则新增记录
            mongoTemplate.insert(e);
            return;
        }
        E qe = null;
        try {
            qe = collectionClass.newInstance();
            ReflectUtil.invokeSetter(qe, "id", id);
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        Update update = getUpdateFromEntity(e);
        mongoTemplate.updateFirst(getEqualsQuery(qe), update, collectionClass);

    }

    public CommandResult runCommand(String command) {

        return  mongoTemplate.executeCommand(command);
    }

    private String getCollectionName() {
        Document document = collectionClass.getAnnotation(Document.class);
        if (document == null) return collectionClass.getSimpleName();
        return document.collection();
    }

    private DBCollection getDBCollection() {
        return mongoTemplate.getDb().getCollection(getCollectionName());
    }



    private boolean collectionExists() {
        return mongoTemplate.getDb().collectionExists(getCollectionName());
    }

    private Query getEqualsQuery(E e) {
        if (e == null) return null;
        Criteria criteria = null;
        boolean firstCriteriaAdded = false;
        for (java.lang.reflect.Field field : collectionClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class) && !field.isAnnotationPresent(Id.class))
                continue;
            String fieldName = field.getName();
            Object fieldValue = ReflectUtil.getValue(e, fieldName,field.getType()==boolean.class);
            if (fieldValue == null) continue;
            if (fieldValue.toString().trim().equals("")) continue;
            if (field.isAnnotationPresent(Id.class)) {
                String key = "_id";
                criteria = null;
                criteria = Criteria.where(key).is(fieldValue);
                break;
            } else {
                String key = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
                if (key == null || key.equals("")) key = fieldName;
                if (firstCriteriaAdded == false) {
                    criteria = Criteria.where(key).is(fieldValue);
                    firstCriteriaAdded = true;
                } else {
                    criteria.and(key).is(fieldValue);
                }
            }
        }
        if (criteria == null) return null;
        Query query = Query.query(criteria);
//        System.out.println(query);
        return query;
    }

    private Query getNotEqualsQuery(E e) {
        Criteria criteria = null;
        boolean firstCriteriaAdded = false;
        for (java.lang.reflect.Field field : collectionClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) continue;
            String fieldName = field.getName();

            Object fieldValue = ReflectUtil.getValue(e, fieldName,field.getType()==boolean.class);
            if (fieldValue == null) continue;
            if (fieldValue.toString().trim().equals("")) continue;
            String key = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
            if (key == null || key.equals("")) key = fieldName;
            if (firstCriteriaAdded == false) {
                criteria = Criteria.where(key).ne(fieldValue);
                firstCriteriaAdded = true;
            } else {
                criteria.and(key).ne(fieldValue);
            }
        }
        if (criteria == null) return null;
        Query query = Query.query(criteria);
        logger.trace(query);
        return query;

    }

    private Update getUpdateFromEntity(E e) {
        Update update = new Update();
        String id=MongoDbUtil.getId(e);
        for (java.lang.reflect.Field field : collectionClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Transient.class)||field.isAnnotationPresent(Id.class)) continue;
            String setterMethodName = ReflectUtil.getSetterMethodName(field.getName());
            Class fieldType = field.getType();
            try {
                field.setAccessible(true);
                String fieldName=field.getName();
                Object fieldValue = field.get(e);
                if (fieldValue == null){
//                    update.set(fieldName, null);
                    continue;
                }
                if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
                    org.springframework.data.mongodb.core.mapping.Field docField = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
                    fieldName = docField.value() == null || docField.value().equals("") ? field.getName() : docField.value();
                    update.set(fieldName, fieldValue);

                }else if(field.isAnnotationPresent(DBRef.class)){
                    //such as //[{"$id":"theId1",$ref:"a db"},{"$id":"theId2",$ref:"a db"}]
                    DBRef dbRef=field.getAnnotation(DBRef.class);
                    String db=dbRef.db();
                    if (fieldValue.getClass().isArray()){
                        Object[] objects=(Object[]) fieldValue;
                        BasicDBList dbListObject=new BasicDBList();
                        for (Object object:objects){
                            DBObject dbObject=new BasicDBObject();
                            if (StringUtils.isEmpty(db)){
                                db=MongoDbUtil.getDbName(object);
                            }
                            dbObject.put("$ref",db);
                            String objectId=MongoDbUtil.getId(object);
                            dbObject.put("$id",new ObjectId(objectId));
                            dbListObject.add(dbObject);
                        }
                        update.set(fieldName,dbListObject);
                    }else if (Collection.class.isAssignableFrom(fieldValue.getClass())){
                        Collection collection=(Collection) fieldValue;
                        Iterator iterator=collection.iterator();
                        BasicDBList dbListObject=new BasicDBList();
                        while (iterator.hasNext()){
                            Object valueObject=iterator.next();
                            DBObject dbObject=new BasicDBObject();
                            if (StringUtils.isEmpty(db)){
                                db=MongoDbUtil.getDbName(valueObject);
                            }
                            dbObject.put("$ref",db);
                            String objectId=MongoDbUtil.getId(valueObject);
                            dbObject.put("$id",new ObjectId(objectId));
                            dbListObject.add(dbObject);
                        }
                        update.set(fieldName,dbListObject);
                    }else{//such as //{"$id":"theId1",$ref:"a db"}

                        DBObject dbObject=new BasicDBObject();
                        if (StringUtils.isEmpty(db)){
                            db=MongoDbUtil.getDbName(fieldValue);
                        }
                        dbObject.put("$ref",db);
                        String objectId=MongoDbUtil.getId(fieldValue);
                        dbObject.put("$id",new ObjectId(objectId));
                        update.set(fieldName,dbObject);
                    }
                }else{
                    fieldName = field.getName();
                    update.set(fieldName, fieldValue);
                }

            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
        return update;
    }
    @Override
    public void removeById(String id){
        DBObject dbObject=new BasicDBObject();
        dbObject.put("_id",new ObjectId(id));
        Query query=new BasicQuery(dbObject);
        getMongoTemplate().remove(query,collectionClass);

    }
    @Override
    public E getMax(String field, String fieldQuery, Object fieldQueryValue){
        //db.collectionName.find({},{"field":"field value"}).sort({"_id":-1}).limit(1);
        DBObject dbObject=new BasicDBObject();
        dbObject.put(fieldQuery,fieldQueryValue);
        return mongoTemplate.findOne(new BasicQuery(dbObject).with(new Sort(Sort.Direction.DESC, field)).limit(1), collectionClass);
    }
  }
