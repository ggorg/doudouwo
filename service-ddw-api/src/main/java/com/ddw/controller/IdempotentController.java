package com.ddw.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.ResponseApiVO;
import com.ddw.beans.WalletBalanceVO;
import com.ddw.services.PayCenterService;
import com.ddw.services.WalletService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.gen.common.util.ThreadLocalUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 幂等
 */
@Api(value = "幂等下发申请",description = "幂等下发申请",tags = {"幂等下发申请"})
@RestController
@RequestMapping("/ddwapp/idempotent")
public class IdempotentController {
    private final Logger logger = Logger.getLogger(IdempotentController.class);


    @Token
    @ApiOperation(value = "申请幂等令牌",produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/appl/{token}")
    @ResponseBody
    public ResponseApiVO appl(@PathVariable String token){
        try {
            String idempStr=TokenUtil.getIdemp(token,"idemp");;
            if(StringUtils.isBlank(idempStr)){
                TokenUtil.putIdempotent(token,"idemp","do");
                return new ResponseApiVO(1,"申请成功",null);
            }else{
                return new ResponseApiVO(-2,"处理中",null);

            }


        }catch (Exception e){
            logger.error("IdempotentController->appl-》申请幂等令牌-》系统异常",e);
        }
        return new ResponseApiVO(-1,"申请失败",null);

    }

    /*@Token
    @ApiOperation(value = "申请幂等令牌(h5)")
    @PostMapping("/h5/appl")
    @ResponseBody
    public ResponseApiVO applByH5(){
        try {
            JSONObject jsonObj=(JSONObject) ThreadLocalUtil.get();
            if(jsonObj!=null){
                String base64Token=jsonObj.getString("t");
                String idempStr=TokenUtil.getIdemp(base64Token,"idemp");;
                if(StringUtils.isBlank(idempStr)){
                    TokenUtil.putIdempotent(base64Token,"idemp","do");
                    return new ResponseApiVO(1,"申请成功",null);
                }else{
                    return new ResponseApiVO(-2,"处理中",null);

                }
            }


        }catch (Exception e){
            logger.error("IdempotentController->appl-》申请幂等令牌-》系统异常",e);
        }
        return new ResponseApiVO(-1,"申请失败",null);

    }*/


}
