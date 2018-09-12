package com.ddw;

import com.ApiApplication;
import com.ddw.beans.CodeDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.BaseDynService;
import com.ddw.services.LiveRadioClientService;
import com.ddw.services.LiveRadioService;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class LiveRadioTest {

    @Autowired
    private LiveRadioClientService liveRadioClientService;

    @Autowired
    private BaseDynService liveRadioService;

    @Test
    public void selectLiveRadioTest()throws Exception{
        CodeDTO codeDTO=new CodeDTO();
        codeDTO.setCode(10);
        String token=TokenUtil.createToken("openid");
        TokenUtil.putStoreid(token,1);
        TokenUtil.putUseridAndName(token,26,"test123");
        ResponseApiVO vo=liveRadioClientService.selectLiveRadio(codeDTO,token);
        System.out.println(vo);
       // System.out.println(vo..getData());
    }
    @Test
    public void saveDyn()throws Exception{
        liveRadioService.saveGoddessLiveDyn("23115_1_8_180830214823");
    }
    @Test
    public void saveBidDyn()throws Exception{
        Map bidMap=new HashMap();
        bidMap.put("id",57);
        bidMap.put("userId",8);
        bidMap.put("luckyDogUserName","gen");
        bidMap.put("luckyDogUserId",26);
        liveRadioService.saveBideDyn(bidMap, DateUtils.parseDate("2018-07-20 21:05:16","yyyy-MM-dd HH:mm:ss"));
    }
}
