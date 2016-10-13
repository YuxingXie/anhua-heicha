package com.lingyun.common.util;


import com.lingyun.entity.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

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

    /**
     * 把DBObject转换成bean对象
     *
     * @param dbObject
     * @param bean
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
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
        for (java.lang.reflect.Field classField : clazz.getDeclaredFields()) {
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
        for (java.lang.reflect.Field classField : clazz.getDeclaredFields()) {
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
        String collection= document==null?null:(document.collection()==null?ReflectUtil.firstLowerCase(t.getSimpleName()):document.collection());
        return collection;
    }
    public static void main(String[] args){
        System.out.println(ReflectUtil.firstLowerCase(User.class.getSimpleName()));
    }
}