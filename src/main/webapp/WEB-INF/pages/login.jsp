<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="path" value="<%=request.getContextPath() %>"/>
<c:if test="${path eq '/'}"><c:set var="path" value=""/></c:if>

<!DOCTYPE html>
<html ng-app="AdminLoginApp">
<head>
    <meta charset="utf-8">
    <title>管理员登录</title>
    <link rel="shortcut icon" type="image/x-icon" href="${path}/statics/img/logo.png"  media="screen" />
    <link href="${path}/statics/plugins/font-awesome-4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/plugins/bootstrap-3.3.0/dist/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <%--<link href="${path}/statics/assets/css/style.css" rel="stylesheet" type="text/css">--%>
    <%--<link href="${path}/statics/assets/css/style-responsive.css" rel="stylesheet" type="text/css">--%>
    <%--<link href="${path}/statics/assets/css/color-bg-color.css" rel="stylesheet" type="text/css">--%>
    <script> path="${path}";</script>
    <script src="${path}/statics/plugins/jquery-2.1.1.min.js" type="text/javascript"></script>
    <script src="${path}/statics/plugins/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
    <%--<script type="text/javascript" src="${path}/statics/assets/plugins/jquery.md5.js"></script>--%>

</head>
<!-- Head END -->

<!-- Body BEGIN -->
<body ng-controller="AdminLoginController">
      <div class="container margin-top-112">
          <div class="row">
              <form name="loginForm"  ng-submit="submit()">
                  <div class="col-lg-6 col-sm-6 col-xs-6 col-lg-push-2 col-sm-push-2 col-xs-push-2">
                      <div class="row font-size-17-5 bg-light-primary">
                          <div class="col-lg-3 col-lg-push-5"> <b>登 录</b></div>
                      </div>
                      <div class="row padding-top-20 padding-bottom-20 bg-light-primary">
                                  <div class="row padding-top-15 text-right">
                                      <label for="name" class="col-lg-3 control-label">用户名 <span class="require fa fa-user">*</span></label>
                                      <div class="col-lg-5">
                                          <input type="text" class="form-control" id="name" name="name" ng-model="administrator.name" ng-required="true">
                                      </div>
                                      <div class="col-md-4 col-sm-4 col-lg-4">
                                          <span ng-if="message" ng-class="{'fa fa-check  color-green':message.success,'fa fa-warning color-red':!message.success}">
                                              {{$parent.message.message}}</span>
                                      </div>
                                  </div>
                                  <div class="row padding-top-15 text-right">
                                      <label for="password" class="col-lg-3 control-label">密码 <span class="require fa fa-lock">*</span></label>
                                      <div class="col-lg-5">
                                          <input type="password" class="form-control" id="password" name="password" ng-model="administrator.password" ng-required="true">
                                      </div>
                                      <div class="col-md-4 col-sm-4 col-lg-4">
                                          <button type="submit" class="btn btn-primary pull-right margin-right-20" ng-disabled="loginForm.$invalid ||(message &&message.success)">登录</button>
                                      </div>
                                  </div>


                      </div>

                  </div>
              </form>
          </div>

      </div>







    <script type="text/javascript" src="${path}/statics/assets/plugins/angular-1.4.8/angular.js"></script>
    <script src="${path}/statics/assets/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <script>
        var mainApp=angular.module("AdminLoginApp",[]);
        mainApp.controller("AdminLoginController",["$http","$scope",function($http,$scope){
            $scope.submit=function(){
                $scope.administrator.password=$.md5($scope.administrator.password);
                $http.post(path+"/admin/login", $scope.administrator).success(function(data){
                    $scope.message=data;
                    setInterval(function(){
                        window.location.href=path+'/admin/index/index';
                    },500);

                });
            }
        }]);
        $(document).ready(function(){

        });
    </script>
    <!-- END PAGE LEVEL JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>