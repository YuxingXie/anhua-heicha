<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="/functions" %>
<c:set var="path" value="<%=request.getContextPath() %>"/>
<c:if test="${path eq '/'}"><c:set var="path" value=""/></c:if>
<html>
<head>
    <title>管理员登录</title>
    <link href="${path}/statics/plugins/bootstrap-3.3.0/dist/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/css/style.css" rel="stylesheet" type="text/css">
    <script> path="${path}";</script>
</head>
<body>
<div class="container">
    <div class="row margin-top-40">
        <div class="col-xs-2"></div>
        <div class="alert alert-info col-xs-8">
            ${message.message}
        </div>
        <div class="col-xs-2"></div>
    </div>



    </div>
</div>
</body>

