package com.lingyun.mall.controller;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.code.AlipayTransStatusEnum;
import com.lingyun.common.code.CardSortEnum;
import com.lingyun.common.code.NotifyTypeCodeEnum;
import com.lingyun.common.code.WrongCodeEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.*;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IAlipayTransService;
import com.lingyun.mall.service.impl.BankService;
import com.lingyun.support.vo.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/alipay")
public class AlipayPayController extends BaseRestSpringController {
    protected static final String DEFAULT_SORT_COLUMNS = null;
    protected static final String REDIRECT_ACTION = "";
    public static long batchNo=1;
    private static Logger logger = LogManager.getLogger();
    @Resource private BankService bankService;
    @Resource private IAlipayTransService alipayTransService;
    @Resource private Validator validator;




    @ModelAttribute
    public void init(ModelMap model) {
//        model.put("now", new java.sql.Timestamp(System.currentTimeMillis()));
    }

    //支付宝支付
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

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = new String(order.getId().getBytes("ISO-8859-1"),"UTF-8");

        //订单名称，必填
        String subject = new String(("YeXin"+out_trade_no).getBytes("ISO-8859-1"),"UTF-8");

        //付款金额，必填
        String total_fee =new String((order.getTotalPrice()+"").getBytes("ISO-8859-1"),"UTF-8");

        //收银台页面上，商品展示的超链接，必填
//        String show_url = new String(("http://hunanyexin.com/order/show/"+orderId).getBytes("ISO-8859-1"),"UTF-8");
        String show_url = new String(("http://hunanyexin.com/vip#/buy").getBytes("ISO-8859-1"),"UTF-8");

        //商品描述，可空
        String body = new String();

        //////////////////////////////////////////////////////////////////////////////////

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AlipayConfig.service);
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("seller_id", AlipayConfig.seller_id);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("payment_type", AlipayConfig.payment_type);
        sParaTemp.put("notify_url", AlipayConfig.notify_url);
        sParaTemp.put("return_url", AlipayConfig.return_url);
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("subject", subject);
        sParaTemp.put("total_fee", total_fee);
        sParaTemp.put("show_url", show_url);
        //sParaTemp.put("app_pay","Y");//启用此参数可唤起钱包APP支付。
        sParaTemp.put("body", body);
        //其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.2Z6TSk&treeId=60&articleId=103693&docType=1
        //如sParaTemp.put("参数名","参数值");


        //建立请求
        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
        model.addAttribute("sHtmlText",sHtmlText);
        return "forward:/alipayapi.jsp";
    }



    /**
     * 付款通知
     * @param request
     * @param response
     * @throws IOException
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

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

        //支付宝交易号

        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        if(AlipayNotify.verify(params)){//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            if(trade_status.equals("TRADE_FINISHED")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            } else if (trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
            Order order= ServiceManager.orderService.findById(out_trade_no);
            order.setPayDate(new Date());
            order.setPayStatus("y");
            ServiceManager.orderService.update(order);
            ServiceManager.userService.updateUserAfterOrder(order);
            response.getWriter().println("success");	//请不要修改或删除

            //////////////////////////////////////////////////////////////////////////////////////////
        }else{//验证失败
            response.getWriter().println("fail");
        }
    }

    @RequestMapping(value = "/return")
    public void returnUrl(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
//        if (true) {
//            request.getRequestDispatcher("/vip").forward(request,response);
////            response.sendRedirect("/vip#/alipay_return");
//            return;
//        }

        //获取支付宝GET过来反馈信息
        SomeTest.end=System.currentTimeMillis();
        SomeTest.printHowLong();
        //获取支付宝GET过来反馈信息
        PrintWriter out=response.getWriter();
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
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

        //支付宝交易号

        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

        //交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        //计算得出通知验证结果
        boolean verify_result = AlipayNotify.verify(params);

        if(verify_result){//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
            if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序


            }

            //该页面可做页面美工编辑

		out.println("您的订单"+out_trade_no+"付款成功!<br/><br/>");
//		out.println("验证成功!<br/><br/>");
		out.println("<a href='/vip'><h4>返回会员中心</h4></a>");
//		response.sendRedirect("/");
//            request.getRequestDispatcher("/vip#/alipay_return").forward(request,response);

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            //////////////////////////////////////////////////////////////////////////////////////////
        }else{
            //该页面可做页面美工编辑
            out.println("验证失败");
        }
    }
    public static void main(String[] args){
        System.out.println(BigDecimalUtil.format_twoDecimal(2.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(22.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(222.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(2222.5d));
    }
} 
