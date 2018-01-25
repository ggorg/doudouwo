package com.gen.common.services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @CachePut(value ="commonCache",key="#key" )
    public Object set(String key,Object value){
        return value;
    }
    @CacheEvict(value = "commonCache", key = "#key")
    public void delete(String key){
    }
    @Cacheable(value ="commonCache",key="#key" )
    public Object get(String key){
        return null;
    }

    @CachePut(value ="validCodeCache",key="#key" )
    public String setValidCode(String key,String value){
        return value;
    }
    @CacheEvict(value = "validCodeCache", key = "#key")
    public void deleteValidCode(String key){
    }
    @Cacheable(value ="validCodeCache",key="#key" )
    public String getValidCode(String key){
        return null;
    }

}
