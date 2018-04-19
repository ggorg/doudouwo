package com.ddw.controller;

import com.ddw.enums.RoleTypeEnum;
import com.ddw.servies.RoleService;
import com.ddw.util.Toolsddw;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统角色
 */
@Controller
@RequestMapping("/manager/role")
public class RoleController {
    private final Logger logger = Logger.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @PostMapping("do-give-hqpower-to-role")
    @ResponseBody
    public ResponseVO doGiveHqPowerToRole(String ridStr){
        try {
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(),RoleTypeEnum.RoleType1_3.getCode());
            if(roleList!=null && roleList.size()>0){
                ResponseVO res=roleService.modifyRoleType(ridStr, RoleTypeEnum.RoleType1_0.getCode());
                if(res.getReCode()==1){
                   return  new ResponseVO(1,"赋予总店权限成功",null);
                }
            }

        }catch (Exception e){
            logger.error("RoleController->doGiveHqPowerToRole",e);
        }
        return   new ResponseVO(-2,"赋予总店权限失败",null);
    }

    @PostMapping("do-cancel-hqpower")
    @ResponseBody
    public ResponseVO doCancelHqPower(String ridStr){
        try {
            List roleList=this.roleService.getRoleByUserId(Toolsddw.getCurrentUserId(),RoleTypeEnum.RoleType1_3.getCode());
            if(roleList!=null && roleList.size()>0){
                ResponseVO res=roleService.modifyRoleType(ridStr, RoleTypeEnum.RoleType1_1.getCode());
                if(res.getReCode()==1){
                   return  new ResponseVO(1,"撤回总店权限成功",null);
                }
            }

        }catch (Exception e){
            logger.error("RoleController->doCancelHqPower",e);
        }
        return   new ResponseVO(-2,"撤回总店权限失败",null);
    }
}
