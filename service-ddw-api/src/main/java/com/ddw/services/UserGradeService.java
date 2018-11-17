package com.ddw.services;

import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class UserGradeService  extends CommonService {


    public BigDecimal getDiscount(Integer gradeId)throws Exception{
        List<Map> list=(List)CacheUtil.get("publicCache","grade");
        if(list==null || list.size()==0){
            list=this.commonObjectsBySearchCondition("ddw_grade",null);
            CacheUtil.put("publicCache","grade",list);
        }
        for(Map m:list){
            if(((Integer)m.get("id")).equals(gradeId) && m.containsKey("discount") && m.get("discount")!=null){

               return  ((BigDecimal) m.get("discount")).divide(BigDecimal.valueOf(10));
            }
        }
        return null;

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void save(BigDecimal d){
        Map i=new HashMap();
        i.put("abc",d);
        this.commonInsertMap("test",i);
    }


}
