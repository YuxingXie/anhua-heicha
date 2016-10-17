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
            管理员登录
        </div>
        <div class="col-xs-2"></div>
    </div>
    <div class="row">
        <div class="col-xs-2"></div>
        <div class="alert alert-info col-xs-8">
            <form action="${path}/admin/login" method="post">
                <fieldset>
                    <div class="row form-group" style="margin-top: 15px;">
                        <div class="col-xs-2 text-right">姓名</div>
                        <div class="col-xs-6" >
                            <input type="text" name="name" placeholder="姓名" class="form-control"/></div>
                    </div>
                    <div class="row form-group" style="margin-top: 15px;">
                        <div class="col-xs-2 text-right">密码</div>
                        <div class="col-xs-6" >
                            <input type="password" name="password" placeholder="密码" class="form-control"/></div>
                    </div>
                    <div class="row" style="margin-top: 15px;">
                        <div class="col-xs-8" ><input type="submit" value="登录" class="btn btn-primary pull-right"/></div>
                    </div>
                </fieldset>
            </form>
        </div>
        <div class="col-xs-2"></div>
    </div>


    </div>
</div>
</body>

