package com.lingyun.mall.filter;

import com.lingyun.common.web.HttpsToHttpKeepSessionRequestWarp;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/21.
 */
public final class HttpsToHttpKeepSessionFilter  implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)request;
        if(httpServletRequest.getRequestURI().indexOf("layerslider/skins/fullwidth/skin")>=0){//屏蔽一个不知道从哪里来的请求
//            System.out.println("i got it");
            httpServletRequest.getRequestDispatcher("/statics/assets/plugins/layerslider/skins/fullwidth/skin.css").forward(request,response);
            return;
        }
        if(httpServletRequest.getRequestURI().indexOf("favicon")>=0){//屏蔽favicon的请求
//            System.out.println("i got it");
            httpServletRequest.getRequestDispatcher("/statics/assets/img/favicon.ico").forward(request,response);
            return;
        }
        HttpsToHttpKeepSessionRequestWarp requestWarp = new HttpsToHttpKeepSessionRequestWarp(httpServletRequest);
        requestWarp.setResponse((HttpServletResponse) response);
        chain.doFilter(requestWarp, response);
    }

    @Override
    public void destroy() {

    }
}