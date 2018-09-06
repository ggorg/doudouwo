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
            if(a1.getFans()<a2.getFans()){
                return 1;
            }
        }else if(!LiveStatusEnum.liveStatus1.getCode().equals(a1.getLiveRadioFlag()) && !LiveStatusEnum.liveStatus1.getCode().equals(a2.getLiveRadioFlag())){
            if(a1.getFans()<a2.getFans()){
                return 1;
            }
        }else if(LiveStatusEnum.liveStatus1.getCode().equals(a1.getLiveRadioFlag()) && !LiveStatusEnum.liveStatus1.getCode().equals(a2.getLiveRadioFlag())){
            return -1;
        }else if(!LiveStatusEnum.liveStatus1.getCode().equals(a1.getLiveRadioFlag()) && LiveStatusEnum.liveStatus1.getCode().equals(a2.getLiveRadioFlag())){
            return 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        AppIndexGoddessVO a1=new AppIndexGoddessVO();
        a1.setDsName("sunnye");
        a1.setFans(14);
        a1.setLiveRadioFlag(1);
        AppIndexGoddessVO a2=new AppIndexGoddessVO();
        a2.setDsName("桥先生");
        a2.setFans(14);
        a2.setLiveRadioFlag(2);
        AppIndexGoddessVO a3=new AppIndexGoddessVO();
        a3.setDsName("sunnyxy");
        a3.setFans(19);
        a3.setLiveRadioFlag(2);
        AppIndexGoddessVO a4=new AppIndexGoddessVO();
        a4.setDsName("gen");
        a4.setFans(1);
        a4.setLiveRadioFlag(1);
        List list=new ArrayList();
        list.add(a4);
        list.add(a3);
        list.add(a2);
       list.add(a1);

        Collections.sort(list,new IndexGoddessComparator());
        System.out.println(list);

    }
}
