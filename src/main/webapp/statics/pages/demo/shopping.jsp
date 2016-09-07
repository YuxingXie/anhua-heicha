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
  <span>购物</span>
</div>
<div ng-if="!isUserLogin()" class="scrollable">
  <div class="scrollable-content section">请先登录!</div>
</div>
<div class="scrollable" ng-if="isUserLogin()">
  <div class="scrollable-content" ng-init="getFriendshipMallShoppingData()">
    <div class='section'>
      <ui-state id='activeTab' default='1'></ui-state>

      <div class="btn-group justified nav-tabs">
        <a ui-set="{'activeTab': 1}"
            ui-class="{'active': activeTab == 1}" class="btn btn-default">购买安化黑茶</a>

        <a ui-set="{'activeTab': 2}"
            ui-class="{'active': activeTab == 2}" class="btn btn-default">去商城消费</a>

       
      </div>
      <div ui-if="activeTab == 1">
        <h3 class="page-header">购买安化黑茶</h3>
        <p>购买安化黑茶，赢取积分奖励。</p>

        <a class="btn btn-primary btn-block" ng-href="/#/buy"><i class="fa fa-credit-card"></i> 去购买</a>
      </div>

      <div ui-if="activeTab == 2">
        <h3 class="page-header">去商城消费</h3>
        <p>您可以将指定数量的积分换算为我们的合作商城的虚拟货币，并在其中消费。</p>
        <form name="aForm" novalidate role="form">
          <div class="input-group" style="margin-bottom: 20px;margin-top: 20px">可用积分：{{totalMembershipPointCount}}</div>
          <div class="row" style="margin-bottom: 20px;margin-top: 20px">
            <div class="col-xs-4">
              <select ng-model="friendshipExchange.mall" name="mall" class="form-control" required="required"
                    ng-options="mall as mall.name for mall in malls" ng-change="initExchangeMessage()">
              <option value="">---选择商城---</option>
            </select>
            </div>
            <div class="col-xs-4">
              <img ng-src="/{{friendshipExchange.mall.ico}}" ng-if="friendshipExchange.mall" class="img-ico-sm img-rounded"/>

            </div>
          </div>
          <div>

            <label class="control-label">兑换积分
              <span class="text-danger"ng-if="friendshipExchange && friendshipExchange.mall && !friendshipExchange.mall.exchangeUrl">该商城暂不支持积分兑换</span>
              <span ng-class="{'fa':true,'fa-smile-o text-success':exchangeMessage&&exchangeMessage.success,'fa-warning text-danger':exchangeMessage&&!exchangeMessage.success}"ng-if="exchangeMessage">{{exchangeMessage.message}}</span>
            </label>
            <div class="input-group">
              <input type="number" class="form-control" ng-min="1" ng-model="friendshipExchange.pointCount" name="pointCount" ng-max="totalMembershipPointCount" required="true" placeholder="兑换积分数" ng-change="initExchangeMessage()"/>
              <span class="input-group-addon btn btn-danger" ng-disabled="aForm.pointCount.$invalid ||aForm.mall.$invalid ||(friendshipExchange && friendshipExchange.mall && !friendshipExchange.mall.exchangeUrl)"
                    ng-click="exchangePoints(friendshipExchange)">兑换</span>
            </div>
          </div>
        </form>
        <div class="margin-top-10">
          <div>
            <label class="control-label ">如果您已经兑换过积分，可以直接去商城消费</label>
          </div>
            <a ng-href="{{friendshipExchange.mall.url}}" class="btn btn-primary"
               target="_blank" ng-disabled="!friendshipExchange.mall.url">直接去消费</a>
          <!--<img ng-src="/{{friendshipExchange.mall.ico}}" class="img-ico-sm" ng-if="friendshipExchange.mall"/>-->

        </div>
      </div>
    </div>

  </div>
</div>