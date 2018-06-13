package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.dao.GoddessMapper;
import com.ddw.dao.UserInfoMapper;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.gen.common.services.CacheService;
import com.gen.common.services.CommonService;
import com.gen.common.util.CacheUtil;
import com.gen.common.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private CacheService cacheService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GoddessMapper goddessMapper;
    @Autowired
    private ReviewService reviewService;


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
     * 当前门店女神列表
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public ResponseVO goddessList(String token,Integer pageNum,Integer pageSize)throws Exception{
        if(pageNum == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
        }
        Integer userId = TokenUtil.getUserId(token);
        Integer storeId = TokenUtil.getStoreId(token);
        Map searchCondition = new HashMap<>();
        searchCondition.put("storeId",storeId);
        List<Map> list = this.commonList("ddw_goddess","bidPrice desc",pageNum,pageSize,searchCondition);
        List<String>userIdList = new ArrayList<String>();
        for(Map map:list){
            userIdList.add(map.get("userId").toString());
        }
        if(!list.isEmpty()){
            List<UserInfoVO> goddessUserInfoList = userInfoMapper.getUserInfoList(userIdList);
            MyAttentionVO myAttentionVO = myAttentionService.queryGoddessByUserId(userId);
            List<UserInfoVO> myAttentionGoddessList = myAttentionVO.getUserInfoList();
            ListIterator<UserInfoVO> goddessUserInfoIterator = goddessUserInfoList.listIterator();
            while (goddessUserInfoIterator.hasNext()){
                UserInfoVO goddessUserInfo = goddessUserInfoIterator.next();
                if(myAttentionGoddessList != null){
                    for(UserInfoVO myAttentionGoddess:myAttentionGoddessList){
                        if(myAttentionGoddess.getId() == goddessUserInfo.getId()){
                            goddessUserInfo.setFollowed(true);
                            break;
                        }
                    }
                }
            }
            return new ResponseVO(1,"成功",goddessUserInfoList);
        }
        return new ResponseVO(1,"成功",null);
    }

    /**
     * 当前门店女神列表,不判断关注状态
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public ResponseVO goddessNoAttentionList(String token,Integer pageNum,Integer pageSize)throws Exception{
        if(pageNum == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
        }
        Integer storeId = TokenUtil.getStoreId(token);
        Integer startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        Integer endRow = pageSize;
        List<AppIndexGoddessVO>AppIndexGoddessList = goddessMapper.getGoddessList(storeId,startRow,endRow);
        ListIterator<AppIndexGoddessVO> appIndexGoddessIterator = AppIndexGoddessList.listIterator();
        while (appIndexGoddessIterator.hasNext()){
            AppIndexGoddessVO appIndexGoddessVO = appIndexGoddessIterator.next();
            ResponseApiVO rav = reviewService.getLiveRadioReviewStatus(appIndexGoddessVO.getId(),storeId);
            if(rav.getReCode() == 2 || rav.getReCode() == -2002){
                appIndexGoddessVO.setLiveRadioFlag(1);
            }else if(rav.getReCode() == -2003){
                appIndexGoddessVO.setLiveRadioFlag(2);
            }else if(rav.getReCode() == 1){
                appIndexGoddessVO.setLiveRadioFlag(0);
//            }else {
//                //判断审核缓存是否存在
//                if (CacheUtil.get("review", "liveRadio" + appIndexGoddessVO.getId()) == null) {
//                    Map condition1 = new HashMap();
//                    condition1.put("drProposer", appIndexGoddessVO.getId());
//                    condition1.put("drBusinessType", ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());
//                    condition1.put("drReviewStatus", ReviewStatusEnum.ReviewStatus2.getCode());
//                    List<Map> list1 = this.getCommonMapper().selectObjects(new CommonSearchBean("ddw_review", condition1));
//                    if (list1.size() > 0) {
//                        CacheUtil.put("review", "liveRadio" + appIndexGoddessVO.getId(), 3);
//                        appIndexGoddessVO.setLiveRadioFlag(3);
//                    }
//                } else {
//                    appIndexGoddessVO.setLiveRadioFlag((Integer) CacheUtil.get("review", "liveRadio" + appIndexGoddessVO.getId()));
//                }
            }
        }
        return new ResponseVO(1,"成功",AppIndexGoddessList);
    }
}
