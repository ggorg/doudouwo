package com.ddw;

import com.ApiApplication;
import com.ddw.beans.CodeDTO;
import com.ddw.beans.UserInfoDTO;
import com.ddw.services.GoodFriendPlayService;
import com.ddw.services.UserInfoService;
import com.ddw.token.TokenUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class GoodFriendServiceTest {

    @Autowired
    private GoodFriendPlayService goodFriendPlayService;

    @Test
    public void testJoin()throws Exception{
        String token= TokenUtil.createToken("o_W_K0S2h_J27eeixvzBSn9j0uSU");
        TokenUtil.putStoreid(token,1);
        TokenUtil.putUseridAndName(token,140,"gen");
        TokenUtil.putRoomId(token,42);
        CodeDTO dto=new CodeDTO();
        dto.setCode(44);
        this.goodFriendPlayService.join(token,dto);
        //String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
      //  System.out.println(this.biddingService.getBidOrderInfoByGoddess(token,"1_8_180516180728"));;
    }

}
