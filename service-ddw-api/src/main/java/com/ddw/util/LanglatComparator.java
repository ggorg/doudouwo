package com.ddw.util;

import com.gen.common.util.Page;

import java.math.BigDecimal;
import java.util.*;

public class LanglatComparator implements Comparator {
    private double longN;
    private double latN;
    public LanglatComparator() {
    }
    public LanglatComparator(String longlat){
        String[] strs=longlat.split(",");
        longN=Double.parseDouble(strs[0]);
        latN=Double.parseDouble(strs[1]);
    }

    public int compare(Object o1, Object o2) {
       Map m1=(Map)o1;
       Map m2=(Map)o2;
       double longN1=Double.parseDouble((String)m1.get("dsLongitude"));
       double latN1=Double.parseDouble((String)m1.get("dsLatitude"));
       double longN2=Double.parseDouble((String)m2.get("dsLongitude"));
       double latN2=Double.parseDouble((String)m2.get("dsLatitude"));

       double d1=Distance.getDistance(longN,latN,longN1,latN1);

       double d2=Distance.getDistance(longN,latN,longN2,latN2);

        if(!m1.containsKey("distance")) m1.put("distance",BigDecimal.valueOf(d1).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue()+"km");
        if(!m2.containsKey("distance"))  m2.put("distance",BigDecimal.valueOf(d2).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue()+"km");
       if(d1>d2){
           return -1;
       }
        return 1;
    }

    public static void main(String[] args) {
        LanglatComparator l=new LanglatComparator("104.075781,30.646875");
        List list=new ArrayList();
        Map map=new HashMap();
        //116.437614,40.014918
        map.put("dsLongitude","116.437614");
        map.put("dsLatitude","40.014918");
        Map map2=new HashMap();
        //106.552113,29.559948
        map2.put("dsLongitude","106.552113");
        map2.put("dsLatitude","29.559948");
        Map map3=new HashMap();
        //106.552113,29.559948
        map3.put("dsLongitude","106.552113");
        map3.put("dsLatitude","29.559948");
        list.add(map3);

        Collections.sort(list,l);
        System.out.println(list);

    }
}
