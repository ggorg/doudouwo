package com.ddw.util;

import com.ddw.beans.CouponVO;

import java.math.BigDecimal;
import java.util.*;

public class CouponComparator implements Comparator {



    public int compare(Object o1, Object o2) {
        CouponVO m1=(CouponVO)o1;
        CouponVO m2=(CouponVO)o2;
       if(m1.getExpire()>m2.getExpire()){
           return -1;
       }
        return 1;
    }

    public static void main(String[] args) {
       CouponVO v=new CouponVO();
       v.setExpire(-1);;
       CouponVO v1=new CouponVO();
       v1.setExpire(1);
       List l=new ArrayList();
       l.add(v);
       l.add(v1);
       Collections.sort(l,new CouponComparator());
        System.out.println(l);
    }
}
