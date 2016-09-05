package com.lingyun.common.util;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/12.
 */
public class CustomServletRequestWrapper extends ServletRequestWrapper {
    private ServletRequest request;
    public CustomServletRequestWrapper(ServletRequest request) {
        super(request);
    }
    public Map<String,String[]> getParameterMap() {
        Map<String, String[]> result = new LinkedHashMap<String, String[]>();
        Enumeration<String> names = this.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            result.put(name, this.getParameterValues(name));
        }
        return result;
    }
}
