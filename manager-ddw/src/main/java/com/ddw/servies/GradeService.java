package com.ddw.servies;

import com.ddw.beans.GradeDTO;
import com.ddw.beans.GradePO;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员等级
 * Created by Jacky on 2018/4/19.
 */
@Service
public class GradeService extends CommonService{

    public Page findList(String gradeName)throws Exception{
        Map condtion=new HashMap();
        if(gradeName !=null && !gradeName.equals("")){
            condtion.put("gradeName",gradeName);
        }
        return super.commonPage("ddw_grade","sort",1,999,condtion);
    }

    public GradePO selectById(String id){
        try {
            return super.commonObjectBySingleParam("ddw_grade","id",id,new GradePO().getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(GradeDTO gradeDTO)throws Exception{
        GradePO gradePO = new GradePO();
        PropertyUtils.copyProperties(gradePO,gradeDTO);
        if(gradeDTO.getId() > 0 ){
            Map updatePoMap= BeanToMapUtil.beanToMap(gradePO);
            return super.commonUpdateBySingleSearchParam("ddw_grade",updatePoMap,"id",gradeDTO.getId());
        }else{
            return super.commonInsert("ddw_grade",gradePO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(String id){
        ResponseVO vo=new ResponseVO();
        int n = super.commonDelete("ddw_grade","id",id);
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }
}
