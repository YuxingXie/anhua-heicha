package com.lingyun.entity;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

//db.mallUser.update({"phone":"18888888888"},{"$set":{"email":"haha12345678987456325565225525@qq.com"}},false,true)
//db.mallUser.update({"phone":"18888888888"},{"$set":{"name":"买光你茶叶"}},false,true)
@Document(collection = "mallUser")
public class User {
    @Id private String id;
    @Field(value = "name")
    @Length(min=2,max=20)
    private String name;
    @Field
    private String realName;
    @Field(value = "sex")
    private String sex;
    @Field(value = "height")
    private Integer height;
    @Length(min=6)
    @Field("password")
    private String password;
    @Field("email")
    @Email
    @Indexed
    private String email;
    @Field(value = "userCategory")
    private String userCategory;//1注册用户 2,。经销商
//    @DBRef private Set<Address> address;
    @Field("registerTime")
    private Date registerTime;

    @Field("validateCode")
    private String validateCode;
    @Field("lastActivateTime")
    private Date lastActivateTime;
    @Field("phone")
    private String phone;
    private String loginStatus;
    @Field(value = "cart")
//    @DBRef
    private Cart cart;
    @Field(value = "addresses")
    private String[] addresses;
    @Field private Boolean disabled;//禁用
    @Field(value = "idCardNo")
    private String idCardNo;
    @Field
    private String registerInviteCode;
    private Boolean activated;//激活
    /**
     *     在九级会员系统中的路径，以id作为路径的一级，可以用文件夹路径类比，文件夹名就是用户的id
     *     每个下一级会员的membershipPath都会在上一级的membershipPath基础上加上自己的id连接，用"/"分隔
     */
    @Field
    private String membershipPath;
    //九级会员系统中的上一级
    @DBRef
    private AuthorizeInfo authorizeInfo;//
    @Transient
    private List<UserPoints> userPointsList;
    private TencentLoginInfo tencentLoginInfo;
    @Transient
    private List<Account> userAccounts;
    @Transient
    private List<Interest> interests;
    @Transient
    private int relativeLevel;
    @Transient
    private List<MembershipInvite> membershipInviteList;
    public String[] getAddresses() {
        return addresses;
    }

    public String getMembershipPath() {
        return membershipPath;
    }

    public void setMembershipPath(String membershipPath) {
        this.membershipPath = membershipPath;
    }

    public AuthorizeInfo getAuthorizeInfo() {
        return authorizeInfo;
    }

    public void setAuthorizeInfo(AuthorizeInfo authorizeInfo) {
        this.authorizeInfo = authorizeInfo;
    }

    public String getRegisterInviteCode() {
        return registerInviteCode;
    }

    public void setRegisterInviteCode(String registerInviteCode) {
        this.registerInviteCode = registerInviteCode;
    }

    public List<UserPoints> getUserPointsList() {
        return userPointsList;
    }

    public void setUserPointsList(List<UserPoints> userPointsList) {
        this.userPointsList = userPointsList;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public int getRelativeLevel() {
        return relativeLevel;
    }

    public void setRelativeLevel(int relativeLevel) {
        this.relativeLevel = relativeLevel;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public String getValidateCode() {
        return validateCode;
    }

    public Date getLastActivateTime() {
        return lastActivateTime;
    }

    public void setLastActivateTime(Date lastActivateTime) {
        this.lastActivateTime = lastActivateTime;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public List<Account> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<Account> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public String getUserCategory() {
        return userCategory;
    }

    public void setUserCategory(String userCategory) {
        this.userCategory = userCategory;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    private PersonalRealMessage realMessage;

    public PersonalRealMessage getRealMessage() {
        return realMessage;
    }

    public void setRealMessage(PersonalRealMessage realMessage) {
        this.realMessage = realMessage;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    public TencentLoginInfo getTencentLoginInfo() {
        return tencentLoginInfo;
    }

    public void setTencentLoginInfo(TencentLoginInfo tencentLoginInfo) {
        this.tencentLoginInfo = tencentLoginInfo;
    }

    /**
     * 这些和登录相关，不保存
     */

    @Transient
    private Boolean remember;
    @NotNull
    @Transient
    private String rePassword;
    @Transient
    private String loginStr;
    @Transient
    private Boolean mergeCart;

    public Boolean getMergeCart() {
        return mergeCart;
    }

    public void setMergeCart(Boolean mergeCart) {
        this.mergeCart = mergeCart;
    }

    public Boolean getRemember() {
        return remember;
    }

    public void setRemember(Boolean remember) {
        this.remember = remember;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getLoginStr() {
        return loginStr;
    }

    public void setLoginStr(String loginStr) {
        this.loginStr = loginStr;
    }

    public List<MembershipInvite> getMembershipInviteList() {
        return membershipInviteList;
    }

    public void setMembershipInviteList(List<MembershipInvite> membershipInviteList) {
        this.membershipInviteList = membershipInviteList;
    }
}
