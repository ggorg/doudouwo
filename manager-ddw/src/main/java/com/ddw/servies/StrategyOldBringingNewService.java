package com.ddw.servies;

import com.ddw.beans.StrategyOldBringingNewDTO;
import com.ddw.beans.StrategyOldBringingNewPO;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Jacky on 2018/5/29.
 */
@Service
@Transactional(readOnly = true)
public class StrategyOldBringingNewService extends CommonService {
    public Page findPage(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        return this.commonPage("ddw_strategy_old_bringing_new","id asc",pageNo,10,condtion);
    }

    public Map getById(Integer id)throws Exception{
        if(id != null){
            Map map= this.commonObjectBySingleParam("ddw_strategy_old_bringing_new","id",id);
            if(map!=null){
                map.put("child",getOldBringingNewCouponOld(id));
                map.put("child2",getOldBringingNewCouponNew(id));
            }
            return map;
        }
        Map map = new HashMap<>();
        map.put("child",new ArrayList());
        return map;
    }

    public List getOldBringingNewCouponOld(Integer strategyId)throws Exception{
        return this.commonObjectsBySingleParam("ddw_strategy_old_bringing_new_coupon_old","strategyId",strategyId);
    }
    public List getOldBringingNewCouponNew(Integer strategyId)throws Exception{
        return this.commonObjectsBySingleParam("ddw_strategy_old_bringing_new_coupon_new","strategyId",strategyId);
    }

    public List getAll()throws Exception{
        return this.commonObjectsBySearchCondition("ddw_strategy_old_bringing_new",new HashMap<>());
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(StrategyOldBringingNewDTO strategyOldBringingNewDTO)throws Exception{
        if(strategyOldBringingNewDTO.getId() > 0){
            Map updatePoMap= new HashMap<>();
            updatePoMap.put("`name`",strategyOldBringingNewDTO.getName());
            updatePoMap.put("levelId",strategyOldBringingNewDTO.getLevelId());
            updatePoMap.put("`describe`",strategyOldBringingNewDTO.getDescribe());
            updatePoMap.put("updateTime",new Date());
            ResponseVO re = super.commonUpdateBySingleSearchParam("ddw_strategy_old_bringing_new",updatePoMap,"id",strategyOldBringingNewDTO.getId());
            if(re.getReCode()==1){
                //修改先删除再添加优惠券和老带新关联表
                this.commonDelete("ddw_strategy_old_bringing_new_coupon_old","strategyId",strategyOldBringingNewDTO.getId());
                this.commonDelete("ddw_strategy_old_bringing_new_coupon_new","strategyId",strategyOldBringingNewDTO.getId());
                if(strategyOldBringingNewDTO.getCouponId() != null){
                    for(Integer couponId:strategyOldBringingNewDTO.getCouponId()){
                        Map map = new HashMap<>();
                        map.put("strategyId",strategyOldBringingNewDTO.getId());
                        map.put("couponId",couponId);
                        map.put("createTime",new Date());
                        map.put("updateTime",new Date());
                        this.commonInsertMap("ddw_strategy_old_bringing_new_coupon_old",map);
                    }
                }
                if(strategyOldBringingNewDTO.getNewCouponId() != null){
                    for(Integer couponId:strategyOldBringingNewDTO.getNewCouponId()){
                        Map map = new HashMap<>();
                        map.put("strategyId",strategyOldBringingNewDTO.getId());
                        map.put("couponId",couponId);
                        map.put("createTime",new Date());
                        map.put("updateTime",new Date());
                        this.commonInsertMap("ddw_strategy_old_bringing_new_coupon_new",map);
                    }
                }
            }
            return re;
        }else{
            StrategyOldBringingNewPO strategyPO = new StrategyOldBringingNewPO();
            strategyPO.setId(null);
            strategyPO.setName(strategyOldBringingNewDTO.getName());
            strategyPO.setDescribe(strategyOldBringingNewDTO.getDescribe());
            strategyPO.setLevelId(strategyOldBringingNewDTO.getLevelId());
            strategyPO.setCreateTime(new Date());
            strategyPO.setUpdateTime(new Date());
            ResponseVO re = this.commonInsert("ddw_strategy_old_bringing_new",strategyPO);
            if(re.getReCode()==1){
                if(strategyOldBringingNewDTO.getCouponId() != null){
                    for(Integer couponId:strategyOldBringingNewDTO.getCouponId()){
                        Map map = new HashMap<>();
                        map.put("strategyId",re.getData());
                        map.put("couponId",couponId);
                        map.put("createTime",new Date());
                        map.put("updateTime",new Date());
                        this.commonInsertMap("ddw_strategy_old_bringing_new_coupon_old",map);
                    }
                }
                if(strategyOldBringingNewDTO.getNewCouponId() != null){
                    for(Integer couponId:strategyOldBringingNewDTO.getNewCouponId()){
                        Map map = new HashMap<>();
                        map.put("strategyId",strategyPO.getId());
                        map.put("couponId",couponId);
                        map.put("createTime",new Date());
                        map.put("updateTime",new Date());
                        this.commonInsertMap("ddw_strategy_old_bringing_new_coupon_new",map);
                    }
                }
            }
            return re;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(int id){
        ResponseVO vo = new ResponseVO();
        int n = this.commonDelete("ddw_strategy_old_bringing_new","id",id);
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
