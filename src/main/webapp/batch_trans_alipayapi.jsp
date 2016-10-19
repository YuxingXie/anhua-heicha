<%
/* *
 *功能：批量付款到支付宝账户有密接口接入页
 *版本：3.3
 *日期：2012-08-14
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *************************注意*****************
 *如果您在接口集成过程中遇到问题，可以按照下面的途径来解决
 *1、商户服务中心（https://b.alipay.com/support/helperApply.htm?action=consultationApply），提交申请集成协助，我们会有专业的技术工程师主动联系您协助解决
 *2、商户帮助中心（http://help.alipay.com/support/232511-16307/0-16307.htm?sh=Y&info_type=9）
 *3、支付宝论坛（http://club.alipay.com/read-htm-tid-8681712.html）
 *如果不想使用扩展功能请把扩展功能参数赋空值。
 **********************************************
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>支付宝批量付款到支付宝账户有密接口</title>
	</head>
	<%
		//建立请求
		//https://mapi.alipay.com/gateway.do?pay_date=20161018&batch_no=20161018000000001&_input_charset=utf-8&sign=cdef0e2f9b1f430bb39f8a224f774b28&notify_url=http%3A%2F%2Fwww.hunanyexin.com%2Fbatch_trans_notify_url.jsp&batch_fee=0.01&batch_num=1&partner=2088422939392418&service=batch_trans_notify&account_name=%3F%3F%3F&sign_type=MD5&email=13387481613&detail_data=58051015a7e9bc0e740cf6f11476780652327%5E18974989697%5E%E8%B0%A2%E5%AE%87%E6%98%9F%5E0.01%5Enull
		//https://mapi.alipay.com/gateway.do?pay_date=20161019&batch_no=201610190000000001&_input_charset=utf-8&sign=8f4b39bbbb58cd1be866209e65be04d3&notify_url=http%3A%2F%2Fwww.hunanyexin.com%2Fbatch_trans_notify_url.jsp&batch_fee=0.01&batch_num=1&partner=2088422939392418&service=batch_trans_notify&account_name=%3F%3F%3F&sign_type=MD5&email=13387481613&detail_data=580499f0d832651eec42b1501476850255350%5E18974989697%5E%E8%B0%A2%E5%AE%87%E6%98%9F%5E0.01%5E%E7%94%A8%E6%88%B7%E6%8F%90%E7%8E%B0
		//https://mapi.alipay.com/gateway.do?pay_date=20161019&batch_no=201610190000000002&_input_charset=utf-8&sign=dbca394c5f943cb61a4313fe4af71e1d&notify_url=http%3A%2F%2Fwww.hunanyexin.com%2Fbatch_trans_notify_url.jsp&batch_fee=0.01&batch_num=1&partner=2088422939392418&service=batch_trans_notify&account_name=%3F%3F%3F&sign_type=MD5&email=13387481613&detail_data=580499f0d832651eec42b1501476850612820%5E18974989697%5E%3F%3F%3F%5E0.01%5E%3F%3F%3F%3F
		String sHtmlText = request.getAttribute("sHtmlText").toString();
		out.println(sHtmlText);
	%>
	<body>
	</body>
</html>
