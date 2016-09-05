package com.lingyun.mall.controller;

import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.constant.Constant;
import com.lingyun.common.helper.service.ProjectContext;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.FileUtil;
import com.lingyun.common.util.OuterRequestUtil;
import com.lingyun.common.web.CookieTool;
import com.lingyun.entity.*;
import com.lingyun.mall.dao.UserDao;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.ServletContextResource;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/xxx")
//@SessionAttributes("loginUser")
public class IndexController extends BaseRestSpringController {
    private static Logger logger = LogManager.getLogger();
    @Resource(name = "userDao")
    private UserDao userDao;
    @RequestMapping(value = "/test")
    public String test(HttpServletRequest request, ModelMap model, HttpSession session) {
        return "forward:/index.jsp?test=test_exit";
    }
    @RequestMapping(value = "")
    public String index(HttpServletRequest request, ModelMap model, HttpSession session) {

        if(request.getParameter("code")!=null){

            qqLogin(request, session);

        }else {
            cookieLogin(request, session);
        }

        List<Interest> interests=ServiceManager.interestService.findInterestsOfUser(getLoginUser(session));
        model.addAttribute("interests", interests);
        TopCarousel topCarousel=ServiceManager.topCarouselService.findByMaxPriority();
        if(topCarousel!=null){
            List<String[]> top3 = topCarousel.getAdContent();
            model.addAttribute("top3", top3);
        }

        List<HomePageBlock> homePageBlocks=ServiceManager.homePageBlockService.findAll(new BasicDBObject("show",true));
        for (HomePageBlock homePageBlock:homePageBlocks){
            setInterest(homePageBlock.getProductSeriesList(),interests);
        }
        model.addAttribute("homePageBlocks",homePageBlocks);
        return "index";
    }

    private void cookieLogin(HttpServletRequest request, HttpSession session) {
        if (session.getAttribute(Constant.LOGIN_USER)!=null) return;
        Cookie nameCookie = CookieTool.getCookieByName(request, "loginStr");
        Cookie pwdCookie = CookieTool.getCookieByName(request, "password");
        String name = null;
        try {
            name = nameCookie == null ? null : URLDecoder.decode(nameCookie.getValue(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String password = pwdCookie == null ? null : pwdCookie.getValue();
        if (name != null && password != null) {
            User user = userDao.findByEmailOrPhone(name);
            if (user!=null && user.getPassword().equalsIgnoreCase(password)) {
                session.setAttribute(Constant.LOGIN_USER, user);
            }
        }

    }

    private void qqLogin(HttpServletRequest request, HttpSession session) {
        if (getLoginUser(session)!=null) return;
        String code=request.getParameter("code");
        String result= OuterRequestUtil.sendGet("https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=101288574&client_secret=a35705ea862db09ae13407ecb76c7bd3&code=" + code + "&redirect_uri=http%3A%2F%2Fwww.lingyun.com%2F");
//            System.out.println(result);//access_token=3D78295038D5DA21FEAF7F66C6C6396E&expires_in=7776000&refresh_token=43ABD3D924DB89DBEB322E5CF3692E11
        if (result.indexOf("&")>=0){
            Map<String,String> params=new HashMap<String, String>();
            for(String keyEqVal:result.split("&")){
                String key=keyEqVal.substring(0,keyEqVal.indexOf("="));
                String val=keyEqVal.substring(keyEqVal.indexOf("=")+1);
//                    System.out.println("key:"+key+",val:"+val);
                params.put(key,val);
            }
            //获取用户OpenID_OAuth2.0
            //PC网站：https://graph.qq.com/oauth2.0/me
            String access_token=params.get("access_token");
            String result_openID=OuterRequestUtil.sendGet("https://graph.qq.com/oauth2.0/me?access_token="+access_token);//callback( {"client_id":"101288574","openid":"3A2B2C427140E522137F4803464BA71F"} );
//                System.out.println(result_openID);
            if(result_openID!=null&&result_openID.indexOf("\"openid\"")>=0){
                JSONObject jsonObject=JSONObject.fromObject(result_openID.substring(result_openID.indexOf("{"), result_openID.lastIndexOf("}") + 1));
                String openId=jsonObject.get("openid")==null?null:jsonObject.get("openid").toString();
//                    System.out.println("client_id:"+jsonObject.get("client_id"));
//                    System.out.println("openid:"+openId);
                if (openId!=null&&!openId.equals("")){
                    User user=userDao.findByTencentOpenId(openId);
                    if (user==null){
                        user=new User();
                        String getUserInfoUrl="https://graph.qq.com/user/get_user_info?access_token="+access_token+"&oauth_consumer_key=101288574&openid="+openId;
                        String userInfoStr=OuterRequestUtil.sendGet(getUserInfoUrl);
                        if (userInfoStr!=null){
                            JSONObject userInfo=JSONObject.fromObject(userInfoStr);
                            TencentLoginInfo tencentLoginInfo=(TencentLoginInfo)(JSONObject.toBean(userInfo,TencentLoginInfo.class));
//                                System.out.println(userInfoStr);
                            user.setName(tencentLoginInfo.getNickname());
                            tencentLoginInfo.setOpenId(openId);
                            user.setActivated(true);
                            user.setTencentLoginInfo(tencentLoginInfo);
                            userDao.insert(user);
                            logger.info("用户"+user.getName()+" 从QQ登陆，信息已保存。 ");
                        }
                    }
                    session.setAttribute(Constant.LOGIN_USER,user);
                }
            }
        }
    }

    private void setInterest(List<ProductSeries> productSeriesList,List<Interest> interestList){
        if (productSeriesList==null) return;
        if (interestList==null) return;
        out:for (ProductSeries productSeries:productSeriesList){
//          if (interestList.contains(productSeries))  productSeries.setInterested(true);
            String productSeriesId=productSeries.getId();
            inner:for (Interest interest:interestList){
                ProductSeries interestProductSeries=interest.getProductSeries();
                if(productSeriesId.equalsIgnoreCase(interestProductSeries.getId())){
                    productSeries.setInterested(true);
                    break inner;
                }
            }
        }
    }

    @RequestMapping(value = "/product/search")
    public String  searchProducts(ModelMap model,String keyWord,Integer page) {
        page=page==null?1:page;
        keyWord=keyWord==null?"":keyWord;
        Page<ProductSeries> productSeriesListPage=ServiceManager.productSeriesService.findProductSeriesesByKeyWord(keyWord,page,3);

        model.addAttribute("_page", productSeriesListPage);
        model.addAttribute("page", page);
        model.addAttribute("keyWord", keyWord);
        return "search-result";

    }

    @RequestMapping(value = "/download/cer")
    public void   downloadCer(HttpServletResponse response) throws IOException {
        ServletContextResource resource=new ServletContextResource(ProjectContext.getServletContext(),"statics/docs/tomcat.cer");
        FileUtil.fileDownload(response,resource.getFile().getAbsolutePath());
    }

    public static void main(String[] args) {
        String jsonStr = "[{id:100,name:'Johnson'},{id:101,name:'Jackson'}]";
    }
}