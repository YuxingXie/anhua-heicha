package com.lingyun.common.bean;

import org.springframework.beans.factory.config.BeanPostProcessor;
import com.lingyun.mall.service.impl.StartOnLoadService;
import org.springframework.beans.BeansException;

import java.io.IOException;

/**
 * Created by Administrator on 2017/5/19.
 */
public class InitDataPostProcessor implements BeanPostProcessor {
    public Object postProcessAfterInitialization(Object obj, String arg1)
            throws BeansException {
        if(obj instanceof StartOnLoadService) {
            try {
                ((StartOnLoadService)obj).loadData(); //调用方法加载数据
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public Object postProcessBeforeInitialization(Object arg0, String arg1)
            throws BeansException {
        return arg0;
    }
}
