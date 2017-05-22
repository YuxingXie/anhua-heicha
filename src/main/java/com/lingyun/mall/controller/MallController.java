package com.lingyun.mall.controller;

import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.code.AlipayTransStatusEnum;
import com.lingyun.common.code.NotifyTypeCodeEnum;
import com.lingyun.common.code.WrongCodeEnum;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.BigDecimalUtil;
import com.lingyun.common.util.DateUtil;
import com.lingyun.common.util.FileUtil;
import com.lingyun.entity.*;
import com.lingyun.mall.service.IAlipayBatchTransService;
import com.lingyun.mall.service.IAlipayTransService;
import com.lingyun.mall.service.IHuanxunSupportBankService;
import com.lingyun.mall.service.IHuanxunSupportOpeningBankService;
import com.lingyun.support.vo.Message;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/mall")
public class MallController extends BaseRestSpringController {
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
    @RequestMapping("")
    public String index(){
        return "redirect:/statics/mall/index.html";
    }
} 
