package com.lingyun.common.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/4.
 */
public class CookieTool {
    /**
     * 设置cookie（接口方法）
     * @author 刘鹏
     * @param response
     * @param name  cookie名字
     * @param value cookie值
     * @param maxAge cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpServletRequest request,HttpServletResponse response,String name,String value,int maxAge){
        Cookie cookie = generateCookie(name, value);
        addCookie(request,response,cookie,maxAge);
    }


    public static void addCookie(HttpServletRequest request,HttpServletResponse response,Cookie cookie,int maxAge){
        cookie.setPath(request.getContextPath());
        if(maxAge>0){
            cookie.setMaxAge(maxAge);
        }
        response.addCookie(cookie);
    }
    public static void removeCookie(HttpServletRequest request,HttpServletResponse response,Cookie cookie){
        if (cookie==null) return;
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    public static void removeCookie(HttpServletRequest request,HttpServletResponse response,String name){
        Cookie cookie=getCookieByName(request,name);
        removeCookie(request,response,cookie);
    }
    /**
     * 根据名字获取cookie（接口方法）
     * @author 刘鹏
     * @param request
     * @param name cookie名字
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request,String name){
        Map<String,Cookie> cookieMap = ReadCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie;
        }else{
            return null;
        }
    }
    public static void removeCookieByName(HttpServletRequest request,String name){
        Cookie cookie=getCookieByName(request, name);
        cookie.setMaxAge(0);
    }
    /**
     * 将cookie封装到Map里面（非接口方法）
     * @author 刘鹏
     * @param request
     * @return
     */
    private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
        Cookie[] cookies = request.getCookies();
        if(null!=cookies){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
    private static Cookie generateCookie(String name, String value) {
        Cookie cookie = null;
        try {
            cookie = new Cookie(name, URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cookie;
    }



}
