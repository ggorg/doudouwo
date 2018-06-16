package com.ddw;

import com.ApiApplication;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.services.ConsumeRankingListService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class)
public class ConsumeRankingListTest {

    @Autowired
    private ConsumeRankingListService consumeRankingListService;

    @Test
    public void testGetList(){
        System.out.println(consumeRankingListService.getList("1_8_180530232503", IncomeTypeEnum.IncomeType1));
    }
    @Test
    public void testResetList(){
        System.out.println(consumeRankingListService.resetCacheRankingCList(8, IncomeTypeEnum.IncomeType1));
    }
    @Test
    public void testSave()throws Exception{
        this.consumeRankingListService.save(26,8,100,IncomeTypeEnum.IncomeType1);
    }
}
