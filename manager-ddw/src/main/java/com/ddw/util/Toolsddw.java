package com.ddw.util;

import com.gen.common.util.Tools;

import java.util.Map;

public class Toolsddw extends Tools {
    public static Integer getCurrentUserId(){
        Map usermap=(Map)getSession("user");
        if(usermap!=null){
            return (Integer)usermap.get("id");
        }
        return null;
    }

    /**
     * key=id,
     * key=uName,
     * key=uNickName
     * @return
     */
    public static Map getUserMap(){
        return (Map)getSession("user");

    }
}
