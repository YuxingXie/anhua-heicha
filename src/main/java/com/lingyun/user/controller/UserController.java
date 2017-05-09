package com.lingyun.user.controller;

import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.code.CardSortEnum;
import com.lingyun.common.code.WrongCodeEnum;
import com.lingyun.common.constant.Constant;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.*;
import com.lingyun.common.web.CookieTool;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IAlipayTransService;
import com.lingyun.mall.service.IProductSeriesService;
import com.lingyun.mall.service.impl.UserService;
import com.lingyun.support.callBack.CallBackInterface;
import com.lingyun.support.callBack.impl.Callback_Zhizihua;
import com.lingyun.support.vo.Message;
import com.lingyun.support.vo.NotifySearch;
import com.lingyun.common.support.Pair;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.util.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController extends BaseRestSpringController {
    private static Logger logger = LogManager.getLogger();
    protected static final String DEFAULT_SORT_COLUMNS = null;
    protected static final String REDIRECT_ACTION = "";

    @Resource private IProductSeriesService productSeriesService;
    @Resource(name = "userService")
    UserService userService;
    @Resource private IAlipayTransService alipayTransService;
    @InitBinder("productSeries")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @RequestMapping(value="/logout")
    public ResponseEntity<Message> logout(HttpServletRequest request,HttpServletResponse response,HttpSession session) {
        Message message=new Message();
        session.setAttribute(Constant.LOGIN_USER,null);
        session.removeAttribute(Constant.LOGIN_USER);
        CookieTool.removeCookie(request, response, "loginStr");
        CookieTool.removeCookie(request, response, "password");
        message.setSuccess(true);
        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }

    @RequestMapping(value = "/directUpperUser/phoneValid", method = RequestMethod.POST)
    public ResponseEntity<Message> phoneValid(@RequestBody User user) {
        Assert.notNull(user);
//        Assert.notNull(user.getPhone());
        Assert.notNull(user.getDirectUpperUser());
        Assert.notNull(user.getDirectUpperUser().getPhone());
        String upperPhone=user.getDirectUpperUser().getPhone();

        Message message=userService.isValidUpper(upperPhone);

        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value = "/is_authenticated", method = RequestMethod.GET)
    public ResponseEntity<Message> isAuthenticated(HttpSession session) {
        Message message=new Message();
        message.setSuccess(session.getAttribute(Constant.LOGIN_USER)==null);
        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value = "/session", method = RequestMethod.GET)
    public ResponseEntity<Message> session(HttpSession session) {
        Message message=new Message();
        User user=getLoginUser(session);
        message.setSuccess(user!=null);
        if (user!=null&&user.getId()!=null){
            user=userService.findById(user.getId());//to refresh user information
            session.setAttribute(Constant.LOGIN_USER,user);
            Map<String,Object> respData=new HashMap<String, Object>();
            Map<String,Object> sessionData=new HashMap<String, Object>();
            sessionData.put("loginUser", user);
            respData.put("session",sessionData);
            List<User> lowerUsers=userService.findLowerOrUpperUsers(user, 9);
            respData.put("lowerUsers",lowerUsers);
            message.setData(respData);
        }

        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Message> login(@RequestBody User form, ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findByEmailOrPhone(form.getLoginStr());
        Message message=new Message();

        if (user==null){
            message.setMessage("用户不存在");
           message.setSuccess(false);
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }
        //form.password可能是原始密码或者经过一次MD5加密，也可能是两次md5加密
        if (form.getPassword().equalsIgnoreCase(user.getPassword())
                ||form.getPassword().equalsIgnoreCase(MD5.convert(user.getPassword()))
                ||MD5.convert(form.getPassword()).equalsIgnoreCase(user.getPassword())){


            Date now =new Date();
            user.setLastActivateTime(now);
            userService.update(user);
            return doLogin(form, session, request, response, user,message);
        }else {
            message.setMessage("用户名/密码错误!");
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }
    }
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<Message> update(@RequestBody User user,HttpSession session, HttpServletRequest request, HttpServletResponse response) {

        Message message=new Message();
        Assert.isTrue(StringUtils.isNotBlank(user.getId()));
        userService.update(user);
        List<Account> accounts=ServiceManager.accountService.findAccountsByUser(user);
        if (StringUtils.isNotBlank(user.getEmail())){
            if (accounts==null||accounts.size()==0){
                Account account = instanceAlipayAccount(user);
                ServiceManager.accountService.insert(account);
            }else {
                boolean alipayLoginNameUpdated=false;
                for (Account account:accounts){
                    if (account.getCardSort().equals( CardSortEnum.ALIPAY.toCode())){
                        if(account.getAccountLoginName()==null){
                            account.setAccountLoginName(user.getEmail());
                        }else{
                            if (!account.getAccountLoginName().equals(user.getEmail())){
                                account.setAccountLoginName(user.getEmail());
                                ServiceManager.accountService.update(account);
                            }
                        }
                        alipayLoginNameUpdated=true;
                        break;
                    }
                }
                if (!alipayLoginNameUpdated){
                    Account account = instanceAlipayAccount(user);
                    ServiceManager.accountService.insert(account);
                }

            }

        }
        Map<String,Object> respData=new HashMap<String, Object>();
        Map<String,Object> sessionData=new HashMap<String, Object>();
        sessionData.put("loginUser", user);
        respData.put("session",sessionData);
        List<User> lowerUsers=userService.findLowerOrUpperUsers(user, 9);
        respData.put("lowerUsers", lowerUsers);
        message.setData(respData);
        message.setMessage("个人信息修改成功!");
        message.setSuccess(true);
        session.setAttribute(Constant.LOGIN_USER, user);
        return new ResponseEntity<Message>(message,HttpStatus.OK);

    }

    private Account instanceAlipayAccount(User user) {
        Account account=new Account();
        account.setAccountLoginName(user.getEmail());
        account.setUser(user);
        account.setCardSort(CardSortEnum.ALIPAY.toCode());
        return account;
    }

    private ResponseEntity<Message> doLogin(User form, HttpSession session, HttpServletRequest request, HttpServletResponse response, User user,Message message) {
        int loginMaxAge = 30 * 24 * 60 * 60;   //定义账户密码的生命周期，这里是一个月。单位为秒
        if (form==null){
            //不是从表单提交登录，可能是刚注册的用户自动登录，do nothing
        }else{
            if (form.getRemember()!=null &&form.getRemember()) {
                CookieTool.addCookie(request, response, "loginStr", form.getLoginStr(), loginMaxAge);
                CookieTool.addCookie(request, response, "password", form.getPassword(), loginMaxAge);
            } else {
                CookieTool.removeCookie(request, response, "loginStr");
                CookieTool.removeCookie(request, response, "password");
            }
            message.setMessage("登录成功!");
        }
        Map<String,Object> respData=new HashMap<String, Object>();
        Map<String,Object> sessionData=new HashMap<String, Object>();
        sessionData.put("loginUser", user);
        respData.put("session",sessionData);
        List<User> lowerUsers=userService.findLowerOrUpperUsers(user, 9);
        respData.put("lowerUsers", lowerUsers);
        message.setData(respData);
        message.setSuccess(true);
        session.setAttribute("loginUser", user);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
    @RequestMapping(value="/register",method = RequestMethod.POST)
    public ResponseEntity<Message> register(@RequestBody User user,HttpSession session,HttpServletRequest request,HttpServletResponse response) {
        Message message=new Message();

        try {
//            if (true) throw new NullPointerException();
            Assert.notNull(user);
            Assert.notNull(user.getPhone());
            Assert.notNull(user.getPassword());
            Assert.notNull(user.getDirectUpperUser());
            Assert.notNull(user.getDirectUpperUser().getPhone());
            String phone=user.getPhone();
            String upperPhone=user.getDirectUpperUser().getPhone();
            if (phone.equals(upperPhone)){
                message.setSuccess(false);
                message.setMessage("注册人与接点人手机号不能相同");
                return new ResponseEntity<Message>(message,HttpStatus.OK);
            }
            message=userService.isValidUpper(upperPhone);
            if (!message.isSuccess()) return new ResponseEntity<Message>(message, HttpStatus.OK);
            User upperUser=(User)message.getData();
            User find=userService.findByPhone(user.getPhone());
            if (find!=null){
                message.setSuccess(false);
                message.setMessage("注册失败，号码已经是系统注册用户！");
            }else{
                user.setPassword(MD5.convert(user.getPassword()));
                Pair<User> brotherUsers=upperUser.getDirectLowerUsers();
                String marketMessage=null;
                if (brotherUsers!=null){
                    User brother=brotherUsers.getFirst();
                    int brotherMarket=brother.getMarket();
                    int market=user.getMarket();
                    if (brotherMarket==0||(brotherMarket!=1&&brotherMarket!=2)){
                        if (market==0){
                            brother.setMarket(1);
                            user.setMarket(2);
                        }else if (market==1){
                            brother.setMarket(2);
                        }else if (market==2){
                            brother.setMarket(1);
                        }else{
                            message.setSuccess(false);
                            message.setMessage("错误的市场编号："+market);
                            return new ResponseEntity<Message>(message, HttpStatus.OK);
                        }
                        userService.update(brother);
                    }else if(brotherMarket==1){
                        if (market==1){
                            user.setMarket(2);
                            marketMessage="接点人一市场已有业务员，您被分配到二市场。";

                        }
                    }else {// (brotherMarket==2)
                        if (market==2){
                            user.setMarket(1);
                            marketMessage="接点人二市场已有业务员，您被分配到一市场。";
                        }
                    }
                }
                userService.insert(user);
                String inviteUserPath=upperUser.getMembershipPath();
                if (StringUtils.isBlank(inviteUserPath)){
                    inviteUserPath="/"+upperUser.getId();
                }
                user.setMembershipPath(inviteUserPath+"/"+user.getId());
                userService.update(user);
                message.setSuccess(true);
                String name=upperUser.getPhone();
                message.setMessage("恭喜您注册成为临时会员，您的接点人是 " + name+"，"+marketMessage);
                return doLogin(null,session,request,response,user,message);
            }
            return new ResponseEntity<Message>(message, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            message.setSuccess(false);
            message.setMessage("服务器异常："+e.getClass().getName());
            return new ResponseEntity<Message>(message, HttpStatus.OK);

        }
    }

    @RequestMapping(value="/phoneUnique",method = RequestMethod.POST)
    public ResponseEntity<Message> phoneUnique(@RequestBody User user,HttpSession session,HttpServletRequest request,HttpServletResponse response) {
        Assert.notNull(user);
        Assert.notNull(user.getPhone());
        Message message=new Message();
        String phone=user.getPhone();
        User userInDb=userService.findByPhone(phone);
        if(userInDb==null)
            message.setSuccess(true);

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }

    @RequestMapping(value="/invite",method = RequestMethod.POST)
    public ResponseEntity<Message> invite(@RequestBody AuthorizeInfo authorizeInfo,HttpSession session) {
        User user=getLoginUser(session);
        Message message=new Message();
        if (user==null){
            message.setSuccess(false);
            message.setMessage("请先登录!");
        }else {
            if(user.getPhone()!=null &&user.getPhone().equals(authorizeInfo.getPhone())){
                message.setSuccess(false);
                message.setMessage("不能邀请自己!");
            }else{
                User userInDB=ServiceManager.userService.findByEmailOrPhone(authorizeInfo.getPhone());
                if(userInDB!=null){
                    message.setSuccess(false);
                    message.setMessage("该用户已经是系统会员，不能邀请!");
                }else{
                    authorizeInfo.setUser(user);
                    if (authorizeInfo.getInviteCode()==null ||authorizeInfo.getInviteCode().trim().equals("")){
                        String inviteCode=StringUtils.generateRandomString(6);
                        authorizeInfo.setInviteCode(inviteCode);
                    }
                    ServiceManager.authorizeInfoService.insert(authorizeInfo);
                    message.setSuccess(true);
//                    message.setMessage("邀请码为 "+account.getInviteCode().toUpperCase()+" (不区分大小写)，邀请号码为  "+account.getPhone());
                    message.setMessage("邀请码为 "+authorizeInfo.getInviteCode().toUpperCase()+" ，邀请的手机号码为  "+authorizeInfo.getPhone()+",自动短信通知功能正在开发中，请用户自行通知。");
                }


            }

        }

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
    @RequestMapping(value="/bindingAccount",method = RequestMethod.POST)
    public ResponseEntity<Message> bindingAccount(@RequestBody Account account,HttpSession session) {
        User user=getLoginUser(session);
        Message message=new Message();
        if (user==null){
            message.setSuccess(false);
            message.setMessage("请先登录!");
        }else {
            if(account==null||(account.getCardSort()==null&&account.getAccountLoginName()==null)){
                message.setSuccess(false);
                message.setMessage("请填写账号信息!");
            }
            else{
                if (account.getCardSort()==null){
                    message.setSuccess(false);
                    message.setMessage("请选择账号类型!");
                }else if(account.getAccountLoginName()==null){
                    message.setSuccess(false);
                    message.setMessage("请填写账号!");
                }else{
                    account.setUser(user);
                    ServiceManager.accountService.insert(account);
                    message.setSuccess(true);
                    message.setMessage("账号绑定成功!");
                    message.setLocationPath("/accounts");
                }
            }
        }

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
    @RequestMapping(value="/accounts")
    public ResponseEntity<Message> accounts(HttpSession session) {
        User user=getLoginUser(session);
        Message message=new Message();
        if (user==null){
            message.setSuccess(false);
            message.setMessage("请先登录!");
        }else {
            List<Account> accounts=ServiceManager.accountService.findAccountsByUser(user);
            message.setSuccess(true);
            message.setData(accounts);
        }

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
    @RequestMapping(value="/index/json")
    public ResponseEntity< Map<String,Object>> index(HttpSession session) {
        Map<String,Object> map=new HashMap<String, Object>();
        long todoOrders=ServiceManager.orderService.findUnHandlerOrdersCount();
        long returnOrders=ServiceManager.orderService.findReturnExchangeOrdersCount();
        DBObject dbObject=new BasicDBObject();
        Administrator administrator=getLoginAdministrator(session);
        dbObject.put("toAdministrator",new DBRef("user",new ObjectId(administrator.getId())));
        long notifies=ServiceManager.notifyService.count(dbObject);

        map.put("todoOrders",todoOrders);
        map.put("returnOrders",returnOrders);
        map.put("notifies",notifies);

        return new ResponseEntity<Map<String, Object>>(map,HttpStatus.OK);
    }
//
@RequestMapping(value="/friendship_mall_shopping")
public ResponseEntity< Map<String,Object>> getFriendshipMallShoppingData(HttpSession session) {
    User user=getLoginUser(session);
    if (user==null) return null;

    Map<String,Object> map=new HashMap<String, Object>();
    List<UserPoints> membershipPointList=ServiceManager.userService.findUserPointsByUser(user.getId());
    map.put("points",membershipPointList);
    DBObject dbObject=new BasicDBObject();
    dbObject.put("valid",true);
    List<FriendshipMall> friendshipMallList= ServiceManager.friendshipMallService.findAll(dbObject);
    map.put("malls",friendshipMallList);
    return new ResponseEntity<Map<String, Object>>(map,HttpStatus.OK);
}

    @RequestMapping(value="/friendship_exchange")
    public ResponseEntity<Message> friendshipExchange(@RequestBody FriendshipExchange friendshipExchange,HttpServletRequest request,HttpSession session) throws IOException {
        Map<String,String[]> params=new HashMap<String, String[]>();
        User user=getLoginUser(session);
        params.put("loginName",new String[]{user.getPhone()});
        params.put("password",new String[]{user.getPassword()});
        String nickName=user.getName()!=null?user.getName():(user.getRealName()!=null?user.getRealName():(user.getPhone()!=null?user.getPhone():(user.getShowName()!=null?user.getShowName():("用户"+user.getId()))));
        params.put("nickName",new String[]{nickName});
        params.put("mobile",new String[]{user.getPhone()});
        params.put("email",new String[]{user.getEmail()});
        params.put("userId",new String[]{user.getId()});
        params.put("pointCount",new String[]{Integer.toString(friendshipExchange.getPointCount())});
        String ret=OuterRequestUtil.sendPost(friendshipExchange.getMall().getExchangeUrl(),params);
        CallBackInterface callBack=new Callback_Zhizihua(ret);
        if (callBack.isSuccess()){
            friendshipExchange.setReturnValue(callBack.getReturnValueMap());
            ServiceManager.friendshipExchangeService.insert(friendshipExchange);
            UserPoints userPoints=new UserPoints();
            userPoints.setType(-1);
            userPoints.setUser(user);
            userPoints.setDate(new Date());
            String virtualMoneyName=friendshipExchange.getMall().getVirtualMoneyName()==null?"虚拟货币":friendshipExchange.getMall().getVirtualMoneyName();
            userPoints.setNote("您使用 "+friendshipExchange.getPointCount()+" 红包兑换"+friendshipExchange.getMall().getName()+"的"+virtualMoneyName);
            ServiceManager.userPointsService.insert(userPoints);
        }
        return new ResponseEntity<Message>(callBack.getMessage(),HttpStatus.OK);
    }
    private List<ProductSeriesPicture> getProductSeriesPicturesAndSaveFiles(MultipartFile[] files, String dirStr, ServletContext context) throws IOException {
        //循环获取file数组中得文件
//        Map<String,ProductSeriesPicture> originalPrefixesMap= new HashMap<String, ProductSeriesPicture>();
        List<ProductSeriesPicture> productSeriesPictures=new ArrayList<ProductSeriesPicture>();
        for(int i = 0;i<files.length;i++){
            MultipartFile file = files[i];
            //保存文件到数据库
            String pictureId=productSeriesService.saveFile(file.getOriginalFilename(), file.getBytes());
            logger.info("产品原图保存："+pictureId);
            String originalFilename=file.getOriginalFilename();
            String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));//后缀名如.jpg

            ProductSeriesPicture productSeriesPicture=new ProductSeriesPicture();
            productSeriesPicture.setBigPicture("pic/" + pictureId);
            String bigPictureStr=dirStr+"/"+pictureId+suffix;
            File bigPictureFile=new ServletContextResource(context,bigPictureStr).getFile();
            file.transferTo(bigPictureFile);

            //生成320*180的中等大小图,无论中图小图都保持16:9的比例
            String mdTempPictureStr=dirStr+"/"+pictureId+".md"+suffix;
            File mdTempPictureFile=new ServletContextResource(context,mdTempPictureStr).getFile();
            IconCompressUtil.compressPic(bigPictureFile,mdTempPictureFile , 320, 180, false);
            String mdPictureId=productSeriesService.saveFile(mdTempPictureFile.getName(), mdTempPictureFile);
            logger.info("产品标准图保存："+mdPictureId);
            String mdPictureStr = dirStr + "/" + mdPictureId+suffix;
            File mdPictureFile=new ServletContextResource(context, mdPictureStr).getFile();
            mdTempPictureFile.renameTo(mdPictureFile);
            productSeriesPicture.setPicture("pic/" + mdPictureId);
            //生成小图标75*75像素小图标
            String smTempPictureStr=dirStr+"/"+pictureId+".sm"+suffix;
            File smTempPictureFile=new ServletContextResource(context,smTempPictureStr).getFile();
            IconCompressUtil.compressPic(bigPictureFile,smTempPictureFile , 64, 36, false);
            String smPictureId=productSeriesService.saveFile(smTempPictureFile.getName(), smTempPictureFile);
            logger.info("产品小图保存："+smPictureId);
            String smPictureStr = dirStr + "/" + smPictureId+suffix;
            File smPictureFile=new ServletContextResource(context, smPictureStr).getFile();
            smTempPictureFile.renameTo(smPictureFile);
            productSeriesPicture.setIconPicture("pic/" + smPictureId);
            productSeriesPictures.add(productSeriesPicture);

        }
        return productSeriesPictures;
    }

    private void mkDirs(ServletContextResource dirResource) throws IOException {
        File dirFile=dirResource.getFile();
        if (!dirFile.exists() || !dirFile.isDirectory()){
            dirFile.mkdirs();
        }
    }


    @RequestMapping(value="/lower_users")
    public ResponseEntity<List<User>> lowerUsers(HttpSession session){
        List<User> list=userService.findLowerOrUpperUsers(getLoginUser(session), 9);
        return new ResponseEntity<List<User>>(list, HttpStatus.OK);
    }

    @RequestMapping(value="/points")
    public ResponseEntity<Message> membershipPointOfUser(HttpSession session) throws ParseException {
        Message message=new Message();
        User user=getLoginUser(session);
        if (user==null){
            message.setSuccess(false);
            message.setMessage("请先登录!");
        }else{
            List<UserPoints> membershipPointList=ServiceManager.userService.findUserPointsByUser(user.getId());
            message.setData(membershipPointList);
            message.setSuccess(true);
        }

        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value="/measures")
    public ResponseEntity<Message> userMeasures(HttpSession session) throws ParseException {
        Message message=new Message();
        User user=getLoginUser(session);
        if (user==null){
            message.setSuccess(false);
            message.setMessage("请先登录!");
        }else{
            List<UserMeasure> userMeasures=ServiceManager.userMeasureService.findByUser(user.getId());
            message.setData(userMeasures);
            message.setSuccess(true);
        }

        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value="/trans_record")
    public ResponseEntity<Message> transRecord(HttpSession session) throws ParseException {
        Message message=new Message();
        User user=getLoginUser(session);
        double  transesSubmittedTotalFee=alipayTransService.findSubmittedTransTotalFeeByUser(user);

        message.setData(transesSubmittedTotalFee);
        message.setSuccess(true);
        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value="/notices")
     public ResponseEntity<Message> notices(HttpSession session) throws ParseException {
        Message message=new Message();
        User user=getLoginUser(session);
        if (user==null){
            message.setSuccess(false);
            message.setMessage("请先登录!");
            message.setWrongCode(WrongCodeEnum.NOT_LOGIN.toCode());
        }else{
            BasicDBList dbList=new BasicDBList();
            dbList.add(new BasicDBObject("toUser",new DBRef("mallUser",user.getId())));
            dbList.add(new BasicDBObject("fromAdministrator",new BasicDBObject("$exists",true)));
            DBObject dbObject=new BasicDBObject("$or",dbList);
            Date afterWhen=null;
            if (user.getRegisterTime()==null){
                if (user.getBecomeMemberDate()!=null){
                    afterWhen=user.getBecomeMemberDate();
                }
            }else{
                if (user.getBecomeMemberDate()==null){
                    afterWhen=user.getRegisterTime();
                }else{
                    afterWhen=user.getBecomeMemberDate();
                }
            }
            if (afterWhen!=null){
                dbObject.put("date", new BasicDBObject("$gte", afterWhen));
            }else{
                message.setSuccess(true);
                message.setData(null);
                return new ResponseEntity<Message>(message,HttpStatus.OK);
            }
            Query query=new BasicQuery(dbObject);
            query.with(new Sort(Sort.Direction.DESC,"date"));
            List<Notify> notifies=ServiceManager.notifyService.findAll(query);
            message.setData(notifies);
            message.setSuccess(true);
        }

        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }

    @RequestMapping(value="/product_brochures/{id}")
    public String make_product_brochures(@PathVariable String id,ModelMap map){
        map.addAttribute("id",id);
        return "admin/product_series/brochures";
    }
    @RequestMapping(value="/do/adjust_price")
    public String do_adjust_price(@ModelAttribute ProductSeriesPrice productSeriesPrice,String productSeriesId,ModelMap map){
        ProductSeries productSeries=productSeriesService.findById(productSeriesId);
        List<ProductSeriesPrice> prices=productSeries.getProductSeriesPrices();
        ProductSeriesPrice lastPrice=prices.get(prices.size()-1);
        Assert.isTrue(lastPrice.getEndDate()==null);
        Date now=new Date();
        lastPrice.setEndDate(now);
        productSeriesPrice.setBeginDate(now);
        productSeriesPrice.setAdjustDate(now);
        prices.add(productSeriesPrice);
        ProductSeries update=new ProductSeries();
        update.setId(productSeriesId);
        update.setProductSeriesPrices(prices);
        productSeriesService.update(update);
        return "redirect:/admin/product_series/list";
    }
    @RequestMapping(value="/adjust_store/{id}")
    public String adjust_store(@PathVariable String id,ModelMap map){
        ProductSeries productSeries=productSeriesService.findProductSeriesById(id);
        map.addAttribute("productSeries",productSeries);
        return "admin/product_series/adjust_store";
    }
    @RequestMapping(value="/adjust_sort/{id}")
    public String adjust_sort(@PathVariable String id,ModelMap map){
        ProductSeries productSeries=productSeriesService.findProductSeriesById(id);
        map.addAttribute("productSeries",productSeries);
        return "admin/product_series/adjust_sort";
    }
    @RequestMapping(value="/do/adjust_sort")
    public String do_adjust_sort(String subCategoryId,String productSeriesId,ModelMap map,HttpSession session){
        Date now=new Date();
        ProductSeries update=new ProductSeries();
        update.setId(productSeriesId);
        ProductSubCategory subCategory=new ProductSubCategory();
        subCategory.setId(subCategoryId);
        update.setProductSubCategory(subCategory);
        productSeriesService.update(update);
        return "redirect:/admin/product_series/list";
    }
    @RequestMapping(value="/do/adjust_store")
    public String do_adjust_store(@ModelAttribute ProductStoreInAndOut inAndOut,String productSeriesId,Integer warningAmount,ModelMap map,HttpSession session){
        ProductSeries productSeries=productSeriesService.findById(productSeriesId);
        ProductStore store=productSeries.getProductStore();
        List<ProductStoreInAndOut> inAndOuts=store.getInAndOutList();
        if (warningAmount!=null &&warningAmount.intValue()!=0) store.setWarningAmount(warningAmount);
        Date now=new Date();
        inAndOut.setDate(now);
        inAndOut.setOperator(getLoginAdministrator(session));
        inAndOuts.add(inAndOut);
        ProductSeries update=new ProductSeries();
        update.setId(productSeriesId);
        update.setProductStore(store);
        productSeriesService.update(update);
        return "redirect:/admin/product_series/list";
    }



    @RequestMapping(value="/read-notice")
    public ResponseEntity<Message> readNotice(ModelMap map, @RequestBody Notify notice){
        Message message=new Message();
        if (notice.getRead()==null||!notice.getRead()){
            notice.setRead(true);
            ServiceManager.notifyService.update(notice);
        }

        message.setSuccess(true);
        message.setMessage("保存成功!");
        message.setData(notice);
        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value="/notify/remove")
    public ResponseEntity<Map<String,Object>> removeProduct( @RequestBody Notify notify,HttpSession session){
        Map<String,Object> map=new HashMap<String,Object>();
        ServiceManager.notifyService.removeById(notify.getId());
        DBObject dbObject=new BasicDBObject();
        Administrator administrator=getLoginAdministrator(session);
        dbObject.put("fromAdministrator",new DBRef("user",new ObjectId(administrator.getId())));
        List<Notify> notifies=ServiceManager.notifyService.findAll(dbObject);
        map.put("list",notifies);
        return new ResponseEntity<Map<String,Object>>(map,HttpStatus.OK);
    }
    @RequestMapping(value="/product_series/remove")
    public ResponseEntity<Map<String,Object>> removeProduct( @RequestBody ProductSeries productSeries,HttpSession session){
        Map<String,Object> map=new HashMap<String,Object>();
        Message message=new Message();
        //查询是否生成订单
        long useInOrder=ServiceManager.orderService.findOrdersCountByProductSeries(productSeries);
        if (useInOrder==0){
            //是否有加入购物车
            List<User> users=ServiceManager.userService.findUsersByProductSeriesInCart(productSeries);
            List<Interest> interests=ServiceManager.interestService.findByProductSeries(productSeries);
            Date now=new Date();
            if (interests!=null&&interests.size()>0){
                for(Interest interest:interests){
                    User user=interest.getUser();
                    Notify notify=new Notify();
                    notify.setToUser(user);
                    notify.setContent("我们很遗憾的通知您，您关注的商品\"" + productSeries.getName() + "\"被系统删除，因此我们将此商品从您的关注列表中移除了。感谢您对大坝的支持！");
                    notify.setDate(now);
                    notify.setTitle("系统通知");
                    notify.setFromAdministrator(getLoginAdministrator(session));
                    ServiceManager.notifyService.insert(notify);
                    ServiceManager.interestService.removeById(interest.getId());
                }
            }
            if(users!=null&&users.size()>0){
                for (User user:users){
                    Assert.notNull(user.getCart());
                    Assert.notNull(user.getCart().getProductSelectedList());
                    List<ProductSelected> productSelectedList=user.getCart().getProductSelectedList();
                    List<ProductSelected> newProductSelectedList=new ArrayList<ProductSelected>();
                    for (ProductSelected productSelected:productSelectedList){
                        if (!productSelected.getProductSeries().getId().equalsIgnoreCase(productSeries.getId())){
                            newProductSelectedList.add(productSelected);
                        }
                    }
                    User updateUser=new User();
                    updateUser.setId(user.getId());
                    Cart cart=new Cart();
                    cart.setProductSelectedList(newProductSelectedList);
                    updateUser.setCart(cart);
                    ServiceManager.userService.update(updateUser);

                    Notify notify=new Notify();
                    notify.setToUser(user);
                    notify.setContent("我们很遗憾的通知您，您购物车中的商品\"" + productSeries.getName() + "\"被系统删除，因此我们将此商品从您的购物车移除了。感谢您对大坝的支持！");
                    notify.setDate(now);
                    notify.setTitle("系统通知");
                    notify.setFromAdministrator(getLoginAdministrator(session));
                    ServiceManager.notifyService.insert(notify);
                }
            }

            //删除关联的DBRef,包括productSubCategory,productCategory,productProperty,productPropertyValue,productEvaluate,salesCampaign,homePageBlock
            ServiceManager.productPropertyService.removeByProductSeries(productSeries);
            ServiceManager.productEvaluateService.removeByProductSeries(productSeries);
            ServiceManager.salesCampaignService.removeProductSeries(productSeries);
            ServiceManager.homePageBlockService.removeProductSeries(productSeries);
            ServiceManager.productSeriesService.removeProductSeriesAndPictures(productSeries);
            message.setSuccess(true);
            message.setMessage("删除成功!");
            List<ProductSeries> list=productSeriesService.findAll();
            map.put("list",list);
        }else{
            message.setSuccess(false);
            message.setMessage("已有订单使用该产品，删除失败!");
        }
        map.put("message",message);
        return new ResponseEntity<Map<String,Object>>(map,HttpStatus.OK);
    }
    @RequestMapping(value="/notifies/data")
    public ResponseEntity<List<Notify>> notifies(ModelMap map, @RequestBody NotifySearch notifySearch,HttpSession session){
        DBObject dbObject=new BasicDBObject();
        String fromTo=notifySearch.getFromToMe();
        String read=notifySearch.getRead();
        Administrator administrator=getLoginAdministrator(session);
        if (fromTo!=null&&fromTo.equals("toMe")){
            dbObject.put("toAdministrator",new DBRef("user",new ObjectId(administrator.getId())));
        }else if (fromTo!=null&&fromTo.equals("fromMe")){
            dbObject.put("fromAdministrator",new DBRef("user",new ObjectId(administrator.getId())));
        }
        if (read!=null&&read.equals("read")){
            dbObject.put("read",true);
        }else if (read!=null&&read.equals("unread")){
            BasicDBList dbList=new BasicDBList();
            dbList.add(new BasicDBObject("read",false));
            dbList.add(new BasicDBObject("read",new BasicDBObject("$exists",false)));
            dbObject.put("$or",dbList);
        }
        List<Notify> notifies=ServiceManager.notifyService.findAll(dbObject);
        return new ResponseEntity<List<Notify>>(notifies,HttpStatus.OK);
    }
    @RequestMapping(value="/first_member")
    public ResponseEntity<Message> first_member(HttpSession session,HttpServletRequest request,HttpServletResponse response){
        Message message=new Message();
        User user=userService.findFirstMember();
        if (user!=null){
            message.setSuccess(true);
            message.setData(user);
        }
        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value="/register_first_member",method = RequestMethod.POST)
    public ResponseEntity<Message> register_first_member(@RequestBody User user,HttpSession session,HttpServletRequest request,HttpServletResponse response) {


        Message message=new Message();
        try {
//            if (true) throw new NullPointerException();
            Assert.notNull(user);
            User firstMember=userService.findFirstMember();
            if (firstMember!=null){
                message.setSuccess(false);
                message.setMessage("非常抱歉，已有用户注册成为第一名会员，您可以重新进入注册页面重新注册！");
                return new ResponseEntity<Message>(message, HttpStatus.OK);
            }
            Assert.notNull(user.getPhone());
            Assert.notNull(user.getPassword());
            String phone=user.getPhone();
            User find=userService.findByPhone(phone);
            if (find!=null){
                message.setSuccess(false);
                message.setMessage("注册失败，号码已经是系统注册用户！");
                return new ResponseEntity<Message>(message, HttpStatus.OK);
            }
            user.setPassword(MD5.convert(user.getPassword()));
            user.setDirectSaleMember(true);
            user.setRegisterTime(new Date());
            userService.insert(user);
            user.setMembershipPath("/"+user.getId());
            userService.update(user);
            message.setSuccess(true);
            message.setMessage("第一名会员添加成功！");
//
            return doLogin(null,session,request,response,user,message);

        }catch (Exception e){
            e.printStackTrace();
            message.setSuccess(false);
            message.setMessage("服务器异常："+e.getClass().getName());
            return new ResponseEntity<Message>(message, HttpStatus.OK);

        }
    }
}
