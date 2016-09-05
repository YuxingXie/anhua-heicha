package com.lingyun.common.util;

import com.lingyun.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {
    private static Logger logger= LogManager.getLogger();
    public static <T> boolean methodExists(T t, String methodName) {
        Method[] methods = t.getClass().getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getGetterMethodName(String fieldName,boolean is_boolean) {
        if (!is_boolean)
        return "get" + firstUpperCase(fieldName);
        return "is"+firstUpperCase(fieldName);
    }
    public static String getGetterMethodName(String fieldName) {
        return getGetterMethodName(fieldName,false);
    }
    public static String getSetterMethodName(String fieldName) {
        return "set" + firstUpperCase(fieldName);
    }

    public static <T> Object invokeGetter(T t, String property) {
        try {
            Method getter = t.getClass().getDeclaredMethod(getGetterMethodName(property));
            return getter.invoke(t);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String firstLowerCase(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }

    public static String firstUpperCase(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static <T> boolean isFieldExist(Class<T> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Object getValue(T t, String property,boolean property_is_boolean) {
        Class clazz = t.getClass();
        Method method;
        try {
            method = clazz.getDeclaredMethod(getGetterMethodName(property,property_is_boolean), null);
            return method.invoke(t, null);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static <E> void invokeSetter(E e, String fieldName, Object value) {

        Class clazz = e.getClass();
        try {
            Method setter = clazz.getDeclaredMethod(getSetterMethodName(fieldName), value.getClass());
            setter.invoke(e, new Object[]{value});
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 解析一个类的所有字段，直到所有元素都为基本数据类型及其包装类或字符串为止
     *
     * @param object
     */
    public static <E> void analysisBean(Object object) throws IllegalAccessException {
        //logger.info("-----begin analysis "+object.getClass().getName()+"--------");
        for (Field field : object.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            //logger.info("field name:" + fieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(object);
            Type genericType=field.getGenericType();
            if (field.getType().isPrimitive() ||ReflectUtil.isWrapClass(field.getType()) ||field.getType()==String.class){
                //logger.info("field is a primitive type type");
            }else if (field.getType().isArray()){
                if (fieldValue!=null){
                    logger.info("field is an array,field values are:");

                    Object[] fieldArrayObject=(Object[])fieldValue;
                    for (Object fieldArrayObjectItem:fieldArrayObject){
                        logger.info(fieldArrayObjectItem + ",class is:" + fieldArrayObjectItem.getClass() + ",");
                    }
                    //logger.info("");
                }else{
                    //logger.info("field is an array,field value is null");
                }
            }else if (genericType instanceof ParameterizedType){

                ParameterizedType parameterizedType=(ParameterizedType)genericType;
                //logger.info("field is a parameterized type:"+parameterizedType);
                Type[] actualTypes=parameterizedType.getActualTypeArguments();
                Type rawType=parameterizedType.getRawType();

                if (rawType==List.class){
                    //logger.info("field is a java.util.List");
                }

                if (actualTypes.length>0){
                    Class class0=(Class<?>)actualTypes[0];
                    //logger.info("field parameterized type:"+class0);
                }
                if (fieldValue!=null&&rawType==List.class){
                    List<?> fieldValueList=(ArrayList)fieldValue;
                    for (Object fieldValueListItem:fieldValueList){
                        analysisBean(fieldValueListItem);
                    }
                }

            }else{
                //logger.info("field is a simple class");
            }

            //logger.info("--------------------------------------------------------------");
        }
    }

    public static void main(String[] args) {
//        logger.info(isWrapClass(String.class));//false
//        logger.info(isWrapClass(Integer.class));//true
//        logger.info(isWrapClass(int.class));//false
//        logger.info(int.class==Integer.class);//false
//        logger.info(int.class.isPrimitive());//true
//        logger.info(Integer.class.isPrimitive());//false
        List<User> users = new ArrayList<User>();
        User user1 = new User();
        User user2 = new User();
        user1.setName("John");
        user2.setName("Marry");
        users.add(user1);
        users.add(user2);
        User user3=new User();
        User user4=new User();
        user3.setName("papa");
        user4.setName("mama");
        User[] papamama=new User[]{user3,user4};
        Person person = new Person();
        person.setChildren(users);
        person.setParents(papamama);
        try {
            analysisBean(person);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
