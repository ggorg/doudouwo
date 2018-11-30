package com.ddw.services;

import com.ddw.beans.OldBringingNewDTO;
import com.ddw.beans.OldBringingNewPO;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacky on 2018/10/29.
 */
@Service
@Transactional(readOnly = true)
public class OldBringingNewService extends CommonService {

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(OldBringingNewDTO oldBringingNewDTO)throws Exception{
        if(this.queryNewOpenid(oldBringingNewDTO.getNewOpenid())>0){
            return new ResponseVO(-1,"已绑定,不能重复绑定",null);
        }
        OldBringingNewPO oldBringingNewPO = new OldBringingNewPO();
        PropertyUtils.copyProperties(oldBringingNewPO,oldBringingNewDTO);
        oldBringingNewPO.setCreateTime(new Date());
        oldBringingNewPO.setUpdateTime(new Date());
        return this.commonInsert("ddw_old_bringing_new",oldBringingNewPO);
    }

    /**
     * 查询是否已经绑定老带新
     * @param newOpenid
     * @return
     * @throws Exception
     */
    public long queryNewOpenid(String newOpenid)throws Exception {
        return  this.commonCountBySingleParam("ddw_old_bringing_new","newOpenid",newOpenid);
    }

    /**
     * 查询老用户成功邀请新用户列表
     * @param oldOpenid
     * @return
     */
    public List inviteList(String oldOpenid){
        try {
            Map<String,Object> searchCondition = new HashMap<>();
            searchCondition.put("oldOpenid",oldOpenid);
            searchCondition.put("status",1);
            return this.commonObjectsBySearchCondition("ddw_old_bringing_new",searchCondition);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
