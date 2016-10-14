package com.lingyun.test.controller;

import com.lingyun.common.base.BaseRestSpringController;
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

}
