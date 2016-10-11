package com.lingyun.test.controller;

import com.lingyun.common.base.BaseRestSpringController;
import com.lingyun.common.code.WrongCodeEnum;
import com.lingyun.common.constant.Constant;
import com.lingyun.common.helper.service.ProjectContext;
import com.lingyun.common.helper.service.ServiceManager;
import com.lingyun.common.util.*;
import com.lingyun.common.web.CookieTool;
import com.lingyun.entity.*;
import com.lingyun.mall.dao.TestUser;
import com.lingyun.mall.service.IProductSeriesService;
import com.lingyun.mall.service.IUserMeasureService;
import com.lingyun.mall.service.impl.UserService;
import com.lingyun.support.callBack.CallBackInterface;
import com.lingyun.support.callBack.impl.Callback_Zhizihua;
import com.lingyun.support.vo.Message;
import com.lingyun.support.vo.NotifySearch;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.util.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
