package com.ddw;

import com.ApiApplication;
import com.ddw.beans.CodeDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.LiveRadioClientService;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class LiveRadioTest {

    @Autowired
    private LiveRadioClientService liveRadioClientService;

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
}
