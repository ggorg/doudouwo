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

import java.util.*;

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
        if(id != null){
            Map map= this.commonObjectBySingleParam("ddw_strategy_single","id",id);
            if(map!=null){
                map.put("child",getStrategySingleCouponOld(id));
            }
            return map;
        }
        Map map = new HashMap<>();
        map.put("child",new ArrayList());
        return map;
    }

    public List getStrategySingleCouponOld(Integer strategyId)throws Exception{
        return this.commonObjectsBySingleParam("ddw_strategy_single_coupon","strategyId",strategyId);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(StrategyDTO strategyDTO)throws Exception{
        StrategyPO strategyPO = new StrategyPO();
        PropertyUtils.copyProperties(strategyPO,strategyDTO);
        if(strategyDTO.getId() > 0){
            PropertyUtils.copyProperties(strategyPO,this.getById(strategyDTO.getId()));
            strategyPO.setUpdateTime(new Date());
            Map updatePoMap= BeanToMapUtil.beanToMap(strategyPO);
            ResponseVO re = super.commonUpdateBySingleSearchParam("ddw_strategy_single",updatePoMap,"id",strategyPO.getId());
            if(re.getReCode()==1){
                //修改先删除再添加优惠券和会员关系
                this.commonDelete("ddw_strategy_single_coupon","strategyId",strategyDTO.getId());
                if(strategyDTO.getCouponId() != null){
                    for(Integer couponId:strategyDTO.getCouponId()){
                        Map map = new HashMap<>();
                        map.put("strategyId",strategyDTO.getId());
                        map.put("couponId",couponId);
                        map.put("createTime",new Date());
                        map.put("updateTime",new Date());
                        this.commonInsertMap("ddw_strategy_single_coupon",map);
                    }
                }
            }
            return re;
        }else{
            strategyPO.setCreateTime(new Date());
            strategyPO.setUpdateTime(new Date());
            ResponseVO re = this.commonInsert("ddw_strategy_single",strategyPO);
            if(re.getReCode()==1){
                //修改先删除再添加优惠券和会员关系
                this.commonDelete("ddw_strategy_single_coupon","strategyId",strategyDTO.getId());
                if(strategyDTO.getCouponId() != null){
                    for(Integer couponId:strategyDTO.getCouponId()){
                        Map map = new HashMap<>();
                        map.put("strategyId",strategyDTO.getId());
                        map.put("couponId",couponId);
                        map.put("createTime",new Date());
                        map.put("updateTime",new Date());
                        this.commonInsertMap("ddw_strategy_single_coupon",map);
                    }
                }
            }
            return re;
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
}
