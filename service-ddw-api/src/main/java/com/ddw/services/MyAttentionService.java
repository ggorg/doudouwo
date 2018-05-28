package com.ddw.services;

import com.ddw.beans.MyAttentionDTO;
import com.ddw.beans.MyAttentionPO;
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
 * 我的关注
 * Created by Jacky on 2018/4/16.
 */
@Service
public class MyAttentionService extends CommonService {
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(int userId,MyAttentionDTO myAttentionDTO)throws Exception{
        MyAttentionPO myAttentionPO = new MyAttentionPO();
        PropertyUtils.copyProperties(myAttentionPO,myAttentionDTO);
        myAttentionPO.setUserId(userId);
        myAttentionPO.setCreateTime(new Date());
        myAttentionPO.setUpdateTime(new Date());
        return this.commonInsert("ddw_my_attention",myAttentionPO);
    }

    public MyAttentionPO query(int userId,MyAttentionDTO myAttentionDTO)throws Exception {
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        if(myAttentionDTO.getGoddessId() != 0 ){
            searchCondition.put("goddessId",myAttentionDTO.getGoddessId());
        }else if(myAttentionDTO.getPracticeId() != 0){
            searchCondition.put("practiceId",myAttentionDTO.getPracticeId());
        }
        return  this.commonObjectBySearchCondition("ddw_my_attention",searchCondition,new MyAttentionPO().getClass());
    }

    public List<Map> queryGoddessByUserId(int userId)throws Exception {
        List<Map> list = this.commonObjectsBySingleParam("ddw_my_attention","userId",userId);
        return list;
    }

    public long coundUserByGoddess(int userId)throws Exception {
        return this.commonCountBySingleParam("ddw_my_attention","goddessId",userId);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(int userId, MyAttentionDTO myAttentionDTO)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        if(myAttentionDTO.getGoddessId() != 0 ){
            searchCondition.put("goddessId",myAttentionDTO.getGoddessId());
        }else if(myAttentionDTO.getPracticeId() != 0){
            searchCondition.put("practiceId",myAttentionDTO.getPracticeId());
        }
        return this.commonDeleteByParams("ddw_my_attention",searchCondition);
    }
}
