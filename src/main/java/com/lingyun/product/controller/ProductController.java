package com.lingyun.product.controller;

import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.constant.Constant;
import com.lingyun.common.helper.service.ProjectContext;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.IconCompressUtil;
import com.lingyun.common.util.MongoDbUtil;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IProductSeriesService;
import com.lingyun.support.vo.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseRestSpringController {
    private static Logger logger = LogManager.getLogger();
    protected static final String DEFAULT_SORT_COLUMNS = null;
    protected static final String REDIRECT_ACTION = "";

    @Resource private IProductSeriesService productSeriesService;

    @InitBinder("productSeries")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }



    @RequestMapping(value="/login",method = RequestMethod.POST)
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
    @RequestMapping(value="/register",method = RequestMethod.POST)
    public ResponseEntity<User> register(@RequestBody User user,HttpSession session) {
        System.out.println(user.getName());
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value="/list")
    public ResponseEntity< List<ProductSeries>> index(HttpSession session) {
        List<ProductSeries> list=ServiceManager.productSeriesService.findAll();
        if (list!=null && list.size()>0){
            User user=getLoginUser(session);

            if (user!=null&&user.getName()!=null&&user.getName().equals("谢宇星(测试用)")){

            }
            return new ResponseEntity< List<ProductSeries>>(list,HttpStatus.OK);
        }
        list=new ArrayList<ProductSeries>();
        ProductSeries productSeries=new ProductSeries();
        productSeries.setName("黑茶一级金装");
        ProductSeriesPicture picture=new ProductSeriesPicture();
        picture.setBigPicture("http://a2.att.hudong.com/10/32/01300259753451132488324466836.jpg");
        List<ProductSeriesPicture> pictures=new ArrayList<ProductSeriesPicture>();
        pictures.add(picture);
        productSeries.setPictures(pictures);
        List<ProductSeriesPrice> productSeriesPrices=new ArrayList<ProductSeriesPrice>();
        ProductSeriesPrice productSeriesPrice=new ProductSeriesPrice();
        productSeriesPrice.setPrice(0.01d);
        productSeriesPrice.setBeginDate(new Date());
        productSeriesPrices.add(productSeriesPrice);
        productSeries.setProductSeriesPrices(productSeriesPrices);
        ProductSeries productSeries2=new ProductSeries();
        productSeries2.setName("黑茶二级金装");
        productSeries2.setPictures(pictures);
        List<ProductSeriesPrice> productSeriesPrices2=new ArrayList<ProductSeriesPrice>();
        ProductSeriesPrice productSeriesPrice2=new ProductSeriesPrice();
        productSeriesPrice2.setPrice(998d);
        productSeriesPrice2.setBeginDate(new Date());
        productSeriesPrices2.add(productSeriesPrice2);
        productSeries2.setProductSeriesPrices(productSeriesPrices2);
        list.add(productSeries);
        list.add(productSeries2);
        ServiceManager.productSeriesService.insertAll(list);
        return new ResponseEntity< List<ProductSeries>>(list,HttpStatus.OK);
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


}
