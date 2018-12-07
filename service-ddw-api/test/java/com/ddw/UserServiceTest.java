package com.ddw;

import com.ApiApplication;
import com.ddw.beans.BiddingDTO;
import com.ddw.beans.UserInfoDTO;
import com.ddw.dao.GoddessMapper;
import com.ddw.services.BiddingService;
import com.ddw.services.IncomeService;
import com.ddw.services.LiveRadioClientService;
import com.ddw.services.UserInfoService;
import com.ddw.token.TokenUtil;
import com.gen.common.util.CacheUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class UserServiceTest {

    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void testSave()throws Exception{
        UserInfoDTO userInfoDTO=new UserInfoDTO();
        userInfoDTO.setSex(1);
        userInfoDTO.setOpenid("o_W_K0YW7k91pdxAy1UWDq402b38");
        userInfoDTO.setRealOpenid("omc2C0pUzsdL4ZTXJ43AhvXoG5k8");
        userInfoDTO.setUnionID("o_W_K0YW7k91pdxAy1UWDq402b38");
        userInfoDTO.setNickName("Gg");
        userInfoDTO.setUserName("Gg");
        userInfoDTO.setSex(1);
        userInfoDTO.setHeadImgUrl("http://thirdwx.qlogo.cn/mmopen/vi_32/Ir8CaJMYTULEoicTOBBm8CkVhdLvJicnDmBXqVA9sNNpqzcRptk1NtyIBwnAyEdJfWVicHvu2b8A7okE1Zo5GaZQA/132");
        userInfoDTO.setPhone("");
        userInfoDTO.setRegisterType(1);
        this.userInfoService.save(userInfoDTO);

        //String retStr=(String) CacheUtil.get("pay","bidding-finish-pay-"+TokenUtil.getUserId(token));
      //  System.out.println(this.biddingService.getBidOrderInfoByGoddess(token,"1_8_180516180728"));;
    }
    @Test
    public void testHandeImUserGrade()throws Exception{
        this.userInfoService.handleImUserGrade("o_W_K0YW7k91pdxAy1UWDq402b38");
    }
}
