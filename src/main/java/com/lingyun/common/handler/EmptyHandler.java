package com.lingyun.common.handler;

import com.lingyun.common.constant.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: lxd
 * Date: 11-5-19
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class EmptyHandler {
    @RequestMapping()
    public void emptyHandler() throws ServletException, IOException {
    }
    @RequestMapping(value = "/vip")
    public void doRedirect(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/statics/pages/demo/index.html").forward(request,response);
    }
    @RequestMapping(value = "/admin")
    public void admin(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws ServletException, IOException {

            request.getRequestDispatcher("/statics/pages/admin/index.html").forward(request,response);

    }
}
