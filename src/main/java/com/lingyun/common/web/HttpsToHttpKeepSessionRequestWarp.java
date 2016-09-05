package com.lingyun.common.web;

import javax.servlet.http.*;

/**
 * Created by Administrator on 2016/1/21.
 */
public class HttpsToHttpKeepSessionRequestWarp extends HttpServletRequestWrapper {
    private HttpServletResponse response = null;

    public HttpsToHttpKeepSessionRequestWarp(HttpServletRequest request) {
        super(request);
    }

    public void setResponse(HttpServletResponse response) { this.response = response;}

    public HttpSession getSession(){
        HttpSession session = super.getSession();
        processSessionCookie(session);
        return session;
    }

    public HttpSession getSession(boolean create){
        HttpSession session = super.getSession(create);
        processSessionCookie(session);
        return session;
    }

    private void processSessionCookie(HttpSession session){
        if (null == response || null == session) {
// No response or session object attached, skip the pre processing
            return;
        }

// cookieOverWritten - 用于过滤多个Set-Cookie头的标志
        Object cookieOverWritten = getAttribute("COOKIE_OVERWRITTEN_FLAG");
        if (null == cookieOverWritten && isSecure() && isRequestedSessionIdFromCookie() && session.isNew()) {
// 当是https协议，且新session时，创建JSESSIONID cookie以欺骗浏览器

            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setMaxAge(-1); // 有效时间为浏览器打开或超时
            String contextPath = getContextPath();
            if ((contextPath != null) && (contextPath.length() > 0)) {
                cookie.setPath(contextPath);
            }
            else {
                cookie.setPath("/");
            }

            response.addCookie(cookie); // 增加一个Set-Cookie头到response
            setAttribute("COOKIE_OVERWRITTEN_FLAG", "true");// 过滤多个Set-Cookie头的标志
        }
    }

}