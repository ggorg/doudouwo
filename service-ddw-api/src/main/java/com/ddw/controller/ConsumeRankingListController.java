package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.services.ConsumeRankingListService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ddwapp/rankinglist")
@Api(description="消费排行榜",tags ="消费排行榜")
public class ConsumeRankingListController {
    private final Logger logger = Logger.getLogger(ConsumeRankingListController.class);

    @Autowired
    private ConsumeRankingListService consumeRankingListService;

    @PostMapping("/goddess/query/{token}")
    @ApiOperation(value = "女神土豪列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Token
    public ResponseApiVO<ListVO<RankingListVO>> queryGoddessList(@PathVariable String token){

        try{
            return this.consumeRankingListService.getList(TokenUtil.getGroupId(token), IncomeTypeEnum.IncomeType1);
        }catch (Exception e){
            logger.error("ConsumeRankingListController->queryGoddessList-》女神土豪列表-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }

    }
    @PostMapping("/practice/query/{token}")
    @ApiOperation(value = "代练土豪列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Token
    public ResponseApiVO<ListVO<RankingListVO>> queryPracticeList(@PathVariable String token){

        try{
            return this.consumeRankingListService.getList(TokenUtil.getGroupId(token), IncomeTypeEnum.IncomeType2);
        }catch (Exception e){
            logger.error("ConsumeRankingListController->queryPracticeList-》代练土豪列表-》系统异常",e);
            return new ResponseApiVO(-1,"失败",null);
        }

    }
}
