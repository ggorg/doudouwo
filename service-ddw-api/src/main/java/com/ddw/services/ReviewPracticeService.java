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
     * 先查询根据订单排序代练列表,列表不足由从未生成过订单的代练根据开启预约时间先后补上,列表不包含自己
     * @param token
     * @param page
     * @param appointment 1的时候查询发布代练,null的时候所有
     * @return
     * @throws Exception
     */
    public List<AppIndexPracticeVO> practiceList(String token,PageDTO page,Integer appointment)throws Exception{
        Integer storeId = TokenUtil.getStoreId(token);
        Integer practiceId = TokenUtil.getUserId(token);
        Integer pageNum = page.getPageNum();
        Integer pageSize = page.getPageSize();
        Integer start = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        Integer end = pageSize;
        List<Integer> userIdList = new ArrayList<>();
        //TODO 优先取发布的代练
        List<AppIndexPracticeVO>appIndexPractice = practiceMapper.getPracticeList(storeId,start,end,appointment);
        for(AppIndexPracticeVO appIndexPracticeVO:appIndexPractice){
            userIdList.add(appIndexPracticeVO.getUserId());
        }
        if(page.getPageSize()-appIndexPractice.size()>0){
            end = page.getPageSize()-appIndexPractice.size();
            List<AppIndexPracticeVO> appIndexPractice2 = practiceMapper.getPracticeListByNotInIds(userIdList,storeId,start,end,appointment);
            appIndexPractice.addAll(appIndexPractice2);
        }
        //设置关注状态
        ListIterator<AppIndexPracticeVO> appIndexPracticeIterator = appIndexPractice.listIterator();
        MyAttentionVO myAttentionVO = (MyAttentionVO)myAttentionService.queryPracticeByUserId(practiceId,1,9999).getData();
        List<UserInfoVO> myAttentionGoddessList = myAttentionVO.getUserInfoList();
        while (appIndexPracticeIterator.hasNext()){
            AppIndexPracticeVO appIndexPracticeVO = appIndexPracticeIterator.next();
            Long fans = myAttentionService.queryPracticeFansCountByUserId(appIndexPracticeVO.getUserId());
            appIndexPracticeVO.setFans(fans==null?0:fans);
            PracticeEvaluationPO practiceEvaluationPO = this.getEvaluation(appIndexPracticeVO.getUserId());
            int star = practiceEvaluationPO == null?0:practiceEvaluationPO.getStar();
            appIndexPracticeVO.setStar(star);
            if (myAttentionGoddessList != null) {
                for(UserInfoVO userInfoVO:myAttentionGoddessList){
                    if(userInfoVO.getId() == appIndexPracticeVO.getUserId()){
                        appIndexPracticeVO.setFollowed(true);
                        break;
                    }
                }
            }
        }
        return appIndexPractice;
    }

    /**
     * 当前门店代练列表,包含关注状态
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
//    public ResponseVO practiceList(String token, Integer pageNum, Integer pageSize)throws Exception{
//        JSONObject json = new JSONObject();
//        if(pageNum == null || pageSize == null){
//            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
//        }
//        Integer storeId = TokenUtil.getStoreId(token);
//        Integer practiceId = TokenUtil.getUserId(token);
//        Integer startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
//        Integer endRow = pageSize;
//        List<AppIndexPracticeVO>appIndexPracticeList = practiceMapper.getPracticeList(practiceId,storeId,startRow,endRow);
//        Integer count = practiceMapper.getPracticeListCount(practiceId,storeId);
//        MyAttentionVO myAttentionVO = (MyAttentionVO)myAttentionService.queryPracticeByUserId(practiceId,1,9999).getData();
//        List<UserInfoVO> myAttentionGoddessList = myAttentionVO.getUserInfoList();
//        ListIterator<AppIndexPracticeVO> appIndexPracticeIterator = appIndexPracticeList.listIterator();
//        while (appIndexPracticeIterator.hasNext()){
//            AppIndexPracticeVO appIndexPracticeVO = appIndexPracticeIterator.next();
//            if (myAttentionGoddessList != null) {
//                for(UserInfoVO userInfoVO:myAttentionGoddessList){
//                    if(userInfoVO.getId() == appIndexPracticeVO.getUserId()){
//                        appIndexPracticeVO.setFollowed(true);
//                        break;
//                    }
//                }
//            }
//        }
//        json.put("list",appIndexPracticeList);
//        json.put("count",count == null?0:count);
//        return new ResponseVO(1,"成功",json);
//    }

    /**
     * 当前门店代练列表,不判断关注状态
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
//    public ResponseVO practiceNoAttentionList(String token, Integer pageNum, Integer pageSize)throws Exception{
//        if(pageNum == null || pageSize == null){
//            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
//        }
//        Integer storeId = TokenUtil.getStoreId(token);
//        Integer pacticeId = TokenUtil.getUserId(token);
//        Integer startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
//        Integer endRow = pageSize;
//        List<AppIndexPracticeVO>AppIndexPracticeList = practiceMapper.getPracticeList(pacticeId,storeId,startRow,endRow);
//        return new ResponseVO(1,"成功",AppIndexPracticeList);
//    }

    /**
     * 插入评价
     * @param practiceEvaluationDetailDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO insertPracticeEvaluationDetail(PracticeEvaluationDetailDTO practiceEvaluationDetailDTO)throws Exception{
        PracticeEvaluationDetailPO practiceEvaluationDetailPO = new PracticeEvaluationDetailPO();
        PropertyUtils.copyProperties(practiceEvaluationDetailPO,practiceEvaluationDetailDTO);
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
    public ResponseVO insertPracticeEvaluation(PracticeEvaluationDetailDTO practiceEvaluationDetailDTO)throws Exception{
        PracticeEvaluationPO practiceEvaluationPO = super.commonObjectBySingleParam("ddw_practice_evaluation","practiceId",practiceEvaluationDetailDTO.getPracticeId(),PracticeEvaluationPO.class);
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
            searchCondition.put("practiceId",practiceEvaluationDetailDTO.getPracticeId());
            responseVO = super.commonUpdateByParams("ddw_practice_evaluation",params,searchCondition);
        }else{
            practiceEvaluationPO = new PracticeEvaluationPO();
            practiceEvaluationPO.setPracticeId(practiceEvaluationDetailDTO.getPracticeId());
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
     * @param practiceEvaluationDetailListDTO
     * @return
     * @throws Exception
     */
    public Page getPracticeEvaluationDetailList(PracticeEvaluationDetailListDTO practiceEvaluationDetailListDTO)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("practiceId",practiceEvaluationDetailListDTO.getPracticeId());
        condtion.put("gameId",practiceEvaluationDetailListDTO.getGameId());
        CommonChildBean cb=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_evaluation_detail","createTime desc","t1.*,ct0.nickName,ct0.headImgUrl",null,null,condtion,cb);
        return this.commonPage(practiceEvaluationDetailListDTO.getPage().getPageNum(),practiceEvaluationDetailListDTO.getPage().getPageSize(),csb);
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
        practiceOrderPO.setStatus(1);
        practiceOrderPO.setPayState(0);
        practiceOrderPO.setIncomeState(0);
        practiceOrderPO.setMoney(money);
        practiceOrderPO.setCreateTime(new Date());
        practiceOrderPO.setUpdateTime(new Date());
        CacheUtil.put("pay","practice-order-"+userId,practiceOrderPO);
        return super.commonInsert("ddw_practice_order",practiceOrderPO);
    }

    public List getPracticeDynamic(Integer practiceId)throws Exception{
        return practiceMapper.getPracticeDynamic(practiceId);
    }

    /**
     * 代练分页按时间倒序展示订单
     * @param practiceId
     * @param page
     * @return
     * @throws Exception
     */
    public ResponseVO getOrderPracticeList(Integer practiceId,PageDTO page)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("practiceId",practiceId);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonChildBean cb4=new CommonChildBean("ddw_rank","id","targetRankId",null);
        CommonChildBean cb5=new CommonChildBean("ddw_store","id","storeId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_order","updateTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct0.openid,ct1.gameName,ct2.rank,ct3.rank AS targetRank,ct4.dsName AS storeName",null,null,condtion,cb1,cb2,cb3,cb4,cb5);
        JSONObject json = new JSONObject();
        Page p = this.commonPage(page.getPageNum(),page.getPageSize(),csb);
        json.put("list",p.getResult());
        json.put("count",p.getTotal());
        return new ResponseVO(1,"成功",json);
    }

    /**
     * 会员分页按时间倒序展示订单
     * @param userId
     * @param page
     * @return
     * @throws Exception
     */
    public ResponseVO getOrderUserList(Integer userId,PageDTO page)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("userId",userId);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","practiceId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonChildBean cb4=new CommonChildBean("ddw_rank","id","targetRankId",null);
        CommonChildBean cb5=new CommonChildBean("ddw_store","id","storeId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_order","updateTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct0.openid,ct1.gameName,ct2.rank,ct3.rank AS targetRank,ct4.dsName AS storeName",null,null,condtion,cb1,cb2,cb3,cb4,cb5);
        JSONObject json = new JSONObject();
        Page p = this.commonPage(page.getPageNum(),page.getPageSize(),csb);
        json.put("list",p.getResult());
        json.put("count",p.getTotal());
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
        }else {
            condtion.put("userId,!=",userId);
        }
        condtion.put("appointment",1);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_game","createTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct1.gameName,ct2.rank",null,null,condtion,cb1,cb2,cb3);
        JSONObject json = new JSONObject();
        Page p = this.commonPage(practicePubTaskDTO.getPage().getPageNum(),practicePubTaskDTO.getPage().getPageSize(),csb);
        ListIterator<Map> listIterator = p.getResult().listIterator();
        while (listIterator.hasNext()){
            Map map = listIterator.next();
            PracticeEvaluationPO practiceEvaluationPO = this.getEvaluation((Integer) map.get("userId"));
            int star = practiceEvaluationPO==null?0:practiceEvaluationPO.getStar();
            map.put("star",star);
        }
        json.put("list",p.getResult());
        json.put("count",p.getTotal());
        return new ResponseVO(1,"成功",json);
    }

    /**
     * 订单结算
     * @param practiceSettlementDTO
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO<PracticeSettlementVO> settlement(PracticeSettlementDTO practiceSettlementDTO)throws Exception{
        // 查询订单信息,查询段位信息,根据段位和星计算金额,段位信息放缓存
        PracticeOrderPO practiceOrderPO = this.getOrder(practiceSettlementDTO.getOrderId());
        int payMoney = 0;//支付金额,单位分
        int gameId = practiceOrderPO.getGameId();
        int rankId = practiceOrderPO.getRankId();//原段位
        int star = practiceOrderPO.getStar();//原星
        int realityRankId = practiceSettlementDTO.getRealityRankId();//实际段位
        int realityStar = practiceSettlementDTO.getRealityStar();//实际星数
        payMoney = this.payMoney(gameId,rankId,star,realityRankId,realityStar);
        PropertyUtils.copyProperties(practiceOrderPO,practiceSettlementDTO);
        //订单状态，1开始接单，2完成,3未完成并结单
        practiceOrderPO.setStatus(payMoney>=practiceOrderPO.getMoney()?2:3);
        practiceOrderPO.setUpdateTime(new Date());
        practiceOrderPO.setRealityMoney(payMoney);
        if(practiceOrderPO.getPayState() != 1){
            practiceOrderPO.setPayState(2);
        }
        //计算收益
        this.incomeService.commonIncome(practiceOrderPO.getPracticeId(),payMoney, IncomeTypeEnum.IncomeType2, OrderTypeEnum.OrderType10,practiceOrderPO.getOrderNo());
        this.baseConsumeRankingListService.save(practiceOrderPO.getUserId(),practiceOrderPO.getPracticeId(),payMoney,IncomeTypeEnum.IncomeType2);
        //更新状态为已计算收益
        practiceOrderPO.setIncomeState(1);
        Map updatePoMap= BeanToMapUtil.beanToMap(practiceOrderPO);
        ResponseVO responseVO = super.commonUpdateBySingleSearchParam("ddw_practice_order",updatePoMap,"id",practiceSettlementDTO.getOrderId());
        //结算后,修改代练状态为关闭
        Map setParams = new HashMap<>();
        setParams.put("appointment",0);
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceOrderPO.getPracticeId());
        searchCondition.put("gameId",gameId);
        ResponseVO responseVO2 = super.commonUpdateByParams("ddw_practice_game",setParams,searchCondition);
        PracticeSettlementVO practiceSettlementVO = new PracticeSettlementVO();
        practiceSettlementVO.setPayMoney(payMoney);
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
     * 计算段位和星需要支付金额,负数则为降星双倍赔偿
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
        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo",null,"t1.nickName,t1.headImgUrl,ct0.gradeName pgradeName ",null,null,searchCondition,
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

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO refund(Integer userId,Integer orderId,String realName, String reason, String describe, MultipartFile pic)throws Exception{
        if(orderId == null){
            return new ResponseVO(-1,"订单号不能为空",null);
        }else{
            Map conditionMap = new HashMap<>();
            conditionMap.put("drProposer",userId);
            conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType9.getCode());
            //查询状态审核未通过
            conditionMap.put("drReviewStatus,!=",ReviewStatusEnum.ReviewStatus2.getCode());
            ReviewPO rePO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewPO().getClass());
            if(rePO != null){
                return new ResponseVO(-2,"不允许重复提交申请",null);
            }
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

}
