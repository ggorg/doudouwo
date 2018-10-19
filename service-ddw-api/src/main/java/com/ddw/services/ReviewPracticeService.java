package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.beans.vo.AppIndexPracticeVO;
import com.ddw.beans.vo.GameVO;
import com.ddw.beans.vo.PracticeGameVO;
import com.ddw.dao.PracticeMapper;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 代练
 * Created by Jacky on 2018/4/16.
 */
@Service
@Transactional(readOnly = true)
public class ReviewPracticeService extends CommonService {
    private final Logger logger = Logger.getLogger(ReviewPracticeService.class);
    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;
    @Autowired
    private CommonReviewService commonReviewService;
    @Autowired
    private MyAttentionService myAttentionService;
    @Autowired
    private GameService gameService;
    @Autowired
    protected IncomeService incomeService;
    @Autowired
    protected BaseConsumeRankingListService baseConsumeRankingListService;
    @Autowired
    private PracticeMapper practiceMapper;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO apply(Integer id,Integer storeId,String userName, String gameId, String rankId, MultipartFile photograph1,MultipartFile photograph2,MultipartFile photograph3)throws Exception{
        if(StringUtils.isBlank(gameId) || StringUtils.isBlank(rankId) ||
                photograph1 == null || photograph2 == null ||photograph3 == null ||
                photograph1.isEmpty() || photograph2.isEmpty() ||photograph3.isEmpty()){
            return new ResponseApiVO(-1,"参数不正确",null);
        }else{
            //允许重复提交申请,审核通过更新资料
//            Map conditionMap = new HashMap<>();
//            conditionMap.put("drProposer",id);
//            conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
//            //查询状态审核未通过
//            conditionMap.put("drReviewStatus,!=",2);
//            ReviewRealNamePO realPO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewRealNamePO().getClass());
//            if(realPO != null){
//                return new ResponseApiVO(-2,"不允许重复提交申请",null);
//            }
            //更新会员代练状态为审核中
            Map setParams = new HashMap<>();
            setParams.put("practiceFlag",2);
            this.commonUpdateBySingleSearchParam("ddw_userinfo",setParams,"id",id);
            //插入审批表
            ReviewPO reviewPO=new ReviewPO();
            String bussinessCode = String.valueOf(new Date().getTime());
            reviewPO.setDrBusinessCode(bussinessCode);
            reviewPO.setCreateTime(new Date());
            reviewPO.setDrProposerName(userName);
            reviewPO.setDrBelongToStoreId(storeId);
            reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
            reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
            reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
            reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
            reviewPO.setDrProposer(Integer.valueOf(id));
            reviewPO.setDrApplyDesc("申请成为代练");
            reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.practiceFlag5.getCode());
            ResponseApiVO responseApiVO = new ResponseApiVO(this.commonReviewService.submitAppl(reviewPO));
            //插入代练认证审核表
            if(responseApiVO.getReCode()>0){
                ReviewPracticePO reviewPracticePO = new ReviewPracticePO();
                logger.info("申请成为代练apply->UserId->"+id+"->storeId->"+storeId+"->userName->"+userName+"->gameId->"+gameId+"->rankId->"+rankId);
                reviewPracticePO.setUserId(Integer.valueOf(id));
                reviewPracticePO.setStoreId(storeId);
                reviewPracticePO.setDrBusinessCode(bussinessCode);
                reviewPracticePO.setGameId(Integer.valueOf(gameId));
                reviewPracticePO.setRankId(Integer.valueOf(rankId));
                reviewPracticePO.setCreateTime(new Date());
                reviewPracticePO.setUpdateTime(new Date());
                reviewPracticePO.setStatus(0);
                String ImgName1= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph1.getOriginalFilename());
                FileInfoVo fileInfoVo1= UploadFileMoveUtil.move( photograph1,mainGlobals.getRsDir(), ImgName1);
                reviewPracticePO.setPicUrl1(mainGlobals.getServiceUrl()+fileInfoVo1.getUrlPath());
                CommonBeanFiles f1=this.fileService.createCommonBeanFiles(fileInfoVo1);
                this.fileService.saveFile(f1);

                String ImgName2= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph2.getOriginalFilename());
                FileInfoVo fileInfoVo2= UploadFileMoveUtil.move( photograph2,mainGlobals.getRsDir(), ImgName2);
                reviewPracticePO.setPicUrl2(mainGlobals.getServiceUrl()+fileInfoVo2.getUrlPath());
                CommonBeanFiles f2=this.fileService.createCommonBeanFiles(fileInfoVo2);
                this.fileService.saveFile(f2);

                String ImgName3= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph3.getOriginalFilename());
                FileInfoVo fileInfoVo3= UploadFileMoveUtil.move( photograph3,mainGlobals.getRsDir(), ImgName3);
                reviewPracticePO.setPicUrl3(mainGlobals.getServiceUrl()+fileInfoVo3.getUrlPath());
                CommonBeanFiles f3=this.fileService.createCommonBeanFiles(fileInfoVo3);
                this.fileService.saveFile(f3);


                Map updatePoMap= BeanToMapUtil.beanToMap(reviewPracticePO);
                this.commonInsertMap("ddw_review_practice",updatePoMap);
            }
            return responseApiVO;
        }
    }

    /**
     * 先查询根据订单排序代练列表,列表不足由从未生成过订单的代练根据开启预约时间先后补上
     * @param token
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<AppIndexPracticeVO> practiceList(String token,Integer pageNo,Integer pageSize,Integer weekList)throws Exception{
        Integer storeId = TokenUtil.getStoreId(token);
        Integer practiceId = TokenUtil.getUserId(token);
        pageSize = pageSize==null?10:pageSize;
        Integer start = pageNo > 0 ? (pageNo - 1) * pageSize : 0;
        Integer end = pageSize;
        List<Integer> userIdList = new ArrayList<>();
        // 已发布按发布时间先后排序的代练
        List<AppIndexPracticeVO> appIndexPractice1 = practiceMapper.getPracticeListByNotInIds(userIdList,storeId,start,end,1);
//        for(AppIndexPracticeVO appIndexPracticeVO:appIndexPractice1){
//            userIdList.add(appIndexPracticeVO.getUserId());
//        }
        //取按订单排序
        if(pageSize-appIndexPractice1.size()>0){
            end = pageSize-appIndexPractice1.size();
            List<AppIndexPracticeVO> appIndexPractice2 = practiceMapper.getPracticeHaveOrderListByNoInIds(userIdList,storeId,start,end,null,weekList);
            appIndexPractice1.addAll(appIndexPractice2);
        }
//        if(pageSize-appIndexPractice1.size()>0){
//            end = pageSize-appIndexPractice1.size();
//            userIdList.clear();
//            for(AppIndexPracticeVO appIndexPracticeVO:appIndexPractice1){
//                userIdList.add(appIndexPracticeVO.getUserId());
//            }
//            List<AppIndexPracticeVO> appIndexPractice3 = practiceMapper.getPracticeListByNotInIds(userIdList,storeId,start,end,null);
//            appIndexPractice1.addAll(appIndexPractice3);
//        }

        //设置关注状态
        ListIterator<AppIndexPracticeVO> appIndexPracticeIterator = appIndexPractice1.listIterator();
        MyAttentionVO myAttentionVO = (MyAttentionVO)myAttentionService.queryPracticeByUserId(practiceId,1,9999).getData();
        List<MyAttentionInfoVO> myAttentionGoddessList = myAttentionVO.getUserInfoList();
        while (appIndexPracticeIterator.hasNext()){
            AppIndexPracticeVO appIndexPracticeVO = appIndexPracticeIterator.next();
            Long fans = myAttentionService.queryPracticeFansCountByUserId(appIndexPracticeVO.getUserId());
            appIndexPracticeVO.setFans(fans==null?0:fans);
            PracticeEvaluationPO practiceEvaluationPO = this.getEvaluation(appIndexPracticeVO.getUserId());
            int star = practiceEvaluationPO == null?0:practiceEvaluationPO.getStar();
            appIndexPracticeVO.setStar(star);
            if (myAttentionGoddessList != null) {
                for(MyAttentionInfoVO userInfoVO:myAttentionGoddessList){
                    if(userInfoVO.getUserId() == appIndexPracticeVO.getUserId()){
                        appIndexPracticeVO.setFocus(true);
                        break;
                    }
                }
            }
        }
        return appIndexPractice1;
    }

    /**
     * 根据订单排序代练列表,列表不足由从未生成过订单的代练补上
     * @param token
     * @param pageNo
     * @param appointment 1的时候查询发布代练,null的时候所有
     * @param weekList 1的时候查询订单本周榜单,null的时候所有
     * @return
     * @throws Exception
     */
//    public List<AppIndexPracticeVO> leaderboard(String token,Integer pageNo,Integer appointment,Integer weekList)throws Exception{
//        Integer storeId = TokenUtil.getStoreId(token);
//        Integer practiceId = TokenUtil.getUserId(token);
//        Integer pageNum = pageNo;
//        Integer pageSize = 10;
//        Integer start = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
//        Integer end = pageSize;
//        List<Integer> userIdList = new ArrayList<>();
//
//        List<AppIndexPracticeVO> appIndexPractice1 = practiceMapper.getPracticeHaveOrderListByNoInIds(userIdList,storeId,start,end,appointment,weekList);
//        for(AppIndexPracticeVO appIndexPracticeVO:appIndexPractice1){
//            userIdList.add(appIndexPracticeVO.getUserId());
//        }
//        if(pageSize-appIndexPractice1.size()>0){
//            end = pageSize-appIndexPractice1.size();
//            userIdList.clear();
//            for(AppIndexPracticeVO appIndexPracticeVO:appIndexPractice1){
//                userIdList.add(appIndexPracticeVO.getUserId());
//            }
//            List<AppIndexPracticeVO> appIndexPractice3 = practiceMapper.getPracticeListByNotInIds(userIdList,storeId,start,end,null);
//            appIndexPractice1.addAll(appIndexPractice3);
//        }
//
//        //设置关注状态
//        ListIterator<AppIndexPracticeVO> appIndexPracticeIterator = appIndexPractice1.listIterator();
//        MyAttentionVO myAttentionVO = (MyAttentionVO)myAttentionService.queryPracticeByUserId(practiceId,1,9999).getData();
//        List<MyAttentionInfoVO> myAttentionGoddessList = myAttentionVO.getUserInfoList();
//        while (appIndexPracticeIterator.hasNext()){
//            AppIndexPracticeVO appIndexPracticeVO = appIndexPracticeIterator.next();
//            Long fans = myAttentionService.queryPracticeFansCountByUserId(appIndexPracticeVO.getUserId());
//            appIndexPracticeVO.setFans(fans==null?0:fans);
//            PracticeEvaluationPO practiceEvaluationPO = this.getEvaluation(appIndexPracticeVO.getUserId());
//            int star = practiceEvaluationPO == null?0:practiceEvaluationPO.getStar();
//            appIndexPracticeVO.setStar(star);
//            if (myAttentionGoddessList != null) {
//                for(MyAttentionInfoVO userInfoVO:myAttentionGoddessList){
//                    if(userInfoVO.getUserId() == appIndexPracticeVO.getUserId()){
//                        appIndexPracticeVO.setFocus(true);
//                        break;
//                    }
//                }
//            }
//        }
//        return appIndexPractice1;
//    }


    /**
     * 插入评价
     * @param practiceEvaluationDetailDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertPracticeEvaluationDetail(PracticeEvaluationDetailDTO practiceEvaluationDetailDTO,PracticeOrderPO practiceOrderPO)throws Exception{
        Map setParams = new HashMap<>();
        setParams.put("evaluationStatus",1);//评价状态
        this.commonUpdateBySingleSearchParam("ddw_practice_order",setParams,"id",practiceEvaluationDetailDTO.getOrderId());
        PracticeEvaluationDetailPO practiceEvaluationDetailPO = new PracticeEvaluationDetailPO();
        practiceEvaluationDetailPO.setPracticeId(practiceOrderPO.getPracticeId());
        practiceEvaluationDetailPO.setGameId(practiceOrderPO.getGameId());
        practiceEvaluationDetailPO.setStar(practiceEvaluationDetailDTO.getStar());
        practiceEvaluationDetailPO.setUserId(practiceOrderPO.getUserId());
        practiceEvaluationDetailPO.setDescribe(practiceEvaluationDetailDTO.getDescribe());
        practiceEvaluationDetailPO.setCreateTime(new Date());
        return super.commonInsert("ddw_practice_evaluation_detail",practiceEvaluationDetailPO);
    }

    /**
     * 插入代练平均评分
     * @param practiceEvaluationDetailDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertPracticeEvaluation(PracticeEvaluationDetailDTO practiceEvaluationDetailDTO,Integer practiceId)throws Exception{
        PracticeEvaluationPO practiceEvaluationPO = super.commonObjectBySingleParam("ddw_practice_evaluation","practiceId",practiceId,PracticeEvaluationPO.class);
        ResponseVO responseVO = new ResponseVO();
        if (practiceEvaluationPO != null) {
            int allStar = practiceEvaluationPO.getAllStar();
            int countEvaluation = practiceEvaluationPO.getCountEvaluation();
            Map params= BeanToMapUtil.beanToMap(practiceEvaluationPO);
            params.put("countEvaluation",countEvaluation+1);
            params.put("allStar",allStar+practiceEvaluationDetailDTO.getStar());
            params.put("star",Math.round(practiceEvaluationPO.getAllStar()/practiceEvaluationPO.getCountEvaluation()));
            params.put("updateTime",new Date());
            Map searchCondition = new HashMap<>();
            searchCondition.put("practiceId",practiceId);
            responseVO = super.commonUpdateByParams("ddw_practice_evaluation",params,searchCondition);
        }else{
            practiceEvaluationPO = new PracticeEvaluationPO();
            practiceEvaluationPO.setPracticeId(practiceId);
            practiceEvaluationPO.setAllStar(practiceEvaluationDetailDTO.getStar());
            practiceEvaluationPO.setCountEvaluation(1);
            practiceEvaluationPO.setStar(Math.round(practiceEvaluationDetailDTO.getStar()));
            practiceEvaluationPO.setCreateTime(new Date());
            responseVO = super.commonInsert("ddw_practice_evaluation",practiceEvaluationPO);
        }
        return responseVO;
    }

    /**
     * 查询代练平均评分
     * @param practiceId 代练编号
     * @return
     * @throws Exception
     */
    public PracticeEvaluationPO getEvaluation(Integer practiceId)throws Exception {
        Map searchCondition = new HashMap<>();
        searchCondition.put("practiceId", practiceId);
        return super.commonObjectBySearchCondition("ddw_practice_evaluation", searchCondition, PracticeEvaluationPO.class);
    }

    /**
     * 查看代练详细评价
     * @param practicePubTaskDTO
     * @return
     * @throws Exception
     */
    public List getPracticeEvaluationDetailList(PracticePubTaskDTO practicePubTaskDTO)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("practiceId",practicePubTaskDTO.getPracticeId());
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_evaluation_detail","createTime desc","t1.*,ct0.nickName,ct0.headImgUrl",null,null,condtion,cb);
        return this.commonPage(practicePubTaskDTO.getPageNo(),10,csb).getResult();
    }

    /**
     * 修改代练预约状态,1开启，2代练中，0关闭
     * @param gameId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updatePracticeGame(Integer practiceId,Integer gameId,int appointment){
        Map setParams = new HashMap<>();
        setParams.put("appointment",appointment);
        setParams.put("updateTime",new Date());
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceId);
        searchCondition.put("gameId",gameId);
        return super.commonUpdateByParams("ddw_practice_game",setParams,searchCondition);
    }

    /**
     * 查询当前代练是否已发布任务,只允许发布一个
     * @param practiceId
     * @return
     * @throws Exception
     */
    public List<Map> practiceGame(Integer practiceId)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceId);
        searchCondition.put("appointment,!=",0);
        return super.commonObjectsBySearchCondition("ddw_practice_game",searchCondition);
    }

    /**
     * 查询未结束订单不允许发布任务
     * @param practiceId
     * @param gameId
     * @return
     * @throws Exception
     */
    public long countPracticeOrder(Integer practiceId,Integer gameId)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("practiceId",practiceId);
        searchCondition.put("gameId",gameId);
        searchCondition.put("status",1);
        return super.commonCountBySearchCondition("ddw_practice_order",searchCondition);
    }

    /**
     * 代练与游戏关联表
     * @param practiceId
     * @param gameId
     * @return
     * @throws Exception
     */
    public PracticeGamePO getPracticeGamePO(Integer practiceId,Integer gameId)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceId);
        searchCondition.put("gameId",gameId);
        return super.commonObjectBySearchCondition("ddw_practice_game",searchCondition,PracticeGamePO.class);
    }
    /**
     * 查询正在进行的订单
     * @param practiceId
     * @return
     * @throws Exception
     */
    public PracticeOrderPO getOrderInProgress(Integer practiceId)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("practiceId",practiceId);
        searchCondition.put("status",1);
        return super.commonObjectBySearchCondition("ddw_practice_order",searchCondition,PracticeOrderPO.class);
    }

    /**
     * 查询订单
     * @param id 订单编号
     * @return
     * @throws Exception
     */
    public PracticeOrderPO getOrder(Integer id)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("id",id);
        return super.commonObjectBySearchCondition("ddw_practice_order",searchCondition,PracticeOrderPO.class);
    }

    /**
     * 查询接单数
     * @param id 代练会员id
     * @return
     * @throws Exception
     */
    public long getOrderCount(Integer id)throws Exception{
        return super.commonCountBySingleParam("ddw_practice_order","id",id);
    }

    /**
     * 插入代练订单
     * @param userId
     * @param practiceGameApplyDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertPracticeOrder(int userId,int storeId,PracticeGameApplyDTO practiceGameApplyDTO,int money)throws Exception{
        PracticeOrderPO practiceOrderPO = new PracticeOrderPO();
        PropertyUtils.copyProperties(practiceOrderPO,practiceGameApplyDTO);
        practiceOrderPO.setUserId(userId);
        practiceOrderPO.setStoreId(storeId);
        //订单状态，1开始接单，2完成,3未完成并结单
        practiceOrderPO.setStatus(PracticeOrderStatusEnum.orderStatus1.getCode());
        practiceOrderPO.setPayState(0);
        practiceOrderPO.setIncomeState(0);
        practiceOrderPO.setMoney(money);
        practiceOrderPO.setCreateTime(new Date());
        practiceOrderPO.setUpdateTime(new Date());
        CacheUtil.put("pay","practice-order-"+userId,practiceOrderPO);
        return super.commonInsert("ddw_practice_order",practiceOrderPO);
    }

    public ResponseVO getPracticeDynamic(PracticeDynamicDTO practiceDynamicDTO)throws Exception{
        Integer pageNum = practiceDynamicDTO.getPageNo();
        Integer pageSize = 10;
        Integer start = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        Integer end = pageSize;
        JSONObject json = new JSONObject();
        List practiceDynamicList = practiceMapper.getPracticeDynamic(practiceDynamicDTO.getPracticeId(),start,end);
        json.put("list",practiceDynamicList);
        return new ResponseVO(1,"成功",json);
    }

    /**
     * 代练分页按时间倒序展示订单
     * @param practiceId
     * @param page
     * @return
     * @throws Exception
     */
    public ResponseVO getOrderPracticeList(Integer practiceId,PageNoDTO page)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("practiceId",practiceId);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonChildBean cb4=new CommonChildBean("ddw_rank","id","targetRankId",null);
        CommonChildBean cb5=new CommonChildBean("ddw_store","id","storeId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_order","updateTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct0.openid,ct1.gameName,ct2.rank,ct3.rank AS targetRank,ct4.dsName AS storeName",null,null,condtion,cb1,cb2,cb3,cb4,cb5);
        JSONObject json = new JSONObject();
        Page p = this.commonPage(page.getPageNo(),10,csb);
        json.put("list",p.getResult());
        return new ResponseVO(1,"成功",json);
    }

    /**
     * 会员分页按时间倒序展示订单
     * @param userId
     * @param page
     * @return
     * @throws Exception
     */
    public ResponseVO getOrderUserList(Integer userId,PageNoDTO page)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("userId",userId);
//        condtion.put("status,in","(1,2,3)");
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","practiceId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonChildBean cb4=new CommonChildBean("ddw_rank","id","targetRankId",null);
        CommonChildBean cb5=new CommonChildBean("ddw_store","id","storeId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_order","updateTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct0.openid,ct1.gameName,ct2.rank,ct3.rank AS targetRank,ct4.dsName AS storeName",null,null,condtion,cb1,cb2,cb3,cb4,cb5);
        JSONObject json = new JSONObject();
        Page p = this.commonPage(page.getPageNo(),10,csb);
        json.put("list",p.getResult());
        return new ResponseVO(1,"成功",json);
    }

    public ResponseVO getPracticeOrder(Integer id)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("id",id);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonChildBean cb4=new CommonChildBean("ddw_rank","id","targetRankId",null);
        CommonChildBean cb5=new CommonChildBean("ddw_store","id","storeId",null);
        CommonChildBean cb6=new CommonChildBean("ddw_userinfo","id","practiceId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_order","updateTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct1.gameName,ct2.rank,ct3.rank AS targetRank,ct4.dsName AS storeName,ct5.nickName pnickName,ct5.headImgUrl pheadImgUrl",null,null,condtion,cb1,cb2,cb3,cb4,cb5,cb6);
        JSONObject json = new JSONObject();
        Page p = this.commonPage(1,10,csb);
        json.put("list",p.getResult());
        return new ResponseVO(1,"成功",json);
    }

    /**
     * 代练已发布代练任务
     * @param practicePubTaskDTO
     * @return
     * @throws Exception
     */
    public ResponseVO getPubTaskList(Integer userId,PracticePubTaskDTO practicePubTaskDTO)throws Exception{
        Map condtion = new HashMap<>();
        if(practicePubTaskDTO !=null && practicePubTaskDTO.getPracticeId() != null){
            condtion.put("userId",practicePubTaskDTO.getPracticeId());
        }
        condtion.put("appointment,!=",0);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_game","appointment asc","t1.*,ct0.nickName,ct0.headImgUrl,ct1.gameName,ct2.rank",null,null,condtion,cb1,cb2,cb3);
        JSONObject json = new JSONObject();
        Page p = this.commonPage(practicePubTaskDTO.getPageNo(),10,csb);
        ListIterator<Map> listIterator = p.getResult().listIterator();
        while (listIterator.hasNext()){
            Map map = listIterator.next();
            PracticeEvaluationPO practiceEvaluationPO = this.getEvaluation((Integer) map.get("userId"));
            int star = practiceEvaluationPO==null?0:practiceEvaluationPO.getStar();
            map.put("star",star);
        }
        json.put("list",p.getResult());
        return new ResponseVO(1,"成功",json);
    }

    /**
     * 订单结算
     * 客户违约（用户先提出结束），按照下单时间来定，客户一小时内要走，就扣罚违约金（总金额*30%），
     * 代练掉星也不用赔偿，超过一小时，则客户提前走就不用罚违约金了，按照实际代练结果结算。
     * 代练未完成要线下双倍赔付，退全款，返回需要线下双倍退款金额。
     * @param userId
     * @param practiceSettlementDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO<PracticeSettlementVO> settlement(Integer userId,PracticeSettlementDTO practiceSettlementDTO)throws Exception{
        // 查询订单信息,查询段位信息,根据段位和星计算金额
        PracticeOrderPO practiceOrderPO = this.getOrder(practiceSettlementDTO.getOrderId());

        int payMoney = 0;//支付金额,单位分
        int gameId = practiceOrderPO.getGameId();
        int rankId = practiceOrderPO.getRankId();//原段位
        int star = practiceOrderPO.getStar();//原星
        int realityRankId = practiceSettlementDTO.getRealityRankId();//实际段位
        int realityStar = practiceSettlementDTO.getRealityStar();//实际星数
        //计算实际需要支付金额
        payMoney = this.payMoney(gameId,rankId,star,realityRankId,realityStar);
        PropertyUtils.copyProperties(practiceOrderPO,practiceSettlementDTO);
        //代练结算
        if(userId.equals(practiceOrderPO.getPracticeId())){
            if(payMoney < practiceOrderPO.getMoney()){//实际金额比预支付低,未达到代练目标,双倍退款
                //订单状态，1开始接单，2完成,3代练结算未完成目标（双倍退差价），4用户提前结算1小时内扣违约金（总金额的30%），
                // 5用户提前结算超过1小时不扣违约金，6退款申请，7退款成功，8退款拒绝
                practiceOrderPO.setStatus(3);
                practiceOrderPO.setRefund(payMoney*2);
            }else {
                practiceOrderPO.setRefund(practiceOrderPO.getMoney()-payMoney);
                practiceOrderPO.setRealityMoney(payMoney);
            }
        }else{//用户结算
            int minutes = (int)(practiceOrderPO.getCreateTime().getTime()-new Date().getTime())/(1000 * 60);
            if(minutes < 60 && payMoney != practiceOrderPO.getMoney()){//代练低于60分钟客户结束,收取违约金
                practiceOrderPO.setStatus(4);
                practiceOrderPO.setRefund(practiceOrderPO.getMoney()-practiceOrderPO.getMoney()*3/10);
                practiceOrderPO.setRealityMoney(practiceOrderPO.getMoney()*3/10);
            }else {
                practiceOrderPO.setStatus(5);
                practiceOrderPO.setRefund(practiceOrderPO.getMoney()-payMoney);
                practiceOrderPO.setRealityMoney(payMoney);
            }
        }
        if(payMoney == practiceOrderPO.getMoney()){
            practiceOrderPO.setStatus(2);
        }
        practiceOrderPO.setUpdateTime(new Date());
        if(practiceOrderPO.getPayState() != 1){
            practiceOrderPO.setPayState(2);
        }
        if(practiceOrderPO.getRealityMoney()!=0){
            //计算收益
            this.incomeService.commonIncome(practiceOrderPO.getPracticeId(),practiceOrderPO.getRealityMoney(), IncomeTypeEnum.IncomeType2, OrderTypeEnum.OrderType10,practiceOrderPO.getOrderNo());
            this.baseConsumeRankingListService.save(practiceOrderPO.getUserId(),practiceOrderPO.getPracticeId(),payMoney,IncomeTypeEnum.IncomeType2);
        }
        //更新状态为已计算收益
        practiceOrderPO.setIncomeState(1);
        Map updatePoMap= BeanToMapUtil.beanToMap(practiceOrderPO);
        if(updatePoMap.containsKey("orderId"))updatePoMap.remove("orderId");
        ResponseVO responseVO = super.commonUpdateBySingleSearchParam("ddw_practice_order",updatePoMap,"id",practiceSettlementDTO.getOrderId());
        //结算后,修改代练状态为关闭
        Map setParams = new HashMap<>();
        setParams.put("appointment",0);
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceOrderPO.getPracticeId());
        searchCondition.put("gameId",gameId);
        ResponseVO responseVO2 = super.commonUpdateByParams("ddw_practice_game",setParams,searchCondition);
        PracticeSettlementVO practiceSettlementVO = new PracticeSettlementVO();
        practiceSettlementVO.setMoney(practiceOrderPO.getMoney());
        practiceSettlementVO.setRealityMoney(practiceOrderPO.getRealityMoney());
        practiceSettlementVO.setRefund(practiceOrderPO.getRefund());
        if(responseVO.getReCode()>0 && responseVO2.getReCode()>0){
            return new ResponseApiVO(1,"成功",practiceSettlementVO);
        }else{
            return new ResponseApiVO(-1,"失败",null);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO endThePractice(Integer orderId,MultipartFile photograph)throws Exception{
        PracticeOrderPO practiceOrderPO = this.getOrder(orderId);
        practiceOrderPO.setUpdateTime(new Date());

        String ImgName1= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph.getOriginalFilename());
        FileInfoVo fileInfoVo1= UploadFileMoveUtil.move( photograph,mainGlobals.getRsDir(), ImgName1);
        practiceOrderPO.setPicUrl(mainGlobals.getServiceUrl()+fileInfoVo1.getUrlPath());
        CommonBeanFiles f1=this.fileService.createCommonBeanFiles(fileInfoVo1);
        this.fileService.saveFile(f1);

        Map updatePoMap= BeanToMapUtil.beanToMap(practiceOrderPO);
        if(updatePoMap.containsKey("orderId"))updatePoMap.remove("orderId");
        ResponseVO responseVO = super.commonUpdateBySingleSearchParam("ddw_practice_order",updatePoMap,"id",orderId);
        //结算后,修改代练状态为关闭
        Map setParams = new HashMap<>();
        setParams.put("appointment",0);
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceOrderPO.getPracticeId());
        searchCondition.put("gameId",practiceOrderPO.getGameId());
        return super.commonUpdateByParams("ddw_practice_game",setParams,searchCondition);
    }

    /**
     * 计算段位和星需要支付金额,负数则为降星赔偿
     * @param gameId 游戏id
     * @param rankId 原段位id
     * @param star 原星
     * @param realityRankId 结算段位
     * @param realityStar 结算星
     */
    public int payMoney(int gameId,int rankId,int star,int realityRankId,int realityStar)throws Exception{
        List<GameVO> gameList = gameService.getGameList();
        int payMoney = 0;//支付金额,单位分
        for(GameVO gameVO:gameList){
            if(gameVO.getId() == gameId){
                //根据段位星数计算金额
                int sort = 0;
                int realitySort = 0;
                int money = 0;
                List<RankPO> rankPOList = gameVO.getRankList();
                for(RankPO rankPO:rankPOList){
                    if(rankId == rankPO.getId()){
                        sort = rankPO.getSort();//段位序号
                    }
                    if(realityRankId == rankPO.getId()){
                        realitySort = rankPO.getSort();//实际段位序号
                        money = rankPO.getMoney();//星单价
                    }
                }
                if(realitySort > sort){
                    for (RankPO rankPO:rankPOList){
                        for(int i=0;i<=realitySort-sort;i++){
                            if(rankPO.getSort() == sort+i){
                                int upStar = 0;
                                if(i == 0){
                                    upStar = rankPO.getStar()-star;
                                }else if(i > 0){
                                    upStar = rankPO.getStar();
                                }else if(i == realitySort-sort){
                                    upStar = realityStar;
                                }
                                payMoney += upStar*rankPO.getMoney();
                            }
                        }
                    }
                }else if(realitySort == sort){//同等段位
//                    if(realityStar > star){
                        payMoney = (realityStar - star) * money;
//                    }else if(realityStar < star){//降星,双倍赔偿
//                        payMoney = (realityStar - star) * money;
//                    }
                }else{//降段降星
                    for (RankPO rankPO:rankPOList){
                        for(int i=0;i<=sort-realitySort;i++){
                            if(rankPO.getSort() == realitySort+i){
                                int upStar = 0;
                                if(i == 0){
                                    upStar = rankPO.getStar()-star;
                                }else if(i > 0){
                                    upStar = rankPO.getStar();
                                }else if(i == sort - realitySort){
                                    upStar = realityStar;
                                }
                                payMoney += upStar*rankPO.getMoney();
                            }
//                            if(rankPO.getSort() == realitySort+i){
//                                payMoney += (rankPO.getStar()-star)*rankPO.getMoney();
//                            }
                        }
                    }
                    payMoney = payMoney*-1;
                }
                break;
            }
        }
        return payMoney;
    }

    /**
     * 代练基本资料
     * @param practiceId
     * @return
     * @throws Exception
     */
    public PracticeVO getPracticeInfo(Integer practiceId)throws Exception{
        PracticeVO practiceVO = new PracticeVO();
        Map searchCondition = new HashMap<>();
        searchCondition.put("id",practiceId);
        Map conditon=new HashMap();
        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo",null,"t1.nickName,t1.headImgUrl,t1.starSign,t1.interest,t1.label,t1.age,t1.openid,ct0.gradeName pgradeName ",null,null,searchCondition,
                new CommonChildBean("ddw_practice_grade","id","practiceGradeId",conditon));
        List list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && list.size()>0){
            PropertyUtils.copyProperties(practiceVO,list.get(0));
        }
        practiceVO.setUserId(practiceId);
        return practiceVO;
    }

    /**
     * 代练游戏简介
     * @param practiceId
     * @return
     * @throws Exception
     */
    public List<PracticeGameVO> getPracticeGameList(Integer practiceId)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceId);
        Map conditon=new HashMap();
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_game","t1.createTime desc","t1.gameId,t1.rankId,t1.appointment,ct0.gameName,ct1.rank ",null,null,searchCondition,
                new CommonChildBean("ddw_game","id","gameId",conditon),new CommonChildBean("ddw_rank","id","rankId",conditon));
        List<Map> list=this.getCommonMapper().selectObjects(csb);
        List<PracticeGameVO> PracticeGameList = new ArrayList<>();
        for(Map map:list){
            PracticeGameVO pg = new PracticeGameVO();
            PropertyUtils.copyProperties(pg,map);
            PracticeGameList.add(pg);
        }
        return PracticeGameList;
    }

    /**
     * 获取代练简历
     * @param practiceId
     * @param storeId
     * @return
     * @throws Exception
     */
    public List<ReviewPracticeVO> getReviewPracticeList(Integer practiceId,Integer storeId)throws Exception{
        List<Map> list = practiceMapper.getReviewPracticeList(practiceId,storeId);
        List<ReviewPracticeVO> reviewPracticeList = new ArrayList<>();
        for(Map map:list){
            ReviewPracticeVO rp = new ReviewPracticeVO();
            PropertyUtils.copyProperties(rp,map);
            reviewPracticeList.add(rp);
        }
        return reviewPracticeList;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO refund(Integer userId,Integer storeId,Integer orderId,String realName, String reason, String describe, MultipartFile pic)throws Exception{
        if(orderId == null){
            return new ResponseVO(-1,"订单号不能为空",null);
        }else{
            Map conditionMap = new HashMap<>();
            conditionMap.put("id",orderId);
            conditionMap.put("status",PracticeOrderStatusEnum.orderStatus6.getCode());
            PracticeOrderPO rePO = this.commonObjectBySearchCondition("ddw_practice_order",conditionMap,new PracticeOrderPO().getClass());
            if(rePO != null){
                return new ResponseVO(-2,"不允许重复提交申请",null);
            }
            //更新代练订单表状态
            Map setParams2 = new HashMap<>();
            setParams2.put("status", PracticeOrderStatusEnum.orderStatus6.getCode());
            Map searchCondition2 = new HashMap<>();
            searchCondition2.put("id",orderId);
            this.commonUpdateByParams("ddw_practice_order",setParams2,searchCondition2);

            //插入审批表
            ReviewPO reviewPO=new ReviewPO();
            String bussinessCode = String.valueOf(new Date().getTime());
            reviewPO.setDrBusinessCode(bussinessCode);
            reviewPO.setCreateTime(new Date());
            reviewPO.setDrProposerName(realName);
            reviewPO.setDrBusinessType(ReviewBusinessTypeEnum.ReviewBusinessType9.getCode());
            reviewPO.setDrReviewStatus(ReviewStatusEnum.ReviewStatus0.getCode());
            reviewPO.setDrProposerType(ReviewProposerTypeEnum.ReviewProposerType1.getCode());
            reviewPO.setDrReviewerType(ReviewReviewerTypeEnum.ReviewReviewerType1.getCode());
            reviewPO.setDrProposer(Integer.valueOf(userId));
            reviewPO.setDrApplyDesc("代练订单退款");
            reviewPO.setDrBelongToStoreId(storeId);
            reviewPO.setDrBusinessStatus(ReviewBusinessStatusEnum.practiceRefund9.getCode());
            ResponseVO responseVO = this.commonReviewService.submitAppl(reviewPO);
            //插入代练退款表
            if(responseVO.getReCode()>0){
                PracticeRefundPO practiceRefundPO = new PracticeRefundPO();
                practiceRefundPO.setUserId(userId);
                practiceRefundPO.setOrderId(orderId);
                practiceRefundPO.setDrBusinessCode(bussinessCode);
                practiceRefundPO.setReason(reason);
                practiceRefundPO.setDescribe(describe);
                practiceRefundPO.setStatus(0);
                practiceRefundPO.setCreateTime(new Date());
                practiceRefundPO.setUpdateTime(new Date());
                String imgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( pic.getOriginalFilename());
                FileInfoVo fileInfoVo= UploadFileMoveUtil.move( pic,mainGlobals.getRsDir(), imgName);

                practiceRefundPO.setPicUrl(mainGlobals.getServiceUrl()+fileInfoVo.getUrlPath());
                CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
                this.fileService.saveFile(f);

                Map updatePoMap= BeanToMapUtil.beanToMap(practiceRefundPO);
                responseVO =this.commonInsertMap("ddw_practice_refund",updatePoMap);

            }
            return responseVO;
        }
    }

    public PracticeRefundVO refundReason(Integer id)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("orderId",id);
        PracticeOrderPO practiceOrderPO = super.commonObjectBySearchCondition("ddw_practice_refund",searchCondition,PracticeOrderPO.class);
        PracticeRefundVO  practiceRefundVO = new PracticeRefundVO();
        PropertyUtils.copyProperties(practiceRefundVO,practiceOrderPO);
        return practiceRefundVO;
    }
}
