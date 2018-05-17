package com.ddw.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.ddw.beans.AliPayCallBackDTO;
import com.ddw.beans.WeiXinPayCallBackDTO;
import com.ddw.enums.OrderTypeEnum;
import com.gen.common.util.BeanToMapUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.InputStream;
import java.util.*;

public class SignUtil {

    public static boolean aliPaySign(Map dto)throws Exception{
        InputStream pis=PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/public_key");
        String publicKey= IOUtils.toString(pis);
        IOUtils.closeQuietly(pis);
        return AlipaySignature.rsaCheckV1(dto,publicKey,"utf-8","RSA2");
    }
    public static boolean wxPaySign(Map<String,String> dto){
        TreeMap treeMap=new TreeMap(dto);
        //treeMap.put("appid",PayApiConstant.WEI_XIN_PAY_APP_ID);
       // treeMap.put("mch_id",PayApiConstant.WEI_XIN_PAY_MCH_ID);
        treeMap.remove("sign");
        Set<String> keys=treeMap.keySet();
        StringBuilder builder=new StringBuilder();
        Object objValue=null;
        for(String key:keys){
            objValue=treeMap.get(key);
            builder.append(key).append("=").append(objValue==null?"":objValue).append("&");


        }
        builder.append("key=").append(PayApiConstant.WEI_XIN_PAY_KEY);
        String sign= DigestUtils.md5Hex(builder.toString()).toUpperCase();
        if(sign.equals(dto.get("sign"))){
            return true;
        }
        return false;

    }


}
