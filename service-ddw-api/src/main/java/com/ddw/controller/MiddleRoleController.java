package com.ddw.controller;


import com.ddw.beans.MiddleRoleDTO;
import com.ddw.beans.MiddleRolePO;
import com.ddw.services.MiddleRoleService;
import com.ddw.token.Token;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 访问地址：/swagger-ui.html
 */
@Api(value = "会员角色关系用例",description = "会员角色关系用例",tags = {"会员角色关系用例"})
@RestController
@RequestMapping("/ddwapp/middleRole")
public class MiddleRoleController {
    private final Logger logger = Logger.getLogger(MiddleRoleController.class);
    @Autowired
    private MiddleRoleService middleRoleService;

    @Token
    @ApiOperation(value = "绑定角色关系")
    @PostMapping("/save/{token}")
    public ResponseVO save(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)MiddleRoleDTO args){
        try {
            MiddleRolePO middleRolePO = middleRoleService.verify(args.getUserId(),args.getRoleName());
            if(middleRolePO == null){
                return middleRoleService.save(args);
            }else{
                return new ResponseVO(-2,"已绑定",null);
            }
        }catch (Exception e){
            logger.error("MiddleRoleController->save",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }


    @Token
    @ApiOperation(value = "会员角色关系用例")
    @PostMapping("/queryRole/{token}")
    public ResponseVO queryRole(@PathVariable String token,@RequestParam @ApiParam(name = "userId",value="userId", required = true) String userId){
        try {
            return new ResponseVO(1,"成功",middleRoleService.query(userId));
        }catch (Exception e){
            logger.error("MiddleRoleController->queryRole",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
}
