package com.lingyun.support.callBack.impl;

import com.lingyun.support.callBack.AbstrackCallback;
import com.lingyun.support.vo.Message;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/7.
 */
public class Callback_Zhizihua extends AbstrackCallback {

    private String returnValue;
    private Message message;

    public Callback_Zhizihua(String returnValue) {
        this.returnValue = returnValue;

    }

    /**
     * 0000 注册成功
     * 1111 操作失败
     * 0111 缺失的参数
     * 0011 存在重复的登录Id
     */
    private Map<String,String> successMap;
    /**
     * 	错误说明
     */
    private Map<String,Object> returnValueMap;
    @Override
    public Map<String, Object> getReturnValueMap() {
        if(returnValue==null)
            return null;
        if (returnValueMap!=null) return this.returnValueMap;
        Map<String, Object> map=new HashMap<String, Object>();
        JSONObject jsonObject=JSONObject.fromObject(returnValue);
        map.put("success", jsonObject.get("success").toString());
        map.put("msg", jsonObject.get("msg").toString());
        this.returnValueMap=map;
        return this.returnValueMap;
    }
    @Override
    public Message getMessage() {
        if (this.message!=null) return this.message;
        this.message=new Message();
        Map returnValueMap=getReturnValueMap();
        if(returnValueMap.get("success")!=null&&returnValueMap.get("success").toString().equals("0000")){
            message.setSuccess(true);
        }else message.setSuccess(false);
        message.setMessage(returnValueMap.get("msg")==null?null:returnValueMap.get("msg").toString());
        return message;
    }

    @Override
    public boolean isSuccess() {
       return super.isSuccess();
    }

    public static void main(String[] args){
        Callback_Zhizihua callBack=new Callback_Zhizihua("{'success':'0000','msg':'some message'}");
        callBack.getReturnValueMap();
    }
}
