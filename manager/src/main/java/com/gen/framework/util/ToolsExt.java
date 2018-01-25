package com.gen.framework.util;

import com.gen.common.util.Tools;
import com.gen.framework.beans.SysMenuBean;
import com.gen.framework.services.SysManagerService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolsExt extends Tools {
    public static List getSysPowerMenu(Integer uid){
        ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        WebApplicationContext wa= WebApplicationContextUtils.getWebApplicationContext(attrs.getRequest().getServletContext());
        SysManagerService cm=wa.getBean(SysManagerService.class);
        List<SysMenuBean> smList=cm.getPowerMenu(uid);
        return smList;
    }
    public static List getUserPowerMenu(){
        ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpSession session=attrs.getRequest().getSession();
        Map useMap=(Map) session.getAttribute("user");
        if(useMap!=null){
            return getSysPowerMenu((Integer)useMap.get("id"));
        }
        return new ArrayList();
    }
}
