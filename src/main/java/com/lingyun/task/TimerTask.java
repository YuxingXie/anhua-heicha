package com.lingyun.task;

import com.lingyun.mall.service.impl.OrderService;
import com.lingyun.mall.service.impl.UserPointsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimerTask {
    @Resource
    private OrderService orderService;
    @Resource
    private UserPointsService userPointsService;
    private static Logger logger = LogManager.getLogger();
    public void printTimeStamp(){
        Calendar ca= Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ", Locale.CHINA);
        //显示当前时间 精确到毫秒
        logger.info(sdf.format(ca.getTime()));
    }
    public TimerTask(){
//        this.printTimeStamp();
        logger.info("计划任务被初始化了");
    }
    public void doTask(){
        int points=20;
        userPointsService.addPointsToAllUser(points);
    }
    public static void main(String[] args){
        String longStr="1000*60*60*24*7";

    }
}