package com.ddw.util;

import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.enums.LiveEventTypeEnum;
import com.ddw.enums.LiveStatusEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IndexGoddessComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        AppIndexGoddessVO a1=(AppIndexGoddessVO)o1;
        AppIndexGoddessVO a2=(AppIndexGoddessVO)o2;
        if(LiveStatusEnum.liveStatus1.getCode().equals(a1.getLiveRadioFlag()) && LiveStatusEnum.liveStatus1.getCode().equals(a2.getLiveRadioFlag())){
            if(a1.getFans()>a2.getFans()){
                return -1;
            }
        }else if(LiveStatusEnum.liveStatus1.getCode().equals(a1.getLiveRadioFlag()) && !LiveStatusEnum.liveStatus1.getCode().equals(a2.getLiveRadioFlag())){
            return -1;
        }else if(!LiveStatusEnum.liveStatus1.getCode().equals(a1.getLiveRadioFlag()) && LiveStatusEnum.liveStatus1.getCode().equals(a2.getLiveRadioFlag())){
            return 1;
        }
        return 1;
    }

    public static void main(String[] args) {
        AppIndexGoddessVO a1=new AppIndexGoddessVO();
        a1.setDsName("a1");
        a1.setFans(10);
        a1.setLiveRadioFlag(0);
        AppIndexGoddessVO a2=new AppIndexGoddessVO();
        a2.setDsName("a2");
        a2.setFans(11);
        a2.setLiveRadioFlag(1);
        AppIndexGoddessVO a3=new AppIndexGoddessVO();
        a3.setDsName("a3");
        a3.setFans(12);
        a3.setLiveRadioFlag(0);
        List list=new ArrayList();
        list.add(a3);
        list.add(a2);
        list.add(a1);
        Collections.sort(list,new IndexGoddessComparator());
        System.out.println(list);

    }
}
