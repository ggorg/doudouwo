package com.ddw;

import com.gen.common.util.HttpUtil;
import org.apache.commons.io.FileUtils;

import java.util.HashMap;
import java.util.Map;

public class HTTPTest {
    public static void main(String[] args) {
        //ODA1MDA0Nzc4NDIwMTgxMDIzNjk1MDAwOTI4MDE1Mzc=
        Map data=new HashMap();
        data.put("name","测试");
        data.put("type",1);
        data.put("tableCode",1);
        data.put("peopleMaxNum",10);
        data.put("endTime","2018-10-31 10:10:10");
        data.put("roomImg", FileUtils.getFile("C:\\Users\\Administrator\\Desktop\\logo.png"));
        HttpUtil.doPostWithFile("http://127.0.0.1:8833/ddwapp/goodfriendplay/room/create/OTYzMDI3NzAxNzIwMTgxMDIzMDM0NDI1ODU4MjE2MzE=",data);
    }
}
