package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.DisabledEnum;
import com.ddw.enums.EvaluateScoreEnum;
import com.ddw.token.TokenUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class AppOrderEvaluationService extends CommonService {
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO orderEvaluate(String token, OrderViewEvaluateDTO dto)throws Exception{
        if(StringUtils.isBlank(EvaluateScoreEnum.getName(dto.getScore()))){
            return new ResponseApiVO(-2,"分数异常",null);
        }
        if(dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO(-2,"编号异常",null);
        }
        OrderViewPO orderViewPO=this.commonObjectBySingleParam("ddw_order_view","id",dto.getCode(),OrderViewPO.class);

        if(orderViewPO==null){
            return new ResponseApiVO(-2,"记录不存在",null);
        }
        if(orderViewPO.getIsEvaluate()!=null && orderViewPO.getIsEvaluate()==1){
            return new ResponseApiVO(-2,"抱歉，已评价过",null);

        }
        Integer userId= TokenUtil.getUserId(token);
        UserInfoPO po= this.commonObjectBySingleParam("ddw_userinfo","id",userId, UserInfoPO.class);

        Map saveMap=new HashMap();
        saveMap.put("orderId",orderViewPO.getOrderId());
        saveMap.put("orderNo",orderViewPO.getOrderNo());
        saveMap.put("comment",dto.getComment());
        saveMap.put("score",dto.getScore());
        saveMap.put("busType",orderViewPO.getOrderType());
        saveMap.put("busCode",orderViewPO.getBusId());
        saveMap.put("createTime",new Date());
        saveMap.put("disabled", DisabledEnum.disabled0.getCode());
        saveMap.put("userId",userId);
        saveMap.put("orderViewId",dto.getCode());
        saveMap.put("storeId",orderViewPO.getStoreId());
        saveMap.put("userName",po.getNickName());
        saveMap.put("userHeadImg",po.getHeadImgUrl());
        this.commonInsertMap("ddw_order_evaluation",saveMap);
        Map param=new HashMap();
        param.put("isEvaluate",1);
       this.commonUpdateBySingleSearchParam("ddw_order_view",param,"id",orderViewPO.getId());
        return new ResponseApiVO(1,"成功",null);
    }
    public ResponseApiVO evaluateList(String token,OrderViewEvaluateListDTO dto){
        if(dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO(-2,"编号异常",null);
        }
        Map search=new HashMap();
        search.put("busCode",dto.getCode());
        search.put("disabled",DisabledEnum.disabled0.getCode());
        List list=this.commonList("ddw_order_evaluation","createTime desc","t1.comment,t1.userName,t1.userHeadImg,DATE_FORMAT(t1.createTime,'%Y-%m-%d %H:%i:%S') time,t1.score",dto.getPageNo(),10,search);
        if(list!=null && list.size()>0){
            return new ResponseApiVO(1,"成功",new ListVO<>(list));
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
    }
    public ResponseApiVO evaluateListByStore(String token,OrderViewEvaluateStoreListDTO dto){
        if(dto.getStoreId()==null || dto.getStoreId()<=0){
            return new ResponseApiVO(-2,"门店ID异常",null);
        }
        Map search=new HashMap();
        search.put("storeId",dto.getStoreId());
        search.put("disabled",DisabledEnum.disabled0.getCode());
        Page page=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        CommonSearchBean csb=new CommonSearchBean("ddw_order_evaluation","t1.createTime desc","t1.comment,t1.userName,t1.userHeadImg,DATE_FORMAT(t1.createTime,'%Y-%m-%d %H:%i:%S') time,t1.score,ct0.headImg img",page.getStartRow(),page.getEndRow(),search,
                new CommonChildBean("ddw_order_view","id","orderViewId",null));
       List list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && list.size()>0){
            return new ResponseApiVO(1,"成功",new ListVO<>(list));
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
    }
}
