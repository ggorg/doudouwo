package com.ddw.servies;

import com.ddw.beans.MaterialPO;
import com.ddw.beans.OrderPO;
import com.ddw.enums.OrderUserTypeEnum;
import com.ddw.enums.PayStatusEnum;
import com.ddw.enums.PayTypeEnum;
import com.ddw.enums.ShipStatusEnum;
import com.gen.common.services.CommonService;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderService extends CommonService {

    @Autowired
    private MaterialService materialService;

    //private Sysm

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO prestoreOrder(Integer userid,Integer storeid,Integer[] mids)throws Exception{
        if(mids==null){
            return new ResponseVO(-2,"请选择要订购的材料",null);
        }
        List list=Arrays.asList(mids);
        List<Map> mList=materialService.getMaterialByCache(storeid);
        if(mList==null){
            return new ResponseVO(-2,"抱歉，购物车的材料已经超过有效期，请重新入购",null);

        }

        OrderPO orderPO=new OrderPO();
        orderPO.setCreateTime(new Date());
        orderPO.setUpdateTime(new Date());
        orderPO.setDoEndTime(DateUtils.addHours(new Date(),24));
        orderPO.setDoOrderDate(DateFormatUtils.format(new Date(),"yyyyMMddHHmmsss"));
        orderPO.setDoPayStatus(PayStatusEnum.PayStatus0.getCode());
        orderPO.setDoUserId(userid);
        orderPO.setDoSellerId(-1);
        orderPO.setDoStoreId(storeid);
        orderPO.setDoPayType(PayTypeEnum.PayType3.getCode());
        orderPO.setDoShipStatus(ShipStatusEnum.ShipStatus0.getCode());
        orderPO.setDoUserType(OrderUserTypeEnum.OrderUserType4.getCode());

        Integer id=null;
        Integer num=null;
        MaterialPO mpo=null;


        for(Map m:mList){
            id=(Integer) m.get("id");
            num=(Integer) m.get("num");
            mpo=new MaterialPO();
            PropertyUtils.copyProperties(mpo,this.materialService.getById(id));
            if(mpo.getDmCurrentCount()<num){
                return new ResponseVO(-2,"抱歉，所选的材料["+mpo.getDmName()+"]库存只剩:+"+mpo.getDmCurrentCount()+",请重新购选",null);

            }

        }
        return null;
    }

}
