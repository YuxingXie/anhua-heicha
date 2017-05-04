//1.初始化第一个用户（密码明文是：111111,假设id是user_id_0）：
db.mallUser.insert({ "name" : "谢宇星(测试用)", "password" : "96e79218965eb72c92a549dd5a330112", "registerTime" : new Date(), "phone" : "18670057061", "directSaleMember" : true, "activated" : true, "market" : 0, "becomeMemberDate" : new Date("2017-05-03 13:05"), "lastActivateTime" : new Date()})
//2.初始化商品信息(假设id为prod_id_1和prod_id_2)
db.productSeries.insert({"_class" : "com.lingyun.entity.ProductSeries", "name" : "手筑茯砖*6", "pictures" : [ { "bigPicture" : "statics/img/products/01.jpg" }, { "bigPicture" : "statics/img/products/02.jpg" } ], "productSeriesPrices" : [ { "price" : 4800, "beginDate" : new Date()} ] })
db.productSeries.insert({"_class" : "com.lingyun.entity.ProductSeries", "name" : "手筑茯砖*6", "pictures" : [ { "bigPicture" : "statics/img/products/01.jpg" }, { "bigPicture" : "statics/img/products/02.jpg" } ], "productSeriesPrices" : [ { "price" : 8800, "beginDate" : new Date()} ] })
//3.页面注册账号(phone:13000000000)，接点人手机号为第一个用户手机号，不购买产品，查询到第一个用户id(假设是“user_id000000001”)后执行插入订单操作：
db.order.insert({ "_class" : "com.lingyun.entity.Order", "productSelectedList" : [ { "amount" : 1, "productSeries" : DBRef("productSeries", ObjectId("590a91ce1f5d6e1c5b00e153")) } ], "orderDate" : ISODate("2017-05-03T08:22:25.446Z"), "orderSubmitInfo" : { "self" : false, "acceptAddress" :"广西贵港市港北区江北东路324号", "acceptPersonName" : "星11", "contactPhone" :"13000000005" }, "user" : DBRef("mallUser", ObjectId("590ae21789b6fb046030186a")),"payStatus" : "y", "payDate" : ISODate("2017-05-02T13:49:49.293Z") })
//4.更新用户(根据测试需要修改日期，根据订单金额设置cost属性值)
db.mallUser.update({"phone":"13000000005"},{"$set":{"cost":8800,"directSaleMember":true,"becomeMemberDate" : new Date("2017-05-03 13:34")}},false,true)
//5.添加更多用户重复3-4，注意选择的接点人接点人


//使用Date()可能有误，用ISODate方法update

db.mallUser.update({"phone":"13000000000"},{"$set":{"becomeMemberDate" : ISODate("2017-05-03 13:01")}},false,true)
db.mallUser.update({"phone":"13000000001"},{"$set":{"becomeMemberDate" : ISODate("2017-05-03 13:02")}},false,true)
db.mallUser.update({"phone":"13000000002"},{"$set":{"becomeMemberDate" : ISODate("2017-05-03 13:03")}},false,true)
db.mallUser.update({"phone":"13000000003"},{"$set":{"becomeMemberDate" : ISODate("2017-05-03 13:04")}},false,true)
db.mallUser.update({"phone":"13000000004"},{"$set":{"becomeMemberDate" : ISODate("2017-05-03 13:05")}},false,true)
db.mallUser.update({"phone":"13000000005"},{"$set":{"becomeMemberDate" : ISODate("2017-05-03 13:06")}},false,true)

