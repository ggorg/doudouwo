package com.ddw.services;

import com.ddw.beans.GradePO;
import com.ddw.beans.OldBringingNewPO;
import com.ddw.beans.UserInfoPO;
import com.ddw.enums.DoubiRecordTypeEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Jacky on 2018/6/26.
 */
@Service
@Transactional(readOnly = true)
public class StraregyService extends CommonService {

    public List queryGrade()throws Exception{
        return super.commonList("ddw_grade","sort",1,9999,new HashMap<>());
    }

    public List querySingle(int money)throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        searchCondition.put("money,<=",money);
        return super.commonList("ddw_strategy_single","money desc",1,1,searchCondition);
    }

    public Map queryFirstRechargeSingle()throws Exception{
        return super.commonObjectBySingleParam("ddw_strategy_single","levelId",1);
    }

    public List queryCumulation(int money)throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        searchCondition.put("money,<=",money);
        return super.commonList("ddw_strategy_cumulation","money desc",1,1,searchCondition);
    }

    public List querySingleCoupon()throws Exception{
        Map<String,Object> searchCondition = new HashMap<>();
        return super.commonObjectsBySearchCondition("ddw_strategy_single_coupon",searchCondition);
    }

    public Map queryAccrualRecharge(int userId)throws Exception{
        return super.commonObjectBySingleParam("ddw_accrual_recharge","userId",userId);
    }

    public UserInfoPO queryUser(int userId)throws Exception{
        return super.commonObjectBySingleParam("ddw_userinfo","id",userId,UserInfoPO.class);
    }

    public UserInfoPO queryUserByOpenid(String openid)throws Exception{
        return super.commonObjectBySingleParam("ddw_userinfo","openid",openid,UserInfoPO.class);
    }

    /**
     * 根据新用户查询老用户
     * @param newOpenid
     * @return
     */
    public OldBringingNewPO getOldBringingNewPO(String newOpenid){
        try {
            return this.commonObjectBySingleParam("ddw_old_bringing_new","newOpenid",newOpenid,OldBringingNewPO.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map queryStrategyOldBringingNew(int levelId)throws Exception{
        return super.commonObjectBySingleParam("ddw_strategy_old_bringing_new","levelId",levelId);
    }

    public List queryCoupon(int strategyId)throws Exception{
        return super.commonObjectsBySingleParam("ddw_strategy_old_bringing_new_coupon","strategyId",strategyId);
    }

    //乐观锁更新累积充值表
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateAccrualRecharge(int money, int userId)throws Exception{
        Map setParams = new HashMap<>();
        setParams.put("money",money);
        setParams.put("updateTime",new Date());
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        return super.commonOptimisticLockUpdateByParam("ddw_accrual_recharge",setParams,searchCondition,"version");
    }
    public Map queryMyWallet(int userId)throws Exception{
        return super.commonObjectBySingleParam("ddw_my_wallet","userId",userId);
    }

    //更新逗币
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateMyWallet(int coin, int userId)throws Exception{
        Map map = this.queryMyWallet(userId);
        int oldCoin = 0;
        if(map != null && map.containsKey("coin")){
            oldCoin = (int) map.get("coin");
        }
        Map setParams = new HashMap<>();
        setParams.put("coin",oldCoin+coin);
        setParams.put("updateTime",new Date());
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",userId);
        ResponseVO resUp = super.commonCalculateOptimisticLockUpdateByParam("ddw_my_wallet",setParams,searchCondition,"version",new String[]{"coin"});
        // 更新ddw_wallet_doubi_record逗币记录
        if(resUp.getReCode() == 1) {
            Map doubiRecord = new HashMap();
            doubiRecord.put("coin", oldCoin+coin);
            doubiRecord.put("userId", userId);
            doubiRecord.put("type", DoubiRecordTypeEnum.Type1.getCode());
            doubiRecord.put("createTime", new Date());
            doubiRecord.put("updatetime", new Date());
            this.commonInsertMap("ddw_my_wallet_doubi_record", doubiRecord);
        }
        return resUp;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertAccrualRecharge(int money, int userId)throws Exception{
        Map setParams = new HashMap<>();
        setParams.put("money",money);
        setParams.put("userId",userId);
        setParams.put("createTime",new Date());
        setParams.put("updateTime",new Date());
        return super.commonInsertMap("ddw_accrual_recharge",setParams);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertCoupon(int couponId, int userId,int storeId)throws Exception{
        Map setParams = new HashMap<>();
        setParams.put("couponId",couponId);
        setParams.put("userId",userId);
        setParams.put("storeId",storeId);
        setParams.put("used",0);
        return super.commonInsertMap("ddw_userinfo_coupon",setParams);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(UserInfoPO userInfoPO)throws Exception{
        userInfoPO.setUpdateTime(new Date());
        Map updatePoMap= BeanToMapUtil.beanToMap(userInfoPO);
        return this.commonUpdateBySingleSearchParam("ddw_userinfo",updatePoMap,"id",userInfoPO.getId());
    }

    /**
     * 根据充值金额执行会员策略
     * @param money 充值金额
     * @param userId 会员id
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void recharge(int money,int userId)throws Exception{
        //初始化赠送逗币
        int coinSingle = 0;
        int coinCumulation = 0;
        //查询出单笔充值策略
        List strategySingleList = this.querySingle(money);
        Map singleMap = new HashMap<>();
        if(strategySingleList !=null && strategySingleList.size()>0){
            singleMap = (Map) strategySingleList.get(0);
            coinSingle = (int)singleMap.get("coin");
        }

        Map map = this.queryAccrualRecharge(userId);
        //查询累积充值策略
        Map cumulationMap = new HashMap<>();
        if(map != null && map.containsKey("money")){
            int accrualMoney = (int) map.get("money");
            //更新会员累积充值表
            this.updateAccrualRecharge(accrualMoney+money,userId);
            List strategyCumulationList = this.queryCumulation(accrualMoney);
            if(strategyCumulationList !=null && strategyCumulationList.size()>0){
                cumulationMap = (Map) strategyCumulationList.get(0);
                coinCumulation = (int) cumulationMap.get("coin");
            }
        }else {
            this.insertAccrualRecharge(money,userId);
        }

        UserInfoPO userInfoPO = queryUser(userId);
        //首次充值策略
        if(userInfoPO.getFirstRechargeFlag() == 0){
            Map firstRechargeSingleMap = this.queryFirstRechargeSingle();
            if(firstRechargeSingleMap != null && firstRechargeSingleMap.containsKey("money")
                && money >= (Integer) firstRechargeSingleMap.get("money")) {
                //赠送新用户首充优惠券
                List<Map> list = this.querySingleCoupon();
                for (Map m : list) {
                    this.insertCoupon(Integer.valueOf(m.get("couponId").toString()), userId, -1);
                }
                //更新用户首充字段为1
                userInfoPO.setFirstRechargeFlag(1);
                this.update(userInfoPO);
            }
        }
        //会员等级
        List<Map> gradeList = new ArrayList<>();
        Object gradeObj = CacheUtil.get("publicCache","grade");
        if(gradeObj!=null){
            gradeList = (List)gradeObj;
        }else{
            gradeList = this.queryGrade();
            CacheUtil.put("publicCache","grade",gradeList);
        }
        GradePO gradePO = new GradePO();
        GradePO gradeSinglePO = new GradePO();
        GradePO gradeCumulationPO = new GradePO();
        //根据符合的单笔,累积策略对应的会员等级与会员现在等级进行对比,如果高出则升级更新
        for(Map gradeMap:gradeList){
            if(singleMap.size() > 0 && Integer.valueOf(singleMap.get("levelId").toString()).intValue() ==  Integer.valueOf(gradeMap.get("id").toString()).intValue()){
                PropertyUtils.copyProperties(gradeSinglePO,gradeMap);
            }
            if(cumulationMap.size() > 0 && Integer.valueOf(cumulationMap.get("levelId").toString()).intValue() ==  Integer.valueOf(gradeMap.get("id").toString()).intValue()){
                PropertyUtils.copyProperties(gradeCumulationPO,gradeMap);
            }
            if(userInfoPO.getGradeId().intValue() ==  Integer.valueOf(gradeMap.get("id").toString()).intValue()){
                PropertyUtils.copyProperties(gradePO,gradeMap);
            }
        }

        if(gradeSinglePO.getSort() > gradePO.getSort() || gradeCumulationPO.getSort() > gradePO.getSort()){
            if(gradeSinglePO.getSort() > gradeCumulationPO.getSort()){
                userInfoPO.setGradeId(gradeSinglePO.getId());
            }else{
                userInfoPO.setGradeId(gradeCumulationPO.getId());
            }
            //更新会员等级
            this.update(userInfoPO);
            //根据升级对应等级,赠送老会员优惠券
            this.setOldBringingNewCoupon(userInfoPO);
        }
        //更新赠送逗币
        if(coinSingle > 0 || coinCumulation > 0){
            this.updateMyWallet((coinSingle>0 && coinCumulation>0)?coinSingle+coinCumulation:(coinSingle>0)?coinSingle:coinCumulation,userId);
        }
    }

    /**
     * 根据升级对应等级,赠送老会员优惠券
     * @param userInfoPO
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void setOldBringingNewCoupon(UserInfoPO userInfoPO)throws Exception{
        OldBringingNewPO oldBringingNewPO = this.getOldBringingNewPO(userInfoPO.getOpenid());
        if(oldBringingNewPO != null){
            UserInfoPO userInfoPO1 = this.queryUserByOpenid(oldBringingNewPO.getOldOpenid());
            Map smap = this.queryStrategyOldBringingNew(userInfoPO.getGradeId());
            if(smap != null && smap.containsKey("id")){
                List couponList = this.queryCoupon((Integer) smap.get("id"));
                for(int i=0;i<couponList.size();i++){
                    Map cmap = (Map)couponList.get(i);
                    //插入ddw_userinfo_coupon
                    this.insertCoupon(Integer.valueOf(cmap.get("couponId").toString()), userInfoPO1.getId(),-1);
                }
            }
        }
    }
}
