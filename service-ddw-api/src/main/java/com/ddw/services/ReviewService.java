package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.ddw.util.BusinessCodeUtil;
import com.ddw.util.LanglatComparator;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 审批
 */
@Service
@Transactional(readOnly = true)
public class ReviewService extends CommonService {
    @Autowired
    private CommonReviewService commonReviewService;

    @Autowired
    private LiveRadioService liveRadioService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${withdraw.max.cost:30000}")
    private Integer withdrawMaxCost;

    /**
     * 判断是否未审核直播
     * @param userid
     * @return
     */
    public boolean hasLiveRadioReviewFromGoddess(Integer userid,Integer storeId){
        return this.hasReview(userid,storeId,ReviewBusinessTypeEnum.ReviewBusinessType3,ReviewBusinessStatusEnum.liveRadio10,ReviewReviewerTypeEnum.ReviewReviewerType1);
    }
    public ResponseApiVO getLiveRadioReviewStatus(Integer userid,Integer storeId)throws Exception{

        LiveRadioPO liveRadioPO=liveRadioService.getLiveRadio(userid,storeId);
        if(liveRadioPO!=null && liveRadioPO.getLiveStatus()==LiveStatusEnum.liveStatus0.getCode()) {
            return new ResponseApiVO(2,"直播等待中，请前往直播房间",liveRadioPO);

        }
        if(liveRadioPO!=null && liveRadioPO.getLiveStatus()==LiveStatusEnum.liveStatus1.getCode()){
            return new ResponseApiVO(-2002,"直播房间已开，请关闭再申请",liveRadioPO);
        }
        boolean hasReview=this.hasLiveRadioReviewFromGoddess(userid,storeId);
        if(hasReview){
            //直播审核中

            return new ResponseApiVO(-2003,"正在审核中，请耐心等待",null);

        }
        return new ResponseApiVO(1,"未申请",null);

    }
    public ResponseVO getLiveRadioReviewStatus(String token)throws Exception{
        Map csbSea=new HashMap();
        csbSea.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());

        Map childSearch=new HashMap();
        childSearch.put("userid",TokenUtil.getUserId(token));
        CommonSearchBean csb=new CommonSearchBean("ddw_review","t1.createTime desc","t1.drReviewStatus,ct0.liveStatus",0,1,csbSea,
                new CommonChildBean("ddw_live_radio_space","userid","drProposer",childSearch).setJoinName("left"));
        List<Map> list=this.getCommonMapper().selectObjects(csb);
        if(list==null || list.isEmpty()){
            return new ResponseVO(2,"未申请",0);
        }
        Map m=list.get(0);
        Integer reviewStatus=(Integer) m.get("drReviewStatus");
        Integer liveStatus=(Integer) m.get("liveStatus");
        if(ReviewStatusEnum.ReviewStatus2.getCode().equals(reviewStatus)){
            return new ResponseVO(2,"拒绝",3);
        }
        if(ReviewStatusEnum.ReviewStatus0.getCode().equals(reviewStatus)){
            return new ResponseVO(2,"审核中",2);
        }
        if(LiveStatusEnum.liveStatus1.equals(liveStatus) || LiveStatusEnum.liveStatus0.equals(liveStatus)){
            return new ResponseVO(2,"审核通过",1);

        }else{
            return new ResponseVO(2,"未申请",0);

        }
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO applyWithPicLiveRadio(String token, LiveRadioApplWithPicDTO dto)throws Exception{
        String openId=TokenUtil.getUserObject(token).toString();
        Integer userId=TokenUtil.getUserId(token);
        Integer storeId=TokenUtil.getStoreId(token);
        if(dto.getLiveHeadImg()!=null && !dto.getLiveHeadImg().isEmpty()){
           userInfoService.uploadPhotograph(userId.toString(),new MultipartFile[]{dto.getLiveHeadImg()});

        }
        return this.applyLiveRadio(openId, storeId, dto);

    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO applyLiveRadio(String userOpenId, Integer storeId, LiveRadioApplDTO dto)throws Exception{
        if(storeId==null){
            return new ResponseApiVO(-2,"请选择一个门店",null);

        }
        Map upo=this.commonObjectBySingleParam("ddw_userinfo","openid",userOpenId);
        if(upo==null){
            return new ResponseApiVO(-2000,"用户不存在",null);
        }
        if(!GoddessFlagEnum.goddessFlag1.getCode().equals((Integer) upo.get("goddessFlag"))){
            return new ResponseApiVO(-2001,"请先申请当女神",null);
        }
        Integer userid=(Integer) upo.get("id");
        ResponseApiVO apiVO=getLiveRadioReviewStatus(userid,storeId);
        if(apiVO.getReCode()!=1){
            return apiVO;
        }
        if(StringUtils.isNotBlank(dto.getLanglat())){
            String[] lls= dto.getLanglat().split(",");
            if(lls.length!=2 || !dto.getLanglat().matches("^[0-9]+[.][^,]+,[0-9]+[.][0-9]+$")){
                return new ResponseApiVO(-2,"坐标格式有误",null);

            }
            Map gooddess=new HashMap();
            gooddess.put("langlat",dto.getLanglat());
            this.commonUpdateBySingleSearchParam("ddw_goddess",gooddess,"userId",userid);
        }

        ReviewPO reviewPO=new ReviewPO();
        reviewPO.setDrBusinessCode(BusinessCodeUtil.createLiveRadioCode(userid,storeId));
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setCreateTime(new Date());
        StringBuilder nameBuild=new StringBuilder();

        if(StringUtils.isNotBlank((String)upo.get("userName"))){
            nameBuild.append((String)upo.get("userName"));
        }else if(StringUtils.isNotBlank((String)upo.get("nickName"))){
            nameBuild.append((String)upo.get("nickName"));
        }
        if(StringUtils.isNotBlank((String)upo.get("realName"))){
            nameBuild.append("(").append((String)upo.get("realName")).append(")");
        }
        reviewPO.setDrProposerName(nameBuild.toString());
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setDrProposer(userid);
        reviewPO.setDrApplyDesc("申请开通直播空间");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.liveRadio10.getCode());
        reviewPO.setDrExtend("房间名称-"+dto.getRoomName());
        return new ResponseApiVO(this.commonReviewService.submitAppl(reviewPO));

    }
    /**
     * 判断是否未审核
     * @param proposerId 业务表的流水号
     * @param storeId 审核单位的ID，空即是总店
     * @param businessType 业务类型
     * @param businessStatus 业务状态
     * @param reviewReviewerTypeEnum 审核人类型
     * @return
     */
    public boolean hasReview(Integer proposerId,Integer storeId, ReviewBusinessTypeEnum businessType, ReviewBusinessStatusEnum businessStatus,ReviewReviewerTypeEnum reviewReviewerTypeEnum){
        Map condition=new HashMap();
        condition.put("drProposer",proposerId);
        condition.put("drBusinessType",businessType.getCode());
        condition.put("drBusinessStatus",businessStatus.getCode());
        condition.put("drReviewStatus", ReviewStatusEnum.ReviewStatus0.getCode());
        condition.put("drProposerType", ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        condition.put("drReviewerType", reviewReviewerTypeEnum.getCode());
        if(storeId!=null && reviewReviewerTypeEnum.getCode()==ReviewReviewerTypeEnum.ReviewReviewerType1.getCode()){
            condition.put("drBelongToStoreId", storeId);
        }
        return this.commonCountBySearchCondition("ddw_review",condition)>0?true:false;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO applyWithdraw(String token,WithdrawDTO dto)throws Exception{
        if(StringUtils.isBlank(IncomeTypeEnum.getName(dto.getIncomeType()))){
            return new ResponseApiVO(-2,"收益类型异常",null);
        }
        if(dto.getCode()==null || dto.getCode()<=0){
            return new ResponseApiVO(-2,"提现途径code异常",null);
        }
        if(dto.getMoney()==null || dto.getMoney()<=0){
            return new ResponseApiVO(-2,"提现金额异常",null);
        }
        if(dto.getMoney()<this.withdrawMaxCost){
            return new ResponseApiVO(-2,"提现金额不能小于"+((double)withdrawMaxCost/100)+"元",null);

        }
        Integer userId= TokenUtil.getUserId(token);
        if(IncomeTypeEnum.IncomeType1.getCode().equals(dto.getIncomeType())){
            WalletGoddessInVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userId, WalletGoddessInVO.class);
           if( balanceVO.getGoddessIncome()==null || dto.getMoney()>balanceVO.getGoddessIncome()){
               return new ResponseApiVO(-2,"女神收益金额不足",null);
           }

        }else{
            WalletPracticeInVO balanceVO=this.commonObjectBySingleParam("ddw_my_wallet","userId",userId, WalletPracticeInVO.class);
            if( balanceVO.getPracticeIncome()==null || dto.getMoney()>balanceVO.getPracticeIncome()){
                return new ResponseApiVO(-2,"代练收益金额不足",null);
            }
        }
        Map searchMap=new HashMap();
        searchMap.put("userId",userId);
        searchMap.put("id",dto.getCode());
        CommonSearchBean csb=new CommonSearchBean("ddw_withdraw_way",null,"t1.*,ct0.realName",null,null,searchMap,
                new CommonChildBean("ddw_userinfo","id","userId",null));
        List<Map> list=this.getCommonMapper().selectObjects(csb);

        if(list==null ||  list.isEmpty()){
            return new ResponseApiVO(-2,"提现失败-没有绑定的信息",null);
        }
        Map map=list.get(0);
        Map insertMap=new HashMap();
        insertMap.put("withdrawWayId",dto.getCode());
        insertMap.put("money",dto.getMoney());
        insertMap.put("createTime",new Date());
        insertMap.put("updateTime",new Date());
        insertMap.put("status",WithdrawStatusEnum.withdrawStatus0.getCode());
        insertMap.put("userId",userId);
        insertMap.put("incomeType",dto.getIncomeType());
        insertMap.put("accountType",map.get("accountType"));
        insertMap.put("accountNoStr",map.get("accountNoStr"));
        insertMap.put("accountRealName",map.get("accountRealName"));

        ResponseVO res=this.commonInsertMap("ddw_withdraw_record",insertMap);
        if(res.getReCode()!=1){
            return new ResponseApiVO(-2,"提现失败-添加记录失败",null);
        }
        Integer storeId=TokenUtil.getStoreId(token);
        ReviewPO reviewPO=new ReviewPO();
        reviewPO.setDrBusinessCode(res.getData().toString());
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setCreateTime(new Date());

        reviewPO.setDrProposerName(TokenUtil.getUserName(token)+"("+map.get("realName")+ ")");
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType8.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        reviewPO.setDrBelongToStoreId(storeId);
        reviewPO.setDrProposer(userId);
        reviewPO.setDrApplyDesc("提现申请("+IncomeTypeEnum.getName(dto.getIncomeType())+")");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.withdrawAppl8.getCode());
        StringBuilder builder=new StringBuilder();
        builder.append("【账户：").append(map.get("accountNoStr"));
        builder.append(",账户姓名：").append(map.get("accountRealName")).append(",").append("提现金额：").append((double)dto.getMoney()/100).append("元】");
        reviewPO.setDrExtend(builder.toString());
        res=this.commonReviewService.submitAppl(reviewPO);
        if(res.getReCode()!=1){
            return new ResponseApiVO(-2,"提现失败-申请审核失败",null);
        }
        return new ResponseApiVO(1,"成功",null);

    }


}
