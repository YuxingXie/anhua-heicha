package com.lingyun.common.base;


import com.lingyun.common.constant.Constant;
import com.lingyun.entity.Administrator;
import com.lingyun.entity.Cart;
import com.lingyun.entity.Order;
import com.lingyun.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


public class BaseRestSpringController  {
    protected static final String CREATED_SUCCESS = "创建成功";
    protected static final String UPDATE_SUCCESS = "更新成功";
    protected static final String DELETE_SUCCESS = "删除成功";
    
    protected static final String RESULT_STRING = "result";
    protected static final String TOTAL = "total";
    protected static final String RESULT_ACTION = "redirect:/result";
    @Value(value = "${app.httpPort}")
    protected String httpPort;

    private static Logger logger = LogManager.getLogger();
    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }


    public static void copyProperties(Object target,Object source) {
        BeanUtils.copyProperties(target, source);
    }

    protected void printRequestParameters(HttpServletRequest request){
        Map<String,String[]> requestMap=request.getParameterMap();
        for (String key:requestMap.keySet()){
            String str=new String();
            str+=key + ":[";
            for (String val:requestMap.get(key)){
                str+=val + ",";
            }
            if (str.endsWith(","))  logger.info(str.substring(0,str.length()-1)+"]\n");
            else logger.info(str+"]\n");
        }
    }
    protected User getLoginUser(HttpSession session) {
        return session.getAttribute(Constant.LOGIN_USER)==null?null:((User)(session.getAttribute(Constant.LOGIN_USER)));
    }
    protected Administrator getLoginAdministrator(HttpSession session) {
        return session.getAttribute(Constant.LOGIN_ADMINISTRATOR)==null?null:((Administrator)(session.getAttribute(Constant.LOGIN_ADMINISTRATOR)));
    }
    protected Cart getCart(HttpSession session) {
        return session.getAttribute(Constant.CART)==null?null:((Cart)(session.getAttribute(Constant.CART)));
    }
    protected Order getOrder(HttpSession session) {
        return session.getAttribute(Constant.ORDER)==null?null:((Order)(session.getAttribute(Constant.ORDER)));
    }
    public static <T> T getOrCreateRequestAttribute(HttpServletRequest request, String key,Class<T> clazz) {
        Object value = request.getAttribute(key);
        if(value == null) {
            try {
                value = clazz.newInstance();
            } catch (Exception e) {
                ReflectionUtils.handleReflectionException(e);
            }
            request.setAttribute(key, value);
        }
        return (T)value;
    }
    protected String getHttpUrlString(HttpServletRequest request,String vieName) {
        String path = request.getContextPath();
//        String basePath ="http://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        String basePath ="http://"+request.getServerName()+":"+httpPort+path+"/";
        return basePath+vieName;
    }
    protected String getInputHttpUrlString(HttpServletRequest request) {
        String path = request.getContextPath();
//        String basePath ="http://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        String basePath ="http://"+request.getServerName()+":"+httpPort+path+"/";
        String uri=request.getRequestURI();
        return basePath+uri;
    }
    protected String getHttpUrlStringFromUri(HttpServletRequest request,String uri) {
        String path = request.getContextPath();
//        String basePath ="http://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        String url ="http://"+request.getServerName()+":"+httpPort+uri;
        return url;
    }

}
