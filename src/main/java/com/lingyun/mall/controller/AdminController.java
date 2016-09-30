package com.lingyun.mall.controller;

import com.alipay.util.httpClient.HttpResponse;
import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.constant.Constant;
import com.lingyun.common.helper.service.ProjectContext;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.common.util.IconCompressUtil;
import com.lingyun.common.util.MongoDbUtil;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IProductSeriesService;
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
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseRestSpringController {
    private static Logger logger = LogManager.getLogger();
    protected static final String DEFAULT_SORT_COLUMNS = null;
    protected static final String REDIRECT_ACTION = "";

    @Resource private IProductSeriesService productSeriesService;

    @InitBinder("productSeries")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @RequestMapping(value="/logout")
    public String logout(HttpSession session) {
        session.setAttribute(Constant.LOGIN_ADMINISTRATOR,null);
        session.removeAttribute(Constant.LOGIN_ADMINISTRATOR);
        return "redirect:/admin-login.jsp";
    }
    @RequestMapping(value="/login")
    public ResponseEntity<Message> login(@RequestBody Administrator administrator,HttpSession session) {
        Message message=new Message();
        if (administrator.getName()==null&&administrator.getName().trim().equals("")){
            message.setMessage("用户名不能为空");
            message.setSuccess(false);
            return new ResponseEntity<Message>(message, HttpStatus.OK);
        }
        if (administrator.getPassword()==null&&administrator.getName().trim().equals("")){
            message.setMessage("密码不能为空");
            message.setSuccess(false);
            return new ResponseEntity<Message>(message, HttpStatus.OK);
        }
        Administrator dbAdmin= ServiceManager.administratorService.findByNameAndPassword(administrator.getName(),administrator.getPassword());

        if (dbAdmin==null){
            message.setMessage("用户名密码错误");
            message.setSuccess(false);
            return new ResponseEntity<Message>(message, HttpStatus.OK);
        }
        message.setSuccess(true);
        message.setMessage("登陆成功，即将为您跳转!");
        session.setAttribute(Constant.LOGIN_ADMINISTRATOR,dbAdmin);
        return new ResponseEntity<Message>(message, HttpStatus.OK);
    }
    @RequestMapping(value="/do_mode_test")
    public String xx(
            double price,
            double anyPointBonus,
            double directPushBonusRate,
            double pairTouchBonusRate,ModelMap modelMap) throws IOException {
        double totalMoney=0;
        double littleAreaTotal=price;
        int userCount=1;
        int levelUserCount=1;
        double pay=0d;
        double in=0d;
        double rank1bonus=1500d;
        double rank2bonus=2500d;
        double rank3bonus=30000d;
        int level=1;
        boolean get1bonus=false;
        boolean get2bonus=false;
        boolean get3bonus=false;
        List<String> out=new ArrayList<String>();

        out.add("##############################################################");
        out.add("见点奖" + anyPointBonus + ",直推奖:" + directPushBonusRate + "*" + price + "=" + (directPushBonusRate * price)
                + ",对碰奖" + pairTouchBonusRate + "*" + price + "=" + (pairTouchBonusRate * price));
        out.add("##############################################################");
        out.add("---------------------------------------------------------------------------------------");
        while(totalMoney>=0d){
            String rank="";
            double rankBonus=0d;
            double levelIn=0d;
            double levelOut=0d;
            double levelAnyPointBonus;
            double levelDirectPushBonus;
            double levelPairTouchBonus;
            level++;
            levelUserCount*=2;
            userCount+=levelUserCount;
            int levelRank1BonusPersonCount=0;
            int levelRank2BonusPersonCount=0;
            int levelRank3BonusPersonCount=0;
            for(int i=1;i<=levelUserCount;i++){
                if(i>levelUserCount/2){
                    littleAreaTotal+=price;
                    if(littleAreaTotal>120000 &&littleAreaTotal <=300000 ){
                        levelOut+=rank1bonus;
                        levelRank1BonusPersonCount++;
                    }
                    if(littleAreaTotal>300000 &&littleAreaTotal <=1000000 ){
                        levelOut+=rank2bonus;
                        levelRank2BonusPersonCount++;
                    }
                }

            }
//                if(in/2>120000 &&in/2 <=300000 &&!get1bonus){
//                    rankBonus=1500d;
//                    levelOut+=rankBonus;
//                    get1bonus=true;
//                    rank="业务主任奖";
//
//                }
//                if(in/2>300000 &&in/2 <=1000000&& !get2bonus){
//                    rank="业务经理奖(价值2500华为手机)";
//                    rankBonus=2500d;
//                    levelOut+=rankBonus;//華為手機價格
//                    get2bonus=true;
//                }
//                if(in/2>1000000 &&!get3bonus){
//                    rank="高级业务经理奖";
//                    rankBonus=30000d;
//                    levelOut+=rankBonus;
//                    get3bonus=true;
//                }

//            }
            //见点奖  只拿15层
            if (level<15){
                levelAnyPointBonus=anyPointBonus*(level-1)*levelUserCount;
                levelOut+=levelAnyPointBonus;
            }else{

                levelAnyPointBonus=anyPointBonus*14*levelUserCount;
                levelOut+=levelAnyPointBonus;
            }
            //直推奖
//            out+=levelUserCount*price*directPushBonusRate;
            levelDirectPushBonus=levelUserCount*price*directPushBonusRate;
            levelOut+=levelDirectPushBonus;

//            in+=price*levelUserCount;
            levelIn+=price*levelUserCount;

            //对碰奖

            levelPairTouchBonus=levelUserCount/2*price*pairTouchBonusRate;
            levelOut+=levelPairTouchBonus;


            in+=levelIn;
            pay+=levelOut;
            totalMoney+=in-pay;

            BigDecimal bd = new BigDecimal(totalMoney);
            bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal inbd = new BigDecimal(levelIn);
            inbd.setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal outbd = new BigDecimal(levelOut);
            outbd.setScale(2, BigDecimal.ROUND_HALF_UP);

            out.add(level + "层,总用户数：" + userCount + ",新增用户:" + levelUserCount + " ,新增收入:" + BigDecimalUtil.format_twoDecimal(inbd.doubleValue()) + ",新增支出:" + BigDecimalUtil.format_twoDecimal(outbd.doubleValue()) + "，余额:" + BigDecimalUtil.format_twoDecimal(bd.doubleValue()));
            if (rankBonus>0d){
                out.add("    支出包括：见点奖：" + BigDecimalUtil.format_twoDecimal(levelAnyPointBonus) + ",直推奖:" + BigDecimalUtil.format_twoDecimal(levelDirectPushBonus) + " ," + rank + ": " + BigDecimalUtil.format_twoDecimal(rankBonus) + ",对碰奖：" + BigDecimalUtil.format_twoDecimal(levelPairTouchBonus));
            }
            else{
                out.add("    支出包括：见点奖：" + BigDecimalUtil.format_twoDecimal(levelAnyPointBonus )+ ",直推奖:" + BigDecimalUtil.format_twoDecimal(levelDirectPushBonus) + " ,对碰奖：" + BigDecimalUtil.format_twoDecimal(levelPairTouchBonus));
            }
            out.add("---------------------------------------------------------------------------------------");
            if (level>1000){
                out.add("太多输出结果，忽略。");
                break;
            }
        }
        out.add("level " + level);
        BigDecimal bd = new BigDecimal(totalMoney);
        bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        out.add("totalMoney " + bd);
        out.add("end ");
        modelMap.addAttribute("lines",out);
        return "admin/mode-result";

    }
//    private List<Member> RankBonusPersons(int level,int bonus,double price,double min,double max){
//        int totalPerson=1;
//        int levelTotalPerson=1;
//        for(int l=1;l<=level;l++){
//            if (l>1) {
//                levelTotalPerson=(l-1)*2;
//                totalPerson+=levelTotalPerson;
//            }
//        }
//        for (int i=1;i<=totalPerson;i++){
//
//
//        }
//        return null;
//    }


    @RequestMapping(value="/index/json")
    public ResponseEntity< Map<String,Object>> index(HttpSession session) {
        Map<String,Object> map=new HashMap<String, Object>();
        long todoOrders=ServiceManager.orderService.findUnHandlerOrdersCount();
        long returnOrders=ServiceManager.orderService.findReturnExchangeOrdersCount();
        DBObject dbObject=new BasicDBObject();
        Administrator administrator=getLoginAdministrator(session);
        dbObject.put("toAdministrator",new DBRef("administrator",new ObjectId(administrator.getId())));
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
    @RequestMapping(value="/home_page_block/remove/{id}")
    public ResponseEntity<List<HomePageBlock>> removeHomePageBlock(@PathVariable String id){
        ServiceManager.homePageBlockService.removeById(id);
        List<HomePageBlock> list=ServiceManager.homePageBlockService.findAll();
        return new ResponseEntity<List<HomePageBlock>>(list, HttpStatus.OK);
    }
    @RequestMapping(value="/adjust_price/{id}")
    public String adjust_price(@PathVariable String id,ModelMap map){
        ProductSeries productSeries=productSeriesService.findProductSeriesById(id);
       map.addAttribute("productSeries",productSeries);
        return "admin/product_series/adjust_price";
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
    @RequestMapping(value="/notify/remove")
    public ResponseEntity<Map<String,Object>> removeProduct( @RequestBody Notify notify,HttpSession session){
        Map<String,Object> map=new HashMap<String,Object>();
        ServiceManager.notifyService.removeById(notify.getId());
        DBObject dbObject=new BasicDBObject();
        Administrator administrator=getLoginAdministrator(session);
        dbObject.put("fromAdministrator",new DBRef("administrator",new ObjectId(administrator.getId())));
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
            dbObject.put("toAdministrator",new DBRef("administrator",new ObjectId(administrator.getId())));
        }else if (fromTo!=null&&fromTo.equals("fromMe")){
            dbObject.put("fromAdministrator",new DBRef("administrator",new ObjectId(administrator.getId())));
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
