package com.ddw;

import com.ApiApplication;
import com.ddw.beans.CodeDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.LiveRadioClientService;
import com.ddw.services.WalletService;
import com.ddw.token.TokenUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class WalletTest {

    @Autowired
    private WalletService walletService;

    @Test
    public void selectLiveRadioTest()throws Exception{
        System.out.println(this.walletService.getAsset(26));
    }
}
