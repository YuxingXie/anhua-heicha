<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%
response.addHeader("Access-Control-Allow-Origin","*");
response.addHeader("Access-Control-Allow-Credentials", "true");
//response.addHeader("Access-Control-Allow-Methods","POST");
//response.addHeader("Access-Control-Allow-Methods","GET");
//response.setHeader("Access-Control-Max-Age","600");
%>
<div ui-content-for="title" >
  <span>支付宝支付返回</span>
</div>

<div class="scrollable">
  <div class="scrollable-content section">

<%
  //商户订单号

//  String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
  String out_trade_no = new String("out_trade_no".getBytes("ISO-8859-1"),"UTF-8");

  //支付宝交易号

//  String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
  String trade_no = new String("trade_no".getBytes("ISO-8859-1"),"UTF-8");

  //交易状态
//  String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
  String trade_status = new String("trade_status".getBytes("ISO-8859-1"),"UTF-8");
%>
    商户订单号:<%=out_trade_no%><br/>
    支付宝交易号:<%=trade_no%><br/>
    交易状态:<%=trade_status%><br/>
  </div>
</div>