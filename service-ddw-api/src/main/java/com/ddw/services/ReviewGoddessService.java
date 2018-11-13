package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.beans.vo.AppIndexGoddessVO;
import com.ddw.dao.GoddessMapper;
import com.ddw.dao.UserInfoMapper;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.util.Tools;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 女神
 * Created by Jacky on 2018/4/16.
 */
@Service
@Transactional(readOnly = true)
public class ReviewGoddessService extends CommonService {
    @Autowired
    private CommonReviewService commonReviewService;
    @Autowired
    private MyAttentionService myAttentionService;
    @Autowired
    private LiveRadioClientService liveRadioClientService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GoddessMapper goddessMapper;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private BasePhotoService photoService;
    @Autowired
    private BiddingService biddingService;


    public GoddessPO getAppointment(Integer userid,Integer storeId)throws Exception{
        String obj=(String) CacheUtil.get("publicCache","goddess-"+storeId+"-"+userid);
        if(obj==null){
            Map searchMap=new HashMap();
            searchMap.put("storeId",storeId);
            searchMap.put("userId",userid);
            GoddessPO po= this.commonObjectBySearchCondition("ddw_goddess",searchMap,GoddessPO.class);
            if(po!=null){
                Integer ap=po.getAppointment()==null?0:po.getAppointment();
                po.setAppointment(ap);
                CacheUtil.put("publicCache","goddess-"+storeId+"-"+userid, JSONObject.toJSONString(po));

                return po;
            }
        }else{
            return  JSONObject.parseObject(obj,GoddessPO.class);
        }
       return null;
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO updateAppointment(String userid, Integer ap)throws Exception{
       GoddessPO po= this.commonObjectBySingleParam("ddw_goddess","userId",userid,GoddessPO.class);
       if(po!=null){
           if(GoddessAppointmentEnum.getName(ap)==null){
               return new ResponseApiVO(-2,"ap状态值不正确",null);
           }
           Map map=new HashMap();
           map.put("appointment",ap);
           ResponseVO vo=this.commonUpdateBySingleSearchParam("ddw_goddess",map,"userId",userid);
           if(vo.getReCode()==1){
               po.setAppointment(ap);
               CacheUtil.put("publicCache","goddess-"+po.getStoreId()+"-"+userid, JSONObject.toJSONString(po));

               return new ResponseApiVO(1,"修改成功",null);
           }
       }
       return new ResponseApiVO(-2,"修改失败",null);

    }

    public GoddessPO getByUserId(Integer userId)throws Exception{
        return this.commonObjectBySingleParam("ddw_goddess","userId",userId,GoddessPO.class);
    }
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO apply(UserInfoVO user,int storeId)throws Exception{
        Map conditionMap = new HashMap<>();
        conditionMap.put("drProposer",user.getId());
        conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
        //查询状态审核未通过
        conditionMap.put("drReviewStatus,!=",2);
        ReviewRealNamePO realPO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewRealNamePO().getClass());
        if(realPO != null){
            return new ResponseVO(-3,"不允许重复提交申请",null);
        }
        //更新会员女神状态为审核中
        Map setConditionMap = new HashMap<>();
        setConditionMap.put("goddessFlag",2);
        this.commonUpdateBySingleSearchParam("ddw_userinfo",setConditionMap,"id",user.getId());
        //插入审批表
        ReviewPO reviewPO=new ReviewPO();
        String bussinessCode = String.valueOf(new Date().getTime());
        reviewPO.setDrBusinessCode(bussinessCode);
        reviewPO.setCreateTime(new Date());
        reviewPO.setDrProposerName(user.getRealName());
        reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
        reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
        reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
        reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        reviewPO.setDrProposer(Integer.valueOf(user.getId()));
        reviewPO.setDrApplyDesc("申请成为女神");
        reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.goddessFlag2.getCode());
        reviewPO.setDrBelongToStoreId(storeId);
        return this.commonReviewService.submitAppl(reviewPO);
    }

    /**
     * 当前门店女神列表,含关注状态
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
//    public ResponseVO goddessList(String token,Integer pageNum,Integer pageSize)throws Exception{
//        JSONObject json = new JSONObject();
//        if(pageNum == null || pageSize == null){
//            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
//        }
//        Integer storeId = TokenUtil.getStoreId(token);
//        Integer userId = TokenUtil.getUserId(token);
//        Integer startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
//        Integer endRow = pageSize;
//        List<AppIndexGoddessVO>AppIndexGoddessList = goddessMapper.getGoddessList(storeId,startRow,endRow);
//        int count = goddessMapper.getGoddessListCount(storeId);
//        ListIterator<AppIndexGoddessVO> appIndexGoddessIterator = AppIndexGoddessList.listIterator();
//        MyAttentionVO myAttentionVO = (MyAttentionVO)myAttentionService.queryGoddessByUserId(userId,1,9999).getData();
//        List<UserInfoVO> myAttentionGoddessList = myAttentionVO.getUserInfoList();
//        while (appIndexGoddessIterator.hasNext()){
//            AppIndexGoddessVO appIndexGoddessVO = appIndexGoddessIterator.next();
//            ResponseApiVO rav = reviewService.getLiveRadioReviewStatus(appIndexGoddessVO.getId(),storeId);
//            /**
//             *    liveStatus0("等待直播",0)-》2,"直播等待中，请前往直播房间
//             liveStatus1("正在直播",1)-》-2002,"直播房间已开，请关闭再申请"
//             liveStatus2("停用",2);其它
//
//             */
//            if(rav.getReCode() == 2){
//                appIndexGoddessVO.setLiveRadioFlag(0);
//            }else if(rav.getReCode() == -2002){
//                appIndexGoddessVO.setLiveRadioFlag(1);
//            }else {
//                appIndexGoddessVO.setLiveRadioFlag(2);
//            }
//            if(myAttentionGoddessList != null){
//                for(UserInfoVO myAttentionGoddess:myAttentionGoddessList){
//                    if(myAttentionGoddess.getId() == appIndexGoddessVO.getId()){
//                        appIndexGoddessVO.setFollowed(true);
//                        break;
//                    }
//                }
//            }
//        }
//        json.put("list",AppIndexGoddessList);
//        json.put("count",count);
//        return new ResponseVO(1,"成功",json);
//    }

    /**
     * 当前门店女神列表,不判断关注状态
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
//    public ResponseVO goddessNoAttentionList(String token,Integer pageNum,Integer pageSize)throws Exception{
//        if(pageNum == null || pageSize == null){
//            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
//        }
//        Integer storeId = TokenUtil.getStoreId(token);
//        Integer startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
//        Integer endRow = pageSize;
//        List<AppIndexGoddessVO>AppIndexGoddessList = goddessMapper.getGoddessList(storeId,startRow,endRow);
//        ListIterator<AppIndexGoddessVO> appIndexGoddessIterator = AppIndexGoddessList.listIterator();
//        while (appIndexGoddessIterator.hasNext()){
//            AppIndexGoddessVO appIndexGoddessVO = appIndexGoddessIterator.next();
//            ResponseApiVO rav = reviewService.getLiveRadioReviewStatus(appIndexGoddessVO.getId(),storeId);
//            LiveRadioPO liveRadioP = (LiveRadioPO)rav.getData();
//            /**
//             *    liveStatus0("等待直播",0)-》2,"直播等待中，请前往直播房间
//             liveStatus1("正在直播",1)-》-2002,"直播房间已开，请关闭再申请"
//             liveStatus2("停用",2);其它
//
//             */
//            if(rav.getReCode() == 2){
//                appIndexGoddessVO.setLiveRadioFlag(0);
//            }else if(rav.getReCode() == -2002){
//                appIndexGoddessVO.setLiveRadioFlag(1);
//                appIndexGoddessVO.setCode(liveRadioP.getId());
//            }else{
//                appIndexGoddessVO.setLiveRadioFlag(2);
//                /*
////            }else {
////                //判断审核缓存是否存在
////                if (CacheUtil.get("review", "liveRadio" + appIndexGoddessVO.getId()) == null) {
////                    Map condition1 = new HashMap();
////                    condition1.put("drProposer", appIndexGoddessVO.getId());
////                    condition1.put("drBusinessType", ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());
////                    condition1.put("drReviewStatus", ReviewStatusEnum.ReviewStatus2.getCode());
////                    List<Map> list1 = this.getCommonMapper().selectObjects(new CommonSearchBean("ddw_review", condition1));
////                    if (list1.size() > 0) {
////                        CacheUtil.put("review", "liveRadio" + appIndexGoddessVO.getId(), 3);
////                        appIndexGoddessVO.setLiveRadioFlag(3);
////                    }
////                } else {
////                    appIndexGoddessVO.setLiveRadioFlag((Integer) CacheUtil.get("review", "liveRadio" + appIndexGoddessVO.getId()));
////                }
//*/
//            }
//        }
//        return new ResponseVO(1,"成功",AppIndexGoddessList);
//    }

    /**
     * 根据粉丝数量降序,展示女神数据,列表不包含自己
     * @param userId 会员id
     * @param pageNo
     * @return
     * @throws Exception
     */
    public List<AppIndexGoddessVO> goddessList(Integer userId,Integer pageNo, Integer weekList)throws Exception{
        Integer pageNum = pageNo;
        Integer pageSize = 10;
        Integer start = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        Integer end = pageSize;
        List<AppIndexGoddessVO> appIndexGoddess = goddessMapper.getGoddessListByNotInIds(null,userId,start,pageSize,weekList);
        List<Integer> userIdList = new ArrayList<>();
        if(appIndexGoddess!=null){
            appIndexGoddess.forEach(a -> {
                try {
                    userIdList.add(a.getId());
                    a.setHeadImgUrl(photoService.getPhotograph(a.getId()));
                    a.setFocus(myAttentionService.isFocusGoddess(userId, a.getId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if(weekList != 1 && pageSize - appIndexGoddess.size()>0){
            end = pageSize-appIndexGoddess.size();
            List<AppIndexGoddessVO> appIndexGoddess1 = goddessMapper.getGoddessListByNotInIdsNoFans(userIdList,userId,start,end);
            if(appIndexGoddess1!=null){
                appIndexGoddess1.forEach(a -> {
                    try {
                        a.setHeadImgUrl(photoService.getPhotograph(a.getId()));
                        a.setFocus(myAttentionService.isFocusGoddess(userId, a.getId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            appIndexGoddess.addAll(appIndexGoddess1);
        }
        return appIndexGoddess;
    }
    public ResponseApiVO getGoddessFansAndContr(String token,Integer goddessCode)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("goddessId",goddessCode);
        Long fansCount = this.commonCountBySearchCondition("ddw_my_attention",searchCondition);

        searchCondition = new HashMap<>();
        searchCondition.put("userId",goddessCode);
        Long attenCount = this.commonCountBySearchCondition("ddw_my_attention",searchCondition);

        searchCondition=new HashMap();
        searchCondition.put("userId", TokenUtil.getUserId(token));
        searchCondition.put("goddessId",goddessCode);
        Map ower=this.commonObjectBySearchCondition("ddw_my_attention",searchCondition);

        searchCondition=new HashMap();
        searchCondition.put("incomeUserId",goddessCode);
        searchCondition.put("consumeUserId",TokenUtil.getUserId(token));
        searchCondition.put("type",IncomeTypeEnum.IncomeType1.getCode());
        Long sum = this.commonSumByBySingleSearchMap("ddw_consume_ranking_list","consumePrice",searchCondition);

        Map userMap=this.commonObjectBySingleParam("ddw_userinfo","id",goddessCode);
        GoddessInfoVO vo=new GoddessInfoVO();
        vo.setAttentionNum(attenCount==null?0:attenCount.intValue());
        vo.setFansNum(fansCount==null?0:fansCount.intValue());
        vo.setContributeNum(sum==null?0:sum.intValue());
        vo.setName(userMap.get("nickName").toString());
        vo.setHeadImg(userMap.get("headImgUrl").toString());
        vo.setIsAttenion(ower==null?0:1);
        return new ResponseApiVO(1,"成功",vo);

    }
    public ResponseApiVO getGoddessRanking(String token,Integer goddessCode)throws Exception{
        Map searchCondition = new HashMap<>();


        searchCondition=new HashMap();
        searchCondition.put("userId", TokenUtil.getUserId(token));
        searchCondition.put("goddessId",goddessCode);
        Map ower=this.commonObjectBySearchCondition("ddw_my_attention",searchCondition);

        searchCondition=new HashMap();
        searchCondition.put("incomeUserId",goddessCode);
        searchCondition.put("consumeUserId",TokenUtil.getUserId(token));
        searchCondition.put("type",IncomeTypeEnum.IncomeType1.getCode());
        Long sum = this.commonSumByBySingleSearchMap("ddw_consume_ranking_list","consumePrice",searchCondition);

        Map userMap=this.commonObjectBySingleParam("ddw_userinfo","id",goddessCode);
        GoddessUserRankingInfoVO vo=new GoddessUserRankingInfoVO();

        vo.setContributeNum(sum==null?0:sum.intValue());
        vo.setName(userMap.get("nickName").toString());
        vo.setHeadImg(userMap.get("headImgUrl").toString());
        vo.setIsAttenion(ower==null?0:1);
        return new ResponseApiVO(1,"成功",vo);

    }

    public ResponseApiVO getDynamics(String token,String dynRoleType,DynamicsDTO pageNoDTO)throws Exception{

        if(pageNoDTO.getCode()==null || pageNoDTO.getCode()<1){
            return new ResponseApiVO(-2,"女神code有误",null);
        }
        Map searchMap=new HashMap();
        searchMap.put("userId",pageNoDTO.getCode());
        searchMap.put("roleType,in","("+dynRoleType+")");
        List<Map> list=this.commonList("ddw_dynamics","createTime desc",pageNoDTO.getPageNo()==null?1:pageNoDTO.getPageNo(),10,searchMap);
       final Date currentDate=new Date();
        if(list!=null && list.size()>0){
            list.forEach(a->{
                a.remove("id");
                a.remove("roleType");
                a.remove("userId");
                if(!a.containsKey("endTime") || a.get("endTime")==null ){
                    a.put("useTime", Tools.getSurplusTimeStr(currentDate,(Date)a.get("createTime")));
                }else if(currentDate.before((Date)a.get("endTime"))){
                    a.put("useTime",Tools.getSurplusTimeStr(currentDate,(Date)a.get("createTime")));
                }else{
                    a.put("useTime",Tools.getSurplusTimeStr((Date)a.get("endTime"),(Date)a.get("createTime")));
                }
                a.put("createTime",DateFormatUtils.format((Date)a.get("createTime"),"yyyy-MM-dd HH:mm"));
                a.remove("busId");
                a.remove("endTime");

            });
            return new ResponseApiVO(1,"成功",new ListVO<>(list));
        }
        return new ResponseApiVO(1,"成功", new ListVO<>(new ArrayList<>()));
    }

    /**
     * 插入评价
     * @param dto
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertGoddessEvaluationDetail(String token,GoddessEvaluationDetailDTO dto)throws Exception{
        Map searchMap=new HashMap();
        Integer userId=TokenUtil.getUserId(token);
        searchMap.put("luckyDogUserId",userId);
        searchMap.put("id",dto.getBidCode());
        Map bidMap=biddingService.getCurrentBidMapBySearchMap(searchMap);
        if(bidMap==null || bidMap.size()==0){
            return new ResponseVO(-2,"记录不存在",null);
        }
        Integer isEvaluate=(Integer) bidMap.get("isEvaluate");
        if(isEvaluate!=null && isEvaluate==1){
            return new ResponseVO(-2,"抱歉，已评价过",null);

        }
        GoddessEvaluationDetailPO goddessEvaluationDetailPO = new GoddessEvaluationDetailPO();
       // PropertyUtils.copyProperties(goddessEvaluationDetailPO,goddessEvaluationDetailDTO);
        goddessEvaluationDetailPO.setDescribe(dto.getDescribe());
        goddessEvaluationDetailPO.setStar(dto.getStar());
        goddessEvaluationDetailPO.setUserId(userId);
        goddessEvaluationDetailPO.setGoddessId((Integer) bidMap.get("userId"));
        goddessEvaluationDetailPO.setCreateTime(new Date());
        super.commonInsert("ddw_goddess_evaluation_detail",goddessEvaluationDetailPO);
        Map updateMap=new HashMap();
        updateMap.put("isEvaluate",1);

        this.commonOptimisticLockUpdateByParam("ddw_goddess_bidding",updateMap,searchMap,"version");
        insertGoddessEvaluation(dto.getStar(),goddessEvaluationDetailPO.getGoddessId());
        return new ResponseVO(1,"成功",null);
    }

    /**
     * 插入女神平均评分
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertGoddessEvaluation(Integer start,Integer goddessId)throws Exception{
        GoddessEvaluationPO goddessEvaluationPO = super.commonObjectBySingleParam("ddw_goddess_evaluation","goddessId",goddessId,GoddessEvaluationPO.class);
        ResponseVO responseVO = new ResponseVO();
        if (goddessEvaluationPO != null) {
            int allStar = goddessEvaluationPO.getAllStar();
            int countEvaluation = goddessEvaluationPO.getCountEvaluation();
            Map params= BeanToMapUtil.beanToMap(goddessEvaluationPO);
            params.put("countEvaluation",countEvaluation+1);
            params.put("allStar",allStar+start);
            params.put("star",Math.round(goddessEvaluationPO.getAllStar()/goddessEvaluationPO.getCountEvaluation()));
            params.put("updateTime",new Date());
            Map searchCondition = new HashMap<>();
            searchCondition.put("goddessId",goddessId);
            responseVO = super.commonUpdateByParams("ddw_goddess_evaluation",params,searchCondition);
        }else{
            goddessEvaluationPO = new GoddessEvaluationPO();
            goddessEvaluationPO.setGoddessId(goddessId);
            goddessEvaluationPO.setAllStar(start);
            goddessEvaluationPO.setCountEvaluation(1);
            goddessEvaluationPO.setStar(Math.round(start));
            goddessEvaluationPO.setCreateTime(new Date());
            responseVO = super.commonInsert("ddw_goddess_evaluation",goddessEvaluationPO);
        }
        return responseVO;
    }

    /**
     * 查询女神平均评分
     * @param goddessId 女神编号
     * @return
     * @throws Exception
     */
    public GoddessEvaluationPO getEvaluation(Integer goddessId)throws Exception {
        Map searchCondition = new HashMap<>();
        searchCondition.put("goddessId", goddessId);
        return super.commonObjectBySearchCondition("ddw_goddess_evaluation", searchCondition, GoddessEvaluationPO.class);
    }

    /**
     * 查看女神详细评价
     * @param dto
     * @return
     * @throws Exception
     */
    public ResponseApiVO getGoddessEvaluationDetailList(GoddessEvaluationDetailListDTO dto)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("goddessId",dto.getGoddessId());
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id","userId",null);
        Page p=new Page(dto.getPageNo()==null?1:dto.getPageNo(),10);
        CommonSearchBean csb=new CommonSearchBean("ddw_goddess_evaluation_detail","t1.createTime desc","DATE_FORMAT(t1.createTime,'%Y-%m-%d %H:%i:%S') time,t1.star,t1.describe,ct0.nickName,ct0.headImgUrl",p.getStartRow(),p.getEndRow(),condtion,cb);
        List list=this.getCommonMapper().selectObjects(csb);
        if(list==null || list.size()==0){
            return new ResponseApiVO(1,"成功",new ListVO<>(new ArrayList<>()));
        }
        return new ResponseApiVO(1,"成功",new ListVO<>(list));

    }

    public static void main(String[] args) throws Exception{
        System.out.println(        Tools.getSurplusTimeStr(new Date(), DateUtils.parseDate("2018-09-12 14:1:1","yyyy-MM-dd HH:mm:ss")));
    }

}
