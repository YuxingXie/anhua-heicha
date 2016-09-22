<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<div class="scrollable">
  <h1 class="scrollable-header app-name">湖南业鑫

    <small>欢迎您
    <%--<c:choose>--%>
    <%--<c:when test="${not empty sessionScope.loginUser}">${sessionScope.loginUser.phone} 1</c:when>--%>
      <%--<c:otherwise>{{session.loginUser.phone}} 2</c:otherwise>--%>
    <%--</c:choose>--%>
      <span ng-if="!isEmptyObject(session) && !isEmptyObject(session.loginUser)">
                        {{session.loginUser.phone}}
                     </span>
      !
  </small></h1>
  <div class="scrollable-content">
    <div class="list-group" ui-turn-off='uiSidebarLeft'>
      <a class="list-group-item" href="#/"><i class="fa fa-home"></i> 首页 <i class="fa fa-chevron-right pull-right"></i></a>
      <a class="list-group-item" href="#/register"><i class="fa fa-user"></i> 注册 <i class="fa fa-chevron-right pull-right"></i></a>
      <a class="list-group-item" href="#/login"><i class="fa fa-key"></i> 登录 <i class="fa fa-chevron-right pull-right"></i></a>
      <a class="list-group-item" href="#/invite"><i class="fa fa-send"></i> 发送邀请 <i class="fa fa-chevron-right pull-right"></i></a>
      <a class="list-group-item" href="#/points-record"><i class="fa fa-star"></i> 我的红包 <i class="fa fa-chevron-right pull-right"></i></a>
      <a class="list-group-item" href="#/measure-record"><i class="fa fa-dollar"></i> 我的财富 <i class="fa fa-chevron-right pull-right"></i></a>
      <a class="list-group-item" href="#/shopping"><i class="fa fa-shopping-cart"></i> 去消费 <i class="fa fa-chevron-right pull-right"></i></a>
      <!--<a class="list-group-item" href="#/accordion"><i class="fa fa-user"></i> Accordion <i class="fa fa-chevron-right pull-right"></i></a>-->
      <!--<a class="list-group-item" href="#/overlay"><i class="fa fa-user"></i> Overlay <i class="fa fa-chevron-right pull-right"></i></a>-->

      <!--<a class="list-group-item" href="#/dropdown"><i class="fa fa-user"></i> Dropdown <i class="fa fa-chevron-right pull-right"></i></a>-->
      <a class="list-group-item" href="#/notice"><i class="fa fa-envelope"></i> 我的消息 <span class="label label-danger" ng-if="unreadNoticesCount">{{unreadNoticesCount}}</span> <i class="fa fa-chevron-right pull-right"></i></a>
      <!--<a class="list-group-item" href="#/carousel">Drag 2 <span class="label label-danger">Experimental</span> <i class="fa fa-chevron-right pull-right"></i></a>-->
    </div>
  </div>
</div>
