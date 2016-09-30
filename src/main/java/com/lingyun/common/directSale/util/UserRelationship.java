package com.lingyun.common.directSale.util;

import com.lingyun.common.constant.Constant;
import com.lingyun.common.util.StringUtils;
import com.lingyun.entity.User;

/**
 * Created by Administrator on 2016/9/12.
 */
public class UserRelationship {
    private User user;
    private User member;
    /**
     *会员之间的等级关系，上一级会员相对下一级会员是负数
     */
    private int relativeLevel;

    /**
     * 分配比例
     */
    private double rate;
    private UserRelationship (){}
    public UserRelationship(User user,User member){
        this.user=user;
        this.member=member;
        if (user==null) return ;
        if (member==null) return ;
        if (user.getId()==null) return;
        if (member.getId()==null) return;
        String membershipPath=member.getMembershipPath();
        if(membershipPath==null||membershipPath.trim().equals("")){
            membershipPath="/"+member.getId();
        }
        String userPath=user.getMembershipPath();
        if(userPath==null||userPath.trim().equals("")){
            userPath="/"+user.getId();
        }
        String lowerOrHigherId=member.getId();
        String userId=user.getId();
        if(membershipPath.indexOf(userId)<0){
            //上级会员
//            String longString=membershipPath.substring(membershipPath.indexOf("/"+lowerOrHigherId)+lowerOrHigherId.length()+1,membershipPath.indexOf(userId));
            //上级member： /sup
            //下级user：/sup/sdfd/asfds/lower
            String longString=userPath.substring(userPath.indexOf("/"+lowerOrHigherId)+lowerOrHigherId.length()+1,userPath.indexOf(userId));
            String shortString="/";
            int occ= StringUtils.occurrenceNumberInString(longString, shortString);
            relativeLevel= occ*-1;
        }else{
            String longString=membershipPath.substring(membershipPath.indexOf("/"+userId)+userId.length()+1,membershipPath.indexOf(lowerOrHigherId));
            String shortString="/";
            int occ=StringUtils.occurrenceNumberInString(longString,shortString);
            relativeLevel= occ;
        }
        if (relativeLevel==0) return;
        int absRelativeLevel=Math.abs(relativeLevel);
        rate=absRelativeLevel==1? Constant.MEMBERSHIP_UPPER_LEVEL_1_RATE:(absRelativeLevel==2?Constant.MEMBERSHIP_UPPER_LEVEL_2_RATE:(absRelativeLevel==3?Constant.MEMBERSHIP_UPPER_LEVEL_3_RATE:0));
    }

    public User getUser() {
        return user;
    }

    public User getMember() {
        return member;
    }
    /**
     * 获得
     * @return
     */
    public int getRelativeLevel() {
        return relativeLevel;
    }

    public double getRate() {
        return rate;
    }
}
