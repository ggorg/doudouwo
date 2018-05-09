package com.gen.common.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.http.HttpEntity;

import java.util.List;

public class CacheUtil {
    private static CacheManager cacheManager;
    public static void put(String cacheName,Object key,Object value){
        Cache cache=createCache(cacheName);
        Element element=new Element(key,value);
        cache.put(element);

    }
    public static Object  get(String cacheName,Object key){
        Cache cache=createCache(cacheName);

        Element element=cache.getQuiet(key);
        cache.flush();
        if(element!=null){
            return element.getObjectValue();
        }
        return null;
    }
    public static void deleteByStartWith(String cacheName,String startWithStr){
        Cache cache=createCache(cacheName);
        List keys=cache.getKeys();
        for(Object k:keys){
            if(k instanceof String ){
                if(((String)k).startsWith(startWithStr)){
                    cache.remove(k);
                }
            }
        }
    }
    public static void delete(String cacheName,Object key){
        Cache cache=createCache(cacheName);

        cache.remove(key);
       // cache.flush();
    }

    private static Cache createCache(String cacheName){
        Cache cache=getCacheManager().getCache(cacheName);
        return cache;
    }
    public static CacheManager getCacheManager(){
        if(cacheManager==null){
            cacheManager=CacheManager.create();
        }
        return cacheManager;
    }

    public static void main(String[] args) {
       //put("pcShoppingCart","abc","test");

    }
}
