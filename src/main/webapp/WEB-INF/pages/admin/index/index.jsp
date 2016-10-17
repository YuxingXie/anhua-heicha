<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="/functions" %>
<c:set var="path" value="<%=request.getContextPath() %>"/>
<c:if test="${path eq '/'}"><c:set var="path" value=""/></c:if>
<html>
<head>
    <title>管理员登录</title>
    <link href="${path}/statics/plugins/font-awesome-4.5.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/plugins/bootstrap-3.3.0/dist/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/css/style.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/css/admin.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/css/color.css" rel="stylesheet" type="text/css">
    <script src="${path}/statics/plugins/jquery-2.1.1.min.js" type="text/javascript"></script>
    <script src="${path}/statics/plugins/bootstrap-3.3.0/dist/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${path}/statics/plugins/angular/1.4.8/angular.min.js"></script>
    <script src="${path}/statics/plugins/angular/1.4.8/angular-route.min.js"></script>
    <script src="${path}/statics/pages/admin/admin.js"></script>
</head>
<body class="bg-info" ng-app="AdminApp"  >
    <div class="container bg-light-grey" ng-controller="AdminController">
    <div class="row margin-top-40 margin-bottom-40 alert alert-info text-center">后台管理系统</div>
    <div class="row margin-bottom-10" style="font-size:large">
        <div class="col-xs-3 btn-group btn-group-xs margin-left-20">
            <a class="btn btn-primary">
                <i class="fa fa-user">${sessionScope.administrator.name}</i>
            </a>
            <a class="btn btn-primary"  href="${path}/admin/logout">
                <i class="fa fa-sign-out">退出</i>
            </a>
        </div>
    </div>
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-3">
                <ul id="main-nav" class="main-nav nav nav-tabs nav-stacked">
                    <li>
                        <div class="nav-header collapsed">
                            <i class="fa fa-user"></i>
                            用户管理
                            <span class="pull-right glyphicon glyphicon-chevron-toggle"></span>
                        </div>
                        <ul class="nav nav-list secondmenu collapse" style="height: 0px;">
                            <%--<li><a href="#"><i class="fa fa-users fa-fw"></i>&nbsp;用户列表</a></li>--%>
                            <li><a ng-href="#/carousel"><i class="fa fa-reply fa-fw"></i>&nbsp;用户业绩</a></li>
                            <li><a ng-href="#/aaa"><i class="fa fa-envelope fa-fw"></i>&nbsp;用户拓扑图</a></li>
                        </ul>
                    </li>

                    <%--<li>--%>
                        <%--<a href="#systemSetting" class="nav-header collapsed" data-toggle="collapse">--%>
                            <%--<i class="fa fa-user"></i>--%>
                            <%--aaaaaaaa--%>
                            <%--<span class="pull-right glyphicon glyphicon-chevron-toggle"></span>--%>
                        <%--</a>--%>
                        <%--<ul id="systemSetting" class="nav nav-list secondmenu collapse" style="height: 0px;">--%>
                            <%--<li><a href="#"><i class="fa fa-users fa-fw"></i>&nbsp;用户列表</a></li>--%>
                            <%--<li><a ng-href="#/carousel"><i class="fa fa-reply fa-fw"></i>&nbsp;用户业绩</a></li>--%>
                            <%--<li><a ng-href="#/aaa"><i class="fa fa-envelope fa-fw"></i>&nbsp;用户拓扑图</a></li>--%>
                        <%--</ul>--%>
                    <%--</li>--%>
                </ul>
            </div>
            <div ng-view class="col-xs-9"></div>
        </div>
    </div>
</div>

</body>

