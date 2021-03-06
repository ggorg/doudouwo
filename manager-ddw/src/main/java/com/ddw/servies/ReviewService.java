package com.ddw.servies;

import com.ddw.beans.ReviewCallBackBean;
import com.ddw.beans.ReviewPO;
import com.ddw.enums.*;
import com.ddw.services.CommonReviewService;
import com.ddw.services.ReviewCallBackService;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonCountBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Max;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审批
 */
@Service
@Transactional(readOnly = true)
public class ReviewService extends CommonService {

    @Autowired
    private CommonReviewService commonReviewService;


    public Page findPage(Integer pageNo,Map condtion)throws Exception{
        //condtion.put("dmStatus",dmStatus);
        return this.commonPage("ddw_review","createTime desc",pageNo,10,condtion);
    }

    public Page findMaterialPageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType1.getCode());
        return this.findPage(pageNo,condtion);
    }
    public Page findWithdrawPageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType8.getCode());
        return this.findPage(pageNo,condtion);
    }

    public Page findLiveRadioPageByStore(Integer pageNo,Integer storeId)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());
        condtion.put("drBelongToStoreId",storeId);
        return this.findPage(pageNo,condtion);
    }

    public Page findGoodFriendPlayPageByStore(Integer pageNo,Integer storeId)throws Exception{
        Page page=new Page(pageNo,10);
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType10.getCode());
        condtion.put("drBelongToStoreId",storeId);
        CommonSearchBean csb= new CommonSearchBean("ddw_review","t1.createTime desc","t1.*,ct0.status roomStatus",page.getStartRow(),page.getEndRow(),condtion,
                new CommonChildBean("ddw_goodfriendplay_room","id","drBusinessCode",null));
        CommonCountBean ccb=new CommonCountBean();
        PropertyUtils.copyProperties(ccb, csb);
        long count = this.getCommonMapper().selectCount(ccb);
        if(count>0){
            List list=this.getCommonMapper().selectObjects(csb);
            page.setResult(list);
            page.setTotal(count);
        }

        return page;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO endGoodFriendPlay(Integer id, Integer storeId)throws Exception{
        if(id==null || id<=0){
            return new ResponseVO(-2,"参数异常",null);
        }
        Map search=new HashMap();
        search.put("id",id);
        search.put("storeId",storeId);
        Map updateMap=new HashMap();
        updateMap.put("status",GoodFriendPlayRoomStatusEnum.status22.getCode());
        updateMap.put("updateTime",new Date());
        Map map=this.commonObjectBySingleParam("ddw_goodfriendplay_room","id",id);
        if(map==null || map.isEmpty()){
            return new ResponseVO(-2,"记录不存在",null);
        }
        if(!map.get("storeId").equals(storeId)){
            return new ResponseVO(-2,"权限不足",null);
        }
        updateMap.put("endTime",new Date());
        this.commonUpdateBySingleSearchParam("ddw_goodfriendplay_room",updateMap,"id",id);
        Map updateTable=new HashMap();
        updateTable.put("status",TableStatusEnum.status0.getCode());
        updateTable.put("updateTime",new Date());
        this.commonUpdateBySingleSearchParam("ddw_goodfriendplay_tables",updateTable,"id",map.get("tableCode"));

        return new ResponseVO(1,"结束成功",null);
    }
    /**
     * 判断门店是否有申请审核
     * @param businessCode 业务流水号
     * @param businessType 业务类型
     * @param businessStatus 业务状态
     * @return
     */
    public boolean hasReviewFromStore(Object businessCode, ReviewBusinessTypeEnum businessType, ReviewBusinessStatusEnum businessStatus){
        Map condition=new HashMap();
        condition.put("drBusinessCode",businessCode.toString());
        condition.put("drBusinessType",businessType.getCode());
        condition.put("drBusinessStatus",businessStatus.getCode());
        condition.put("drReviewStatus", ReviewStatusEnum.ReviewStatus0.getCode());
        condition.put("drProposerType", ReviewProposerTypeEnum.ReviewProposerType0.getCode());
        condition.put("drReviewerType", ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        return this.commonCountBySearchCondition("ddw_review",condition)>0?true:false;
    }
    @Cacheable(value="reviewInfo",key="#businessCode")
    public ReviewPO getReviewByBusinessCode(Object businessCode)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("drBusinessCode",businessCode.toString());
        List list=this.commonList("ddw_review","createTime desc",null,null,conditoin);
        if(list!=null && !list.isEmpty()){
            ReviewPO rpo=new ReviewPO();
            PropertyUtils.copyProperties(rpo,list.get(0));
            return rpo;
        }
        return new ReviewPO();
    }

    public ReviewPO getReviewById(Integer id)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("id",id);
        return this.commonObjectBySingleParam("ddw_review","id",id,ReviewPO.class);

    }
    /**
     * 根据业务流水号获取审批记录
     * @param businessCode
     * @return
     * @throws Exception
     */
    public List getReviewRecord(Object businessCode)throws Exception{
        Map searchCondition=new HashMap();
        searchCondition.put("drBusinessCode",businessCode);
       return  this.commonList("ddw_review_record","updateTime desc",null,null,searchCondition);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO editReivew(Integer id,Object businessCode,String drReviewDesc ,ReviewStatusEnum reviewStatus,Map userMap)throws Exception{

        Map params=new HashMap();
        params.put("drReviewDesc",drReviewDesc);
        params.put("drReviewStatus",reviewStatus.getCode());
        params.put("updateTime",new Date());
       // params.put("drBusinessType",businessType.getCode());
        params.put("drReviewer", (Integer)userMap.get("id"));
        params.put("drReviewerName", (String)userMap.get("uNickName"));

        return this.commonReviewService.submitReview(params,id,businessCode.toString());


    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveReviewFromStore(Object businessCode,String drApplyDesc,ReviewBusinessTypeEnum businessType, ReviewBusinessStatusEnum businessStatus,Map userMap)throws Exception{
        if(StringUtils.isBlank(drApplyDesc)){
            return new ResponseVO(-2,"请填写原因",null);
        }
        Map condition=new HashMap();
        condition.put("drBusinessCode",businessCode.toString());
        condition.put("drBusinessType",businessType.getCode());
        condition.put("drBusinessStatus",businessStatus.getCode());
        condition.put("drReviewStatus", ReviewStatusEnum.ReviewStatus0.getCode());
        condition.put("drProposerType", ReviewProposerTypeEnum.ReviewProposerType0.getCode());
        condition.put("drReviewerType", ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condition.put("drApplyDesc", drApplyDesc);
        condition.put("drProposer", (Integer)userMap.get("id"));
        condition.put("drProposerName", (String)userMap.get("uNickName"));
        condition.put("createTime", new Date());

       return this.commonReviewService.submitAppl(condition);

    }


}
