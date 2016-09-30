package com.lingyun.mall.dao;

import com.lingyun.common.base.BaseMongoDao;
import com.lingyun.common.code.NotifyTypeCodeEnum;
import com.lingyun.common.directSale.util.UserRelationship;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.common.util.BusinessException;
import com.lingyun.entity.*;
import com.lingyun.support.yexin.DirectSalePairTouchMode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/11.
 */
@Repository
public class UserMeasureDao extends BaseMongoDao<UserMeasure> {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private MongoOperations mongoTemplate;
    @Resource
    private UserDao userDao;
    @Resource
    private DirectSalePairTouchMode directSalePairTouchMode;
    public void sendMeasureToUpperUser(Order order) {
        if (order==null) return;
        User user=order.getUser();
        if (user==null) return;
        if (!user.isDirectSaleMember()){
            DBObject dbObject=new BasicDBObject();
            dbObject.put("user",new DBRef("mallUser",new ObjectId(user.getId())                         ));
            List<Order> orders=ServiceManager.orderService.findAll(dbObject);
            double totalOldOrderPrice=0d;
            for(Order oldOrder:orders){
                totalOldOrderPrice+=oldOrder.getTotalPrice();
                if (totalOldOrderPrice>directSalePairTouchMode.getMembershipLine()){
                    user.setDirectSaleMember(true);
                    //TODO 去掉注释
                    //userDao.upsert(user);
                    break;
                }
            }
        }
        if (!user.isDirectSaleMember()) return;

        List<User> upperUsers=userDao.findLowerOrUpperUsers(user,-1);
        if (upperUsers==null) return;
        if (upperUsers.size()>1) throw new RuntimeException("不能有一个以上推荐人");
        User directUpperUser=upperUsers.get(0);
        logger.info("用户 " + user.getPhone() + "的推荐人是 " + directUpperUser.getPhone());
        List<User> directLowerUsers=userDao.findLowerOrUpperUsers(directUpperUser,1);//找上一级的直接下级，即与用户相同上级的平级用户，包括用户自己
        if (directLowerUsers==null ||directLowerUsers.size()==0) throw new RuntimeException("程序异常，没有找到业务员。");
        if (directLowerUsers.size()>2) throw new RuntimeException("数据异常，对碰模式下找到2个以上业务员。");
        if (directLowerUsers.size()==1){//只有一个业务员，20层上级都拿见点奖
            List<User> anyPointBonusUsers=userDao.findLowerOrUpperUsers(user,-1*directSalePairTouchMode.getAnyPointFloors());
            List<UserMeasure> userMeasures=new ArrayList<UserMeasure>();
            for(User anyPointBonusUser:anyPointBonusUsers){
                UserMeasure userMeasure=new UserMeasure();
                userMeasure.setType(1);
                userMeasure.setUser(anyPointBonusUser);
                userMeasure.setDate(new Date());
                userMeasure.setFromUser(user);
                double bonus=new BigDecimal(directSalePairTouchMode.getMembershipLine()*directSalePairTouchMode.getAnyPointRate()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                userMeasure.setCount(bonus);
                String note="您获得用户 "+user.getPhone()+" 见点奖 "+bonus+" 元。";
                userMeasure.setNote(note);
                userMeasures.add(userMeasure);
            }
            //TODO 去掉注释
            //insertAll(userMeasures);

        }else if (directLowerUsers.size()==2){//2个业务员，拿对碰
            User pairTouchOtherUser=null;
            if (user.getId().equalsIgnoreCase(directLowerUsers.get(0).getId()))
                pairTouchOtherUser=directLowerUsers.get(1);
            if (user.getId().equalsIgnoreCase(directLowerUsers.get(1).getId()))
                pairTouchOtherUser=directLowerUsers.get(0);
            UserMeasure userMeasure=new UserMeasure();
            userMeasure.setType(1);
            userMeasure.setUser(directUpperUser);
            userMeasure.setDate(new Date());
            userMeasure.setFromUser(user);
            double bonus=new BigDecimal(directSalePairTouchMode.getMembershipLine()*directSalePairTouchMode.getAnyPointRate()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            userMeasure.setCount(bonus);
            String note="用户 "+directLowerUsers.get(0).getPhone()+" 和用户 "+directLowerUsers.get(1).getPhone()+"完成对碰，获得对碰奖 "+bonus+" 元。";
            userMeasure.setNote(note);
        }




        System.out.println("user's upper user count is:"+upperUsers==null?0:upperUsers.size());
        Date now=new Date();
        List<UserMeasure> measures=new ArrayList<UserMeasure>();
        for (User upperUser:upperUsers){
            logger.info("---------------------");
            logger.info("first upper user");
            UserRelationship userRelationship=new UserRelationship(user,upperUser);
            double rate= userRelationship.getRate();
            logger.info("    rate is :" + rate);
            if (rate==0d) {
                logger.info("    rate is 0,to be continue");
                continue;
            }
            double count= rate*order.getTotalPrice();
            logger.info("    order total price is zero,to be continue");
            if (count==0d){
               logger.info("    measure count is 0 ,to be continue");
                continue;
            }
            UserMeasure measure=new UserMeasure();
            measure.setNote(userRelationship.getRelativeLevel() * -1 + "级用户 " + user.getShowName() + " 为您提供了 " + BigDecimalUtil.multiply(count * 1) + " 元佣金");
//            logger.info(userRelationship.getRelativeLevel() * -1 + "级用户 " + user.getShowName() + " 为您提供了 " + BigDecimalUtil.multiply(count * 1) + " 元财富");
            measure.setDate(now);
            measure.setCount(BigDecimalUtil.multiply(count * 1));
            measure.setFromUser(user);
            measure.setUser(upperUser);
            measure.setType(1);
            measures.add(measure);
        }
        mongoTemplate.insertAll(measures);
        Notify notify=new Notify();
        notify.setRead(false);
        notify.setToUser(user);
        notify.setContent("您的订单号为 "+order.getId()+" 的订单付款成功！");
        notify.setDate(new Date());
        notify.setNotifyType(NotifyTypeCodeEnum.SYSTEM.toCode());
        notify.setTitle("系统消息");
        ServiceManager.notifyService.insert(notify);
//        System.out.println("insert all:"+measures.size());
    }

    public List<UserMeasure> findByUser(String userId) {
        DBObject dbObject=new BasicDBObject();
        dbObject.put("user",new DBRef("mallUser",new ObjectId(userId)));
        Query query=new BasicQuery(dbObject);
        query.with(new Sort(Sort.Direction.DESC,"date"));
        return findAll(query);
    }
}
