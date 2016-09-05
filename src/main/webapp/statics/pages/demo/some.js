(function () {
    var app = angular.module('app', ['ngRoute']);
    app.config(['$logProvider', '$routeProvider', function ($logProvider, $routeProvider) {
        $logProvider.debugEnabled(true);
        $routeProvider.when('/', {
            controller: 'HomeController',
            controllerAs: 'home',
            templateUrl: '/app/templates/home.html'
        }).when('/schools', {
            controller: 'AllSchoolsController',
            controllerAs: 'schools',
            templateUrl: '/app/templates/allSchools.html',
            caseInsensitiveMatch: true
        }).when('/classrooms/:id', {
            controller: 'AllClassroomsController',
            controllerAs: 'classrooms',
            templateUrl: '/app/templates/allClassrooms.html',
            resolve: {
                promise: function () {
                    throw 'error transitioning to classrooms';
                }
            }
        }).when('/activities', {
            controller: 'AllActivitiesController',
            controllerAd: 'activities',
            templateUrl: '/app/tempaltes/allActivities.html',
            resolve: {
                //dataService.getAllActivities方法返回一个promise,必须要被resolved之后才会转到相应的视图页。接着，activities可以被注入到AllActivitiesController中。
                activities: function (dataService) {
                    return dataService.getAllActivities();
                }
            }
        }).otherwise('/');
    }]);
    app.run(['$rootScope', '$log', function ($rootScope, $log) {
        //通过$on为$rootScope添加路由事件
        $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
            $log.debug('successfully changed routes');
            $log.debug(event);
            $log.debug(current);
            $log.debug(previous);
        });
        $rootScope.$on('$routeChangeError', function (event, current, previous, rejection) {
            $log.debug('error changing routes');
            $log.debug(event);
            $log.debug(current);
            $log.debug(previous);
            $log.debug(rejection);
        });
    }
    ]);
}());
(function () {
    angular.module('app').controller('AllActivitiesController', ['dataService', 'notifier', '$location', 'activities', AllActivitiesController]);
    function AllActivitiesController(dataService, notifier, $location, activities) {
        var vm = this;
        vm.seletedMonth = 1;
        //这里的activites中路由的resolve中来
        // 原先的getAllActivities方法就不需要存在了
        vm.allActivities = actvities;
        //搜索过滤
        vm.search = function () {
            var classroom_detail_url = '/classrooms/' + vm.selectedClassroom.id + '/detail/' + vm.seletedMonth;
            $location.url(classroom_detail_url);
        };
        dataService.getAllClassrooms().then(function (classrooms) {
            vm.allClassrooms = classroom;
            vm.seletedClassroom = classrooms[0];
        }).catch(showError);
        function showError(message) {
            notifier.error(message);
        }
    }
}());