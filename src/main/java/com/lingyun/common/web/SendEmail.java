package com.lingyun.common.web;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
/**
 * Created by Administrator on 2015/7/6.
 */
public class SendEmail {
    public static final String HOST = "smtp.163.com";
//    public static final String HOST = "smtp.sina.com";
    public static final String PROTOCOL = "smtp";
    public static final int PORT = 25;
    public static final String FROM = "lingyun";//发件人的email
//    public static final String PWD = "dbst123456789";//发件人密码
    public static final String PWD = "upljstdsalbkscyr";//发件人密码


    /**
     * 获取Session
     *
     * @return
     */
    private static Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST);//设置服务器地址
        props.put("mail.store.protocol", PROTOCOL);//设置协议
        props.put("mail.transport.protocol", PROTOCOL);//设置协议
        props.put("mail.smtp.port", PORT);//设置端口
        props.put("mail.smtp.auth", "true");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PWD);
            }

        };
//        Session session = Session.getInstance(props, authenticator);
        Session session = Session.getDefaultInstance(props, authenticator);

        return session;
    }

    public static void send(String toEmail, String content) {
        Session session = getSession();
        try {
//            System.out.println("--send--" + content);
            // Instantiate a message
            Message msg = new MimeMessage(session);

            //Set message attributes
            msg.setFrom(new InternetAddress(FROM));
            InternetAddress[] address = {new InternetAddress(toEmail)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("账号激活邮件");
            msg.setSentDate(new Date());
            msg.setContent(content, "text/html;charset=utf-8");
            //add
            msg.saveChanges();
            //Send the message
            Transport transport = session.getTransport();
            // 打开连接
            transport.connect(HOST,PORT,FROM, PWD);
            // 将message对象传递给transport对象，将邮件发送出去
            transport.sendMessage(msg, msg.getAllRecipients());
            // 关闭连接
            transport.close();
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}