package com.gen.framework.util;

import com.gen.framework.beans.SysMenuBean;

import java.util.Comparator;
import java.util.Map;

public class MenuMapComparator implements Comparator {

    public int compare(Object o1, Object o2) {

        Integer mSort=null;
        Integer mSort2=null;
        Integer mParentId=null;
        Integer mParentId2=null;
        if(o1 instanceof Map && o2 instanceof  Map){
            Map map=(Map)o1;
            Map map2=(Map)o2;

            mSort=(Integer) map.get("mSort");
            mSort2=(Integer) map2.get("mSort");
            mParentId=(Integer) map.get("mParentId");
            mParentId2=(Integer) map2.get("mParentId");
        }else if(o1 instanceof SysMenuBean && o2 instanceof  SysMenuBean){
            SysMenuBean map=(SysMenuBean)o1;
            SysMenuBean map2=(SysMenuBean)o2;

            mSort=map.getmSort();
            mSort2=map2.getmSort();
            mParentId= map.getmParentId();
            mParentId2= map2.getmParentId();

        }

        if(mParentId==mParentId2){
            if(mSort2>mSort)return -1;
        }
        return 1;
    }
}
