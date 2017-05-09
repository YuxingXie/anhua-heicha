package com.lingyun.mall.service.impl;

import com.lingyun.common.helper.service.SmsManager;
import com.lingyun.common.util.BusinessException;
import com.lingyun.entity.User;
import com.lingyun.mall.dao.UserDao;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Administrator on 2015/7/6.
 */
@Service
public class EmailService {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private UserDao userDao;
    @Value(value = "${app.email.hostName}")
    private String emailHostName;
    @Value(value = "${app.email.address}")
    private String emailAddress;
    @Value(value = "${app.email.authentication.userName}")
    private String emailUserName;
    @Value(value = "${app.email.authentication.password}")
    private String emailPassword;
    @Value(value = "${app.email.smtpPort}")
    private int smtpPort;
    @Resource
    private SmsManager smsManager;

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getEmailHostName() {
        return emailHostName;
    }

    public void setEmailHostName(String emailHostName) {
        this.emailHostName = emailHostName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailUserName() {
        return emailUserName;
    }

    public void setEmailUserName(String emailUserName) {
        this.emailUserName = emailUserName;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    /**
     * 注册时发送验证码到邮箱
     */

    public User sendValidateCodeToMailAndUpsertUser(String email) throws EmailException {
        User userToUpdate=userDao.findByEmail(email);
        if (userToUpdate!=null && userToUpdate.getActivated()!=null&&userToUpdate.getActivated()) return null;
        ///邮件的内容
        StringBuffer sb = new StringBuffer("<p>您的大坝生态邮箱注册验证码是:");
        int validateCode=(int)(Math.random()*999999-99999);
        sb.append("<font color='red'>").append(validateCode).append("</font></p>");
        sb.append("<p>验证码有效时间为30分钟。</p>");

        //发送邮件
        HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(emailHostName);//设置使用发电子邮件的邮件服务器
        htmlEmail.setAuthentication(emailUserName, emailPassword);
        htmlEmail.setCharset("UTF-8");
        htmlEmail.setSubject("大坝生态账号激活");
//        htmlEmail.setSmtpPort(smtpPort);
        htmlEmail.getBounceAddress();
        htmlEmail.addTo(email);
        htmlEmail.setFrom(emailAddress);
        htmlEmail.setMsg(sb.toString());
        htmlEmail.send();
        //保存注册信息,如果发送邮件抛出异常，不会保存
        if (userToUpdate==null){
            userToUpdate=new User();
        }
        userToUpdate.setRegisterTime(new Date());
        userToUpdate.setValidateCode("" + validateCode);
        userToUpdate.setEmail(email);
        userDao.upsert(userToUpdate);
        return userToUpdate;
    }

    /**
     * 用户修改邮箱时发送验证码
     * @param email
     * @return
     * @throws org.apache.commons.mail.EmailException
     */
    public int sendValidateCodeToMail(String email) throws EmailException {
        ///邮件的内容
        StringBuffer sb = new StringBuffer("<p>您的大坝生态邮箱注册验证码是:");
        int validateCode=(int)(Math.random()*999999-99999);
        sb.append("<font color='red'>").append(validateCode).append("</font></p>");
        sb.append("<p>验证码有效时间为30分钟。</p>");

        //发送邮件
        HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(emailHostName);//设置使用发电子邮件的邮件服务器
        htmlEmail.setAuthentication(emailUserName, emailPassword);
        htmlEmail.setCharset("UTF-8");
        htmlEmail.setSubject("大坝生态账号激活");
//        htmlEmail.setSmtpPort(smtpPort);
        htmlEmail.getBounceAddress();
        htmlEmail.addTo(email);
        htmlEmail.setFrom(emailAddress);
        htmlEmail.setMsg(sb.toString());
        htmlEmail.send();
        //保存注册信息,如果发送邮件抛出异常，不会保存

        return validateCode;
    }
    /**
     * 注册时发送验证码到手机
     */

    public User sendValidateCodeToPhoneAndUpsertUser(String phone) throws Exception {
        User userToUpdate=userDao.findByPhone(phone);
        if (userToUpdate!=null && userToUpdate.getActivated()!=null&&userToUpdate.getActivated()) return null;
        ///邮件的内容
        StringBuffer sb = new StringBuffer("您的手机注册验证码是:");
        int validateCode=Math.abs((int) (Math.random() * 999999 - 99999));
        sb.append(validateCode)
        .append(",验证码有效时间为30分钟。");
        logger.info("用户注册，给手机 "+phone+" 发送验证码："+validateCode);
        String msg=smsManager.send(sb.toString(),phone);
        if (!msg.equals("100")){
            throw new BusinessException("发送短信时出现了问题，问题代码："+msg);
        }
        //保存注册信息
        if (userToUpdate==null){
            userToUpdate=new User();
        }
        userToUpdate.setRegisterTime(new Date());
        userToUpdate.setValidateCode("" + validateCode);
        userToUpdate.setPhone(phone);
        userDao.upsert(userToUpdate);
        return userToUpdate;
    }
    /**
     * 修改手机时发送验证码到手机
     */

    public int sendValidateCodeToPhone(String phone) throws Exception {
        ///邮件的内容
        StringBuffer sb = new StringBuffer("您的手机注册验证码是:");
        int validateCode=Math.abs((int) (Math.random() * 999999 - 99999));

        sb.append(validateCode)
                .append(",验证码有效时间为30分钟。");
        logger.info("用户修改手机号码，给手机 "+phone+" 发送验证码："+validateCode);

        String msg=smsManager.send(sb.toString(),phone);
        if (!msg.equals("100")){
            throw new BusinessException("发送短信时出现了问题，问题代码："+msg);
        }
        //保存注册信息
        return validateCode;
    }
    /**
     * 处理激活
     *
     * @throws java.text.ParseException
     */
    ///传递激活码和email过来
    public void processActivate(String email, String validateCode) throws ServiceException, ParseException {
        //数据访问层，通过email获取用户信息
        User user = userDao.findByEmail(email);
        //验证用户是否存在
        if (user != null) {
            //验证用户激活状态
            if (user.getActivated()==null|| !user.getActivated()) {
                ///没激活
                Date currentTime = new Date();//获取当前时间
                //验证链接是否过期
                currentTime.before(user.getRegisterTime());
                if (currentTime.before(user.getLastActivateTime())) {
                    //验证激活码是否正确
                    if (validateCode.equals(user.getValidateCode())) {
                        //激活成功， //并更新用户的激活状态，为已激活
//                        System.out.println("==sq===" + user.getStatus());
                        user.setActivated(true);//把状态改为激活
//                        System.out.println("==sh===" + user.getStatus());
                        userDao.upsert(user);
                    } else {
                        throw new ServiceException("激活码不正确");
                    }
                } else {
                    throw new ServiceException("激活码已过期！");
                }
            } else {
                throw new ServiceException("邮箱已激活，请登录！");
            }
        } else {
            throw new ServiceException("该邮箱未注册（邮箱地址不存在）！");
        }

    }

    public boolean isEmailUsed(String email) {
        return userDao.isEmailUsed(email);
    }

    public boolean isPhoneUsed(String phone) {
        return  userDao.isPhoneUsed(phone);
    }

    public boolean isNameUsed(String name) {
        return userDao.isNameUsed(name);
    }
    public boolean isNameUsed(String name,String userId) {
        return userDao.isNameUsed(name,userId);
    }

    public boolean isEmailUsed(String email, String userId) {
        return userDao.isEmailUsed(email,userId);
    }

    public boolean isPhoneUsed(String phone, String userId) {
        return userDao.isPhoneUsed(phone,userId);
    }
}
