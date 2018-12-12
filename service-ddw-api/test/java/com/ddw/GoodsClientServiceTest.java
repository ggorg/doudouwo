package com.ddw;

import com.ApiApplication;
import com.ddw.enums.GoodsPlatePosEnum;
import com.ddw.services.GoodsClientService;
import com.ddw.token.TokenUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class GoodsClientServiceTest {

    @Autowired
    private GoodsClientService goodsClientService;

    @Test
    public void testGoodsIndex()throws Exception{
        String token= TokenUtil.createToken("openid");
        TokenUtil.putStoreid(token,2);
        TokenUtil.putUseridAndName(token,26,"test123");
        this.goodsClientService.goodsIndex(2, GoodsPlatePosEnum.GoodsPlatePos1);
    }
}
