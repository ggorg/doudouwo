package com.ddw.services;

import com.ddw.beans.ContributeNumVO;
import com.ddw.beans.ListVO;
import com.ddw.beans.OpenIdDTO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.enums.IncomeTypeEnum;
import com.ddw.token.TokenUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
public class ConsumeRankingListService extends BaseConsumeRankingListService {

    public ResponseApiVO getList(String groupId,IncomeTypeEnum incomeTypeEnum){
        String useridStr="";
        if(StringUtils.isNotBlank(groupId)){
            useridStr=groupId.replaceAll("([0-9]+_)([0-9]+)(_[0-9]{12})","$2");
        }else{
            useridStr="-1";
        }

        List list=(List)CacheUtil.get("publicCacheTimer","ranking-list-"+incomeTypeEnum.getCode()+"-"+useridStr);
        if(list!=null){
            return new ResponseApiVO(1,"成功",list);
        }
        return new ResponseApiVO(1,"成功",new ListVO(resetCacheRankingCList(Integer.parseInt(useridStr),incomeTypeEnum)));

    }
    public ResponseApiVO getUserSum(String token, OpenIdDTO dto){
        if(StringUtils.isBlank(dto.getOpenId())){
            return new ResponseApiVO(-2,"参数异常",null);

        }
        Map searchCondition=new HashMap();

        searchCondition.put("openid",dto.getOpenId());
        CommonSearchBean csb=new CommonSearchBean("ddw_consume_ranking_list",null,"sum(t1.consumePrice) contributeNum,ct0.nickName name,ct0.headImgUrl headImg",null,null,null,
                new CommonChildBean("ddw_userinfo","id","consumeUserId",searchCondition));
        csb.setJointName("right");
        List list=this.getCommonMapper().selectObjects(csb);
        if(list==null || list.isEmpty()){
            return new ResponseApiVO(1,"成功",new ContributeNumVO());
        }
        Map map=(Map)list.get(0);
        if(map==null){
            return new ResponseApiVO(1,"成功",new ContributeNumVO());
        }
        Object o=map.get("contributeNum");
        if(o==null){
            map.put("contributeNum",0);
        }
        return new ResponseApiVO(1,"成功",map);

    }

}
