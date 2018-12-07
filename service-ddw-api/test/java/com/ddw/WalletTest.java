package com.ddw;

import com.ApiApplication;
import com.ddw.beans.CodeDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.WalletDealRecordDTO;
import com.ddw.services.LiveRadioClientService;
import com.ddw.services.UserInfoService;
import com.ddw.services.WalletService;
import com.ddw.token.TokenUtil;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class WalletTest extends CommonService {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void selectLiveRadioTest()throws Exception{
        System.out.println(this.walletService.getAsset(26));
    }
    @Test
    public void transetTest()throws Exception{

        Map setMap=new HashMap();
        setMap.put("money",-1);
        setMap.put("updateTime",new Date());
        Map searchMap=new HashMap();
        searchMap.put("userId",8);
        ResponseVO res=this.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setMap,searchMap,"version",new String[]{"money"});

    }
    @Test
    public void getCoinTest()throws Exception{
        this.walletService.getCoin(8);
    }
    @Test
    public void getDealRecordTest()throws Exception{
        String token= TokenUtil.createToken("openid");
        TokenUtil.putUseridAndName(token,132,"test");
        ResponseApiVO res=this.walletService.getDealRecord(token,new WalletDealRecordDTO());
        System.out.println(res.getData());
    }
    @Test
    public void testUserInfo()throws Exception{
        //UserInfoPO po=this.commonObjectBySingleParam("ddw_userinfo","id",8,UserInfoPO.class);
       // System.out.println(0==userInfoService(8).getFirstRechargeFlag());
    }
}
