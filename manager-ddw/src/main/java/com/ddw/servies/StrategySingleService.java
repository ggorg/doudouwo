package com.ddw.servies;

import com.ddw.beans.StrategyDTO;
import com.ddw.beans.StrategyPO;
import com.gen.common.services.CommonService;
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
 * Created by Jacky on 2018/5/29.
 */
@Service
@Transactional(readOnly = true)
public class StrategySingleService extends CommonService {
    public Page findPage(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        return this.commonPage("ddw_strategy_single","id asc",pageNo,10,condtion);
    }

    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_strategy_single","id",id);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(StrategyDTO strategyDTO)throws Exception{
        StrategyPO strategyPO = new StrategyPO();
        PropertyUtils.copyProperties(strategyPO,strategyDTO);
        if(strategyDTO.getId() > 0){
            PropertyUtils.copyProperties(strategyPO,this.getById(strategyDTO.getId()));
            strategyPO.setUpdateTime(new Date());
            Map updatePoMap= BeanToMapUtil.beanToMap(strategyPO);
            return super.commonUpdateBySingleSearchParam("ddw_strategy_single",updatePoMap,"id",strategyPO.getId());
        }else{
            strategyPO.setCreateTime(new Date());
            strategyPO.setUpdateTime(new Date());
            return this.commonInsert("ddw_strategy_single",strategyPO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(int id){
        ResponseVO vo = new ResponseVO();
        int n = this.commonDelete("ddw_strategy_single","id",id);
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }
    //TODO 根据充值金额修改会员等级
}
