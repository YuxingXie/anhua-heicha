package com.lingyun.mall.controller;

import com.alipay.bathTrans.util.UtilDate;
import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.google.gson.JsonArray;
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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
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
public class AlipayController extends BaseRestSpringController {
    protected static final String DEFAULT_SORT_COLUMNS = null;
    protected static final String REDIRECT_ACTION = "";
    public static long batchNo=1;
    private static Logger logger = LogManager.getLogger();
    @Resource private BankService bankService;
    @Resource private IAlipayTransService alipayTransService;
    @Resource private Validator validator;


    @InitBinder("bank")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }


    @ModelAttribute
    public void init(ModelMap model) {
//        model.put("now", new java.sql.Timestamp(System.currentTimeMillis()));
    }
    @RequestMapping(value="/json")
    public ResponseEntity<List<Bank>> jsonBanks(ModelMap model) {
        List<Bank> banks=bankService.findAll();
        return new ResponseEntity<List<Bank>>(banks, HttpStatus.OK);
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
    //支付宝批量转账
    @RequestMapping(value="/batch_trans")
    public String batchTrans(String data,HttpServletRequest request,HttpSession session,ModelMap model) throws IOException {
        JSONArray transList= JSONArray.fromObject(data);
        if (transList==null||transList.size()==0) throw new RuntimeException("转账记录为空");
        if (transList.size()>1000) throw new RuntimeException("转账笔数大于1000");

            ////////////////////////////////////请求参数//////////////////////////////////////

         //服务器异步通知页面路径
         String notify_url = com.alipay.bathTrans.config.AlipayConfig.notify_url;

         //付款账号,(测试是否可以是邮箱格式或手机号码格式)
         String email = new String("13387481613".getBytes("ISO-8859-1"),"UTF-8");
         //必填

         //付款账户名
         String account_name = new String("蔡文学".getBytes("ISO-8859-1"),"UTF-8");
         //必填，个人支付宝账号是真实姓名公司支付宝账号是公司名称


        //付款当天日期
        String pay_date = new String(UtilDate.getDate().getBytes("ISO-8859-1"),"UTF-8");
        //必填，格式：年[4位]月[2位]日[2位]，如：20100801

        //批次号
//        批量付款批次号。
//
//        11～32位的数字或字母或数字与字母的组合，且区分大小写。
//
//        注意：
//
//        批量付款批次号用作业务幂等性控制的依据，一旦提交受理，请勿直接更改批次号再次上传。
        String batch_no = new String((pay_date+getBatchNo()).getBytes("ISO-8859-1"),"UTF-8");
        //必填，格式：当天日期[8位]+序列号[3至16位]，如：201008010000001

        //付款总金额,格式：10.01，精确到分。
//        String batch_fee = new String(BigDecimalUtil.format_twoDecimal(trans.getBatchFee()).getBytes("ISO-8859-1"),"UTF-8");
        String batch_fee =getBatch_fee(transList);
        //必填，即参数detail_data的值中所有金额的总和

        //付款笔数
//        批量付款笔数（最少1笔，最多1000笔）。
        String batch_num = new String((""+transList.size()).getBytes("ISO-8859-1"),"UTF-8");
        //必填，即参数detail_data的值中，“|”字符出现的数量加1，最大支持1000笔（即“|”字符出现的数量999个）

        //付款详细数据

        String detail_data = getTransDetailData(transList);

                //////////////////////////////////////////////////////////////////////////////////

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "batch_trans_notify");
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("email", email);
        sParaTemp.put("account_name", account_name);
        sParaTemp.put("pay_date", pay_date);
        sParaTemp.put("batch_no", batch_no);
        sParaTemp.put("batch_fee", batch_fee);
        sParaTemp.put("batch_num", batch_num);
        sParaTemp.put("detail_data", detail_data);


        //建立请求
        String sHtmlText = com.alipay.bathTrans.util.AlipaySubmit.buildRequest(sParaTemp,"get","确认");
        model.addAttribute("sHtmlText",sHtmlText);
        return "forward:/batch_trans_alipayapi.jsp";
    }

    private String getTransDetailData(JSONArray transList) {
        //必填，格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
        //流水号不能超过64字节，收款方账号小于100字节，备注不能超过200字节。当付款方为企业账户，且转账金额达到（大于等于）50000元，备注不能为空。
        //样例：0315006^testture0002@126.com^常炜买家^20.00^hello
        String transDetailData="";
        for (int i=0;i<transList.size();i++){
            JSONObject alipayTrans=transList.getJSONObject(i);
            if (!transDetailData.equals("")) transDetailData+="|";
            JSONObject account=alipayTrans.getJSONObject("account");
            transDetailData+=alipayTrans.getString("id")+System.currentTimeMillis()+"^"+account.getString("accountLoginName")+"^"+account.getString("accountName");
            transDetailData+="^"+BigDecimalUtil.format_twoDecimal(Double.parseDouble(alipayTrans.getString("fee")));
            if (StringUtils.isNotBlank(alipayTrans.getString("note")))
                transDetailData+="^"+alipayTrans.getString("note");
        }
        System.out.println(transDetailData);
        return transDetailData;
    }

    private String getBatch_fee(JSONArray transList) {
        if (transList==null||transList.size()==0) return "0";
        double batch_fee=0d;
        for (int i=0;i<transList.size();i++){
            JSONObject alipayTrans=transList.getJSONObject(i);
            batch_fee+=Double.parseDouble(alipayTrans.get("fee").toString());
        }
        return BigDecimalUtil.format_twoDecimal(batch_fee);
    }

    @RequestMapping(value = "/trans", method = RequestMethod.POST)
    public ResponseEntity<Message> fillOrder(@RequestBody AlipayTrans alipayTrans,HttpSession session) {
        User loginUser=getLoginUser(session);
        Message message=new Message();
        if (loginUser==null){
            message.setSuccess(false);
            message.setMessage("登录超时，请重新登录!!");
            message.setWrongCode(WrongCodeEnum.NOT_LOGIN.toCode());
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }
        if (alipayTrans.getAccount()==null){
            message.setSuccess(false);
            message.setMessage("没有选择支付宝账号");
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }
        if (alipayTrans.getAccount().getCardSort()==null||!alipayTrans.getAccount().getCardSort().equals(CardSortEnum.ALIPAY.toCode())){
            message.setSuccess(false);
            message.setMessage("所选账号不是支付宝账号");
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }
        User user=alipayTrans.getAccount().getUser();
        if (!loginUser.getId().equals(user.getId())){
            message.setSuccess(false);
            message.setMessage("您选择的账号不是本人账号!!");
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }
        List<AlipayTrans> transes=alipayTransService.findSubmittedTransByUser(user);
        double totalFee=0d;
        if (transes!=null&&transes.size()>0){
            for (AlipayTrans trans:transes){
                totalFee+=trans.getFee();
            }
            List<UserMeasure> measures=ServiceManager.userMeasureService.findByUser(user);
            double totalMeasure=0d;
            if (measures!=null&&measures.size()>0){
                for (UserMeasure userMeasure:measures){
                    totalMeasure+=userMeasure.getType()*userMeasure.getCount();
                }
            }
            if (totalFee+alipayTrans.getFee()>totalMeasure){
                message.setSuccess(false);
                message.setMessage("系统查询到您的未处理提现请求金额为"+BigDecimalUtil.format_twoDecimal(totalFee)+"元，此次请求提现金额为"+BigDecimalUtil.format_twoDecimal(alipayTrans.getFee())+"元，总数已超过您的佣金"+BigDecimalUtil.format_twoDecimal(totalMeasure)+"元，请您重新输入金额。");
                message.setLocationPath("/vip#/accounts");
                return new ResponseEntity<Message>(message,HttpStatus.OK);
            }
        }
        alipayTrans.setAlipayTransStatus(AlipayTransStatusEnum.SUBMITTED.toCode());
        Date now=new Date();
        alipayTrans.setDate(now);
        ServiceManager.alipayTransService.insert(alipayTrans);
        Notify notify=new Notify();
        notify.setTitle("系统通知");
        notify.setDate(now);
        notify.setNotifyType(NotifyTypeCodeEnum.SYSTEM.toCode());
        notify.setToUser(user);
        notify.setContent("您的提现请求已提交，提现金额为" + BigDecimalUtil.format_twoDecimal(alipayTrans.getFee()) + "元,请等候处理。");
        ServiceManager.notifyService.insert(notify);
        message.setSuccess(true);
        message.setMessage("您的提现请求已提交，请等候处理。");
        return new ResponseEntity<Message>(message,HttpStatus.OK);

    }

    private String getBatchNo() {
        String bn=batchNo+"";
        int bnLength=bn.length();
        int maxLength=10;
        int addZero=maxLength-bnLength;
        String ret="";
        for (int i=0;i<addZero;i++){
            ret+="0";
        }
        batchNo++;
        return ret;
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
    @RequestMapping(value="/product/{id}")
    public String payProduct(@PathVariable String id,ModelMap model) throws IOException {
        ////////////////////////////////////请求参数//////////////////////////////////////
        Order order= ServiceManager.orderService.findById(id);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = new String(order.getId().getBytes("ISO-8859-1"),"UTF-8");

        //订单名称，必填
        String subject = new String(("大坝生态订单"+out_trade_no).getBytes("ISO-8859-1"),"UTF-8");

        //付款金额，必填

        String total_fee = new String((order.getTotalPrice()+"").getBytes("ISO-8859-1"),"UTF-8");

        //商品描述，可空
        String body = new String(("订单号为"+order.getId()+"的所有商品").getBytes("ISO-8859-1"),"UTF-8");



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
//        sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
//        sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("subject", subject);
        sParaTemp.put("total_fee", total_fee);
        sParaTemp.put("body", body);
        //其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
        //如sParaTemp.put("参数名","参数值");

        //建立请求
        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        model.addAttribute("sHtmlText",sHtmlText);
        return "forward:/daba-alipayapi.jsp";
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
