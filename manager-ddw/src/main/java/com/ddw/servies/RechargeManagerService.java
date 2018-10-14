package com.ddw.servies;


import com.ddw.beans.RechargeDTO;
import com.ddw.enums.DisabledEnum;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 充值卷
 */
@Service
@Transactional(readOnly = true)
public class RechargeManagerService extends CouponService{

    public Page findPage(Integer pageNo)throws Exception{


        // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_recharge","updateTime desc",pageNo,10,null);
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_recharge","id",id);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(String idStr, Integer status){
        String ids= MyEncryptUtil.getRealValue(idStr);
        if(StringUtils.isBlank(ids)){
            return new ResponseVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(DisabledEnum.getName(status))){
            return new ResponseVO(-2,"状态值异常",null);
        }
        Map map=new HashMap();
        map.put("drDisabled",status);
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_recharge",map,"id",Integer.parseInt(ids));
        if(res.getReCode()==1){
            CacheUtil.delete("publicCache","recharge-all");

            if(DisabledEnum.disabled0.getCode().equals(status)){
                return new ResponseVO(1,"启用成功",null);

            }else if(DisabledEnum.disabled1.getCode().equals(status)){
                return new ResponseVO(1,"停用成功",null);

            }
        }
        return new ResponseVO(-2,"操作失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(RechargeDTO dto)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getDrName())){
            return new ResponseVO(-2,"请填写充值卷名称",null);

        }
        if(dto.getDrCost()==null ||dto.getDrCost()<0){
            return new ResponseVO(-2,"请填写有效的价格",null);

        }
        if(dto.getDrDiscount()!=null && dto.getDrDiscount()<0){
            return new ResponseVO(-2,"请填写有效的活动价格",null);

        }
        Map map= BeanToMapUtil.beanToMap(dto,true);
        map.put("updateTime",new Date());
        if(dto.getId()==null){
            map.put("createTime",new Date());
            map.put("drDisabled",DisabledEnum.disabled0.getCode());
            ResponseVO res=this.commonInsertMap("ddw_recharge",map);
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","recharge-all");
                return new ResponseVO(1,"提交成功",null);
            }
        }else{
            ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_recharge",map,"id",dto.getId());
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","recharge-all");
                return new ResponseVO(1,"提交成功",null);
            }
        }
        return new ResponseVO(-2,"提交失败",null);

    }
}
