package com.lingyun.common.sms;


//        import com.slkj.entity.SSmsRecord;

/**
 * 短信群发服务
 *
 */
public class MessageServer {
    /** 用户名常量 */
    public static final String UID = "test";
    /** 用户密码常量 */
    public static final String PWD = "123456";

    /** 用户名 */
    private String uid;
    /** 用户密码 */
    private String pwd;
    /** 群发号码组 */
    private String mobile;
    /** 短信内容 */
    private String content;
    /** 短信最大长度 */
    private Integer length;
    /** 群发短信记录管理 */
//
//    /**
//     * 完全构造函数：进行密码的MD5加密，内容的URL编码
//     *
//     * @param mobile
//     *            手机号码字符串（用","隔开）
//     * @param content
//     *            短信内容(未指定长度，默认长度参数为0)
//     * @param senderId
//     */
//    public MessageServer(String mobile, String content) {
//        this.uid = UID;
//        this.pwd = PasswordProcessing.processMd5(PWD+UID);
//        this.mobile = mobile;
//        this.length = 70;
//        if (content.length() > this.length) {
//            content = content.substring(0, this.length - 1);
//        }
//        this.content = content;
//    }
//
//    /**
//     * 完全构造函数：进行密码的MD5加密，内容的URL编码
//     *
//     * @param uid
//     *            用户名
//     * @param pwd
//     *            用户密码
//     * @param mobile
//     *            手机号码字符串（用","隔开）
//     * @param content
//     *            短信内容(未指定长度，默认长度参数为0)
//     */
//    public MessageServer(String uid, String pwd, String mobile, String content) {
//        this.uid = uid;
//        this.pwd = PasswordProcessing.processMd5(pwd+uid);
//        this.mobile = mobile;
//        this.length = 70;
//        if (content.length() > this.length) {
//            content = content.substring(0, this.length - 1);
//        }
//        this.content = content;
//    }
//
//    /**
//     * 完全构造函数：进行密码的MD5加密，内容的URL编码
//     *
//     * @param uid
//     *            用户名
//     * @param pwd
//     *            用户密码
//     * @param mobile
//     *            手机号码字符串（用","隔开）
//     * @param content
//     *            短信内容
//     * @param length
//     *            短信内容最大长度
//     */
//    public MessageServer(String uid, String pwd, String mobile, String content,
//                         int length) {
//        this.uid = uid;
//        this.pwd = PasswordProcessing.processMd5(pwd+uid);
//        this.mobile = mobile;
//        this.length = length < 76 ? 76 : length;
//        if (content.length() > this.length) {
//            content = content.substring(0, this.length - 1);
//        }
//        this.content = content;
//    }
//
//    /**
//     * 发送信息的接口方法
//     *
//     * @param parentIds
//     * @param senderId
//     *
//     * @return 发送信息返回状态结果
//     */
//    public SSmsRecord send(Long senderId, String parentIds) {
//        return sendMessage(senderId, parentIds);
//    }
//
//    /**
//     * 使用http协议发送信息
//     *
//     * @return 发送信息返回状态结果
//     */
//    @SuppressWarnings("finally")
//    private SSmsRecord sendMessage(Long senderId, String parentIds) {
//        Log logger = LogFactory.getLog(PasswordProcessing.class);
//        String result = "";
//        // 创建StringBuffer对象用来操作字符串
//        StringBuffer sb = new StringBuffer("http://api.sms.cn/mt/?");
//        // 向StringBuffer追加用户名
//        sb.append("uid=").append(this.getUid());
//        // 向StringBuffer追加密码（密码采用MD5 32位 小写）
//        sb.append("&pwd=").append(this.getPwd());
//        // 向StringBuffer追加手机号码
//        sb.append("&mobile=").append(this.getMobile());
//        // 向StringBuffer追加消息内容转URL标准码
//        sb.append("&content=").append(URLEncoder.encode(this.getContent()));
//        sb.append("&encode=utf8");
//        System.out.println(sb.toString());
//        try {
//            // 创建url对象
//            URL url = new URL(sb.toString());
//            // 打开url连接
//            HttpURLConnection connection = (HttpURLConnection) url
//                    .openConnection();
//
//            // 设置url请求方式 ‘get’ 或者 ‘post’
//            connection.setRequestMethod("POST");
//
//            // 发送
//            BufferedReader in = new BufferedReader(new InputStreamReader(url
//                    .openStream()));
//
//            // 返回发送结果
//            result = in.readLine().toString();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            logger.info("编码转换异常");
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//            logger.info("协议使用异常");
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.info("返回结果异常");
//        } finally {
//            SSmsRecord smsRecord = new SSmsRecord();
//            smsRecord.setSenderId(senderId);
//            smsRecord.setUserName(this.getUid());
//            smsRecord.setSendTime(new Date());
//            smsRecord.setSendContent(this.getContent());
//            smsRecord.setMobiles(this.getMobile());
//            smsRecord.setReceiverCount(this.getMobile().split(",").length
//                    * (this.getContent().length() / 70 + 1));
//            smsRecord.setReceiverIds(parentIds);
//            smsRecord.setResponseMessage(getResponse(result));
//            return smsRecord;
//        }
//    }
//
//    /**
//     * 将返回状态编码转化为描述结果
//     *
//     * @param logger
//     *            打印信息
//     * @param result
//     *            状态编码
//     * @return 描述结果
//     */
//    private String getResponse(String result) {
//        Log logger = LogFactory.getLog(PasswordProcessing.class);
//        if (result.equals("100")) {
//            logger.info("发送成功");
//            return "发送成功";
//        }
//        if (result.equals("101")) {
//            logger.info("验证失败");
//            return "验证失败";
//        }
//        if (result.equals("102")) {
//            logger.info("短信不足");
//            return "短信不足";
//        }
//        if (result.equals("103")) {
//            logger.info("操作失败");
//            return "操作失败";
//        }
//        if (result.equals("104")) {
//            logger.info("非法字符");
//            return "非法字符";
//        }
//        if (result.equals("105")) {
//            logger.info("内容过多");
//            return "内容过多";
//        }
//        if (result.equals("106")) {
//            logger.info("号码过多");
//            return "号码过多";
//        }
//        if (result.equals("107")) {
//            logger.info("频率过快");
//            return "频率过快";
//        }
//        if (result.equals("108")) {
//            logger.info("号码内容空");
//            return "号码内容空";
//        }
//        if (result.equals("109")) {
//            logger.info("账号冻结");
//            return "账号冻结";
//        }
//        if (result.equals("110")) {
//            logger.info("禁止频繁单条发送");
//            return "禁止频繁单条发送";
//        }
//        if (result.equals("111")) {
//            logger.info("系统暂定发送");
//            return "系统暂定发送";
//        }
//        if (result.equals("112")) {
//            logger.info("号码不正确");
//            return "号码不正确";
//        }
//        if (result.equals("120")) {
//            logger.info("系统升级");
//            return "系统升级";
//        }
//        return result;
//    }
//
//    /**
//     * 查询默认帐户剩余短信数量
//     *
//     * @return
//     */
//    public static String queryRemainingCount() {
//        return queryRemainingCount(UID, PWD);
//    }
//
//    /**
//     * 查询指定帐户剩余短信数量
//     *
//     * @param uid
//     *            用户名
//     * @param password
//     *            用户密码
//     * @return
//     */
//    @SuppressWarnings("finally")
//    public static String queryRemainingCount(String uid, String password) {
//        Log logger = LogFactory.getLog(PasswordProcessing.class);
//        String result = "";
//        // 创建StringBuffer对象用来操作字符串
//        StringBuffer sb = new StringBuffer("http://api.sms.cn/mm/?");
//        // 向StringBuffer追加用户名
//        sb.append("uid=").append(uid);
//        // 向StringBuffer追加密码（密码采用MD5 32位 小写）
//        sb.append("&pwd=").append(PasswordProcessing.processMd5(password+uid));
//        try {
//            // 创建url对象
//            URL url = new URL(sb.toString());
//            // 打开url连接
//            HttpURLConnection connection = (HttpURLConnection) url
//                    .openConnection();
//
//            // 设置url请求方式 ‘get’ 或者 ‘post’
//            connection.setRequestMethod("POST");
//
//            // 发送
//            BufferedReader in = new BufferedReader(new InputStreamReader(url
//                    .openStream()));
//
//            // 返回发送结果
//            result = in.readLine().toString();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            logger.info("编码转换异常");
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//            logger.info("协议使用异常");
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.info("返回结果异常");
//        } finally {
//            if (result.startsWith("100")) {
//                logger.info(result);
//                logger.info("查询短信剩余数量成功，共" + result.substring(5));
//            } else {
//                logger.info("查询短信验证失败");
//            }
//            return result;
//        }
//    }
//
//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }
//
//    public String getPwd() {
//        return pwd;
//    }
//
//    public void setPwd(String pwd) {
//        this.pwd = pwd;
//    }
//
//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public Integer getLength() {
//        return length;
//    }
//
//    public void setLength(Integer length) {
//        this.length = length;
//    }
}
