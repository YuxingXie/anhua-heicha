
(function () {
    "use strict";
    var app = angular.module('AdminApp', [
        'ngRoute'
    ]);
    window.app = app;
    app.config(function ($routeProvider) {
        $routeProvider.when('/', {templateUrl: '/admin/index/index', reloadOnSearch: false});
        $routeProvider.when('/carousel', {templateUrl: '/statics/pages/demo/carousel.html', reloadOnSearch: false});
        $routeProvider.when('/common_result', {templateUrl: '/statics/pages/demo/common_result.html',reloadOnSearch: false});
        $routeProvider.when('/trans_finished', {templateUrl: '/statics/pages/admin/trans_finished_list.html', reloadOnSearch: false})
        $routeProvider.when('/trans_unfinished', {templateUrl: '/statics/pages/admin/trans_unfinished_list.html', reloadOnSearch: false})
        $routeProvider.when('/add_first_member', {templateUrl: '/statics/pages/admin/add_first_member.html', reloadOnSearch: false})
        $routeProvider.when('/send_notice', {templateUrl: '/statics/pages/admin/send_notice.html', reloadOnSearch: false})

        .otherwise({
            redirectTo : '/'
        });
    });
    app.config(function ($httpProvider) {
        $httpProvider.defaults.useXDomain=true;
        delete $httpProvider.defaults.headers
            .common['X-Requested-With']
    });
    app.controller('AdminController', ["$rootScope", "$scope", "$http", "$location","$window","$routeParams",function ($rootScope, $scope, $http, $location, $window,$routeParams) {
        $scope.isEmptyObject= function (e) {
            var t;
            for (t in e)
                return !1;
            return !0
        }
        $scope.login= function (administrator) {
            $http.post("/admin/login", JSON.stringify(administrator)).success(function (message) {
                $scope.message = message;

                if(message){
                    $scope.adminLogin=message.success;
                    if (message.success) {
                        $location.path("/common_result");
                    }else{
                        $scope.administrator = message.data;
                    }
                }
            });
        }
        $scope.getAdministrator=function(){
            $http.get("/admin/get_admin").success(function (message) {
                $scope.message = message;
                if(message){
                    $scope.adminLogin=message.success;
                    $scope.administrator = message.data;
                }
            });
        }
        $scope.initMenu= function () {
            var menu={};
            var menuItems=[];
            var menuItem1={};
            var menuItem2={};
            var menuItem3={};
            menuItem1.name="用户管理";
            menuItem1.menuItems=[];
            menuItem1.menuItems.push({name:"添加第一个会员",url:"#/add_first_member"});
            menuItem2.name="提现管理";
            menuItem2.menuItems=[];
            menuItem2.menuItems.push({name:"已处理列表",url:"#/trans_finished"});
            menuItem2.menuItems.push({name:"未处理列表",url:"#/trans_unfinished"});
            menuItem3.name="通知管理";
            menuItem3.menuItems=[];
            menuItem3.menuItems.push({name:"发送通知",url:"#/send_notice"});
            //menuItem1.menuItems.push({name:"用户拓扑图",url:"#/aaa"});
            menuItems.push(menuItem1);
            menuItems.push(menuItem2);
            menuItems.push(menuItem3);
            menu.menuItems=menuItems;
            console.log(JSON.stringify(menu));
            $scope.menu=menu;
        }
        $scope.registerFirstMember = function (user) {

            $http.post("/user/register_first_member", JSON.stringify(user)).success(function (message) {
                //console.log(JSON.stringify(message));
                if (message) {
                    $scope.message = message;
                    $location.path("/common_result");

                } else {
                    $location.path("/common_error");
                }
            }).error(function(){
                $scope.message.message = "服务器错误！";
            });
        };
        $scope.sendNotice= function(notify){

            $http.post("/admin/notify", JSON.stringify(notify)).success(function (message) {
                if (message) {
                    $scope.message = message;
                    $location.path("/common_result");

                } else {
                    $location.path("/common_error");
                }
            }).error(function(){
                $scope.message.message = "服务器错误！";
            });
        };
        $scope.getUnfinishedTrans=function(){
            $http.get("/admin/unfinished_trans_list").success(function (message) {
                $scope.message = message;
                if(message){
                    if(message.success){
                        $scope.alipayTransList = message.data;
                    }else{
                        $location.path("/common_result");
                    }

                }
            });
        }
        $scope.getFinishedTrans=function(){
            $http.get("/admin/finished_trans_list").success(function (message) {
                $scope.message = message;
                if(message){
                    if(message.success){
                        $scope.alipayTransList = message.data;
                    }else{
                        $location.path("/common_result");
                    }

                }
            });
        }
        $scope.getFirstMember=function(){
            $http.get("/user/first_member").success(function (message) {
                $scope.message = message;
                if(message){
                    if(message.success){
                        $scope.firstMember = message.data;
                    }else{
                        $location.path("/common_result");
                    }

                }
            });
        }

        $scope.addTrans=function(trans){
            if(!$scope.toHandlerTransList) $scope.toHandlerTransList=[];
            var index = $scope.toHandlerTransList.indexOf(trans);
            if (index > -1) {
                $scope.toHandlerTransList.splice(index, 1);
            }else{
                $scope.toHandlerTransList.push(trans);
            }
            $scope.getTransTotalFee();
        }
        $scope.addAllTrans=function(){
            if(!$scope.toHandlerTransList) $scope.toHandlerTransList=[];

            if ($scope.isEmptyObject($scope.toHandlerTransList)) {
                //$scope.toHandlerTransList=$scope.alipayTransList;//can't like this
                for(var i=0;i<$scope.alipayTransList.length;i++){//must like this
                    $scope.toHandlerTransList.push($scope.alipayTransList[i]);
                }
            }else{
                $scope.toHandlerTransList=[];
            }
            $scope.getTransTotalFee();
        }
        $scope.getTransTotalFee=function(){
            $scope.transTotalFee=0;
            if($scope.isEmptyObject($scope.toHandlerTransList)) {
                return;
            }
            for(var i=0;i<$scope.toHandlerTransList.length;i++){
                $scope.transTotalFee+=$scope.toHandlerTransList[i].fee;
            }

        }
        $scope.isChecked=function(alipayTrans){
            if(!$scope.toHandlerTransList) $scope.toHandlerTransList=[];
            var index = $scope.toHandlerTransList.indexOf(alipayTrans);
            if (index > -1) {
                return true;
            }else{
                return false;
            }
        }
        $scope.submitTrans=function(){
            if ($scope.isEmptyObject($scope.toHandlerTransList)) {
                $scope.message={};
                $scope.message.success=false;
                $scope.message.message="没有选择转账条目！";
            }else{
                var form=document.getElementById("form");
                form.action="/alipay/batch_trans/trans";
                document.getElementById("data").value=JSON.stringify($scope.toHandlerTransList);
                //$http.post("/alipay/batch_trans",JSON.stringify($scope.toHandlerTransList)).success(function (message) {
                //    $scope.message = message;
                //    if(message){
                //        $scope.adminLogin=message.success;
                //        $scope.administrator = message.data;
                //    }
                //});
                form.submit();
            }
        }
        $scope.collapse=function(menuItem){
            var index = $scope.menu.menuItems.indexOf(menuItem);
            for(var i=0;i<$scope.menu.menuItems.length;i++){
                if(i==index){
                    if(!$scope.menu.menuItems[i].collapse){
                        $scope.menu.menuItems[i].collapse=true;
                        return;
                    }
                    $scope.menu.menuItems[i].collapse=false;
                }else{
                    $scope.menu.menuItems[i].collapse=true;
                }
            }
        }
        if(!$scope.administrator){
            $scope.getAdministrator();
        }
    }])
})();
