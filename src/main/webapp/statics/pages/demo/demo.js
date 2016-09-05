
  //
// Here is how to define your module 
// has dependent on mobile-angular-ui
// 
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
]);
  app.constant('WrongCode', {
      NOT_LOGIN: 1,
      NO_PERMISSION: 2

  })
app.service('Session', function () {
    this.create = function (sessionId, userId, userRole) {
        this.id = sessionId;
        this.userId = userId;
        this.userRole = userRole;
    };
    this.destroy = function () {
        this.id = null;
        this.userId = null;
        this.userRole = null;
    };
    return this;
})



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

    $routeProvider.when('/shopping', {templateUrl: '/statics/pages/demo/shopping.html', reloadOnSearch: false});
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
    $routeProvider.when('/register_success', {templateUrl: '/statics/pages/demo/register_success.html', reloadOnSearch: false});

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
app.controller('MainController', ["$rootScope", "$scope", "$http", "$location", "WrongCode", function ($rootScope, $scope, $http, $location, WrongCode) {
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
       if($scope.isEmptyObject($scope.session)||$scope.isEmptyObject($scope.session.loginUser))
            return false;
        return true;
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
    $scope.buyProduct=function(product){
        console.log(JSON.stringify(product));

        if(product){
            $http.post("/alipay/order", JSON.stringify(product));
        }

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
    $scope.synchronizeData= function (getLoginUser,getNotices,getLowerUsers,getPointsRecords){
        if(getLoginUser){
            console.log("get login user");
            $scope.getLoginUser();
        }
        if($scope.isEmptyObject($scope.session)||$scope.isEmptyObject($scope.session.loginUser)) return;
       if(getNotices){
           console.log("get notices");
           $scope.getNotices();
       }
        if(getLowerUsers){
            console.log("get lower users");
            $scope.getLowerUsers();
        }
        if(getPointsRecords){
            console.log("get points records");
            $scope.getPointsRecords();
        }
    }
    $scope.deleteNotice = function (notice) {
        var index = $scope.notices.indexOf(notice);
        if (index > -1) {
            $scope.notices.splice(index, 1);
        }
    };

}])

