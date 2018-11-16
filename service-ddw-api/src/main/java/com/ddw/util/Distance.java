package com.ddw.util;

import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.vo.LiveRadioListVO;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
 * 参数为double类型
 *  long1 位置1经度
 *  lat1  位置1纬度
 *  long2 位置2经度
 *  lat2  位置2纬度
 */
public class Distance {

    private static final double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return BigDecimal.valueOf(d).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static ResponseApiVO mathGoddessDistance(String paramLanglat,List<LiveRadioListVO> newList){
        if(StringUtils.isNotBlank(paramLanglat)){
            String[] lls= paramLanglat.split(",");
            if(lls.length!=2 || !paramLanglat.matches("^[0-9]+[.][^,]+,[0-9]+[.][0-9]+$")){
                return new ResponseApiVO(-2,"坐标格式有误",null);

            }
            String[] strs=paramLanglat.split(",");
            String[] dataStrs=null;

            for(LiveRadioListVO v:newList){
                if(StringUtils.isNotBlank(v.getLanglat())){
                    dataStrs=v.getLanglat().split(",");
                    if(dataStrs.length==2 && paramLanglat.matches("^[0-9]+[.][^,]+,[0-9]+[.][0-9]+$")){
                        v.setDistance(Distance.getDistance(Double.parseDouble(strs[0]),Double.parseDouble(strs[1]),Double.parseDouble(dataStrs[0]),Double.parseDouble(dataStrs[1]))+"km");
                    }else{
                        v.setDistance("");
                    }

                }

            }

        }
        return new ResponseApiVO(1,"成功",null);
    }
}