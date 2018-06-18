package com.ddw.servies;

import com.ddw.beans.ButtonDTO;
import com.ddw.beans.ButtonPO;
import com.ddw.services.CommonReviewService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacky on 2018/5/16.
 */
@Service
@Transactional(readOnly = true)
public class ButtonService extends CommonReviewService {

    public Page findPage(Integer pageNum)throws Exception{
        Map condition=new HashMap();
        return super.commonPage("ddw_button","sort",pageNum,10,condition);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(ButtonDTO buttonDTO)throws Exception{
        ButtonPO buttonPO = this.query(buttonDTO.getId());
        PropertyUtils.copyProperties(buttonPO,buttonDTO);
        Map updatePoMap= BeanToMapUtil.beanToMap(buttonPO);
        return super.commonUpdateBySingleSearchParam("ddw_button",updatePoMap,"id",buttonPO.getId());
    }

    public ButtonPO query(Integer id)throws Exception{
        return super.commonObjectBySingleParam("ddw_button","id",id,ButtonPO.class);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(ButtonDTO buttonDTO)throws Exception{
        if(buttonDTO.getId() > 0 ){
            Map updatePoMap= BeanToMapUtil.beanToMap(buttonDTO);
            return super.commonUpdateBySingleSearchParam("ddw_button",updatePoMap,"id",buttonDTO.getId());
        }else{
            ButtonPO buttonPO = new ButtonPO();
            PropertyUtils.copyProperties(buttonPO,buttonDTO);
            buttonPO.setCreateTime(new Date());
            return super.commonInsert("ddw_button",buttonPO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(Integer id)throws Exception{
        ResponseVO vo=new ResponseVO();
        int n = super.commonDelete("ddw_button","id",id);
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
