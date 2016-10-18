
(function () {
    "use strict";
    var app = angular.module('AdminApp', [
        'ngRoute'
    ]);
    app.config(function ($routeProvider) {
        $routeProvider.when('/', {templateUrl: '/admin/index/index', reloadOnSearch: false});
        $routeProvider.when('/carousel', {templateUrl: '/statics/pages/demo/carousel.html', reloadOnSearch: false});
        $routeProvider.when('/common_result', {templateUrl: '/statics/pages/demo/common_result.html',reloadOnSearch: false});
        $routeProvider.when('/trans_finished', {templateUrl: '/statics/pages/admin/trans_finished_list.html', reloadOnSearch: false})
        $routeProvider.when('/trans_unfinished', {templateUrl: '/statics/pages/admin/trans_unfinished_list.html', reloadOnSearch: false})
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
            menuItem1.name="用户提现管理";
            menuItem1.menuItems=[];
            menuItem1.menuItems.push({name:"已处理列表",url:"#/trans_finished"});
            menuItem1.menuItems.push({name:"未处理列表",url:"#/trans_unfinished"});
            //menuItem1.menuItems.push({name:"用户拓扑图",url:"#/aaa"});
            menuItems.push(menuItem1);
            menu.menuItems=menuItems;
            console.log(JSON.stringify(menu));
            $scope.menu=menu;
        }
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
                form.action="/alipay/batch_trans/submit";
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
        if(!$scope.administrator){
            $scope.getAdministrator();
        }
    }])
})();
