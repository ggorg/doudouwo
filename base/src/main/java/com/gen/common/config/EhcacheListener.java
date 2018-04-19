package com.gen.common.config;


import net.sf.ehcache.CacheManager;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class EhcacheListener  implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent){
            System.setProperty("net.sf.ehcache.enableShutdownHook","true");
        }
        else if (event instanceof ApplicationReadyEvent) {


        }
        else if (event instanceof ContextStartedEvent) {

        }

        else if (event instanceof ContextStoppedEvent) {

        }
        else if (event instanceof ContextClosedEvent) {

            List knownCacheManagers = CacheManager.ALL_CACHE_MANAGERS;


            while(!knownCacheManagers.isEmpty()) {
                ((CacheManager)CacheManager.ALL_CACHE_MANAGERS.get(0)).shutdown();
            }
                 // 应用关闭
             }
    }
}
