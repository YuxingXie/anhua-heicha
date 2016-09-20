<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE html>
<html>
<head>
	<title>支付宝手机网站支付订单商品展示</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link  rel="stylesheet" href="/statics/plugins/bootstrap-3.3.0/dist/css/bootstrap.min.css">
</head>
<body class="container">
<header class="am-header">
	<h1>订单产品列表</h1>
</header>
<div class="table-responsive">
		<table id="body" class="table table-hover">
			<tr>
				<td>商户订单号
					：</td>
				<td>
					${order.id}
				</td>
			</tr>
				<c:forEach var="productSelected" items="${order.productSelectedList}">
					<tr>
					<td>${productSelected.productSeries.name}&times;${productSelected.amount}
						：</td>
					<td>
							￥ ${productSelected.productSeries.commonPrice*productSelected.amount}
					</td>
					</tr>
				</c:forEach>
				<tr>
					<td>总计
						：</td>
					<td>
						￥${order.totalPrice}
					</td>
				</tr>



		</table>

	<div class="text-center">

				湖南业鑫股份有限公司版权所有

	</div>
</div>
</body>