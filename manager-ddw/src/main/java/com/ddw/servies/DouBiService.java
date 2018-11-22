package com.ddw.servies;


import com.ddw.beans.DoubiDTO;
import com.ddw.beans.TicketPO;
import com.ddw.enums.DisabledEnum;
import com.ddw.enums.DisountEnum;
import com.ddw.enums.DoubiValueEnum;
import com.ddw.enums.RechargeValueEnum;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 逗币卷
 */
@Service
@Transactional(readOnly = true)
public class DouBiService extends CouponService{

    public Page findPage(Integer pageNo)throws Exception{


        // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_doubi","updateTime desc",pageNo,10,null);
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_doubi","id",id);
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
        ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_doubi",map,"id",Integer.parseInt(ids));
        if(res.getReCode()==1){
            CacheUtil.delete("publicCache","doubi-all");

            if(DisabledEnum.disabled0.getCode().equals(status)){
                return new ResponseVO(1,"启用成功",null);

            }else if(DisabledEnum.disabled1.getCode().equals(status)){
                return new ResponseVO(1,"停用成功",null);

            }
        }
        return new ResponseVO(-2,"操作失败",null);


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(DoubiDTO dto)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getDrName())){
            return new ResponseVO(-2,"请填写名称",null);

        }
        if(dto.getDrCost()==null ||dto.getDrCost()<0 || StringUtils.isBlank(DoubiValueEnum.getName(dto.getDrCost()))){
            return new ResponseVO(-2,"请选择有效的价格",null);

        }

        if(dto.getDrDiscountCode()!=null && StringUtils.isBlank(DisountEnum.getName(dto.getDrDiscountCode()))){
            return new ResponseVO(-2,"请选择有效的折扣",null);

        }

        dto.setDrDoubiNum((Integer) (dto.getDrCost()/10));
        Map map= BeanToMapUtil.beanToMap(dto,true);
        map.put("updateTime",new Date());
        if(StringUtils.isNotBlank(DisountEnum.getName(dto.getDrDiscountCode()))){
            map.put("drDiscount", BigDecimal.valueOf(dto.getDrCost()).multiply(BigDecimal.valueOf(dto.getDrDiscountCode()).divide(BigDecimal.valueOf(100))).intValue());
        }
        if(dto.getId()==null){
            map.put("createTime",new Date());
            map.put("drDisabled",DisabledEnum.disabled1.getCode());
            ResponseVO res=this.commonInsertMap("ddw_doubi",map);
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","doubi-all");
                return new ResponseVO(1,"提交成功",null);
            }
        }else{
            ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_doubi",map,"id",dto.getId());
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","doubi-all");
                return new ResponseVO(1,"提交成功",null);
            }
        }
        return new ResponseVO(-2,"提交失败",null);

    }
}
