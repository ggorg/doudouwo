package com.ddw.controller;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.PayStatusDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.token.Idemp;
import com.ddw.token.Token;
import com.ddw.util.ApiConstant;
import com.ddw.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/ddwapp/page")
@Api(description="页面",tags = "页面")
public class PageController {

    private final Logger logger = Logger.getLogger(PageController.class);

    @Value("{wx.oauth.redirectUri}")
    private String authUrl;

    @ApiOperation(value = "商城页面",produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/shop")
    public String toShopPage (String DDW){
        try {
            if(StringUtils.isBlank(DDW)){
                return "redirect:/404";
            }
            logger.info("searchPayStatus->start：base64:"+DDW);
            String jsonStr=new String(Base64Utils.decodeFromString(DDW));
            logger.info("searchPayStatus->start：jsonStr:"+jsonStr);
            JSONObject json=JSONObject.parseObject(jsonStr);


            String jumpAuthUrl=CommonUtil.createOauthUrl(ApiConstant.WEI_XIN_PUBLIC_APP_ID,authUrl,"shop",json.getInteger("storeId"),json.getInteger("tableNumber"));
            logger.info("searchPayStatus->end,jumpAuthUrl:"+jumpAuthUrl);
            return "redirect:"+authUrl;

        }catch (Exception e){
            logger.error("PageController-toShopPage-》商城页面-》系统异常",e);

        }
        return "redirect:/404";
    }
}
