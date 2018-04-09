package com.ddw.controller;

import com.ddw.beans.*;
import com.gen.common.util.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ddwapp/stores")
@Api(description="门店接口")
public class AppStoresController {

    @PostMapping("/show-nearby-stores")
    @ApiOperation(value = "定位展示门店",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseVO<PageVO<ShowNearbyStoresVO>> showNearbyStores(@RequestBody @ApiParam(name="参数",value="传入json格式",required=true)RequestDTO<ShowNearbyStoresDTO> args){
        List array=new ArrayList();
        for(int i=1;i<=10;i++){
            ShowNearbyStoresVO sv=new ShowNearbyStoresVO();
            sv.setDsName("门店"+i);
            sv.setDistance("100"+i);
            array.add(sv);
        }

        return new ResponseVO(1,"获取成功",new PageVO("1","1",array));
    }

}
