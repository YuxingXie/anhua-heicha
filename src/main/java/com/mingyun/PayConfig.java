package com.mingyun;

/**
 * Created by Administrator on 2017/5/17.
 */
public class PayConfig {
//    参数名	参数	可空	加入签名	说明
//    商户ID	uid	     N	       Y	   商户id，由铭云分配
//    银行类型	type	N	Y	支付101
//    金额	m	N	Y	单位元（人民币），2位小数，最小支付金额为1
//    商户订单号	orderid	N	Y	商户系统订单号，该订单号将作为铭云接口的返回数据。该值需在商户系统内唯一，铭云系统暂时不检查该值是否唯一
//    下行异步通知地址	callbackurl	N	Y	下行异步通知过程的返回地址，需要以http://开头且没有任何参数
//    下行同步通知地址（成功）	gotrue	Y	N	下行同步通知过程的返回地址(在支付完成后铭云接口将会跳转到的商户系统连接地址)。
//    注：若提交值无该参数，或者该参数值为空，则在支付完成后，用户将停留在铭云接口系统提示支付成功的页面。
//    下行同步通知地址（失败）	gofalse	y	N	下行同步通知过程的返回地址(在支付失败后铭云接口将会跳转到的商户系统连接地址)。
//    编码	charset	y	N	编码 （建议UTF-8）
//    备注消息	token	Y	N	备注信息，下行中会原样返回。若该值包含中文，请注意编码
//    MD5签名	sign	N	-	32位小写MD5签名值

    public static final String uid="";
    public static final String type="101";
    public static final String callbackurl="http://www.hunanyexin.com/mingyun_pay/notify";
    public static final String gotrue="http://www.hunanyexin.com/mingyun_pay/notify";
    public static final String gofalse="";
    public static final String charset="UTF-8";
    public static final String sign="MD5";
//    public static final String token="gffddfgf-8";


}
