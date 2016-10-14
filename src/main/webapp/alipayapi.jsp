<%
/* *
 *功能：即时到账交易接口接入页
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *************************注意*****************
 *如果您在接口集成过程中遇到问题，可以按照下面的途径来解决
 *1、开发文档中心（https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.KvddfJ&treeId=62&articleId=103740&docType=1）
 *2、商户帮助中心（https://cshall.alipay.com/enterprise/help_detail.htm?help_id=473888）
 *3、支持中心（https://support.open.alipay.com/alipay/support/index.htm）
 *如果不想使用扩展功能请把扩展功能参数赋空值。
 **********************************************
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.alipay.config.*"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>支付宝即时到账交易接口</title>
	</head>
	<%
		//建立请求
		String sHtmlText = request.getAttribute("sHtmlText").toString();
		out.println(sHtmlText);
//		System.out.println(sHtmlText);
		/**
		 * sHtmlText likes follow without line break:
		 <form id="alipaysubmit" name="alipaysubmit" action="https://mapi.alipay.com/gateway.do?_input_charset=utf-8" method="get">
		 <input type="hidden" name="sign" value="d88454692dc3e6de44055d89cfcca02f"/>
		 <input type="hidden" name="body" value="??????"/>
		 <input type="hidden" name="_input_charset" value="utf-8"/>
		 <input type="hidden" name="subject" value="test??123"/>
		 <input type="hidden" name="total_fee" value="0.01"/>
		 <input type="hidden" name="sign_type" value="MD5"/>
		 <input type="hidden" name="service" value="create_direct_pay_by_user"/>
		 <input type="hidden" name="notify_url" value="http://www.dabast.com/notify_url.jsp"/>
		 <input type="hidden" name="partner" value="2088221273018992"/>
		 <input type="hidden" name="seller_id" value="2088221273018992"/>
		 <input type="hidden" name="out_trade_no" value="test20160328103653"/>
		 <input type="hidden" name="payment_type" value="1"/>
		 <input type="hidden" name="return_url" value="http://www.dabast.com/alipay_return.jsp"/>
		 <input type="submit" value="确认" style="display:none;">
		 </form>
		 <script>document.forms['alipaysubmit'].submit();</script>
		 */

	%>
	<body>
	</body>
</html>
