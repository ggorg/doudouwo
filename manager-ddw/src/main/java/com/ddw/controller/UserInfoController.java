package com.ddw.controller;

import com.ddw.beans.UserInfoDTO;
import com.ddw.servies.UserInfoService;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 会员
 * Created by Jacky on 2018/4/13.
 */
@Controller
@RequestMapping("/manager/userInfo")
public class UserInfoController {
    private final Logger logger = Logger.getLogger(UserInfoController.class);

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("update")
    @ResponseBody
    public ResponseVO update(UserInfoDTO userInfoDTO){
        try {
            return userInfoService.update(userInfoDTO);
        }catch (Exception e){
            logger.error("UserInfoController->update",e);
            return new ResponseVO(-1,"提交失败",null);
        }

    }

    @PostMapping("query")
    @ResponseBody
    public Map query(String username){
        try {
            return userInfoService.query(username);
        }catch (Exception e){
            logger.error("UserInfoController->update",e);
            return null;
        }

    }

}
