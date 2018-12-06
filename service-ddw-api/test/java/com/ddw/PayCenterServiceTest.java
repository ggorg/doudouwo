package com.ddw;

import com.ApiApplication;
import com.ddw.beans.OrderPO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.config.DDWGlobals;
import com.ddw.dao.OrderSalesMapper;
import com.ddw.services.PayCenterService;
import com.ddw.services.UserGradeService;
import com.ddw.token.TokenUtil;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class PayCenterServiceTest  {

    @Autowired
    private PayCenterService payCenterService;

    @Autowired
    private OrderSalesMapper orderSalesMapper;

    @Autowired
    private UserGradeService userGradeService;




    @Test
    public void executeCouponTest()throws Exception{
        String token= TokenUtil.createToken("openid");
        TokenUtil.putStoreid(token,1);
        TokenUtil.putUseridAndName(token,26,"test123");
        //TokenUtil.putStreamId(token,"23115_1_8_180516180728");

        OrderPO po=new OrderPO();
        po.setDoCost(10000);
        ResponseApiVO vo=this.payCenterService.executeCoupon(po,1,token);
        System.out.println(vo+","+po.getDoCost());

    }
    @Test
    public void executeSales()throws Exception{
        System.out.println(this.orderSalesMapper.sales(1,"6,7","2018-06%"));
    }
    @Test
    public void testDistcount()throws Exception{
        this.userGradeService.save(userGradeService.getDiscount(2));
    }
    @Test
    public void handleOrderImg()throws Exception{
       this.payCenterService.handleOrderImg();
    }
    public static void main(String[] args) {
        System.out.println((int)(10000*((float)100/100)));
    }
}
