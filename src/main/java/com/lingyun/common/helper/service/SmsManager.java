package com.lingyun.common.helper.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/1/9.
 */
@Component
public class SmsManager {
    private static Logger logger = LogManager.getLogger();
    @Value(value = "${app.sms.addr}")
    private String addr;
    //    private static final String addr = "\"http://api.sms.cn/mtutf8";
    @Value(value = "${app.sms.userId}")
    private String userId;

	/*
	 * 如uid是：test，登录密码是：123123
	 * 加密后：则加密串为  md5(123123test)=b9887c5ebb23ebb294acab183ecf0769
	 *
	 * 可用在线生成地址：http://www.sms.cn/password
	 */
    @Value(value = "${app.sms.pwd}")
    private String pwd ;
    //5dcd15557f05314072ad9f6e94b408d7
    @Value(value = "${app.sms.encode}")
    private String encode;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String send(String msgContent, String mobile) throws Exception {
        String sign="【大坝生态】";
        msgContent+=sign;
        // 组建请求
        String straddr = addr + "?uid=" + userId + "&pwd=" + pwd + "&mobile="
                + mobile + "&encode=" + encode + "&content="
                + URLEncoder.encode(msgContent, "UTF-8");

        StringBuffer sb = new StringBuffer(straddr);
//        System.out.println("URL:" + sb);

        // 发送请求
        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        // 返回结果
        String inputline = in.readLine();
//        System.out.println("Response:" + inputline);
        String msg=inputline.substring(inputline.indexOf("stat=")+5,inputline.indexOf("stat=")+8);
        logger.info("短信发送结果："+getResponse(msg));
        return msg;
    }
    /**
     * 将返回状态编码转化为描述结果
     *
     *            打印信息
     * @param result
     *            状态编码
     * @return 描述结果
     */
    private static String getResponse(String result) {
        if (result.equals("100")) {
            return "发送成功";
        }
        if (result.equals("101")) {
            return "验证失败";
        }
        if (result.equals("102")) {
            return "短信不足";
        }
        if (result.equals("103")) {
            return "操作失败";
        }
        if (result.equals("104")) {
            return "非法字符";
        }
        if (result.equals("105")) {
            return "内容过多";
        }
        if (result.equals("106")) {
            return "号码过多";
        }
        if (result.equals("107")) {
            return "频率过快";
        }
        if (result.equals("108")) {
            return "号码内容空";
        }
        if (result.equals("109")) {
            return "账号冻结";
        }
        if (result.equals("110")) {
            return "禁止频繁单条发送";
        }
        if (result.equals("111")) {
            return "系统暂定发送";
        }
        if (result.equals("112")) {
            return "号码不正确";
        }
        if (result.equals("120")) {
            return "系统升级";
        }
        return result;
    }


    public static void main(String[] args) {
        System.out.println(Math.abs(-100001));

    }
}
