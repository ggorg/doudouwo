package com.ddw;

import com.GenFrameworkApplication;
//import com.ddw.servies.TimerTaskService;
import com.gen.common.services.CommonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= GenFrameworkApplication.class)
public class TimerTaskServiceTest extends CommonService {

    @Autowired
  //  private TimerTaskService timerTaskService;


    @Test
    public void handleFreeBidTest()throws Exception{

       // this.timerTaskService.handleFreeBid();
    }
}
