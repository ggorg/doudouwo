package com.ddw;

import com.gen.common.util.HttpUtil;
import org.apache.commons.io.FileUtils;

import java.util.HashMap;
import java.util.Map;

public class HTTPTest {
    public static void main(String[] args) {
        String url="http://api.map.baidu.com/location/ip";
        Map map=new HashMap();
        map.put("ak","8H8QQNmZL8BSbzL1sXcuTZ8v");
        map.put("coor","bd09ll");
        map.put("ip","113.102.203.182");
        System.out.println(HttpUtil.doPost(url,map));
    }
}
