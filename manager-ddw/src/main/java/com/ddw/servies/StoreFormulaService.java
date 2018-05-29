package com.ddw.servies;

import com.ddw.beans.StoreFormulaDTO;
import com.gen.common.services.CommonService;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class StoreFormulaService extends CommonService {

    public Page findPage(Integer pageNo,Integer storeId)throws Exception{
        Map condtion=new HashMap();
        condtion.put("storeId",storeId);
        return this.commonPage("ddw_formula","updateTime desc",pageNo,10,condtion);

    }
    public List getAllByStore(Integer storeId){
        Map condition=new HashMap();
        condition.put("storeId",storeId);
        return this.commonList("ddw_formula","updateTime desc",null,null,condition);
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
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveFormula(StoreFormulaDTO dto,Integer storeId){
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(dto.getDfName())){
            return new ResponseVO(-2,"配方名称是空",null);

        }
        if(dto.getMaterialId()==null){
            return new ResponseVO(-2,"材料是空",null);

        }
        if(dto.getWeight()==null){
            return new ResponseVO(-2,"净含量是空",null);

        }

        Map fMap=new HashMap();
        fMap.put("dfName",dto.getDfName());
        fMap.put("dfNumber",dto.getDfNumber());
        fMap.put("updateTime",new Date());
        fMap.put("storeId",storeId);

        if(dto.getId()==null){
            fMap.put("createTime",new Date());
            ResponseVO<Integer> inVo=this.commonInsertMap("ddw_formula",fMap);
            if(inVo.getReCode()!=1){
                return new ResponseVO(-2,"保存失败",null);
            }
            dto.setId(inVo.getData());
        }else{
            this.commonUpdateBySingleSearchParam("ddw_formula",fMap,"id",dto.getId());
        }
        this.commonDelete("ddw_formula_store_material","formulaId",dto.getId());
        Map map=null;
        for(int i=0;i<dto.getMaterialId().length;i++){
            map=new HashMap();
            map.put("formulaId",dto.getId());
            map.put("materialId",dto.getMaterialId()[i]);
            map.put("createTime",new Date());
            map.put("weight",dto.getWeight()[i]);
            this.commonInsertMap("ddw_formula_store_material",map);

        }
        return new ResponseVO(1,"保存成功",null);


    }
}
