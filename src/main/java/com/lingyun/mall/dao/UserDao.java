package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.MD5;
import com.lingyun.common.util.StringUtils;
import com.lingyun.common.util.UtilDateTime;
import com.lingyun.entity.*;
import com.mongodb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/22.
 */
@Repository
public class UserDao extends BaseMongoDao<User>  {
    private static Logger logger = LogManager.getLogger();
    //单个插入
    @Resource
    private MongoOperations mongoTemplate;

    /**
     * 获得所有的user
     */

    public List<User> getAllObjects() {
        return mongoTemplate.findAll(User.class);
    }

    /**
     * 保存一个user对象
     */

    public void saveObject(User user) {
        mongoTemplate.insert(user);
    }

    /**
     * 通过id进行查找
     */


    public User getObject(String id) {
        return mongoTemplate.findOne(new Query(Criteria.where(id).is(id)),
                User.class);
    }

    /**
     * 根据id和name进行查找
     */

    public WriteResult updateObject(String id, String name) {
        return mongoTemplate.updateFirst(
                new Query(Criteria.where(id).is(id)),
                Update.update(name, name), User.class);
    }

    /**
     * 根据id删除user
     */

    public void deleteObject(String id) {
        mongoTemplate
                .remove(new Query(Criteria.where(id).is(id)), User.class);
    }

    /**
     * 如果collection不存在则建立
     */

    public void createCollection() {
        if (!mongoTemplate.collectionExists(User.class))
            mongoTemplate.createCollection(User.class);
    }

    /**
     * 如果collection存在则删除之
     */


    public void dropCollection() {
        if (mongoTemplate.collectionExists(User.class)) {
            mongoTemplate.dropCollection(User.class);
        }
    }


    public User findByNameAndPwd(String loginName, String loginPwd) {
        User user=new User();
        user.setName(loginName);
        user.setPassword(loginPwd);//密码123
        return findOne(user);
    }

    private User findByName(String name) {
        return findOne(new BasicDBObject("name",name));
    }
    public User findByEmail(String email) {
        return findOne(new BasicDBObject("email",email));
    }
    public User findByPhone(String phone) {
        return findOne(new BasicDBObject("phone",phone));
    }
    private User findByName(String name,boolean activated) {
        DBObject dbObject=new BasicDBObject("name",name);
        dbObject.put("activated",activated);
        return findOne(dbObject);
    }
    public User findByEmail(String email,boolean activated) {
        DBObject dbObject=new BasicDBObject("email",email);
        if (!activated){
            BasicDBList dbList=new BasicDBList();
            dbList.add(new BasicDBObject("activated",activated));
            dbList.add(new BasicDBObject("activated",new BasicDBObject("$exists",false)));
            dbObject.put("$or",dbList);
        }else {
            dbObject.put("activated",activated);
        }
        return findOne(dbObject);
    }
    public User findByPhone(String phone,boolean activated) {
        DBObject dbObject=new BasicDBObject("phone",phone);
        dbObject.put("activated",activated);
        return findOne(dbObject);
    }
    public boolean isNameUsed(String name) {
        User user=findByName(name,true);
        if (user==null) return false;
        return true;
    }

    /**
     * 用户改昵称时判断昵称是否可用
     * @param name 新昵称
     * @param userId 用户Id
     * @return
     */
    public boolean isNameUsed(String name, String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("_id", new BasicDBObject("$ne",userId));
        dbObject.put("activated", true);
        dbObject.put("name", name);
        List<User> users=mongoTemplate.find(new BasicQuery(dbObject),User.class);
        return users!=null&&users.size()>0;
    }

    /**
     * 用户邮箱称时判断邮箱是否可用
     * @param email
     * @param userId
     * @return
     */
    public boolean isEmailUsed(String email, String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("_id", new BasicDBObject("$ne",userId));
        dbObject.put("activated", true);
        dbObject.put("email", email);
        List<User> users=mongoTemplate.find(new BasicQuery(dbObject),User.class);
        return users!=null&&users.size()>0;
    }
    public boolean isPhoneUsed(String phone, String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("_id", new BasicDBObject("$ne",userId));
        dbObject.put("activated", true);
        dbObject.put("phone", phone);
        List<User> users=mongoTemplate.find(new BasicQuery(dbObject),User.class);
        return users!=null&&users.size()>0;
    }
    public boolean isEmailUsed(String email) {
        User user=findByEmail(email,true);
        if (user==null) return false;
//        if (user.getStatus()!=null && user.getStatus()==0) return false;
        return true;
    }

    public boolean isPhoneUsed(String phone) {
        User user=findByPhone(phone,true);
        if (user==null) return false;
        if (user.getActivated()==null) return false;
        if (!user.getActivated()) return false;
        return true;
    }

    public User findByEmailOrPhone(String name) {
        DBObject queryCondition = new BasicDBObject();
        queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("phone", name));
        values.add(new BasicDBObject("email", name));
        queryCondition.put("$or", values);
        Query query=new BasicQuery(queryCondition);
//        return mongoTemplate.findOne(query,User.class);
//        User user=findOne(queryCondition);
        User user=mongoTemplate.findOne(query,User.class);
        if (user!=null){
            Cart cart=user.getCart();
            if (cart!=null){
                List<ProductSelected> productSelectedList=cart.getProductSelectedList();
                if (productSelectedList!=null){
                    for (ProductSelected productSelected:productSelectedList){
                        ProductSeries productSeries= productSelected.getProductSeries();
                        if (productSeries==null) continue;
//                        productSeries.setProductSeriesPrices(ServiceManager.productSeriesPriceService.findByProductSeriesId(productSeries.getId()));

//                        productSelected.setProductSeries(productSeries);
                        List<String> productPropertyValueIds=productSelected.getProductPropertyValueIds();
                        if (productPropertyValueIds!=null){
                            List<ProductPropertyValue> productPropertyValueList=new ArrayList<ProductPropertyValue>();
                            for (String productPropertyValueId:productPropertyValueIds){
                                ProductPropertyValue productPropertyValue=ServiceManager.productPropertyValueService.findById(productPropertyValueId);
                                productPropertyValueList.add(productPropertyValue);
                            }
                            productSelected.setProductPropertyValueList(productPropertyValueList);
                        }
                    }
                }
            }
        }
        return user;
    }


    public void clearCart(User user) {
        Assert.notNull(user.getId());
        Update update=new Update();
        update.set("cart", null);
        mongoTemplate.updateFirst(new BasicQuery(new BasicDBObject("_id",user.getId())), update, User.class);
    }


    public List<User> findUsersByProductSeriesInCart(ProductSeries productSeries) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("cart.productSelectedList.productSeries",productSeries);
        return getMongoTemplate().find(new BasicQuery(dbObject),User.class);
    }

    public User findByTencentOpenId(String openId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("tencentLoginInfo.openId",openId);
        return getMongoTemplate().findOne(new BasicQuery(dbObject),User.class);
    }
       public User findInviteUserByPhoneAndInviteCode(String phone, String inviteCode) {
        DBObject condition=new BasicDBObject();
        condition.put("phone",phone);
        condition.put("inviteCode",inviteCode);
        AuthorizeInfo authorizeInfo= getMongoTemplate().findOne(new BasicQuery(condition),AuthorizeInfo.class);
        if (authorizeInfo==null) return null;
        return authorizeInfo.getUser();
    }

    public void insertUser(User user) {
        ObjectId id=new ObjectId();
        user.setId(id.toString());
        mongoTemplate.insert(user);
    }

    public List<User> findLowerUsers(User user) {
        if (user==null) return null;
        if (user.getId()==null) return null;
        String userId=user.getId();
        List<User> users = mongoTemplate.find(new Query(new Criteria("membershipPath").regex(".*?" + userId + ".*")), User.class);
        if (users==null) return null;
        List<User> ret = new ArrayList<User>();
        for(User members :users){
            if (user.getId().equalsIgnoreCase(members.getId())){
                continue;
            }
            String membershipPath= members.getMembershipPath();
            if (membershipPath.indexOf(members.getId())<0){
                continue;//没有包含自己id的路径是程序错误导致的，忽略
            }
            String lowerId= members.getId();
            int relativeLevel=getRelativeLevel(userId, lowerId, membershipPath);
            if(relativeLevel<0){
                continue;//上级会员，忽略
            }
            members.setRelativeLevel(relativeLevel);
            ret.add(members);
        }
        return ret;

    }

    /**
     * 获得会员之间的等级关系，上一级会员相对下一级会员是负数
     * @param userId
     * @param lowerOrHigherId
     * @param membershipPath
     * @return
     */
    private int getRelativeLevel(String userId, String lowerOrHigherId, String membershipPath) {

        if(membershipPath.indexOf(lowerOrHigherId)<membershipPath.indexOf(userId)){
            //上级会员
            String longString=membershipPath.substring(membershipPath.indexOf("/"+lowerOrHigherId)+lowerOrHigherId.length()+1,membershipPath.indexOf(userId));
            String shortString="/";
            int occ=StringUtils.occurrenceNumberInString(longString,shortString);
            return occ*-1;
        }else{
            String longString=membershipPath.substring(membershipPath.indexOf("/"+userId)+userId.length()+1,membershipPath.indexOf(lowerOrHigherId));
            String shortString="/";
            int occ=StringUtils.occurrenceNumberInString(longString,shortString);
            return occ;
        }
    }

    public User findByEmailOrPhoneAndPassword(String loginStr, String password) {
        DBObject queryCondition = new BasicDBObject();
        queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("phone", loginStr));
        values.add(new BasicDBObject("email", loginStr));
        queryCondition.put("$or", values);
        queryCondition.put("password", MD5.convert(password));
        Query query=new BasicQuery(queryCondition);
//        return mongoTemplate.findOne(query,User.class);
//        User user=findOne(queryCondition);
        User user=mongoTemplate.findOne(query,User.class);
        if (user!=null){
            Cart cart=user.getCart();
            if (cart!=null){
                List<ProductSelected> productSelectedList=cart.getProductSelectedList();
                if (productSelectedList!=null){
                    for (ProductSelected productSelected:productSelectedList){
                        ProductSeries productSeries= productSelected.getProductSeries();
                        if (productSeries==null) continue;
//                        productSeries.setProductSeriesPrices(ServiceManager.productSeriesPriceService.findByProductSeriesId(productSeries.getId()));

//                        productSelected.setProductSeries(productSeries);
                        List<String> productPropertyValueIds=productSelected.getProductPropertyValueIds();
                        if (productPropertyValueIds!=null){
                            List<ProductPropertyValue> productPropertyValueList=new ArrayList<ProductPropertyValue>();
                            for (String productPropertyValueId:productPropertyValueIds){
                                ProductPropertyValue productPropertyValue=ServiceManager.productPropertyValueService.findById(productPropertyValueId);
                                productPropertyValueList.add(productPropertyValue);
                            }
                            productSelected.setProductPropertyValueList(productPropertyValueList);
                        }
                    }
                }
            }
        }
        return user;
    }

    public List<UserPoints> findUserPointsByUser(String userId) {
        DBObject dbObject=new BasicDBObject();
        DBRef userDBRef=new DBRef("mallUser",userId);
        dbObject.put("user",userDBRef);
        return getMongoTemplate().find(new BasicQuery(dbObject),UserPoints.class);
    }
}
