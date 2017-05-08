package com.lingyun.mall.controller;

import com.alipay.bathTrans.config.AlipayConfig;
import com.alipay.bathTrans.util.AlipayNotify;
import com.alipay.bathTrans.util.AlipaySubmit;
import com.alipay.bathTrans.util.UtilDate;
import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.code.AlipayTransStatusEnum;
import com.lingyun.common.code.CardSortEnum;
import com.lingyun.common.code.NotifyTypeCodeEnum;
import com.lingyun.common.code.WrongCodeEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IAlipayBatchTransService;
import com.lingyun.mall.service.IAlipayTransService;
import com.lingyun.support.vo.Message;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/alipay/batch_trans")
public class AlipayBatchTransController extends BaseRestSpringController {
    protected static final String DEFAULT_SORT_COLUMNS = null;
    protected static final String REDIRECT_ACTION = "";
    private static Logger logger = LogManager.getLogger();
    @Resource private IAlipayTransService alipayTransService;
    @Resource private IAlipayBatchTransService alipayBatchTransService;

    @ModelAttribute
    public void init(ModelMap model) {
//        model.put("now", new java.sql.Timestamp(System.currentTimeMillis()));
    }

    //支付宝批量转账
    @RequestMapping(value="/trans")
    public String batchTrans(String data,HttpServletRequest request,HttpSession session,ModelMap model) throws IOException {
        JSONArray transList= JSONArray.fromObject(data);
        if (transList==null||transList.size()==0) throw new RuntimeException("转账记录为空");
        if (transList.size()>1000) throw new RuntimeException("转账笔数大于1000");

            ////////////////////////////////////请求参数//////////////////////////////////////

         //服务器异步通知页面路径
         String notify_url = AlipayConfig.notify_url;

         //付款账号,(测试是否可以是邮箱格式或手机号码格式)
         String email = new String(AlipayConfig.email.getBytes("ISO-8859-1"),"UTF-8");
         //必填

         //付款账户名
         String account_name = new String(AlipayConfig.account_name.getBytes("ISO-8859-1"),"UTF-8");
//         String account_name = new String("湖南业鑫电子商务有限公司".getBytes("ISO-8859-1"),"UTF-8");

         //必填，个人支付宝账号是真实姓名公司支付宝账号是公司名称


        //付款当天日期
        String payDate=UtilDate.getDate();
        String pay_date = new String(payDate.getBytes("ISO-8859-1"),"UTF-8");
        //必填，格式：年[4位]月[2位]日[2位]，如：20100801

        //批次号
//        批量付款批次号。
//
//        11～32位的数字或字母或数字与字母的组合，且区分大小写。
//
//        注意：
//
//        批量付款批次号用作业务幂等性控制的依据，一旦提交受理，请勿直接更改批次号再次上传。
        int batchNoSn=getBatchNoSn(pay_date);
        String batch_no = new String((getBatchNo(batchNoSn)).getBytes("ISO-8859-1"),"UTF-8");
        //必填，格式：当天日期[8位]+序列号[3至16位]，如：201008010000001

        //付款总金额,格式：10.01，精确到分。
//        String batch_fee = new String(BigDecimalUtil.format_twoDecimal(trans.getBatchFee()).getBytes("ISO-8859-1"),"UTF-8");
        double batchFee=getBatch_fee(transList);
        String batch_fee =new String(BigDecimalUtil.format_twoDecimal(batchFee).getBytes("ISO-8859-1"),"UTF-8");
        //必填，即参数detail_data的值中所有金额的总和

        //付款笔数
//        批量付款笔数（最少1笔，最多1000笔）。
        int batchNum=transList.size();
        String batch_num = new String((""+batchNum).getBytes("ISO-8859-1"),"UTF-8");
        //必填，即参数detail_data的值中，“|”字符出现的数量加1，最大支持1000笔（即“|”字符出现的数量999个）

        //付款详细数据
        String detailData=getTransDetailData(transList);
        String detail_data = new String(detailData.getBytes("ISO-8859-1"),"UTF-8");


        AlipayBatchTrans alipayBatchTrans=new AlipayBatchTrans();
        alipayBatchTrans.setBatchFee(batchFee);
        alipayBatchTrans.setBatchNum(batchNum);
        alipayBatchTrans.setDetailData(detailData);
        alipayBatchTrans.setPayDate(payDate);
        alipayBatchTrans.setDate(new Date());
        alipayBatchTrans.setBatchNoSn(batchNoSn);
        alipayBatchTransService.insert(alipayBatchTrans);
        List<String> ids=getIds(transList);
        alipayTransService.updateByIds(ids,"alipayBatchTrans",alipayBatchTrans);

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
        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        model.addAttribute("sHtmlText",sHtmlText);
        return "forward:/batch_trans_alipayapi.jsp";
    }



    /**
     * 用户提交提现请求
     * @param alipayTrans
     * @param session
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseEntity<Message> submitTrans(@RequestBody AlipayTrans alipayTrans,HttpSession session) {
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
    /**
     * 付款通知
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/notify")
    public void notify_url(HttpServletRequest request,HttpServletResponse response) throws IOException {
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
        //批量付款数据中转账成功的详细信息

        String success_details = new String(request.getParameter("success_details").getBytes("ISO-8859-1"),"UTF-8");

        //批量付款数据中转账失败的详细信息
        String fail_details = new String(request.getParameter("fail_details").getBytes("ISO-8859-1"),"UTF-8");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        if(AlipayNotify.verify(params)){//验证成功
            //////////////////////////////////////////////////////////////////////////////////////////
            //请在这里加上商户的业务逻辑程序代码

            //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

            //判断是否在商户网站中已经做过了这次通知返回的处理
            //如果没有做过处理，那么执行商户的业务程序
            //如果有做过处理，那么不执行商户的业务程序

            response.getWriter().println("success");	//请不要修改或删除

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

            //////////////////////////////////////////////////////////////////////////////////////////
        }else{//验证失败
            response.getWriter().println("fail");
        }
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
            transDetailData+="^"+BigDecimalUtil.format_twoDecimal(0.9d*Double.parseDouble(alipayTrans.getString("fee")));//需要缴纳10%的税
//            if (StringUtils.isNotBlank(alipayTrans.getString("note")))
//                transDetailData+="^"+alipayTrans.getString("note");
            transDetailData+="^"+"用户提现";

        }
        System.out.println(transDetailData);
        return transDetailData;
    }
    private double getBatch_fee(JSONArray transList) {
        if (transList==null||transList.size()==0) return 0;
        double batch_fee=0d;
        for (int i=0;i<transList.size();i++){
            JSONObject alipayTrans=transList.getJSONObject(i);
            batch_fee+=Double.parseDouble(alipayTrans.get("fee").toString());
        }
        //扣除10%
        return batch_fee*0.9;
    }
    private List<String> getIds(JSONArray transList) {
        if (transList==null||transList.size()==0) return null;
        List<String> ids=new ArrayList<String>();
        for (int i=0;i<transList.size();i++){
            JSONObject alipayTrans=transList.getJSONObject(i);
            String id=alipayTrans.getString("id");
            ids.add(id);
        }
        return ids;
    }
    private String getBatchNo(int batchNo) {
        String bn=batchNo+"";
        int bnLength=bn.length();
        int maxLength=10;
        int addZero=maxLength-bnLength;
        String ret="";
        for (int i=0;i<addZero;i++){
            ret+="0";
        }
        ret+=batchNo;
        return ret;
    }

    private int getBatchNoSn(String pay_date) {
        int batchNo=0;
        AlipayBatchTrans alipayBatchTrans=alipayBatchTransService.getMax("batchNoSn", "payDate", pay_date);
        if (alipayBatchTrans!=null)
            batchNo=alipayBatchTrans.getBatchNoSn();
        batchNo++;
        return batchNo;
    }

} 
