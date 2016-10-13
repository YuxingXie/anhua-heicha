<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<div class="scrollable">
    <div class="scrollable-content">

                <div class="list-group" ui-toggle="uiSidebarRight">
                     <span ng-if="isEmptyObject(session) || isEmptyObject(session.loginUser)">
                         <span class="label label-success">您尚未登录，点击<a ng-href="/vip#/login">登录</a></span>
                     </span>
                    <span ng-if="!isEmptyObject(session) && !isEmptyObject(session.loginUser) && !isEmptyObject(lowerUsers)">
                        <a class="list-group-item media" href="" ng-repeat="user in lowerUsers">
                          <div class="pull-left">
                              <i class="fa fa-user chat-user-avatar"></i>
                          </div>
                          <div class="media-body">
                            <h5 class="media-heading">{{user.name}} {{user.phone}}</h5>
                            <small>
                              <span class="label label-success">{{user.relativeLevel}}级</span>
                              <%--<span class="label label-success">{{user.marketString}}</span>--%>
                              <span class="label label-success">{{user.directSaleMember?"正式会员":"临时会员"}}</span>
                          </small>
                      </div>
                  </a>
              </span>

            <span ng-if="(!isEmptyObject(session) &&!isEmptyObject(session.loginUser))&&isEmptyObject(lowerUsers)" class="label label-success pull-left">
                您还没有业务员，请加油哦！
                <br>
                <br>
                点击<a ng-href="/vip#/invite">这里</a>邀请好友加入。</span>
            </span>
            </div>

    </div>
</div>
