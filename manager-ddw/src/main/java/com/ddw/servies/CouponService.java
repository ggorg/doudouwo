package com.ddw.servies;

import com.ddw.beans.CouponDTO;
import com.ddw.beans.TicketDTO;
import com.ddw.beans.TicketPO;
import com.ddw.config.DDWGlobals;
import com.ddw.enums.CouponTypeEnum;
import com.ddw.enums.DisabledEnum;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.MyEncryptUtil;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CouponService extends CommonService {

  
    public Page findPage(Integer pageNo)throws Exception{


        // condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_coupon","updateTime desc",pageNo,10,null);
    }
    public Map getById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_coupon","id",id);
    }
    public TicketPO getBeanById(Integer id)throws Exception{
        return this.commonObjectBySingleParam("ddw_coupon","id",id,TicketPO.class);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(CouponDTO dto,Integer storeId)throws Exception{
        if(dto==null){
            return new ResponseVO(-2,"参数异常",null);

        }
        if(StringUtils.isBlank(dto.getDcDesc())){
            return new ResponseVO(-2,"请填写优惠卷名称",null);

        }
        if(StringUtils.isBlank(dto.getDcMoney()) || !NumberUtils.isNumber(dto.getDcMoney())){
            return new ResponseVO(-2,"请填写有效的优惠幅度",null);

        }
        if(StringUtils.isBlank(CouponTypeEnum.getName(dto.getDcType()))){
            return new ResponseVO(-2,"请选择有效的类型",null);
        }



        Map map=BeanToMapUtil.beanToMap(dto,true);
        map.put("updateTime",new Date());
        map.put("storeId",storeId);
        if(CouponTypeEnum.CouponType2.getCode().equals(dto.getDcType())){
            float m=Float.parseFloat( dto.getDcMoney());
            if(m>10){
                return new ResponseVO(-2,"折扣幅度不能超过10折",null);
            }
            if(dto.getDcMoney().length()==1){
                map.put("dcMoney",dto.getDcMoney()+"0");

            }else if(dto.getDcMoney().indexOf(".")>-1){
                map.put("dcMoney",m*10);
            }
        }
        if(StringUtils.isNotBlank(dto.getDtActiveTime())){
            String[] dateStr=dto.getDtActiveTime().split(" - ");
            map.put("dcStartTime", DateUtils.parseDate(dateStr[0],"yyyy-MM-dd HH:mm"));
            map.put("dcEndTime", DateUtils.parseDate(dateStr[1],"yyyy-MM-dd HH:mm"));
        }
        map.remove("dtActiveTime");
        if(dto.getId()==null){
            map.put("createTime",new Date());

            ResponseVO res=this.commonInsertMap("ddw_coupon",map);
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","coupon-"+storeId);
                return new ResponseVO(1,"提交成功",null);
            }
        }else{
            ResponseVO res=this.commonUpdateBySingleSearchParam("ddw_coupon",map,"id",dto.getId());
            if(res.getReCode()==1){
                CacheUtil.delete("publicCache","coupon-"+storeId);
                return new ResponseVO(1,"提交成功",null);
            }
        }
        return new ResponseVO(-2,"提交失败",null);

    }
}
