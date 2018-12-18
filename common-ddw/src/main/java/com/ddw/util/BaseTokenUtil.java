package com.ddw.util;

import com.gen.common.util.CacheUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Base64Utils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseTokenUtil {
    private static final Logger logger = Logger.getLogger(BaseTokenUtil.class);

    public static String createToken(String openId){
        String tokenStr= DateFormatUtils.format(new Date(),RandomStringUtils.randomNumeric(10)+"yyyyMMdd"+ RandomStringUtils.randomNumeric(10)+"HHmm");
        String base64Token=Base64Utils.encodeToString(tokenStr.getBytes());
        base64Token=base64Token.replace("+","-").replace("/","_");
        Map map=new ConcurrentHashMap();
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
                    map=new ConcurrentHashMap(cacheMap);
                    cache.remove(key);
                    break;
                }
            }
        }

        CacheUtil.put("tokenCache",base64Token,map);
       // cs.set(base64Token,map);
        return base64Token;
    }
    public static void putUserIdAndStoreId(String token,Integer userId,Integer storeId){
        Object obj=CacheUtil.get("tokenCache",token);
        if(obj!=null){
            Map map=(Map)obj;
            map.put("userId",userId);
            map.put("storeId",storeId);
            CacheUtil.put("tokenCache",token,map);
        }
    }

}
