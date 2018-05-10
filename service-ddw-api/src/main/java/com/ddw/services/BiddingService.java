package com.ddw.services;

import com.ddw.beans.BiddingDTO;
import com.ddw.beans.BiddingVO;
import com.ddw.beans.ResponseApiVO;
import com.ddw.token.TokenUtil;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;

/**
 * 竞价
 */
@Service
public class BiddingService extends CommonService {

    @Autowired
    private LiveRadioService liveRadioService;

    public ResponseApiVO getCurrentMaxPrice(String groupId){
        List<BiddingVO> list=(List)CacheUtil.get("commonCache","groupId-"+groupId);
        if(list==null || list.isEmpty()){
           return new ResponseApiVO(2,"目前还没人竞价",null);
        }
        return new ResponseApiVO(1,"成功",list.get(list.size()-1));
    }
    public ResponseApiVO putPrice(String token,BiddingDTO dto){
        if(dto==null){
            return new ResponseApiVO(-2,"参数异常",null);
        }
        if(StringUtils.isBlank(dto.getGroupId())){
            return new ResponseApiVO(-2,"群组ID为空",null);

        }
        if(dto.getPrice()==null ||  dto.getPrice()<=0){
            return new ResponseApiVO(-2,"请输入有效的金额",null);
        }
        //liveRadioService.getLiveRadioByIdAndStoreId(TokenUtil.getStoreId(token))
       // this.commonSingleFieldBySingleSearchParam()
          return null;
    }
}
