
  //
// Here is how to define your module 
// has dependent on mobile-angular-ui
//
(function () {
  "use strict";
  var app = angular.module('MobileAngularUiExamples', [
    'ngRoute',
    'mobile-angular-ui',

    // touch/drag feature: this is from 'mobile-angular-ui.gestures.js'
    // it is at a very beginning stage, so please be careful if you like to use
    // in production. This is intended to provide a flexible, integrated and and
    // easy to use alternative to other 3rd party libs like hammer.js, with the
    // final pourpose to integrate gestures into default ui interactions like
    // opening sidebars, turning switches on/off ..
    'mobile-angular-ui.gestures'
      //,'w5c.validator'
]);
  app.constant('WrongCode', {
      NOT_LOGIN: 1,
      NO_PERMISSION: 2

  })
  window.app = app;
  //app.config(["w5cValidatorProvider", function (w5cValidatorProvider) {
  //    // 全局配置
  //    w5cValidatorProvider.config({
  //
  //        blurTrig   : false,//光标移除元素后是否验证并显示错误提示信息
  //        showError  : true,//可以是bool和function，每个元素验证不通过后调用该方法显示错误信息，默认true，显示错误信息在元素的后面。
  //        removeError: true//可以是bool和function，每个元素验证通过后调用该方法移除错误信息，默认true，验证通过后在元素的后面移除错误信息。
  //    });
  //    w5cValidatorProvider.setRules({
  //        contactPhone         : {
  //            required: "联系电话不能为空",
  //            pattern   : "电话格式不正确，固定电话区号及分机用'-'分隔"
  //        },
  //        acceptPersonName      : {
  //            required      : "收件人不能为空"
  //            ,pattern       : "收件人必须输入字母、数字、下划线,以字母开头"
  //            //,w5cuniquecheck: "输入用户名已经存在，请重新输入"
  //        },acceptAddress      : {
  //            required      : "配送地址不能为空"
  //            //,w5cuniquecheck: "输入用户名已经存在，请重新输入"
  //        },
  //        password      : {
  //            required : "密码不能为空",
  //            minlength: "密码长度不能小于{minlength}",
  //            maxlength: "密码长度不能大于{maxlength}"
  //        },
  //        repeatPassword: {
  //            required: "重复密码不能为空",
  //            repeat  : "两次密码输入不一致"
  //        },
  //        number        : {
  //            required: "数字不能为空"
  //        },
  //        customizer    : {
  //            customizer: "自定义验证数字必须大于上面的数字"
  //        },
  //        dynamicName:{
  //            required: "动态Name不能为空"
  //        },
  //        dynamic       : {
  //            required: "动态元素不能为空"
  //        }
  //    });
  //}]);


//
// You can configure ngRoute as always, but to take advantage of SharedState location
// feature (i.e. close sidebar on backbutton) you should setup 'reloadOnSearch: false'
// in order to avoid unwanted routing.
//
app.config(function ($routeProvider) {
    $routeProvider.when('/', {templateUrl: '/statics/pages/demo/home.html', reloadOnSearch: false});
    $routeProvider.when('/points-record', {
        templateUrl: '/statics/pages/demo/points-record.html',
        reloadOnSearch: false
    });
    $routeProvider.when('/measure-record', {
        templateUrl: '/statics/pages/demo/measure-record.html',
        reloadOnSearch: false
    });

    $routeProvider.when('/shopping', {templateUrl: '/statics/pages/demo/shopping.jsp', reloadOnSearch: false});
    $routeProvider.when('/accordion', {templateUrl: '/statics/pages/demo/accordion.html', reloadOnSearch: false});
    $routeProvider.when('/overlay', {templateUrl: '/statics/pages/demo/overlay.html', reloadOnSearch: false});
    $routeProvider.when('/register', {templateUrl: '/statics/pages/demo/register.html', reloadOnSearch: false});
    $routeProvider.when('/invite', {templateUrl: '/statics/pages/demo/invite.html', reloadOnSearch: false});
    $routeProvider.when('/common_result', {
        templateUrl: '/statics/pages/demo/common_result.html',
        reloadOnSearch: false
    });
    $routeProvider.when('/common_error', {templateUrl: '/statics/pages/demo/common_error.html', reloadOnSearch: false});
    $routeProvider.when('/login', {templateUrl: '/statics/pages/demo/login.html', reloadOnSearch: false});
    $routeProvider.when('/dropdown', {templateUrl: '/statics/pages/demo/dropdown.html', reloadOnSearch: false});
    $routeProvider.when('/notice', {templateUrl: '/statics/pages/demo/notice.html', reloadOnSearch: false});
    $routeProvider.when('/carousel', {templateUrl: '/statics/pages/demo/carousel.html', reloadOnSearch: false});
    $routeProvider.when('/buy', {templateUrl: '/statics/pages/demo/buy_product.html', reloadOnSearch: false});
    $routeProvider.when('/fill_order/:id', {templateUrl: '/statics/pages/demo/fill_order.html', reloadOnSearch: false});
    $routeProvider.when('/register_success', {templateUrl: '/statics/pages/demo/register_success.html', reloadOnSearch: false});
    $routeProvider.when('/to_pay/:id', {templateUrl: '/alipay/to_pay', reloadOnSearch: false});

});
    app.config(function ($httpProvider) {
        $httpProvider.defaults.useXDomain=true;
        delete $httpProvider.defaults.headers
            .common['X-Requested-With']
    });
//
// `$drag` example: drag to dismiss
//
app.directive('dragToDismiss', function ($drag, $parse, $timeout) {
    return {
        restrict: 'A',
        compile: function (elem, attrs) {
            var dismissFn = $parse(attrs.dragToDismiss);
            return function (scope, elem, attrs) {
                var dismiss = false;

                $drag.bind(elem, {
                    constraint: {
                        minX: 0,
                        minY: 0,
                        maxY: 0
                    },
                    move: function (c) {
                        if (c.left >= c.width / 4) {
                            dismiss = true;
                            elem.addClass('dismiss');
                        } else {
                            dismiss = false;
                            elem.removeClass('dismiss');
                        }
                    },
                    cancel: function () {
                        elem.removeClass('dismiss');
                    },
                    end: function (c, undo, reset) {
                        if (dismiss) {
                            elem.addClass('dismitted');
                            $timeout(function () {
                                scope.$apply(function () {
                                    dismissFn(scope);
                                });
                            }, 400);
                        } else {
                            reset();
                        }
                    }
                });
            };
        }
    };
});

//
// Another `$drag` usage example: this is how you could create
// a touch enabled "deck of cards" carousel. See `carousel.html` for markup.
//
app.directive('carousel', function () {
    return {
        restrict: 'C',
        scope: {},
        controller: function ($scope) {
            this.itemCount = 0;
            this.activeItem = null;

            this.addItem = function () {
                var newId = this.itemCount++;
                this.activeItem = this.itemCount == 1 ? newId : this.activeItem;
                return newId;
            };

            this.next = function () {
                this.activeItem = this.activeItem || 0;
                this.activeItem = this.activeItem == this.itemCount - 1 ? 0 : this.activeItem + 1;
            };

            this.prev = function () {
                this.activeItem = this.activeItem || 0;
                this.activeItem = this.activeItem === 0 ? this.itemCount - 1 : this.activeItem - 1;
            };
        }
    };
});

app.directive('carouselItem', function ($drag) {
    return {
        restrict: 'C',
        require: '^carousel',
        scope: {},
        transclude: true,
        template: '<div class="item"><div ng-transclude></div></div>',
        link: function (scope, elem, attrs, carousel) {
            scope.carousel = carousel;
            var id = carousel.addItem();

            var zIndex = function () {
                var res = 0;
                if (id == carousel.activeItem) {
                    res = 2000;
                } else if (carousel.activeItem < id) {
                    res = 2000 - (id - carousel.activeItem);
                } else {
                    res = 2000 - (carousel.itemCount - 1 - carousel.activeItem + id);
                }
                return res;
            };

            scope.$watch(function () {
                return carousel.activeItem;
            }, function (n, o) {
                elem[0].style['z-index'] = zIndex();
            });


            $drag.bind(elem, {
                constraint: {minY: 0, maxY: 0},
                adaptTransform: function (t, dx, dy, x, y, x0, y0) {
                    var maxAngle = 15;
                    var velocity = 0.02;
                    var r = t.getRotation();
                    var newRot = r + Math.round(dx * velocity);
                    newRot = Math.min(newRot, maxAngle);
                    newRot = Math.max(newRot, -maxAngle);
                    t.rotate(-r);
                    t.rotate(newRot);
                },
                move: function (c) {
                    if (c.left >= c.width / 4 || c.left <= -(c.width / 4)) {
                        elem.addClass('dismiss');
                    } else {
                        elem.removeClass('dismiss');
                    }
                },
                cancel: function () {
                    elem.removeClass('dismiss');
                },
                end: function (c, undo, reset) {
                    elem.removeClass('dismiss');
                    if (c.left >= c.width / 4) {
                        scope.$apply(function () {
                            carousel.next();
                        });
                    } else if (c.left <= -(c.width / 4)) {
                        scope.$apply(function () {
                            carousel.next();
                        });
                    }
                    reset();
                }
            });
        }
    };
});


//
// For this trivial demo we have just a unique MainController
// for everything
//
app.controller('MainController', ["$rootScope", "$scope", "$http", "$location","$window","$routeParams", "WrongCode", function ($rootScope, $scope, $http, $location, $window,$routeParams,WrongCode) {
    $scope.isEmptyObject= function (e) {
        var t;
        for (t in e)
            return !1;
        return !0
    }
    $scope.doPrint=function(){
        console.log("do print")
    }
    $scope.loginUser = {};
    $scope.loginUser.remember = true;
    //$scope.phone = '18678878888';
    $scope.session = {};
    $scope.session.loginUser = {};
    $scope.message = {};
    $scope.lowerUsers = [];
    $scope.getLowerUsers = function(){
        $http.get('/user/lower_users').then(
            function success(response) {
                //console.log(JSON.stringify(response.data))
                $scope.lowerUsers = response.data;
            }
            , function error(reason) {
                //console.log("error");
            })
    }
    $scope.getLowerUsers();
    $http.get("/user/session").success(function (message) {
        //console.log(JSON.stringify(message));
        //console.log($scope.isEmptyObject(message.data));
        $scope.message = message;
        if(message.success){
            if (message.data && message.data.session) {
                $scope.session=message.data.session;
                $scope.session.loginUser = message.data.session.loginUser;
            }
        }
      });
    $scope.isUserLogin=function(){
        //$scope.getLoginUser();
       if($scope.isEmptyObject($scope.session)||$scope.isEmptyObject($scope.session.loginUser))
            return false;
        else return true;
    }
    $scope.getLoginUser=function(){
        $http.get("/user/session").success(function (message) {
            //console.log(JSON.stringify(message));
            //console.log($scope.isEmptyObject(message.data));
            $scope.message = message;
            if(message.success){
                if (message.data && message.data.session) {
                    console.log("用户处于登录状态");
                    $scope.session=message.data.session;
                    $scope.session.loginUser = message.data.session.loginUser;
                }
            }
        });
    }
    //app.controller('MainController', function($rootScope, $scope,$http){

    // User agent displayed in home page
    $scope.userAgent = navigator.userAgent;

    // Needed for the loading screen
    $rootScope.$on('$routeChangeStart', function () {
        $rootScope.loading = true;
    });

    $rootScope.$on('$routeChangeSuccess', function () {
        $rootScope.loading = false;
    });


    $scope.bottomReached = function () {
        alert('Congrats you scrolled to the end of the list!');
    }

    $scope.login = function () {
        $http.post("/user/login", JSON.stringify($scope.loginUser)).success(function (message) {
            //console.log(JSON.stringify(message));
            $scope.message = message;
            if(message.success){
                if (message.data && message.data.session) {
                    $scope.session.loginUser = message.data.session.loginUser;
                    $scope.lowerUsers = message.data.lowerUsers;
                    $scope.synchronizeData(false,true,true,true);
                }
            }
            $location.path("/common_result");




        });
    };
    //
    // user register
    //
    $scope.user = {};
    $scope.register = function () {
        //alert('You submitted the register form');
        //console.log($scope.user);
        //alert(JSON.stringify($scope.user));
        $http.post("/user/register", JSON.stringify($scope.user)).success(function (message) {
            //console.log(JSON.stringify(message));
            if (message) {
                if (message.success) {

                    $location.path("/register_success");
                } else {
                    $scope.message = message;
                    $location.path("/common_result");
                }
            } else {
                $location.path("/common_error");
            }
        });
    };
    $scope.getProductList=function(){
        console.log("get Product List");
        $http.get('/product/list').then(
            function success(response) {
                //console.log(JSON.stringify(response.data));
                $scope.productList = response.data;
            }
            , function error(reason) {
                //console.log("error");
            })
    }

    $scope.generateOrder=function(productSelected,product){
        console.log("generate Order");
        if(!productSelected) return;
        var order={};
        var productSelectedList=[];
        productSelectedList.push(productSelected);
        order.productSelectedList=productSelectedList;
        //order.user=$scope.session.loginUser;
        $http.post("/order/gen", JSON.stringify(order)).success(function (message) {
            $scope.order=message.data;
            console.log(JSON.stringify($scope.order));
            $location.path("/fill_order/"+$scope.order.id);
        });

    }
    //发送邀请
    $scope.authorizeInfo = {};
    $scope.sendInvite = function () {
        //console.log(JSON.stringify($scope.authorizeInfo));
        $http.post("/user/invite", JSON.stringify($scope.authorizeInfo)).success(function (message) {
            //console.log(JSON.stringify(message));
            $scope.message = message;
            $location.path("/common_result");
        });
    };


    $scope.getPointsRecords = function () {
        $http.get('/user/points').then(
            function success(response) {
                //console.log(JSON.stringify(response.data));
                if(!response.data.success){
                    $scope.message=response.data;
                    //$location.path("/common_result");
                }else{
                    $scope.membershipPoints = response.data.data;
                    var totalMembershipPointCount = 0;
                    for (var i = 0; i < $scope.membershipPoints.length; i++) {
                        var membershipPoint = $scope.membershipPoints[i];
                        var count = membershipPoint.count;
                        if (membershipPoint.type == -1) {
                            totalMembershipPointCount -= count;
                        } else {
                            totalMembershipPointCount += count;
                        }
                        $scope.totalMembershipPointCount = totalMembershipPointCount;
                    }
                }
            }
            , function error(reason) {
                //console.log("error");
            })

        }
    $scope.getMeasureRecords = function () {
        $http.get('/user/measure').then(
            function success(response) {
                if(!response.data.success){
                    $scope.message=response.data;
                }else{
                    $scope.membershipMeasures = response.data.data;
                    var totalMembershipMeasure = 0;
                    for (var i = 0; i < $scope.membershipMeasures.length; i++) {
                        var membershipPoint = $scope.membershipMeasures[i];
                        var count = membershipPoint.count;
                        if (membershipPoint.type == -1) {
                            totalMembershipMeasure -= count;
                        } else {
                            totalMembershipMeasure += count;
                        }
                        $scope.totalMembershipMeasures = totalMembershipMeasure;
                    }
                }
            }
            , function error(reason) {
                //console.log("error");
            })

    }
    //去合作商城消费需要的数据
    $scope.getFriendshipMallShoppingData=function () {
        $http.get('/user/friendship_mall_shopping').then(
            function success(response) {
                //console.log(JSON.stringify(response.data));
                if(response.data){
                    $scope.membershipPoints = response.data.points;
                    var totalMembershipPointCount = 0;
                    for (var i = 0; i < $scope.membershipPoints.length; i++) {
                        var membershipPoint = $scope.membershipPoints[i];
                        var count = membershipPoint.count;
                        if (membershipPoint.type == -1) {
                            totalMembershipPointCount -= count;
                        } else {
                            totalMembershipPointCount += count;
                        }
                        $scope.totalMembershipPointCount = totalMembershipPointCount;
                    }
                    $scope.malls = response.data.malls;
                }
            }
            , function error(reason) {
                //console.log("error");
            })

    }
    //
    // 'Drag' screen
    //
    //$scope.notices = [];
    $scope.getNotices=function(){
        $http.get('/user/notices').then(
            function success(response) {
                if(!response.data.success){
                    $scope.message=response.data;
                    console.log(JSON.stringify($scope.message));
                    if(!response.data.session || !response.data.session.loginUser){
                        $scope.session={};
                        $scope.session.loginUser={};
                    }
                }else{
                    $scope.notices = response.data.data;
                    if(!$scope.isEmptyObject($scope.notices)){
                        var unreadNoticesCount=0;
                        for (var i = 0; i <$scope.notices.length; i++) {
                            var notice=$scope.notices[i];
                            if(!notice.read){
                                unreadNoticesCount++;
                            }
                        }
                        $scope.unreadNoticesCount=unreadNoticesCount;
                        console.log("未读条数："+unreadNoticesCount)
                    }

                }
            }
            , function error(reason) {
                //console.log("error");
            })

    }
    $scope.getNotices();
    $scope.readNotice= function (notice){
        //$scope.showMessage=!$scope.showMessage;
        if(!notice) return;
        if(notice.read) return;
        //console.log(JSON.stringify(notice));
        $http.post("/user/read-notice", JSON.stringify(notice)).success(function (message) {
            //console.log(message.data);
            notice=message.data;
            $scope.getNotices();
        });
    }
    $scope.synchronizeData= function (getLoginUser,getNotices,getLowerUsers,getPointsRecords,getMeasure){
        if(getLoginUser){
            console.log("get login user");
            $scope.getLoginUser();
        }
        if($scope.isEmptyObject($scope.session)||$scope.isEmptyObject($scope.session.loginUser)) return;
       if(getNotices)
           $scope.getNotices();
        if(getLowerUsers)
            $scope.getLowerUsers();
        if(getPointsRecords)
            $scope.getPointsRecords();
        if(getMeasure){
            $scope.getMeasureRecords();
        }
    }
    $scope.exchangePoints=function(friendshipExchange){
        if(!$scope.isUserLogin()){
            $scope.message={};
            $scope.message.message="您长时间没有操作，已自动从系统登出。";
            $scope.message.success=false;
            return;
        }
        $http.post("/user/friendship_exchange",JSON.stringify(friendshipExchange)).success(function (message) {
        //$http.jsonp(friendshipExchange.mall.exchangeUrl+"?callback=JSON_CALLBACK",{params:{"data":shoppingData}}).success(function (message) {
            console.log(JSON.stringify(message));
            $scope.exchangeMessage=message;
            $scope.synchronizeData(false,false,false,true);
        });

    }
    $scope.initExchangeMessage=function(){
        $scope.exchangeMessage=null;
    }

    $scope.deleteNotice = function (notice) {
        var index = $scope.notices.indexOf(notice);
        if (index > -1) {
            $scope.notices.splice(index, 1);
        }
    };
    $scope.initVm=function(){
        var vm = $scope.vm = {
            htmlSource        : "",
            showErrorType     : "1",
            showDynamicElement: true,
            dynamicName       : "dynamicName",
            entity            : {}
        };
        $scope.orderId=$routeParams.id;
        console.log("init vm")
        if(!$scope.order){
            console.log("!$scope.order")
            console.log("$routeParams.id " +$routeParams.id)
            $http.get("/order/"+$routeParams.id).success(function (data) {
                $scope.order=data;
                vm.entity=$scope.order;
            });

        }
        else vm.entity=$scope.order;
        //vm.entity.self=0;
        vm.saveEntity = function ($event) {
            console.log(JSON.stringify(vm.entity));
            var form=document.getElementById("form");
            document.getElementById("orderId").value=$routeParams.id;
            form.action="/alipay/to_pay";
            form.submit();
            //$http.post("/order/fill", JSON.stringify(vm.entity)).success(function (message) {
            //   if(!message.success){
            //       $location.path("/common_result");
            //   }else{
            //       $scope.order=message.data;
            //       $location.path("/to_pay");
            //   }
            //});
        };

        //每个表单的配置，如果不设置，默认和全局配置相同
        vm.validateOptions = {
            blurTrig: true
        };



        vm.changeShowType = function () {
            if (vm.showErrorType == 2) {
                vm.validateOptions.showError = false;
                vm.validateOptions.removeError = false;
            } else {
                vm.validateOptions.showError = true;
                vm.validateOptions.removeError = true;
            }
        };

        //vm.self = [
        //    {
        //        value: 1,
        //        text : "是本人"
        //    },
        //    {
        //        value: 0,
        //        text : "非本人"
        //    }
        //];

        //$http.get("/statics/pages/demo/demo.js").success(function (result) {
        //    vm.jsSource = result;
        //});
        //$http.get("/fill_order").success(function (result) {
        //    vm.htmlSource = result;
        //});
        //$http.get("/statics/pages/demo/w5c_validator/css/css.less").success(function (result) {
        //    vm.lessSource = result;
        //});

    }
    }])
})();

