<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="p" uri="/pageTag" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="form" class="com.lingyun.entity.Order" scope="session"></jsp:useBean>
<c:set var="path" value="<%=request.getContextPath() %>"/>
<c:if test="${path eq '/'}"><c:set var="path" value=""/></c:if>
<!DOCTYPE html>
<html ng-app="mainApp">
<head>
    <meta charset="utf-8">
    <title>订单支付</title>
    <link rel="shortcut icon" type="image/x-icon" href="${path}/statics/assets/img/logo.png"  media="screen" />
    <link href="${path}/statics/assets/plugins/font-awesome-4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/assets/css/style.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/assets/css/style-responsive.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/assets/css/color-bg-color.css" rel="stylesheet" type="text/css">
    <link href="${path}/statics/assets/plugins/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css">
    <style>
        #msg{position:fixed; top:300px; right:35px; z-index:10000; width:1px; height:52px; line-height:52px; font-size:20px; text-align:center; color:#fff; background:#360; display:none}
    </style>
    <script> path="${path}";</script>
    <script src="${path}/statics/assets/plugins/jquery-2.1.1.min.js" type="text/javascript"></script>
    <script src="${path}/statics/assets/plugins/jquery-migrate-1.2.1.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="${path}/statics/assets/plugins/jquery.md5.js"></script>
    <script src="${path}/statics/assets/plugins/pingpp-html5-master/src/pingpp.js"></script>
</head>
<body ng-controller="bankController">
<div class="container" ng-init="matchCode=true;useAccountPay=true" id="bankAppMain">
    <div class="row margin-top-40">
        <div class="center-block">
            <h3>大坝生态农业提醒您：</h3>
            <h4>订单提交成功，请您尽快付款！ </h4>
            <h4> 订单号：${form.id},应付金额<fmt:formatNumber value="${form.totalPrice}" pattern="##.##" minFractionDigits="2"></fmt:formatNumber>元</h4>
            <h4><i class="fa fa-warning color-red"></i> 请您在24小时内完成支付，否则订单会被自动取消。</h4>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-8 bg-primary" style="display: none;"  id="pay-msg">已支付成功！</div>
    </div>
    <div class="row">
        <form class="form-without-legend" novalidate="novalidate" action="${path}/order/pay" id="form" autocomplete="off" modelAttribute="form">
            <div ng-show="useAccountPay">
                <div class="form-group form-inline">
                <span ng-show="accounts &&accounts.length">
                    <label class="control-label">银行卡(暂不支持)</label>
                    <label class="control-label">
                        <img class="img-ico-md" ng-src="${path}/statics/assets/plugins/bank/ico/{{useAccount.bank.ico}}" alt="{{useAccount.bank.name}}"/>
                    </label>
                    <label class="control-label">
                        {{useAccount.bankCardSort.cardName}}
                    </label>
                    <select class="form-control" ng-model="useAccount"
                            ng-options="useAccount as useAccount.bank.name+' '+useAccount.cardNo+' '+useAccount.cardSortString for useAccount in accounts">
                    </select>
                </span>
                <label class="margin-top-20">选择在线支付方式:</label>
                <div>
                    <div class="padding-bottom-10">
                        <a disabled class="btn btn-sm btn-primary fa fa-credit-card" ng-click="useAccountPay=!useAccountPay;getBanks();">使用新银行卡(暂不支持)</a>
                        <%--<label class="control-label padding-left-10" ng-show="!accounts||!accounts.length">您没有在本站使用过银行卡 </label>--%>
                    </div>
                    <div class="padding-bottom-10">
                        <a ng-href="${path}/alipay//order/${form.id}">
                            <img src="${path}/statics/alipay/img/alipay_logo.png" height="30px">
                        </a>
                    </div>

                </div>




                    <label class="control-label">支付<fmt:formatNumber value="${form.totalPrice}" pattern="##.##" minFractionDigits="2"></fmt:formatNumber>元</label>
                </div>
                <div ng-show="accounts &&accounts.length" class="form-group form-inline">
                    <div class="center-block"><a type="button" id="pay" class="fa fa-credit-card fa-lg btn btn-danger btn-lg">立即支付</a> </div>
                </div>
            </div>
            <div class="row form-horizontal" ng-show="!useAccountPay">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="#netBank" data-toggle="tab">网银支付</a></li>
                    <li><a href="#shortcuts" data-toggle="tab">快捷支付</a></li>
                </ul>
                <div class="form-inline padding-top-10">
                    <input type="text" class="bank-code form-control" placeholder="输入银行卡号或简码识别" ng-model="cardNoOrCode"/>
                    <a class="fa fa-history btn btn-xs btn-primary" ng-click="useAccountPay=!useAccountPay">返回我的银行卡</a>
                </div>
                <div class="tab-content">
                    <div class="tab-pane fade in active" id="netBank">

                    </div>
                    <div class="tab-pane" id="shortcuts">
                        <div>
                            <div ng-repeat="bank in banks">
                                <div class="col-lg-3 col-sm-3 bank-shortcuts" style="margin-top: 5px;margin-bottom: 5px;"
                                     data-code="{{bank.code}}"  data-bank-name="{{bank.name}}" data-ico="{{bank.ico}}" ng-if="matches(bank)">
                                    <a href="#" class="bank-ico" ng-click="shortcutsPay(bank)">
                                        <img ng-src="${path}/statics/assets/plugins/bank/ico/{{bank.ico}}" alt="{{bank.name}}"/></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <div class="modal fade active" id="shortcutsPayModal" tabindex="-1" role="dialog" aria-labelledby="shortcutsPayModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"> &times;</button>
                    <h2 class="modal-title" id="shortcutsPayModalLabel">
                        快捷付款
                    </h2>
                </div>
                <form method="post" name="billForm" id="billForm" ng-submit="submit()" autocomplete="off"
                      <%--action="${path}/order/pay"--%>
                        >
                    <div class="modal-body table-responsive">
                        <table class="table table-striped">
                            <tr>
                                <td width="20%" class="text-left"><i class="fa fa-bank padding-top-15">付款银行</i></td>
                                <td class="text-left">
                                    <img src="${path}/statics/assets/plugins/bank/ico/{{bank.ico}}"/>
                                    <input type="hidden" ng-init="order.id='${form.id}'" name="orderId" ng-model="order.id"/>
                                    <%--<input type="hidden" ng-init="order.payAccount.bank.id=bank.id" name="bankId" ng-model="order.payAccount.bank.id"/>--%>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-left"><i class="fa fa-adjust padding-top-10">卡种</i></td>
                                <td class="text-left form-inline">
                                    <label>
                                        <input type="radio" card_type_valid name="cardSort" value="1" required="true"
                                               ng-model="order.payAccount.cardSort"/>信用卡
                                    </label>&nbsp;&nbsp;&nbsp;
                                    <label><input type="radio" card_type_valid name="cardSort" value="2" required="true"
                                                  ng-model="order.payAccount.cardSort"/>储蓄卡</label>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-left"><i class="fa fa-cc-visa padding-top-10">银行卡号</i></td>
                                <td class="text-left">
                                    <input type="text" class="form-control" required="true" name="cardNo" ng-model="order.payAccount.cardNo"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-left"><i class="fa fa-user padding-top-10">姓名</i></td>
                                <td class="text-left">
                                    <input style="width: 200px;" type="text" class="form-control " ng-init="order.payAccount.cardUserName='${order.user.name}'" required="true" name="cardUserName" ng-model="order.payAccount.cardUserName"/>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-left"><i class="fa fa-credit-card padding-top-10">身份证</i> </td>
                                <td class="text-left">
                                    <input type="text" name="cardUserIdCardNo" class="form-control" ng-init="order.payAccount.cardUserIdCardNo='${order.user.idCardNo}'" required="true"
                                           ng-model="order.payAccount.cardUserIdCardNo" id_card_valid="true" maxlength="18"/>
                                <label class="has-error" ng-if="billForm.cardUserIdCardNo.$error.validIdCard">请输入有效身份证号码</label>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-left"><i class="glyphicon glyphicon-phone padding-top-10">手机号</i></td>
                                <td class="text-left">
                                    <input type="text" name="cardUserPhone" class="form-control " ng-init="order.payAccount.cardUserPhone='${order.user.phone}'" required="true"
                                           phone_number_valid="true" ng-model="order.payAccount.cardUserPhone"/>
                                    <label class="control-label margin-left-20" ng-show="billForm.cardUserPhone.$error.validPhoneNumber"> 请输入一个有效的手机号</label>
                                </td>
                            </tr>


                            <tr ng-show="order.payAccount.cardSort==1" class="text-left">
                                <td class="text-left"><i class="fa fa-clock-o">有效期</i></td>
                                <td class="input-group date form_date text-left">
                                    <input class="form-control" style="width: 150px;" size="16" type="text" name="cardValidDate" ng-model="order.payAccount.cardValidDate" readonly ng-required="order.payAccount.cardSort==1"/>
                                    <span class="input-group-addon pull-left"><span class="glyphicon glyphicon-remove"></span></span>
                                    <span class="input-group-addon pull-left"><span class="glyphicon glyphicon-calendar"></span></span>
                                </td>
                            </tr>
                            <tr ng-if="order.payAccount.cardSort==1">
                                <td class="text-left"><i class="fa fa-reorder">卡验证码</i></td>
                                <td class="text-left form-inline">
                                    <input type="text" style="width: 150px;" name="cardValidateCode" ng-model="order.payAccount.cardValidateCode" class="form-control bg-success"
                                           placeholder="签名栏后3位数" ng-required="order.payAccount.cardSort==1" maxlength="3" />
                                    <label class="control-label"><input type="checkbox" data-ng-click="showPic()" class="form-control checkbox"/>看示例</label>
                                </td>
                            </tr>
                            <tr ng-if="order.payAccount.cardSort==1&&isShow">
                                <td name="credit_card_example" class="text-left" colspan="2">
                                    <img src="${path}/statics/assets/plugins/bank/credit_card_example.jpg" class="img-responsive">
                                </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td class="text-left form-inline">
                                    <div class="center-block">
                                        <label class="control-label">
                                            <input type="checkbox" value="${order.user.phone}" required="true" class="checkbox form-control" ng-model="xy"/>《大坝快捷支付用户协议》
                                        </label>
                                        <a href="${path}/statics/docs/kuaijiezhifuyonghuxieyi.html" target="_blank" class="fa fa-book">阅读</a>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                </td>
                                <td class="form-inline">
                                    <button type="button" class="btn btn-primary modal-close  form-control pull-right margin-left-20" data-dismiss="modal" aria-hidden="true">关闭</button>
                                    <input type="submit" value="同意开通并支付" class="btn btn-primary form-control pull-right" ng-disabled="billForm.$invalid"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>

            </div>
        </div>
    </div>
</div>
<script src="${path}/statics/assets/plugins/angular-1.4.8/angular.js"></script>
<script src="${path}/statics/assets/plugins/angular-1.4.8/angular-sanitize.min.js"></script>
<script src="${path}/statics/assets/plugins/angular-1.4.8/angular-route.min.js"></script>
<script src="${path}/statics/assets/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script type="text/javascript" src="${path}/statics/assets/plugins/bootstrap/js/bootstrap.js" ></script>
<script type="text/javascript" src="${path}/statics/assets/scripts/top.js"></script>
<script src="${path}/statics/assets/plugins/angular-1.2.19/ui-bootstrap-tpls.min.js"></script>
<script>
    var mainApp=angular.module("mainApp",['ui.bootstrap', 'ngRoute','ngSanitize']);
    mainApp .controller('mainController', ['$scope', '$http', function ($scope, $http) {

    }])

</script>
<script src="${path}/statics/assets/scripts/form-validate.js"></script>
<script type="text/javascript" src="${path}/statics/assets/plugins/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
<script type="text/javascript" src="${path}/statics/assets/plugins/bootstrap-datetimepicker-master/js/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
<script>
    $('.form_date').datetimepicker({
        language:'zh-CN',
        format:'mm/yy',
        weekStart: 1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 3,
        minView: 3
    });
    var containsIgnoreCases=function(full,part){
        if(!full) return false;
        if(full==="") return false;
        if(!part) return true;
        if(part==="") return true;
        var fullStr=full.toString();
        var partStr=part.toString();
        if(fullStr.length<partStr.length) return false;
        for(var i=0;i<partStr.length;i++){//abc
            var partStrChar=partStr.substring(0,i+1);
            if(fullStr.indexOf(partStrChar)<0 &&fullStr.indexOf(partStrChar.toLowerCase())<0&&fullStr.indexOf(partStrChar.toUpperCase())<0){
                return false;
            }
        }
        return true;
    }
    var matchCode=function(code,input){
        return containsIgnoreCases(code,input);
    }
    var matchNo=function(cardSorts,input){
        if(!input) return true;
        if(input==="") return true;
        if(!cardSorts) return false;
        if(cardSorts==="") return false;
        if(cardSorts==="[]") return false;
        if(!Array.isArray(cardSorts)) return false;
        for(var i=0;i<cardSorts.length;i++){
            var cardSort=cardSorts[i];
            var noStart=cardSort.noStart;
            if(!noStart) continue;
            if(noStart==="")continue;
            if(noStart.indexOf(input)>=0){
                return true;
            }
            continue;
        }
        return false;
    }
    mainApp.controller("bankController",["$scope","$http",function($scope,$http){
        $scope.isShow=false;
        $scope.order={};
        $scope.order.payWay='2'
        $scope.order.payAccount={};
        $scope.order.payAccount.bank={};
        $scope.bank={};
        $scope.getBanks=function(){
            if($scope.banks) return;
            $http.get(path+"/bank/json")
                    .success(function(response){
                        $scope.banks=response;
                    });
        }

        $scope.isShow=false;
        $http.get(path+"/user/accs")
                .success(function(data){
                    $scope.accounts=data;
                    $scope.useAccount=$scope.accounts[0];
                });
        $scope.matches=function(bank){
            var code=bank.code;
            var cardSorts=bank.cardSorts;
            if(!$scope.cardNoOrCode){
                return true;
            }
            if(matchCode(code,$scope.cardNoOrCode)||matchNo(cardSorts,$scope.cardNoOrCode)){
                return true;
            }
            return false;
        };
        $scope.showPic=function(){
            $scope.isShow=! $scope.isShow;
        }
        $scope.submit=function(){
            var url="${path}/order/pay";
//                        console.log(JSON.stringify($scope.bank));
            $scope.order.payAccount.bank.id=$scope.bank.id;
//                        console.log(JSON.stringify($scope.order));
            $http.post( url,$scope.order).success(function (charge) {
                console.log(JSON.stringify(charge));
//                                console.log(JSON.stringify(pingppPc))
//                                window.pingppPc.payment(charge, function(result, err){
//                                // 处理错误信息
//                                alert(err);
//                                });
                var $shortcutsPayModal=$("#shortcutsPayModal");
                $shortcutsPayModal.modal("hide");
                $("#pay-msg").show().animate({width: '250px'}, 200).fadeOut(2500);

            }).error(function(){ console.log("error！"); });
            return false;
        }
        $scope.shortcutsPay=function(bank){
            $scope.bank=bank;
            var $shortcutsPayModal=$("#shortcutsPayModal");
            $shortcutsPayModal.modal().show();
        }
    }])
    .directive("cardValidDateValid", function () {//
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                scope.$watch(attrs.ngModel,function(n){
                    if(!n) return;
                    if(scope.order.payAccount.cardSort==='2')
                    {
                        c.$setValidity('validCardValidDate',true);
                    }else{
                        c.$setValidity('validCardValidDate',true);
                    }
                });

            }
        }
    })
    $(document).ready(function(){});
</script>
</body>
</html>