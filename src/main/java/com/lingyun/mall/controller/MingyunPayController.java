package com.lingyun.mall.controller;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.common.util.BusinessException;
import com.lingyun.common.util.OuterRequestUtil;
import com.lingyun.common.util.SomeTest;
import com.lingyun.entity.Order;
import com.lingyun.entity.OrderSubmitInfo;
import com.lingyun.entity.User;
import com.mingyun.PayConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/mingyun_pay")
public class MingyunPayController extends BaseRestSpringController {
    private static Logger logger = LogManager.getLogger();

    @ModelAttribute
    public void init(ModelMap model) {
//        model.put("now", new java.sql.Timestamp(System.currentTimeMillis()));
    }

    //支付
    @RequestMapping(value="/to_pay")
    public String payOrder(OrderSubmitInfo orderSubmitInfo,@RequestParam String orderId,HttpSession session,ModelMap model) throws IOException {
        User user=getLoginUser(session);
        if (user==null){
            throw new BusinessException("用户未登录");
        }
        Order order=ServiceManager.orderService.findById(orderId);
        order.setOrderSubmitInfo(orderSubmitInfo);
        ServiceManager.orderService.update(order);

    ////////////////////////////////////请求参数//////////////////////////////////////


        String orderid = new String(order.getId().getBytes("ISO-8859-1"),"UTF-8");

        String uid = new String((PayConfig.uid).getBytes("ISO-8859-1"),"UTF-8");
        String type = new String((PayConfig.type).getBytes("ISO-8859-1"),"UTF-8");
        String charset = new String((PayConfig.charset).getBytes("ISO-8859-1"),"UTF-8");
        String gotrue = new String((PayConfig.gotrue).getBytes("ISO-8859-1"),"UTF-8");
        String gofalse = new String((PayConfig.gofalse).getBytes("ISO-8859-1"),"UTF-8");
        String sign = new String((PayConfig.sign).getBytes("ISO-8859-1"),"UTF-8");
        String token = new String((System.currentTimeMillis()+"").getBytes("ISO-8859-1"),"UTF-8");
        String m =new String((order.getTotalPrice()+"").getBytes("ISO-8859-1"),"UTF-8");
        Map<String,String[]> params=new HashMap<String, String[]>();
        params.put("orderid",new String[]{orderid});
        params.put("uid",new String[]{uid});
        params.put("type",new String[]{type});
        params.put("charset",new String[]{charset});
        params.put("gotrue",new String[]{gotrue});
        params.put("gofalse",new String[]{gofalse});
        params.put("sign",new String[]{sign});
        params.put("token",new String[]{token});
        params.put("m",new String[]{m});
        OuterRequestUtil.sendPost("http://pay.cnmypay.com/pay",params);
        return null;
    }



    /**
     * 付款通知
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value="/notify")
    public void notify_url(HttpServletRequest request,HttpServletResponse response) throws IOException {
        SomeTest.begin=System.currentTimeMillis();
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //商户订单号
        String orderid = new String(request.getParameter("orderid").getBytes("ISO-8859-1"),"UTF-8");


    }

    @RequestMapping(value = "/return_true")
    public void returnUrl(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        //获取支付宝GET过来反馈信息
        SomeTest.end=System.currentTimeMillis();
        SomeTest.printHowLong();
        //获取支付宝GET过来反馈信息
        PrintWriter out=response.getWriter();
        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		out.println("您的订单"+out_trade_no+"付款成功!<br/><br/>");
//		out.println("验证成功!<br/><br/>");
		out.println("<a href='/vip'><h4>返回会员中心，您可能需要重新登录。</h4></a>");
    }
    @RequestMapping(value = "/return_false")
    public void returnFalseUrl(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        //获取支付宝GET过来反馈信息
        SomeTest.end=System.currentTimeMillis();
        SomeTest.printHowLong();
        //获取支付宝GET过来反馈信息
        PrintWriter out=response.getWriter();
        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        out.println("您的订单"+out_trade_no+"付款失败!<br/><br/>");
//		out.println("验证成功!<br/><br/>");
        out.println("<a href='/vip'><h4>返回会员中心，您可能需要重新登录。</h4></a>");
    }
    public static void main(String[] args){
        System.out.println(BigDecimalUtil.format_twoDecimal(2.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(22.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(222.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(2222.5d));
    }
} 
