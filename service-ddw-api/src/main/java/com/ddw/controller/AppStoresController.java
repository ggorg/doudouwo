package com.ddw.controller;

import com.ddw.beans.*;
import com.ddw.token.Token;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ddwapp/stores")
@Api(description="门店接口",tags ="门店接口")
public class AppStoresController {

    @Token
    @PostMapping("/show-nearby-stores/{token}")
    @ApiOperation(value = "定位展示门店",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseVO<PageVO<AppStoresShowNearbyVO>> showNearbyStores(@PathVariable String token,@RequestBody @ApiParam(name="args",value="传入json格式",required=true)AppStoresShowNearbyDTO args){

        List array=new ArrayList();
        for(int i=1;i<=10;i++){
            AppStoresShowNearbyVO sv=new AppStoresShowNearbyVO();
            sv.setDsName("门店"+i);
            sv.setDistance("100"+i);
            array.add(sv);
        }

        return new ResponseVO(1,"获取成功",new PageVO("1","1",array));
    }

    public static void main(String[] args) {
        System.out.println(Base64Utils.encodeToString("12412409125839395555".getBytes()));
    }

}
