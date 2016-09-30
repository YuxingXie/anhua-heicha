<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="path" value="<%=request.getContextPath() %>"/>
<c:if test="${path eq '/'}"><c:set var="path" value=""/></c:if>

<!DOCTYPE html>
<html ng-app="AdminLoginApp">
<head>
    <meta charset="utf-8">
    <title>测试模式</title>
    <link rel="shortcut icon" type="image/x-icon" href="${path}/statics/img/logo.png"  media="screen" />
    <link href="${path}/statics/plugins/font-awesome-4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/plugins/bootstrap-3.3.0/dist/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/css/style.css" rel="stylesheet" type="text/css">
    <%--<link href="${path}/statics/assets/css/style-responsive.css" rel="stylesheet" type="text/css">--%>
    <%--<link href="${path}/statics/assets/css/color-bg-color.css" rel="stylesheet" type="text/css">--%>
    <script> path="${path}";</script>
    <script src="${path}/statics/plugins/jquery-2.1.1.min.js" type="text/javascript"></script>
    <script src="${path}/statics/plugins/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
    <%--<script type="text/javascript" src="${path}/statics/assets/plugins/jquery.md5.js"></script>--%>

</head>
<!-- Head END -->

<!-- Body BEGIN -->
<body>
      <div class="container">
          <c:forEach var="line" items="${lines}">
              <div class="row">
                ${line}
              </div>
          </c:forEach>


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