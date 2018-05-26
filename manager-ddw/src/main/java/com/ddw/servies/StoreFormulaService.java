package com.ddw.servies;

import com.gen.common.services.CommonService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreFormulaService extends CommonService {

    public Page findPage(Integer pageNo,Integer storeId)throws Exception{
        Map condtion=new HashMap();
        condtion.put("storeId",storeId);
        return this.commonPage("ddw_formula","updateTime desc",pageNo,10,condtion);

    }

    public Map getFormula(String idStr)throws Exception{
        if(StringUtils.isNotBlank(idStr)){
            Integer id=Integer.parseInt(MyEncryptUtil.getRealValue(idStr));
            Map map= this.commonObjectBySingleParam("ddw_formula","id",id);
            if(map!=null){
                map.put("child",getStoreMaterial(id));
            }
            return map;
        }
        Map map=new HashMap();
        map.put("child",new ArrayList());
        return map;
    }
    public List getStoreMaterial(Integer formulaId)throws Exception{
        return this.commonObjectsBySingleParam("ddw_formula_store_material","formulaId",formulaId);
    }
}
