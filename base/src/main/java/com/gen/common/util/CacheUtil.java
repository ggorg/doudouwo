package com.gen.common.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.http.HttpEntity;

public class CacheUtil {
    public static void put(String cacheName,Object key,Object value){
        Cache cache=createCache(cacheName);
        Element element=new Element(key,value);
        cache.put(element);
    }
    public static Object  get(String cacheName,Object key){
        Cache cache=createCache(cacheName);
        Element element=cache.get(key);
        return element.getObjectValue();

    }
    public static void delete(String cacheName,Object key){
        Cache cache=createCache(cacheName);

        cache.remove(key);
       // cache.flush();
    }
    private static Cache createCache(String cacheName){
        CacheManager cacheManager=CacheManager.create();

        Cache cache=cacheManager.getCache(cacheName);
        return cache;
    }
}
