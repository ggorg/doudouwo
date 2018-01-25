package com.gen.framework.util;

import java.util.Comparator;
import java.util.Map;

public class MenuMapComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Map map=(Map)o1;
        Map map2=(Map)o2;
        Integer mSort=(Integer) map.get("mSort");
        Integer mSort2=(Integer) map2.get("mSort");
        Integer mParentId=(Integer) map.get("mParentId");
        Integer mParentId2=(Integer) map2.get("mParentId");
        if(mParentId==mParentId2){
            if(mSort2>mSort)return -1;
        }
        return 1;
    }
}
