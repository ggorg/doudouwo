package com.gen.framework.util;

import com.gen.common.util.Tools;
import com.gen.framework.beans.SysMenuBean;
import com.gen.framework.services.SysManagerService;
import okhttp3.Address;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolsExt extends Tools {
    public static List getSysPowerMenu(Integer uid){
        WebApplicationContext wa= getWebapplication();
        SysManagerService cm=wa.getBean(SysManagerService.class);
        List<SysMenuBean> smList=cm.getPowerMenu(uid);
        return smList;
    }
    public static Integer getCurrentUserId(){
        Map usermap=(Map)getSession("user");
        if(usermap!=null){
            return (Integer)usermap.get("id");
        }
        return null;
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
