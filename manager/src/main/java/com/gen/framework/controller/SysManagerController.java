package com.gen.framework.controller;


import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import com.gen.framework.beans.SysMenuBean;
import com.gen.framework.beans.SysRoleBean;
import com.gen.framework.beans.SysUserBean;
import com.gen.framework.services.SysManagerService;
import com.gen.framework.services.TimerTaskManagerService;


import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafView;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.TemplateModeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys")
public class SysManagerController {
    private final Logger logger = Logger.getLogger(SysManagerController.class);
    @Autowired
    private SysManagerService sysManagerService;

    @Autowired
    private TimerTaskManagerService timerTaskManagerService;
    @GetMapping
    public String toIndex(){
        return "pages/manager/system/blankFrame";
    }
    @GetMapping("/user/to-list")
    public String toUserList(@RequestParam(defaultValue = "1") Integer pageNo,String uName, Model model){
        try {
            model.addAttribute("userPage",this.sysManagerService.getUserPage(pageNo,uName));
        }catch (Exception e){
            logger.error("SysUserController->toUserList",e);
        }
        return "pages/manager/system/user";
    }
    @PostMapping("/user/do-edit-user")
    @ResponseBody
    public ResponseVO doEditUser(SysUserBean sysUserBean){
        try {
            return this.sysManagerService.saveUser(sysUserBean);
        }catch (Exception e){
            logger.error("SysUserController->doCreateUser->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }
    }

    @GetMapping("/user/to-edit")
    public String toUserEdit(Integer uId, Model model){
        try {
            model.addAttribute("userObject",this.sysManagerService.getUserById(uId));
        }catch (Exception e){
            logger.error("SysUserController->toUserEdit->系统异常",e);

        }

        return "pages/manager/system/userEdit";
    }

    @PostMapping("/user/do-disabled-user")
    @ResponseBody
    public ResponseVO doDisabledUser(SysUserBean sysUserBean){
        try {
            return this.sysManagerService.disabledUser(sysUserBean);
        }catch (Exception e){
            logger.error("SysUserController->doDisabledUser->系统异常",e);
            return new ResponseVO(-1,"操作失败",null);
        }
    }
    @GetMapping("/user/to-modify-pwd")
    public String toModifyPwd(){
        return "pages/manager/system/modifyPwd";
    }

    @PostMapping("/user/do-modify-pwd")
    @ResponseBody
    public ResponseVO doModifyPwd(String oldPwd, String newPwd, String makeSurePwd){
        try {
            Map map=(Map) Tools.getSession("user");
            if(map!=null && map.get("id")!=null){
                ResponseVO vo=this.sysManagerService.modifyPwd((Integer) map.get("id"),oldPwd,newPwd,makeSurePwd);
                if(vo.getReCode()==1){
                    toLogout();
                }
                return vo;
               // if(vo.)
               // return ;
            }
        }catch (Exception e){
            logger.error("SysUserController->doModifyPwd->系统异常",e);

        }
        return new ResponseVO(-1,"修改失败",null);

    }

    @GetMapping("/role/to-edit")
    public String toRoleEdit(Integer rId,Model model){
        try {
            model.addAllAttributes(this.sysManagerService.getRoleById(rId));
        }catch (Exception e){
            logger.error("SysUserController->toRoleEdit->系统异常",e);

        }
        return "pages/manager/system/roleEdit";
    }

    @GetMapping("/role/to-list")
    public String toRoleList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("rolePage",this.sysManagerService.getRolePage(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toRoleList",e);
        }
        return "pages/manager/system/role";
    }

    @PostMapping("/role/do-edit-role")
    @ResponseBody
    public ResponseVO doEditRole(SysRoleBean sysRoleBean, Integer[] uId){
        try {
            return this.sysManagerService.saveRole(sysRoleBean,uId);
        }catch (Exception e){
            logger.error("SysUserController->doCreateRole->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }

    }


    @GetMapping("/menu/to-list")
    public String toMemuList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("menuPage",this.sysManagerService.getMenuPage());
        }catch (Exception e){
            logger.error("SysUserController->toMemuList",e);
        }
        return "pages/manager/system/menu";
    }
    @GetMapping("/menu/to-edit")
    public String toMenuEdit(Integer id,Model model){
        try {
            if(id!=null && id>0){
                model.addAttribute("allMenu",this.sysManagerService.getAllMenu());
                model.addAttribute("menuObject",this.sysManagerService.getMenuById(id));
            }
        }catch (Exception e){
            logger.error("SysUserController->toMemuEdit",e);
        }
        return "pages/manager/system/menuEdit";
    }
    @PostMapping("/menu/do-edit-menu")
    @ResponseBody
    public ResponseVO doEditMenu(SysMenuBean sysMenuBean){
        try {
            ResponseVO responseVO=this.sysManagerService.saveMenu(sysMenuBean);
            if(responseVO.getReCode()==1){
                this.sysManagerService.resetPowerByMid(sysMenuBean.getId());
            }
            return responseVO;
        }catch (Exception e){
            logger.error("SysUserController->doCreateMenu->系统异常",e);
            return new ResponseVO(-1,"创建失败",null);
        }

    }

    @GetMapping("/role/to-menu-power")
    public String toMenuPower(Integer rId,Model model){
        try {
           // model.addAttribute("menuPage",this.sysManagerService.getAllMenu());
            model.addAttribute("menuPage",this.sysManagerService.handlePower(rId));
        }catch (Exception e){
            logger.error("SysUserController->toMenuPower->系统异常",e);
        }
        return "pages/manager/system/rolePower";
    }
    @PostMapping("/role/do-power")
    @ResponseBody
    public ResponseVO doPower(Integer rId, Integer[] mId){
        try{
            ResponseVO responseVO=this.sysManagerService.savePower(rId,mId);
            if(responseVO.getReCode()==1){
                this.sysManagerService.resetPowerByRid(rId);
            }

            return responseVO;
        }catch (Exception e){
            logger.error("SysUserController->doPower->系统异常",e);
            return new ResponseVO(-1,"授权失败",null);
        }
    }
    @GetMapping("/to-login")
    public String toLogin(){
        return "pages/manager/system/login";
    }

    @GetMapping("/to-logout")
    public String toLogout(){
        Tools.clearLoginSession();
        return "redirect:/sys";
    }
    @PostMapping("/do-login")
    @ResponseBody
    public ResponseVO doLogin(SysUserBean sysUserBean){

        try{
            ResponseVO<Map> vo=this.sysManagerService.login(sysUserBean);
            if(vo.getReCode()!=1){
                return vo;
            }
            Tools.setSession("user",vo.getData());
            List<SysMenuBean> menus=this.sysManagerService.getPowerMenu((Integer)vo.getData().get("id"));
           // Tools.setSession("userPower",menus);
            Map jumpMap=new HashMap();
            if(menus!=null && menus.size()>0){
                jumpMap.put("jumpUrl",menus.get(0).getmUrl());
            }else{
                jumpMap.put("jumpUrl","/sys");
            }

            vo.setData(jumpMap);
            return vo;
        }catch (Exception e){
            logger.error("SysUserController->doLogin->系统异常",e);
            return new ResponseVO(-1,"登录失败",null);
        }
    }
    @PostMapping("/role/do-del-role")
    @ResponseBody
    public ResponseVO deleteRole(Integer rid){
        try {

            return this.sysManagerService.deleteRole(rid);
        }catch (Exception e){
            logger.error("SysUserController->deleteRole->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
    @PostMapping("/menu/do-del-menu")
    @ResponseBody
    public ResponseVO deleteMenu(String midStr){
        try {
            return this.sysManagerService.deleteMenu(midStr);
        }catch (Exception e){
            logger.error("SysUserController->deleteMenu->系统异常",e);
            return new ResponseVO(-1,"删除失败",null);
        }
    }
    @GetMapping("/timertask/to-list")
    public String toTimerTaskList(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        try {
            model.addAttribute("timerTaskPage",this.timerTaskManagerService.getTimerTaskPage(pageNo));
        }catch (Exception e){
            logger.error("SysUserController->toTimerTaskList",e);
        }
        return "pages/manager/timertask/timerTaskList";
    }

}
