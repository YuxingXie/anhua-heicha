    app.directive('pwCheck', [function () {
        return {
            require: 'ngModel',
            link: function (scope, elem, attrs, ctrl) {
                //var firstPassword =attrs.pwCheck;
                //elem.add($(firstPassword)).on('keyup', function () {
                scope.$watch(attrs.ngModel,function(n){
                    //scope.$apply(function () {
                        if(!n) return;
                        //console.log("password:"+scope.user.password+",rePassword:"+scope.user.rePassword);
                        //console.log("first:"+firstPassword+"-"+$(firstPassword).val());
                        //var v = elem.val()===$(firstPassword).val();
                        //console.log("scope.rePassword:"+scope.rePassword);
                        ctrl.$setValidity('pwmatch', scope.user.password===scope.user.rePassword);
                        //ctrl.$setValidity('pwmatch', (!scope.user.password &&!scope.user.rePassword) && scope.user.password===scope.user.rePassword);
                    //});
                });
            }
        }
    }])
   .directive("acceptAddressValid", function () {
       return{
           require:"ngModel",
           link:function(scope,ele,attrs,c){
               scope.$watch(attrs.ngModel,function(n){
                   if(!n) return;
                   if(!scope.d)
                   {
                       c.$setValidity('validAcceptAddress',false);
                   }else{
                       c.$setValidity('validAcceptAddress',true);
                   }
               });

           }
       }
   })
    .directive("idCardValid", function () {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                scope.$watch(attrs.ngModel,function(n){
                    if(!n) return;
                    if(/^(\d{15}$|^\d{18}$|^\d{17}(\d|X|x))$/i.test(n))
                    {
                        c.$setValidity('validIdCard',true);
                    }else{
                        c.$setValidity('validIdCard',false);
                    }
                });

            }
        }
    })
    .directive("telephoneNumberValid", function () {//手机和座机
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                scope.$watch(attrs.ngModel,function(n){
                    if(!n) return;
                    if(!/(^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\d{8}$|(^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$))/i.test(n))
                    {
                        c.$setValidity('validTelephoneNumber',false);
                    }else{
                        c.$setValidity('validTelephoneNumber',true);
                    }
                });

            }
        }
    })
    .directive("phoneNumberValid", function () {//手机
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                scope.$watch(attrs.ngModel,function(n){
                    if(!n) return;
                    if(!/(^(13[0-9]|14[0-9]|15[0-9]|18[0-9])\d{8}$)/i.test(n))
                    {
                        c.$setValidity('validPhoneNumber',false);

                    }else{
                        c.$setValidity('validPhoneNumber',true);
                    }
                });

            }
        }
    })
    .directive("phoneUnique", function ($http,$timeout) {
            return{
                require:"ngModel",
                link:function(scope,ele,attrs,c){
                    var timeout;
                    scope.$watch(attrs.ngModel,function(n){
                        if(!n) return;
                        if(n.length!=11) return;
                        if(timeout) {$timeout.cancel(timeout);}
                        timeout=$timeout(function(){
                            $http({
                                method:"POST",
                                url:"/user/phoneUnique",
                                data:scope.user
                            }).success(function(data){
                                c.$setValidity('phoneUnique',data.success);
                            }).error(function(data){
                                c.$setValidity('phoneUnique',false);
                            });
                        },300);

                    });

                }
            }
        })
    .directive("ensureNameUnique", function ($http,$timeout) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                var timeout;
                scope.$watch(attrs.ngModel,function(n){

                    if(!n) return;
                    if(timeout) {$timeout.cancel(timeout);}
                    timeout=$timeout(function(){
                        $http({
                            method:"POST",
                            url:"/user/exist_name",
                            data:scope.user
                        }).success(function(data){
                            c.$setValidity('unique',data.unique);
                        }).error(function(data){
                            c.$setValidity('unique',false);
                        });
                    },300);

                });

            }
        }
    })
    .directive("ensureNameUnique2", function ($http,$timeout) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                var timeout;
                scope.$watch(attrs.ngModel,function(n){
                    if(!n) return;
                    if(timeout) {$timeout.cancel(timeout);}
                    timeout=$timeout(function(){
                        $http({
                            method:"POST",
                            url:"/user/exist_name2",
                            data:scope.user
                        }).success(function(data){
                            c.$setValidity('unique',data.unique);
                        }).error(function(data){
                            c.$setValidity('unique',false);
                        });
                    },300);

                });

            }
        }
    })
    .directive("ensureEmailUnique", function ($http,$timeout) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                var timeout;
                scope.$watch(attrs.ngModel,function(n){
//                         console.log(scope.ngModel);
                    if(!n) return;
                    if(timeout) $timeout.cancel(timeout);
                    timeout=$timeout(function(){
                        //var data={}
                        //data[attrs.ngModel]=attrs.ensureEmailUnique;
                        //console.log("key:"+attrs.ngModel)
                        //console.log("value:"+attrs.ensureUnique)
                        $http({
                            method:"POST",
                            url:"/user/exist_email",
                            data:scope.user
                        }).success(function(data){
                            console.log(JSON.stringify(data));
                            c.$setValidity('unique',data.unique);
                        }).error(function(data){
                            c.$setValidity('unique',false);
                        });
                    },300);

                });

            }
        }
    })
    .directive("ensureEmailUnique2", function ($http,$timeout) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                //var timeout;
                scope.$watch(attrs.ngModel,function(n){
//                         console.log(scope.ngModel);
                    if(!n) return;

                    //if(timeout) $timeout.cancel(timeout);
                    //timeout=$timeout(function(){
                        //var data={}
                        //data[attrs.ngModel]=attrs.ensureEmailUnique;
                        //console.log("key:"+attrs.ngModel)
                        //console.log("value:"+attrs.ensureUnique)
                        $http({
                            method:"POST",
                            url:"/user/exist_email2",
                            data:scope.user
                        }).success(function(data){
                            c.$setValidity('unique',data.unique);
                        }).error(function(data){
                            c.$setValidity('unique',false);
                        });
                    //},300);

                });

            }
        }
    })
    .directive("ensurePhoneUnique", function ($http,$timeout) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                var timeout;
                scope.$watch("user.phone",function(n){
                    if(!n) return;
                    if(n.length!==11) {c.$setValidity('unique',true);return;}
                    if(timeout) $timeout.cancel(timeout);
                    timeout=$timeout(function(){
                        $http({
                            method:"POST",
                            url:"/user/exist_phone",
                            data:scope.user
                        }).success(function(data){
                            c.$setValidity('unique',data.unique);
                        }).error(function(data){
                            c.$setValidity('unique',false);
                        });
                    },300);

                });

            }
        }
    })
    .directive("ensureValidateCode", function ($http,$timeout) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                var timeout;
                scope.$watch(attrs.ngModel,function(newVal){
                    if(!newVal) return;
                    c.$setValidity('finishValid',false);
                    if(timeout) $timeout.cancel(timeout);
                    timeout=$timeout(function(){
                        var data={};
                        data["email"]=scope.email;
                        data["validateCode"]=attrs.ensureValidateCode;
//                        data["validateCode"]=scope.validateCode;
                        $http({
                            method:"POST",
                            url:"/user/email/validate",
                            data:data
                        }).success(function(data){
                            c.$setValidity('codeValid',data.codeValid);
                            c.$setValidity('finishValid',true);
                        }).error(function(data){
                            c.$setValidity('codeValid',false);
                            c.$setValidity('finishValid',false);
                        });
                    },300);

                });

            }
        }
    })
    .directive("ensureValidateCode2", function ($http) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                scope.$watch(attrs.ngModel,function(newVal){
                    if(!newVal) return;
                    if(scope.user.validateCode===scope.emailUser.validateCode){
                        c.$setValidity('codeValid',true);
                    }else{
                        c.$setValidity('codeValid',false);
                    }
                });
            }
        }
    })
    .directive("ensurePhoneValidateCode", function ($http,$timeout) {
       return{
           require:"ngModel",
           link:function(scope,ele,attrs,c){
               var timeout;
               scope.$watch(attrs.ngModel,function(newVal){
                   if(!newVal) return;
                   c.$setValidity('finishValid',false);
                   if(timeout) $timeout.cancel(timeout);
                   timeout=$timeout(function(){
                       //var data={};
                       //data["phone"]=scope.phone;
                       //data["validateCode"]=attrs.ensurePhoneValidateCode;
//                        data["validateCode"]=scope.validateCode;
                       $http({
                           method:"POST",
                           url:"/user/phone/validate",
                           data:scope.user
                       }).success(function(data){
                           c.$setValidity('codeValid',data.codeValid);
                           c.$setValidity('finishValid',true);
                       }).error(function(data){
                           c.$setValidity('codeValid',false);
                           c.$setValidity('finishValid',false);
                       });
                   },300);

               });

           }
       }
   })
    .directive("ensurePictureValidateCode", function ($http,$timeout) {
        return{
            require:"ngModel",
            link:function(scope,ele,attrs,c){
                //var timeout;
                scope.$watch(attrs.ngModel,function(n){
                    if(!n) return;
                    $http({
                        method:"POST",
                        url:"/user/identify_image/match",
                        data:{success:false,message:attrs.ensurePictureValidateCode}
                    }).success(function(data){
                        c.$setValidity('match',data.success);
                    }).error(function(data){
                        c.$setValidity('match',false);
                    });
                    //},300);

                });

            }
        }
    })
    .directive("validUpperPhone", function ($http,$timeout) {
            return{
                require:"ngModel",
                link:function(scope,ele,attrs,c){
                    var timeout;
                    scope.$watch(attrs.ngModel,function(newVal){
                        c.$setValidity('notExists',true);
                        c.$setValidity('marketFull',true);
                        c.$setValidity('notMember',true);
                        if(!newVal) return;
                        if(newVal.length!=11) return;
                        if(timeout) $timeout.cancel(timeout);
                        timeout=$timeout(function(){
                            //var data={};
                            //data["email"]=scope.email;
                            //data["validateCode"]=attrs.validPhone;
//                        data["validateCode"]=scope.validateCode;
                            $http({
                                method:"POST",
                                url:"/user/directUpperUser/phoneValid",
                                data:scope.user
                            }).success(function(data){
                                if(!data.success){
                                    if(data.message==="not_exists"){
                                        c.$setValidity('notExists',false);
                                    }
                                    if(data.message==="not_member"){
                                        c.$setValidity('notMember',false);
                                    }
                                    if(data.message==="market_full"){
                                        c.$setValidity('marketFull',false);
                                    }
                                }
                            }).error(function(data){
                                c.$setValidity('upperPhoneValid',false);
                            });
                        },300);

                    });

                }
            }
        })
    .constant('pw_min',6)
    .controller("formController", ["$scope","$http","$timeout","pw_min","$location",function ($scope,$http,$timeout,pw_min) {

        $scope.user={};
        $scope.pw_min=pw_min;
        $scope.$watch('user.password', function (newVal, oldVal, scope) {
            if (newVal && newVal !== oldVal && newVal.length >= pw_min) {
                $scope.password=newVal;
                if (newVal.length >=pw_min && newVal.length < (pw_min+3)) {
                    $scope.passwordStrength = "弱";
                    $scope.cls1 = "pw_weak";
                    $scope.cls2 = "pw_un_reach";
                    $scope.cls3 = "pw_un_reach";
                }
                else if (newVal.length >= pw_min+3 && newVal.length < pw_min+6) {
                    $scope.passwordStrength = "中";
                    $scope.cls1 = "pw_mid";
                    $scope.cls2 = "pw_mid";
                    $scope.cls3 = "pw_un_reach";
                }
                else {
                    $scope.passwordStrength = "强";
                    $scope.cls1 = "pw_strong";
                    $scope.cls2 = "pw_strong";
                    $scope.cls3 = "pw_strong";
                }
            }
        });

        $scope.getValidCode = function (type){
            $scope.sent=false;
            $scope.sending=true;
            var requestUrl;
            if(type==="email"){
                requestUrl="/user/validate_code/email?email="+$scope.user.email
            }else{
                requestUrl="/user/validate_code/phone?phone="+$scope.user.phone
            }
            $http({
                method:"POST",
                url:requestUrl
            }).success(function(data){
                $scope.user.id=data.id;
                if(type==="email"){
                    $scope.sending=false;
                    $scope.sent=true;
                    $scope.emailUser=data;
                }
                if(type==="phone"){
                    var seconds=25;
                    var test=true;
                    console.log(data.message);
                    if(data.message!=="100"&&!test){
                        $scope.sending=true;
                        $scope.sent=false;
                    }else{
                        $scope.sending=false;
                        $scope.sent=true;
                        $scope.phoneUser=data;
                        var countDown=function(){
                            $timeout(function(){
                                seconds--;
                                $scope.seconds=seconds;
                                if(seconds==0) $timeout.cancel();
                                else countDown();
                            },1000);
                        };
                        countDown();
                    }
                }
                $scope.validateCode=""
            }).error(function(data){
                $scope.sending=true;
                $scope.sent=false;
                //$scope.message="服务器的错误导致邮件发送失败";
            });
        }
        $scope.reloadImg=function (){

            document.getElementById("createCheckCode").src=document.getElementById("createCheckCode").src + "?nocache="+new Date().getTime();
            $scope.checkCode="";
        }
    }])

