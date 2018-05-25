package com.ddw.servies;

import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StoreFormulaService extends CommonService {

    public Page findPage(Integer pageNo,Integer storeId)throws Exception{
        Map condtion=new HashMap();
        return this.commonPage("ddw_formula","updateTime desc",pageNo,10,condtion);

    }
}
