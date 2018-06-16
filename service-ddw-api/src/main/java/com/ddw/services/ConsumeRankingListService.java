package com.ddw.services;

import com.ddw.beans.ListVO;
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



        List list=(List)CacheUtil.get("publicCache","ranking-list-"+incomeTypeEnum.getCode()+"-"+useridStr);
        if(list!=null){
            return new ResponseApiVO(1,"成功",list);
        }
        return new ResponseApiVO(1,"成功",new ListVO(resetCacheRankingCList(Integer.parseInt(useridStr),incomeTypeEnum)));

    }

}
