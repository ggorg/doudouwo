package com.ddw.servies;

import com.gen.common.beans.RankPO;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacky on 2018/5/16.
 */
@Service
public class RankService extends CommonService {

    public Page findList(String gameId)throws Exception{
        Map condition=new HashMap();
        condition.put("gameId",gameId);
        return super.commonPage("ddw_rank","createTime",1,999,condition);
    }

    public RankPO selectById(String id){
        try {
            return super.commonObjectBySingleParam("ddw_rank","id",id,RankPO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(RankPO rankPO)throws Exception{
        if(rankPO.getId() > 0 ){
            Map updatePoMap= BeanToMapUtil.beanToMap(rankPO);
            return super.commonUpdateBySingleSearchParam("ddw_rank",updatePoMap,"id",rankPO.getId());
        }else{
            return super.commonInsert("ddw_rank",rankPO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(String id){
        ResponseVO vo=new ResponseVO();
        int n = super.commonDelete("ddw_rank","id",id);
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
