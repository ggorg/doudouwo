package com.ddw.token;

import com.gen.common.services.CacheService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {
    public static String createToken(Object userobj){
        CacheService cs=getCacheService();
        String tokenStr= DateFormatUtils.format(new Date(),RandomStringUtils.randomNumeric(10)+"yyyyMMdd"+ RandomStringUtils.randomNumeric(10)+"HHmm");

        cs.set(tokenStr,userobj);
        return tokenStr;
    }
    public static boolean hasToken(String token){
        CacheService cs=getCacheService();
        if(cs.get(token)!=null){
            return true;
        }
        return false;
    }
    public static Object getObjByToken(String token){
        CacheService cs=getCacheService();
        return cs.get(token);
    }
    public static String  getTimeByToken(String token){
        if(token.length()==32){
            return token.replaceAll("^([0-9]{10})([0-9]{8})([0-9]{10})([0-9]{4})$","$2$4");
        }
        return null;

    }

    public static boolean isOverTime(String token,Integer hour){
        String timeStr=getTimeByToken(token);
        if(timeStr!=null){
            try {
                Date newDate=DateUtils.addHours(DateUtils.parseDate(timeStr,"yyyyMMddHHmm"),hour);
                return !newDate.before(new Date());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
    private static CacheService getCacheService(){
        ServletRequestAttributes attrs =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        WebApplicationContext wa= WebApplicationContextUtils.getWebApplicationContext(attrs.getRequest().getServletContext());
        CacheService cs=wa.getBean(CacheService.class);
        return cs;
    }

    public static void main(String[] args) {
        //55498704702018041150605650631754
        System.out.println(isOverTime("55498704702018041150605650631754",24));
    }
}
