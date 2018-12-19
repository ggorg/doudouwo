package com.ddw.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.services.OldBringingNewService;
import com.ddw.token.Token;
import com.ddw.token.TokenUtil;
import com.ddw.util.ApiConstant;
import com.gen.common.util.CommonUtil;
import com.gen.common.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Api(value = "老带新",description = "老带新",tags = {"老带新"})
@Controller
@RequestMapping("/ddwapp/oldBringingNew")
public class OldBringingNewController {
    private final Logger logger = Logger.getLogger(OldBringingNewController.class);
    private final String OAUTHMAINURL = "http://doudouwo.cn/weixin/oauth";

    @Autowired
    private OldBringingNewService oldBringingNewService;

    @Token
    @ApiOperation(value = "老带新分享链接")
    @PostMapping("/shareUrl/{token}")
    public ResponseVO shareUrl(@PathVariable String token){
        try {
            JSONObject json = new JSONObject();
            String openid = TokenUtil.getUserObject(token).toString();
            String shareUrl = CommonUtil.createOauthUrl(ApiConstant.WEI_XIN_PUBLIC_APP_ID,OAUTHMAINURL,"obn",openid.substring(openid.length()-6,openid.length()));
            json.put("shareUrl",shareUrl);
            return new ResponseVO(1,"成功",json);
        }catch (Exception e){
            logger.error("OldBringingNewController->shareUrl",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }

    @Token
    @ApiOperation(value = "老带新成功邀请列表")
    @PostMapping("/inviteList/{token}")
    public ResponseVO inviteList(@PathVariable String token){
        try {
            JSONObject json = new JSONObject();
            String openid = TokenUtil.getUserObject(token).toString();
            json.put("list",oldBringingNewService.inviteList(openid));
            return new ResponseVO(1,"成功",json);
        }catch (Exception e){
            logger.error("OldBringingNewController->inviteList",e);
            return new ResponseVO(-1,"提交失败",null);
        }
    }
    @ApiOperation(value = "老带新邀请页（H5）")
    @GetMapping("/h5inviteList/{token}")
    public String inviteListh5(@PathVariable String token,Model model){
        try {
            String openid = TokenUtil.getUserObject(token).toString();
            model.addAttribute("inviteList", oldBringingNewService.inviteList(openid));
        }catch (Exception e){
            logger.error("OldBringingNewController->h5inviteList",e);
        }
        return "pages/api/obn/obnIndex";
    }
}
