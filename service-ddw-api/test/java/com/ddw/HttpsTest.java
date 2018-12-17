package com.ddw;

import com.ApiApplication;
import com.ddw.config.DDWGlobals;
import com.ddw.util.PayApiUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class HttpsTest {

    @Autowired
    private DDWGlobals ddwGlobals;

    @Test
    public void testPost()throws Exception{
        PayApiUtil.setDdwGlobals(ddwGlobals);
        System.out.println(ddwGlobals);
        Map map=PayApiUtil.reqeustWeiXinExitOrder("01201806261653520401000000000473","20180626165922551213",1,1);
        System.out.println(map);
    }
}
