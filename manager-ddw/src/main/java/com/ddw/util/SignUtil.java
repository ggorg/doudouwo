package com.ddw.util;

import com.alipay.api.internal.util.AlipaySignature;
import com.ddw.beans.AliPayCallBackDTO;
import com.ddw.beans.WeiXinPayCallBackDTO;
import com.gen.common.util.BeanToMapUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Set;
import java.util.TreeMap;

public class SignUtil {

    public static boolean aliPaySign(AliPayCallBackDTO dto)throws Exception{
        TreeMap treeMap=new TreeMap(BeanToMapUtil.beanToMap(dto,true));
        treeMap.remove("sign");
        InputStream pis=PayApiUtil.class.getClassLoader().getResourceAsStream("alipaysign/public_key");
        String publicKey= IOUtils.toString(pis);
        IOUtils.closeQuietly(pis);
        return AlipaySignature.rsaCheckV1(treeMap,publicKey,"utf-8","RSA2");
    }
    public static boolean wxPaySign(WeiXinPayCallBackDTO dto){
        TreeMap treeMap=new TreeMap(BeanToMapUtil.beanToMap(dto,true));
        treeMap.put("appid",PayApiConstant.WEI_XIN_PAY_APP_ID);
        treeMap.put("mch_id",PayApiConstant.WEI_XIN_PAY_MCH_ID);
        treeMap.remove("sign");
        Set<String> keys=treeMap.keySet();
        StringBuilder builder=new StringBuilder();
        for(String key:keys){
            builder.append(key).append("=").append(treeMap.get(key)).append("&");
        }

        builder.deleteCharAt(builder.length()-1);
        String sign= DigestUtils.md5Hex(builder.toString()).toUpperCase();
        if(sign.equals(dto.getSign())){
            return true;
        }
        return false;

    }
}
