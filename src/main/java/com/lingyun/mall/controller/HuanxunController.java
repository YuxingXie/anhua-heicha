package com.lingyun.mall.controller;

import com.alipay.bathTrans.config.AlipayConfig;
import com.alipay.bathTrans.util.AlipaySubmit;
import com.alipay.bathTrans.util.UtilDate;
import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.code.AlipayTransStatusEnum;
import com.lingyun.common.code.CardSortEnum;
import com.lingyun.common.code.NotifyTypeCodeEnum;
import com.lingyun.common.code.WrongCodeEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.*;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IAlipayBatchTransService;
import com.lingyun.mall.service.IAlipayTransService;
import com.lingyun.mall.service.IHuanxunSupportBankService;
import com.lingyun.mall.service.IHuanxunSupportOpeningBankService;
import com.lingyun.support.vo.Message;
import com.mingyun.PayConfig;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/huanxun")
public class HuanxunController extends BaseRestSpringController {
    private static Logger logger = LogManager.getLogger();
    @Resource
    private IHuanxunSupportBankService huanxunSupportBankService;
    @Resource private IAlipayTransService alipayTransService;
    @Resource private IAlipayBatchTransService alipayBatchTransService;
    @Resource
    private IHuanxunSupportOpeningBankService huanxunSupportOpeningBankService;
    @ModelAttribute
    public void init(ModelMap model) {
//        model.put("now", new java.sql.Timestamp(System.currentTimeMillis()));
    }

    //支付
    @RequestMapping(value="/to_pay")
    public String payOrder(OrderSubmitInfo orderSubmitInfo,@RequestParam String orderId,HttpSession session,ModelMap model) throws IOException {

        return null;
    }
    /**
     * 用户提交环迅的提现请求
     * 为什么叫类名叫AlipayTrans是历史原因
     * @param alipayTrans
     * @param session
     * @return
     */
    @RequestMapping(value = "/trans/submit", method = RequestMethod.POST)
    public ResponseEntity<Message> submitTrans(@RequestBody AlipayTrans alipayTrans,HttpSession session) {
        User loginUser=getLoginUser(session);
        Message message=new Message();
        if (loginUser==null){
            message.setSuccess(false);
            message.setMessage("登录超时，请重新登录!!");
            message.setWrongCode(WrongCodeEnum.NOT_LOGIN.toCode());
            return new ResponseEntity<Message>(message,HttpStatus.OK);
        }

        List<AlipayTrans> transes=alipayTransService.findSubmittedTransByUser(loginUser);
        double totalFee=0d;
        if (transes!=null&&transes.size()>0){
            for (AlipayTrans trans:transes){
                totalFee+=trans.getFee();
            }
            List<UserMeasure> measures=ServiceManager.userMeasureService.findByUser(loginUser);
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
        alipayTrans.setUser(loginUser);
        Date now=new Date();
        alipayTrans.setDate(now);
        ServiceManager.alipayTransService.insert(alipayTrans);
        Notify notify=new Notify();
        notify.setTitle("系统通知");
        notify.setDate(now);
        notify.setNotifyType(NotifyTypeCodeEnum.SYSTEM.toCode());
        notify.setToUser(loginUser);
        notify.setContent("您的提现请求已提交，提现金额为" + BigDecimalUtil.format_twoDecimal(alipayTrans.getFee()) + "元,请等候处理。");
        ServiceManager.notifyService.insert(notify);
        message.setSuccess(true);
        message.setMessage("您的提现请求已提交，请等候处理。");
        return new ResponseEntity<Message>(message,HttpStatus.OK);

    }
    //支付宝批量转账
    @RequestMapping(value="/batch_trans/trans")
    public void batchTrans(String data,HttpServletRequest request,HttpServletResponse response,HttpSession session,ModelMap model)  {
        JSONArray transList= JSONArray.fromObject(data);
        // 定义一个list集合假数据
        List<String[]> lst = new ArrayList();
        double totalFee=0d;
        for (int i=0;i<transList.size();i++){
            String[] strs = new String[10];
            JSONObject trans=transList.getJSONObject(i);
            JSONObject user= (JSONObject) trans.get("user");
            JSONObject bankAccount= (JSONObject) user.get("bankAccount");

            double fee=Double.parseDouble(trans.get("fee").toString());
            strs[0]=(i+1)+"";
            strs[1]=bankAccount.get("accountName").toString();
            strs[2]=bankAccount.get("idCardNo").toString();
            strs[3]=bankAccount.get("phone").toString();
            strs[4]=bankAccount.get("bankName").toString();
            strs[5]=bankAccount.get("province").toString();
            strs[6]=bankAccount.get("city").toString();
            strs[7]=bankAccount.get("openingBank").toString();
            strs[8]=bankAccount.get("accountNo").toString();
            strs[9]=BigDecimalUtil.format_twoDecimal(fee);
            totalFee+=fee;
            lst.add(strs);
        }
        String string="顺序号|收款户名|身份证号|手机号|收款银行|收款账号省份|收款账号地市|收款账号开户行|收款账号|收款金额\r\n";
        int lineNumber=0;
        for(String[] strings:lst){
            lineNumber++;
            String line="";
            for (String str:strings){
                if (line.length()>0)
                    line+="|"+str;
                else line+=str;
            }
            if (lst.size()!=lineNumber){
                string+=line+"\r\n";
            }else{
                string+=line;
            }

        }
        String fileDirPath="D:\\develop\\projects\\ideaProjects\\anhua-heicha\\src\\main\\webapp\\statics\\temp";
        String filePath=fileDirPath+"\\"+ DateUtil.getCurrentYMD()+".txt";
        try {
            FileUtil.writeFile(string, filePath);
            FileUtil.fileDownload(response,filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AlipayBatchTrans alipayBatchTrans=new AlipayBatchTrans();
        alipayBatchTrans.setBatchFee(totalFee);

        alipayBatchTrans.setPayDate(DateUtil.getCurrentYMD());
        alipayBatchTrans.setDate(new Date());
        alipayBatchTrans.setDetailData(string);
        alipayBatchTransService.insert(alipayBatchTrans);
        List<String> ids=getIds(transList);
        alipayTransService.updateByIds(ids,"alipayBatchTrans",alipayBatchTrans);
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
    /**
     * 付款通知
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value="/notify")
    public void notify_url(HttpServletRequest request,HttpServletResponse response) throws IOException {



    }

    @RequestMapping(value = "/return_true")
    public void returnUrl(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");

        PrintWriter out=response.getWriter();

		out.println("<a href='/vip'><h4>返回会员中心，您可能需要重新登录。</h4></a>");
    }
    @RequestMapping(value = "/return_false")
    public void returnFalseUrl(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");

        PrintWriter out=response.getWriter();

        out.println("<a href='/vip'><h4>返回会员中心，您可能需要重新登录。</h4></a>");
    }
    @RequestMapping(value = "/banks")
    public ResponseEntity<List<HuanxunSupportBank>> bankNames() throws IOException {
        List<HuanxunSupportBank> banks = huanxunSupportBankService.findAll();
        return new ResponseEntity<List<HuanxunSupportBank>>(banks, HttpStatus.OK);
    }



    public static void main(String[] args){
        System.out.println(BigDecimalUtil.format_twoDecimal(2.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(22.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(222.5d));
        System.out.println(BigDecimalUtil.format_twoDecimal(2222.5d));
    }
} 
