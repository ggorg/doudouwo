package com.ddw.controller;

import com.ddw.beans.DoubiVO;
import com.ddw.beans.ListVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.services.DoubiClientService;
import com.ddw.services.PayCenterService;
import com.ddw.token.Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 逗币
 */
@Api(value = "逗币",description = "逗币",tags = {"逗币"})
@RestController
@RequestMapping("/ddwapp/doubi")
public class DoubiController {
    private final Logger logger = Logger.getLogger(DoubiController.class);

    @Autowired
    private DoubiClientService doubiService;

    @Autowired
    private PayCenterService payCenterService;

    @Token
    @ApiOperation(value = "获取逗币充值卷列表",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/query/list/{token}")
    @ResponseBody
    public ResponseApiVO<ListVO<DoubiVO>> list(@PathVariable String token ){
        try {
            return new ResponseApiVO(1,"成功",new ListVO(this.doubiService.getList()));


        }catch (Exception e){
            logger.error("DoubiController->list-》获取逗币卷列表-》系统异常",e);
        }
        return new ResponseApiVO(-1,"获取充值卷列表",null);

    }




}
