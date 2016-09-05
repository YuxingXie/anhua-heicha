package com.lingyun.user.controller;

import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.code.WrongCodeEnum;
import com.lingyun.common.constant.Constant;
import com.lingyun.common.helper.service.ProjectContext;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.IconCompressUtil;
import com.lingyun.common.util.MD5;
import com.lingyun.common.util.MongoDbUtil;
import com.lingyun.common.util.StringUtils;
import com.lingyun.common.web.CookieTool;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IProductSeriesService;
import com.lingyun.mall.service.impl.UserService;
import com.lingyun.support.vo.Message;
import com.lingyun.support.vo.NotifySearch;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.util.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
    @InitBinder("productSeries")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @RequestMapping(value="/")
    public String adminLogin() {
         return "redirect:/admin-login.jsp";
    }
    @RequestMapping(value="")
    public String index_() {
        return "redirect:/admin-login.jsp";
    }

    @RequestMapping(value="/logout")
    public String logout(HttpSession session) {
        session.setAttribute(Constant.LOGIN_ADMINISTRATOR,null);
        session.removeAttribute(Constant.LOGIN_ADMINISTRATOR);
        return "redirect:/admin-login.jsp";
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
        if (user!=null){
            Map<String,Object> respData=new HashMap<String, Object>();
            Map<String,Object> sessionData=new HashMap<String, Object>();
            sessionData.put("loginUser", user);
            respData.put("session",sessionData);
            List<User> lowerUsers=userService.findLowerUsers(user);
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
            User updateUser=new User();
            updateUser.setId(user.getId());
            Date now =new Date();
            updateUser.setLastActivateTime(now);
            userService.update(updateUser);
            user.setLastActivateTime(now);
            return doLogin(form, session, request, response, user,message);
        }else {
            message.setMessage("用户名/密码错误!");
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }
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
        List<User> lowerUsers=userService.findLowerUsers(user);
        respData.put("lowerUsers", lowerUsers);
        message.setData(respData);
        message.setSuccess(true);
        session.setAttribute("loginUser", user);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
    @RequestMapping(value="/register",method = RequestMethod.POST)
    public ResponseEntity<Message> register(@RequestBody User user,HttpSession session,HttpServletRequest request,HttpServletResponse response) {
//        System.out.println(user.getName());
        Assert.notNull(user);
        Assert.notNull(user.getPhone());
        Assert.notNull(user.getPassword());
        Assert.notNull(user.getRegisterInviteCode());
        User find=userService.findByEmailOrPhone(user.getPhone());
        Message message=new Message();
        if (find!=null){
            message.setSuccess(false);
            message.setMessage("注册失败，该号码已经是系统注册用户！");
        }else{
            User inviteUser=userService.findInviteUserByPhoneAndInviteCode(user.getPhone(),user.getRegisterInviteCode());
            if (inviteUser!=null){
                user.setPassword(MD5.convert(user.getPassword()));
                userService.insert(user);
                String inviteUserPath=inviteUser.getMembershipPath();
                if (StringUtils.isBlank(inviteUserPath)){
                    inviteUserPath="/"+inviteUser.getId();
                }
                user.setMembershipPath(inviteUserPath+"/"+user.getId());
                userService.update(user);
                message.setSuccess(true);
                String name=inviteUser.getName()!=null?inviteUser.getName():inviteUser.getPhone();
                message.setMessage("恭喜您注册成为临时会员，您的上级是 " + name);
                return doLogin(null,session,request,response,user,message);
            }else{
                message.setSuccess(false);
                message.setMessage("没有找到邀请码对应的发送人，请确认邀请码正确！");
            }
        }
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
//                    message.setMessage("邀请码为 "+authorizeInfo.getInviteCode().toUpperCase()+" (不区分大小写)，邀请号码为  "+authorizeInfo.getPhone());
                    message.setMessage("邀请码为 "+authorizeInfo.getInviteCode().toUpperCase()+" ，邀请的手机号码为  "+authorizeInfo.getPhone()+",自动短信通知功能正在开发中，请用户自行通知。");
                }


            }

        }

        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
    @RequestMapping(value="/test")
    public String xx() {
        ServiceManager.orderService.removeOrderInterval(1000*60*60*24*2);
        return "redirect:/admin/index/index";
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

    @RequestMapping(value="/product_series/new")
    public ResponseEntity<ProductSeries> saveProductSeries(@RequestBody ProductSeries productSeries,HttpServletRequest request,HttpSession session) throws IOException {
        Assert.notNull(productSeries.getProductSeriesPrices());
        Assert.notNull(productSeries.getProductSeriesPrices().get(0));
        Date now=new Date();
        productSeries.getProductSeriesPrices().get(0).setBeginDate(now);
        productSeries.getProductSeriesPrices().get(0).setAdjustDate(now);
        productSeries.setShelvesDate(new Date());
        Assert.notNull(productSeries.getProductStore());
        Assert.notNull(productSeries.getProductStore().getInAndOutList());
        Assert.notNull(productSeries.getProductStore().getInAndOutList().get(0));
        productSeries.getProductStore().getInAndOutList().get(0).setOperator(getLoginAdministrator(session));
        productSeries.getProductStore().getInAndOutList().get(0).setType("in");
        productSeriesService.insert(productSeries);
        List<ProductProperty> productProperties=productSeries.getProductProperties();
        if (productProperties!=null){
            for (ProductProperty productProperty:productProperties){
                productProperty.setProductSeries(productSeries);
                ServiceManager.productPropertyService.insert(productProperty);
                List<ProductPropertyValue> propertyValues=productProperty.getPropertyValues();
                for (ProductPropertyValue propertyValue:propertyValues){
                    propertyValue.setProductProperty(productProperty);
                    ServiceManager.productPropertyValueService.insert(propertyValue);
                }
            }
        }

        MongoDbUtil.clearTransientFields(productSeries);
        return new ResponseEntity<ProductSeries>(productSeries,HttpStatus.OK);
    }
    @RequestMapping(value="/product_series/update_img")
    public String uploadImg(String productSeriesId,@RequestParam("files") MultipartFile[] files,HttpServletRequest request,HttpSession session) throws IOException {

        ProductSeries productSeries=new ProductSeries();
        productSeries.setId(productSeriesId);
        if(files!=null&&files.length>0){
            String dirStr="statics/img/product";
            ServletContext context= ProjectContext.getServletContext();
            ServletContextResource dirResource=new ServletContextResource(context,dirStr);
            mkDirs(dirResource);
            List<ProductSeriesPicture> productSeriesPictures = getProductSeriesPicturesAndSaveFiles(files, dirStr, context);
            productSeries.setPictures(productSeriesPictures);
            productSeriesService.update(productSeries);
        }
        return "redirect:/admin/product_series/list";
    }

    @RequestMapping(value="/product_series/update_brochures")
    public String update_brochures(String productSeriesId,@RequestParam("file") MultipartFile file,HttpServletRequest request,HttpSession session) throws IOException {

        ProductSeries productSeries=new ProductSeries();
        productSeries.setId(productSeriesId);
        String dirStr="statics/img/product";
        ServletContext context= ProjectContext.getServletContext();
        ServletContextResource dirResource=new ServletContextResource(context,dirStr);
        mkDirs(dirResource);


        productSeriesService.update(productSeries);

        return "redirect:/admin/product_series/list";
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
    @RequestMapping(value="/product_category/new")
    public String createProductCategory(ModelMap model, HttpServletRequest request,String categoryType,String categoryName,String subCategoryName,String productCategoryId){
        Assert.notNull(subCategoryName);
        printRequestParameters(request);
        if (categoryType.equals("1")){
            Assert.notNull(categoryName);
            ProductCategory productCategory=new ProductCategory();
            productCategory.setCategoryName(categoryName);
            ServiceManager.productCategoryService.insert(productCategory);
            ProductSubCategory productSubCategory=new ProductSubCategory();
            productSubCategory.setProductCategory(productCategory);
            productSubCategory.setSubCategoryName(subCategoryName);
            ServiceManager.productSubCategoryService.insert(productSubCategory);
        }else if (categoryType.equals("2")){
            ProductCategory productCategory=null;
            if(productCategoryId==null){
                Assert.notNull(categoryName);
                productCategory=new ProductCategory();
                productCategory.setCategoryName(categoryName);
                ServiceManager.productCategoryService.insert(productCategory);
            }else productCategory=ServiceManager.productCategoryService.findById(productCategoryId);
            ProductSubCategory productSubCategory=new ProductSubCategory();
            productSubCategory.setSubCategoryName(subCategoryName);
            productSubCategory.setProductCategory(productCategory);
            ServiceManager.productSubCategoryService.insert(productSubCategory);
        }
        return "redirect:/admin/product_category/create_input";
    }
    @RequestMapping(value="/home_page_block/new")
    public ResponseEntity<HomePageBlock> create(@RequestBody HomePageBlock homePageBlock){
        ServiceManager.homePageBlockService.insert(homePageBlock);
        return new ResponseEntity<HomePageBlock>(homePageBlock, HttpStatus.OK);
    }
    @RequestMapping(value="/home_page_block/list/json")
    public ResponseEntity<List<HomePageBlock>> jsonList(){
        List<HomePageBlock> list=ServiceManager.homePageBlockService.findAll();
        return new ResponseEntity<List<HomePageBlock>>(list, HttpStatus.OK);
    }
    @RequestMapping(value="/lower_users")
    public ResponseEntity<List<User>> lowerUsers(HttpSession session){
        List<User> list=userService.findLowerUsers(getLoginUser(session));
        return new ResponseEntity<List<User>>(list, HttpStatus.OK);
    }
    @RequestMapping(value="/home_page_block/remove/{id}")
    public ResponseEntity<List<HomePageBlock>> removeHomePageBlock(@PathVariable String id){
        ServiceManager.homePageBlockService.removeById(id);
        List<HomePageBlock> list=ServiceManager.homePageBlockService.findAll();
        return new ResponseEntity<List<HomePageBlock>>(list, HttpStatus.OK);
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
    @RequestMapping(value="/notices")
         public ResponseEntity<Message> notices(HttpSession session) throws ParseException {
        Message message=new Message();
        User user=getLoginUser(session);
        if (user==null){
            message.setSuccess(false);
            message.setMessage("请先登录!");
            message.setWrongCode(WrongCodeEnum.NOT_LOGIN.toCode());
        }else{
            List<Notify> notifies=ServiceManager.notifyService.findAll(new BasicDBObject("toUser",new DBRef("mallUser",user.getId())));
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
    @RequestMapping(value="/top3")
    public String top3Maker(ModelMap map){
        List<String[]> list=ServiceManager.productSeriesService.getTop3ProductSeriesDemo();
        map.addAttribute("top3",list);
        return "admin/top3/create_input";
    }
    @RequestMapping(value="/top3/edit/{id}")
    public String top3Maker(ModelMap map,@PathVariable String id){
        TopCarousel topCarousel=ServiceManager.topCarouselService.findById(id);
        map.addAttribute("topCarousel",topCarousel);
        map.addAttribute("top3",topCarousel.getAdContent());
        map.addAttribute("id",id);
        return "admin/top3/create_input";
    }
    @RequestMapping(value="/top3/demo")
    public String top3Demo(ModelMap map){
        List<String[]> list=ServiceManager.productSeriesService.getTop3ProductSeriesDemo();
        map.addAttribute("top3",list);
        return "forward:/top3preview.jsp";
    }
    @RequestMapping(value="/top3/preview")
    public String top3preview(ModelMap map, String data){
        List<String[]> s= (List<String[]>)JSON.parse(data);
        map.addAttribute("top3",s);
        return "forward:/top3preview.jsp";
    }
    @RequestMapping(value="/top3/preview2")
    public String top3preview2(ModelMap map, List<String[]> data){
        map.addAttribute("top3",data);
        return "forward:/top3preview.jsp";
    }
    @RequestMapping(value="/topCarousel/list/json")
    public ResponseEntity<List<TopCarousel>> topCarouselList(ModelMap map){
        List<TopCarousel> topCarousels= ServiceManager.topCarouselService.findAll();
        return new ResponseEntity<List<TopCarousel>>(topCarousels,HttpStatus.OK);
    }
    @RequestMapping(value="/topCarousel/remove/{id}")
    public ResponseEntity<Map<String,Object>> removeTopCarousel(@PathVariable String id){
        Map<String,Object> map=new HashMap<String, Object>();
        ServiceManager.topCarouselService.removeById(id);
        List<TopCarousel> topCarousels= ServiceManager.topCarouselService.findAll();
        map.put("topCarousels",topCarousels);
        Message message=new Message();
        message.setSuccess(true);
        map.put("message",message);
        return new ResponseEntity<Map<String,Object>>(map,HttpStatus.OK);
    }
    @RequestMapping(value="/topCarousel/new")
    public ResponseEntity<Message> topCarouselCreate(ModelMap map, @RequestBody TopCarousel topCarousel){
        Message message=new Message();
        ServiceManager.topCarouselService.update(topCarousel);
        if (topCarousel.getId()!=null){
            message.setSuccess(true);
            message.setMessage("保存成功!");
        }else{
            message.setSuccess(false);
            message.setMessage("保存失败!");
        }

        return new ResponseEntity<Message>(message,HttpStatus.OK);
    }
    @RequestMapping(value="/read-notice")
    public ResponseEntity<Message> topCarouselCreate(ModelMap map, @RequestBody Notify notice){
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
    @RequestMapping(value="/evaluate/update")
    public ResponseEntity<ProductEvaluate> evaluateUpdate(ModelMap map, @RequestBody ProductEvaluate evaluate,HttpSession session){
        ProductEvaluate updateEvaluate=new ProductEvaluate();
        updateEvaluate.setId(evaluate.getId());
        EvaluateFilterInfo evaluateFilterInfo=evaluate.getEvaluateFilterInfo();
        evaluateFilterInfo.setAdministrator(getLoginAdministrator(session));
        evaluateFilterInfo.setDate(new Date());
        updateEvaluate.setEvaluateFilterInfo(evaluateFilterInfo);
        ServiceManager.productEvaluateService.update(updateEvaluate);
        return new ResponseEntity<ProductEvaluate>(evaluate,HttpStatus.OK);
    }
}
