package com.lingyun.test.controller;

import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.entity.AlipayBatchTrans;
import com.lingyun.entity.AlipayTrans;
import com.lingyun.entity.User;
import com.lingyun.mall.dao.TestUser;
import com.lingyun.mall.service.IProductSeriesService;
import com.lingyun.mall.service.IUserMeasureService;
import com.lingyun.mall.service.impl.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/test")
public class TestController extends BaseRestSpringController {
    private static Logger logger = LogManager.getLogger();
    protected static final String DEFAULT_SORT_COLUMNS = null;
    protected static final String REDIRECT_ACTION = "";
    @Resource private IProductSeriesService productSeriesService;
    @Resource(name = "userService")
    UserService userService;
    @Resource
    IUserMeasureService userMeasureService;
    @InitBinder("productSeries")
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }


    @RequestMapping(value="/mode")
    public String measure() {
        int level=1;
        int levelUserCount=1;
        int totalUserCount=1;
        Map<Integer,Integer> levelUserCountMap=new HashMap<Integer, Integer>();;
        Map<Integer,Integer> totalUserCountMap=new HashMap<Integer, Integer>();;
        Map<Integer,List<TestUser>> levelUsersMap=new HashMap<Integer, List<TestUser>>();
        levelUserCountMap.put(1, 1);
        totalUserCountMap.put(1, 1);
        TestUser user=new TestUser();
        user.setLevel(1);
        user.setOrderInLevel(1);
        List<TestUser> levelUsers=new ArrayList<TestUser>();
        levelUsers.add(user);
        levelUsersMap.put(1,levelUsers);
        userMeasureService.testMode(level,levelUserCount,totalUserCount,levelUserCountMap,totalUserCountMap,levelUsersMap);
        return "forward:/indexsss";
    }
    @RequestMapping(value="/trans")
    public void trans(HttpServletRequest request,HttpServletResponse response){
        User user=ServiceManager.userService.findById("57ac237d2f02c8fa50a9b5f9");
        List<AlipayTrans> transes=ServiceManager.alipayTransService.findSubmittedTransByUser(user);
        return;
    }
    @RequestMapping(value="/batch_trans")
    public void batch_trans(HttpServletRequest request,HttpServletResponse response){
        AlipayBatchTrans alipayBatchTrans=ServiceManager.alipayBatchTransService.getMax("batchNoSn", "batchFee", 0.01);

//        ServiceManager.alipayTransService.updateByIds(new String[]{"5803db56d8326520d0be51e0","580499f0d832651eec42b150"},"alipayBatchTrans",alipayBatchTrans);
        return;
    }
    @RequestMapping(value="/removeAll")
    public void removeAll(HttpServletRequest request,HttpServletResponse response){
        ServiceManager.alipayTransService.removeAll();
        ServiceManager.accountService.removeAll();
        ServiceManager.alipayBatchTransService.removeAll();
        ServiceManager.authorizeInfoService.removeAll();
        ServiceManager.bankService.removeAll();
        ServiceManager.notifyService.removeAll();
        ServiceManager.orderService.removeAll();
        ServiceManager.userService.removeAll();
        ServiceManager.userMeasureService.removeAll();
        ServiceManager.userPointsService.removeAll();
    }
}
