package com.ddw.util;

import com.ddw.enums.BusinessCodeRuleEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class BusinessCodeUtil {

    public static String createLiveRadioCode(Integer userid,Integer storeid){
        StringBuilder builder=new StringBuilder();
        builder.append("$1").append(storeid).append(userid).append(RandomStringUtils.randomNumeric(5));
       String codeStr= BusinessCodeRuleEnum.liveRadioCode.getName().replaceAll("(.*)([{]storeid[}])([{]userid[}])([{]random[}])",builder.toString());
        return DateFormatUtils.format(new Date(),codeStr);
    }
}
