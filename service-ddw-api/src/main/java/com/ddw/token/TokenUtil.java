package com.ddw.token;

import com.ddw.beans.UserInfoVO;
import com.gen.common.services.CacheService;
import com.gen.common.util.CacheUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenUtil {
    private static final Logger logger = Logger.getLogger(TokenUtil.class);

    public static String createToken(String openId){
        CacheService cs=getCacheService();
        String tokenStr= DateFormatUtils.format(new Date(),RandomStringUtils.randomNumeric(10)+"yyyyMMdd"+ RandomStringUtils.randomNumeric(10)+"HHmm");
        String base64Token=Base64Utils.encodeToString(tokenStr.getBytes());
        base64Token=base64Token.replace("+","-").replace("/","_");
        Map map=new HashMap();
        map.put("user",openId);
        Cache cache=CacheUtil.createCache("tokenCache");
        List<String> keys=cache.getKeys();
        if(keys!=null && !keys.isEmpty()){
            Map cacheMap=null;
            Element element=null;
            for(String key:keys){
                element=cache.getQuiet(key);
                cacheMap=(Map) element.getObjectValue();
                if(openId.equals(cacheMap.get("user"))){
                    cache.remove(key);
                    break;
                }
            }
        }
        CacheUtil.put("tokenCache",base64Token,map);
       // cs.set(base64Token,map);
        return base64Token;
    }
    public static void resetUserObject(String base64Token,Object userobj){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("user",userobj);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static Object getUserObject(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return map.get("user");
        }
        return null;
    }
    public static void putStoreid(String base64Token,Integer storeId){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("storeId",storeId);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static String getStoreLongLat(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (String)map.get("longLat");
        }
        return null;
    }
    public static void putStoreLongLat(String base64Token,String storeLongLat){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("longLat",storeLongLat);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static void putGroupId(String base64Token,String groupId){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("groupId",groupId);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static void putBidCode(String base64Token,Integer bidCode){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("bidCode",bidCode);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static Integer getBidCode(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (Integer)map.get("bidCode");
        }
        return null;
    }
    public static void putStreamId(String base64Token,String streamId){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("streamId",streamId);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }

    /**
     * 支付校验code
     * @param base64Token
     * @param payCode
     */
    public static void putPayCode(String base64Token,String payCode){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("payCode",payCode);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }

    public static void putIdempotent(String base64Token,String idemp){

        CacheUtil.put("idemp",base64Token,idemp);

    }
    public static void putUserInfo(String base64Token, UserInfoVO vo){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("userId",vo.getId());
            map.put("name",vo.getNickName());
            if(vo.getGradeId()!=null && vo.getGradeId()>0){
                map.put("gradeId",vo.getGradeId());
            }

            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static void putUseridAndName(String base64Token,Integer userId,String name){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("userId",userId);
            map.put("name",name);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static void putUserGrade(String base64Token,Integer gradeId){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("gradeId",gradeId);
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static void putRoomId(String base64Token,Integer roomId){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            if(roomId==null){
                map.remove("roomId");
            }else{
                map.put("roomId",roomId);
            }
            CacheUtil.put("tokenCache",base64Token,map);
        }
    }
    public static Integer getUseGrade(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (Integer) map.get("gradeId");
        }
        return null;
    }
    public static String getUserName(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (String) map.get("name");
        }
        return null;
    }
    public static String getStreamId(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (String) map.get("streamId");
        }
        return null;
    }
    public static String getPayCode(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (String) map.get("payCode");
        }
        return null;
    }
    public static String getIdemp(String base64Token){
        Object obj=CacheUtil.get("idemp",base64Token);
        if(obj!=null){
            return (String) obj;
        }
        return null;
    }
    public static Integer getStoreId(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (Integer) map.get("storeId");
        }
        return null;
    }
    public static String getGroupId(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (String) map.get("groupId");
        }
        return null;
    }
    public static Integer getUserId(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (Integer) map.get("userId");
        }
        return null;
    }
    public static Integer getRoomId(String base64Token){
        Object obj=CacheUtil.get("tokenCache",base64Token);
        if(obj!=null){
            Map map=(Map)obj;
            return (Integer) map.get("roomId");
        }
        return null;
    }
    public static void deleteToken(String token){
        CacheUtil.delete("tokenCache",token);

    }
    public static void deletePayCode(String token){
        Object obj=CacheUtil.get("tokenCache",token);
        if(obj!=null){
            Map map=(Map)obj;
            map.remove("payCode");
            CacheUtil.put("tokenCache",token,map);
        }

    }
    public static String getBaseToken(String base64Token){
        String token=null;
        try {
            token=new String(Base64Utils.decodeFromString(base64Token.replace("-","+").replace("_","/")));

        }catch (Exception e){
            logger.error("token异常-》getBaseToken："+base64Token);

        }
        return token;
    }
    public static boolean hasToken(String base64Token){

        if(CacheUtil.get("tokenCache",base64Token)!=null){
            return true;
        }
        return false;
    }
    public static boolean validToken(String baseToken){
        return baseToken.matches("^([0-9]{10})([0-9]{8})([0-9]{10})([0-9]{4})$");
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
