package com.ddw.token;

import com.gen.common.services.CacheService;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {
    private static final Logger logger = Logger.getLogger(TokenUtil.class);

    public static String createToken(Object userobj){
        CacheService cs=getCacheService();
        String tokenStr= DateFormatUtils.format(new Date(),RandomStringUtils.randomNumeric(10)+"yyyyMMdd"+ RandomStringUtils.randomNumeric(10)+"HHmm");
        String base64Token=Base64Utils.encodeToString(tokenStr.getBytes());
        base64Token=base64Token.replace("+","-").replace("/","_");
        cs.set(base64Token,userobj);
        return base64Token;
    }
    public static void deleteToken(String token){
        CacheService cs=getCacheService();
        cs.delete(token);
    }
    public static String getBaseToken(String base64Token){
        String token=null;
        try {
            token=new String(Base64Utils.decodeFromString(base64Token.replace("-","+").replace("_","/")));

        }catch (Exception e){
            logger.error("getBaseToken",e);
        }
        return token;
    }
    public static boolean hasToken(String base64Token){
        CacheService cs=getCacheService();
        if(cs.get(base64Token)!=null){
            return true;
        }
        return false;
    }
    public static boolean validToken(String baseToken){
        return baseToken.matches("^([0-9]{10})([0-9]{8})([0-9]{10})([0-9]{4})$");
    }
    public static Object getObjByToken(String base64Token){
        CacheService cs=getCacheService();
        return cs.get(base64Token);
    }
    public static String  getTimeByToken(String baseToken){

        if(baseToken.length()==32){
            return baseToken.replaceAll("^([0-9]{10})([0-9]{8})([0-9]{10})([0-9]{4})$","$2$4");
        }
        return null;

    }

    public static boolean isOverTime(String baseToken,Integer hour){
        String timeStr=getTimeByToken(baseToken);
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
        System.out.println(new String(Base64Utils.decodeFromString(Base64Utils.encodeToString("123456".getBytes()))));
        String tokenStr= DateFormatUtils.format(new Date(),RandomStringUtils.randomNumeric(10)+"yyyyMMdd"+ RandomStringUtils.randomNumeric(10)+"HHmm");
        String base64Token=Base64Utils.encodeToString(tokenStr.getBytes());
        System.out.println(base64Token);
        System.out.println(Hex.encodeHexString(base64Token.getBytes()));
    }
}
