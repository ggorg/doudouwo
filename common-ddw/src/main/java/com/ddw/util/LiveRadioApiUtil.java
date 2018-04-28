package com.ddw.util;

import com.ddw.beans.LiveRadioUrlBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class LiveRadioApiUtil {
    public static LiveRadioUrlBean createLiveUrl(String streamIdExt,Date endDate)throws Exception{
        String txTime=txTime= Long.toHexString(endDate.getTime()/1000).toUpperCase();
        String streamid=LiveRadioConstant.BIZID+"_"+streamIdExt;
        String md5str= DigestUtils.md5Hex(LiveRadioConstant.REFERER_KEY+streamid+txTime);
        LiveRadioUrlBean liveRadioUrlBean=new LiveRadioUrlBean();
        StringBuilder url=new StringBuilder();
        url.append("rtmp://");
        url.append(LiveRadioConstant.BIZID);
        url.append(".livepush.myqcloud.com/live/");
        url.append(streamid);
        liveRadioUrlBean.setPullUrl(url.toString().replace("livepush","liveplay"));

        url.append("?bizid=").append(LiveRadioConstant.BIZID).append("&");

        url.append("txSecret=").append(md5str);
        url.append("&txTime=").append(txTime);
        liveRadioUrlBean.setPushUrl(url.toString());
        liveRadioUrlBean.setStreamid(streamid);
        return liveRadioUrlBean;
    }
}
