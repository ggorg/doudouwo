package com.ddw.controller;

import com.ddw.beans.ListVO;
import com.ddw.beans.RechargeVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.WalletBalanceVO;
import com.ddw.services.PayCenterService;
import com.ddw.services.RechargeService;
import com.ddw.services.WalletService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 充值卷
 */
@Api(value = "充值卷",description = "充值卷",tags = {"充值卷"})
@RestController
@RequestMapping("/ddwapp/recharge")
public class RechargeController {
    private final Logger logger = Logger.getLogger(RechargeController.class);

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private PayCenterService payCenterService;

    @Token
    @ApiOperation(value = "获取充值卷列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/list/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<RechargeVO>> list(@PathVariable String token ){
        try {
            return new ResponseApiVO(1,"成功",new ListVO(this.rechargeService.getRechargeList()));


        }catch (Exception e){
            logger.error("RechargeController->list-》获取充值卷列表-》系统异常",e);
        }
        return new ResponseApiVO(-1,"获取充值卷列表",null);

    }



}
