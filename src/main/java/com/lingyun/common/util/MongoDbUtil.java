package com.lingyun.common.util;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MongoDbUtil {

    /**
     * 把实体bean对象转换成DBObject
     *
     * @param bean
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> DBObject bean2DBObject(T bean) throws IllegalArgumentException,
            IllegalAccessException {
        if (bean == null) {
            return null;
        }
        DBObject dbObject = new BasicDBObject();
        // 获取对象对应类中的所有属性域
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 获取属性名
            String varName = field.getName();
            // 修改访问控制权限
            boolean accessFlag = field.isAccessible();
            if (!accessFlag) {
                field.setAccessible(true);
            }
            Object param = field.get(bean);
            if (param == null) {
                continue;
            } else if (param instanceof Integer) {//判断变量的类型
                int value = ((Integer) param).intValue();
                dbObject.put(varName, value);
            } else if (param instanceof String) {
                String value = (String) param;
                dbObject.put(varName, value);
            } else if (param instanceof Double) {
                double value = ((Double) param).doubleValue();
                dbObject.put(varName, value);
            } else if (param instanceof Float) {
                float value = ((Float) param).floatValue();
                dbObject.put(varName, value);
            } else if (param instanceof Long) {
                long value = ((Long) param).longValue();
                dbObject.put(varName, value);
            } else if (param instanceof Boolean) {
                boolean value = ((Boolean) param).booleanValue();
                dbObject.put(varName, value);
            } else if (param instanceof Date) {
                Date value = (Date) param;
                dbObject.put(varName, value);
            }
            // 恢复访问控制权限
            field.setAccessible(accessFlag);
        }
        return dbObject;
    }
    public static<T> Update getUpdateFromEntity(T e,boolean ignoreNullValue,Class<?> collectionClass) {
        Update update = new Update();
        String id=MongoDbUtil.getId(e);
        for (Field field : collectionClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Transient.class)||field.isAnnotationPresent(Id.class)) continue;
            String setterMethodName = ReflectUtil.getSetterMethodName(field.getName());
            Class fieldType = field.getType();
            try {
                field.setAccessible(true);
                String fieldName=field.getName();
                Object fieldValue = field.get(e);
                if (fieldValue == null){
                    if(!ignoreNullValue){
                        update.set(fieldName, null);
                    }
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
                            if (org.apache.commons.lang.StringUtils.isEmpty(db)){
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
                            if (org.apache.commons.lang.StringUtils.isEmpty(db)){
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
                        if (org.apache.commons.lang.StringUtils.isEmpty(db)){
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
    public static<T> Query getEqualsQuery(T e) {
        Class<?> collectionClass=e.getClass();
        if (e == null) return null;
        Criteria criteria = null;
        boolean firstCriteriaAdded = false;
        for (Field field : collectionClass.getDeclaredFields()) {
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
    /**
     * 把DBObject转换成bean对象
     *
     * @param dbObject
     * @param bean
     * @return
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static <T> T dbObject2Bean(DBObject dbObject, T bean) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        if (bean == null)  return null;
        if (dbObject==null) return null;
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            String dbFieldName = null;
            if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
                dbFieldName = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
            } else if (field.isAnnotationPresent(Id.class)) {
                dbFieldName = "_id";
            } else {
                dbFieldName = field.getName();
            }
            Object object = dbObject.get(dbFieldName);
            if (object != null) {
                BeanUtils.setProperty(bean, field.getName(), object);
            }
        }
        return bean;
    }

    public static <T> boolean isKeyExists(Class<T> clazz, String key) {
        if (clazz == null) return false;
        if (key == null) return false;
        for (Field classField : clazz.getDeclaredFields()) {
            org.springframework.data.mongodb.core.mapping.Field documentField = clazz.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
            if (documentField == null) continue;
            String documentFieldValue = documentField.value();
            if (documentFieldValue.equals(key)) {
                return true;
            }
        }
        return ReflectUtil.isFieldExist(clazz, key);
    }

    public static <T> String getId(T t) {
        Class<?> clazz = t.getClass();
        if (clazz == null) return null;
        for (Field classField : clazz.getDeclaredFields()) {
            if (!classField.isAnnotationPresent(Id.class)) continue;
            Object idValue = ReflectUtil.getValue(t, classField.getName(),classField.getType()==boolean.class);
            return idValue == null ? null : idValue.toString();
        }
        return null;
    }

    public static  <T>  String getDbName(T t) {
        Class<?> clazz = t.getClass();
        if (clazz == null) return null;
        if (!clazz.isAnnotationPresent(Document.class)) return clazz.getSimpleName();
        String  collection=clazz.getAnnotation(Document.class).collection();
        if (!org.apache.commons.lang.StringUtils.isEmpty(collection)) return collection;
        return clazz.getSimpleName();

    }
    public static <T> void clearTransientFields(T t)  {
        Class clz=t.getClass();
        for (Field field:clz.getDeclaredFields()){
            if (field.getType().isPrimitive() ||ReflectUtil.isWrapClass(field.getType()) ||field.getType()==String.class) continue;
            field.setAccessible(true);
            Object fieldValue= null;
            try {
                fieldValue = field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (fieldValue==null) continue;
//            System.out.println("------"+field.getName());
            if (field.isAnnotationPresent(Transient.class)){
//                field.setAccessible(true);
                try {
                    field.set(t,null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
//                String setterMethodName= ReflectUtil.getSetterMethodName(field.getName());
//                Method setter=clz.getDeclaredMethod(setterMethodName,field.getType());
//                setter.invoke(t,null);
//                ReflectUtil.invokeSetter(t,field.getName(),null);
            }else{
                clearTransientFields(fieldValue);
            }
        }
    }
    public static<T> String collectionName(Class<T> t){
        Document document=t.getAnnotation(Document.class);
        String collection= document==null?null:(document.collection()==null?StringUtils.firstLowerCase(t.getSimpleName()):document.collection());
        return collection;
    }
    public static<T>  List<String> getUpdateFromEntity(Class collectionClass) {
        List<String> list = new ArrayList<String>();
        for (Field field : collectionClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Id.class)) continue;
            String setterMethodName = ReflectUtil.getSetterMethodName(field.getName());
            Class fieldType = field.getType();
            field.setAccessible(true);
            String fieldName = field.getName();
            if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
                org.springframework.data.mongodb.core.mapping.Field docField = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
                fieldName = docField.value() == null || docField.value().equals("") ? field.getName() : docField.value();

            }
            list.add(fieldName);
        }
        return list;
    }

}