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
import com.gen.common.util.Page;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
    private PracticeMapper practiceMapper;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO apply(Integer id,Integer storeId,String userName, String gameId, String rankId, MultipartFile photograph1,MultipartFile photograph2,MultipartFile photograph3)throws Exception{
        if(StringUtils.isBlank(gameId) || StringUtils.isBlank(rankId) ||
                photograph1 == null || photograph2 == null ||photograph3 == null ||
                photograph1.isEmpty() || photograph2.isEmpty() ||photograph3.isEmpty()){
            return new ResponseApiVO(-1,"参数不正确",null);
        }else{
            //允许重复提交申请
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
     * 当前门店代练列表,包含关注状态
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public ResponseVO practiceList(String token, Integer pageNum, Integer pageSize)throws Exception{
        JSONObject json = new JSONObject();
        if(pageNum == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
        }
        Integer storeId = TokenUtil.getStoreId(token);
        Integer userId = TokenUtil.getUserId(token);
        Integer startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        Integer endRow = pageSize;
        List<AppIndexPracticeVO>AppIndexPracticeList = practiceMapper.getPracticeList(storeId,startRow,endRow);
        int count = practiceMapper.getPracticeListCount(storeId);
        MyAttentionVO myAttentionVO = (MyAttentionVO)myAttentionService.queryPracticeByUserId(userId,1,9999).getData();
        List<UserInfoVO> myAttentionGoddessList = myAttentionVO.getUserInfoList();
        ListIterator<AppIndexPracticeVO> appIndexPracticeIterator = AppIndexPracticeList.listIterator();
        while (appIndexPracticeIterator.hasNext()){
            AppIndexPracticeVO appIndexPracticeVO = appIndexPracticeIterator.next();
            if (myAttentionGoddessList != null) {
                for(UserInfoVO userInfoVO:myAttentionGoddessList){
                    if(userInfoVO.getId() == appIndexPracticeVO.getId()){
                        appIndexPracticeVO.setFollowed(true);
                        break;
                    }
                }
            }
        }
        json.put("list",AppIndexPracticeList);
        json.put("count",count);
        return new ResponseVO(1,"成功",json);
    }

    /**
     * 当前门店代练列表,不判断关注状态
     * @param token
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    public ResponseVO practiceNoAttentionList(String token, Integer pageNum, Integer pageSize)throws Exception{
        if(pageNum == null || pageSize == null){
            return new ResponseVO(-2,"提交失败,pageNum或pageSize格式不对",null);
        }
        Integer storeId = TokenUtil.getStoreId(token);
        Integer startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        Integer endRow = pageSize;
        List<AppIndexPracticeVO>AppIndexPracticeList = practiceMapper.getPracticeList(storeId,startRow,endRow);
        return new ResponseVO(1,"成功",AppIndexPracticeList);
    }

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
        Map searchCondition = new HashMap<>();
        searchCondition.put("userId",practiceId);
        searchCondition.put("gameId",gameId);
        return super.commonUpdateByParams("ddw_practice_game",setParams,searchCondition);
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
    public ResponseVO insertPracticeOrder(int userId,PracticeGameApplyDTO practiceGameApplyDTO)throws Exception{
        PracticeOrderPO practiceOrderPO = new PracticeOrderPO();
        PropertyUtils.copyProperties(practiceOrderPO,practiceGameApplyDTO);
        practiceOrderPO.setUserId(userId);
        //订单状态，1开始接单，2完成,3未完成并结单
        practiceOrderPO.setStatus(1);
        practiceOrderPO.setCreateTime(new Date());
        practiceOrderPO.setUpdateTime(new Date());
        return super.commonInsert("ddw_practice_order",practiceOrderPO);
    }

    /**
     * 代练分页按时间倒序展示订单
     * @param practiceEvaluationDetailListDTO
     * @return
     * @throws Exception
     */
    public Page getOrderPracticeList(PracticeEvaluationDetailListDTO practiceEvaluationDetailListDTO)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("practiceId",practiceEvaluationDetailListDTO.getPracticeId());
        condtion.put("gameId",practiceEvaluationDetailListDTO.getGameId());
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_order","updateTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct1.gameName,ct2.rank",null,null,condtion,cb1,cb2,cb3);
        return this.commonPage(practiceEvaluationDetailListDTO.getPage().getPageNum(),practiceEvaluationDetailListDTO.getPage().getPageSize(),csb);
    }

    /**
     * 会员分页按时间倒序展示订单
     * @param userId
     * @param page
     * @return
     * @throws Exception
     */
    public Page getOrderUserList(Integer userId,PageDTO page)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("userId",userId);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_order","updateTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct1.gameName,ct2.rank",null,null,condtion,cb1,cb2,cb3);
        return this.commonPage(page.getPageNum(),page.getPageSize(),csb);
    }

    /**
     * 代练已发布代练任务
     * @param userId 代练编号
     * @param page
     * @return
     * @throws Exception
     */
    public Page getPubTaskList(Integer userId,PageDTO page)throws Exception{
        Map condtion = new HashMap<>();
        condtion.put("userId",userId);
        condtion.put("appointment,!=",0);
        CommonChildBean cb1=new CommonChildBean("ddw_userinfo","id","userId",null);
        CommonChildBean cb2=new CommonChildBean("ddw_game","id","gameId",null);
        CommonChildBean cb3=new CommonChildBean("ddw_rank","id","rankId",null);
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_game","createTime desc","t1.*,ct0.nickName,ct0.headImgUrl,ct1.gameName,ct2.rank",null,null,condtion,cb1,cb2,cb3);
        return this.commonPage(page.getPageNum(),page.getPageSize(),csb);
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
        practiceOrderPO.setStatus(payMoney>=0?2:3);
        practiceOrderPO.setUpdateTime(new Date());
        practiceOrderPO.setMoney(payMoney);
        Map updatePoMap= BeanToMapUtil.beanToMap(practiceOrderPO);
        ResponseVO responseVO = super.commonUpdateBySingleSearchParam("ddw_practice_order",updatePoMap,"id",practiceSettlementDTO.getOrderId());
        ResponseApiVO responseApiVO = new ResponseApiVO();
        PropertyUtils.copyProperties(responseApiVO,responseVO);
        PracticeSettlementVO practiceSettlementVO = new PracticeSettlementVO();
        practiceSettlementVO.setPayMoney(payMoney);
        responseApiVO.setData(practiceSettlementVO);
        return responseApiVO;
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
                        realitySort = rankPO.getSort();//段位序号
                        money = rankPO.getMoney();//星单价
                    }
                }
                if(realitySort > sort){
                    for (RankPO rankPO:rankPOList){
                        for(int i=0;i<realitySort-sort;i++){
                            if(rankPO.getSort() == sort+i){
                                int upStar = 0;
                                if(i == 0){
                                    upStar = rankPO.getStar()-star;
                                }else if(i != 0){
                                    upStar = rankPO.getStar();
                                }else if(i == realitySort-sort){
                                    upStar = realityStar;
                                }
                                payMoney += upStar*rankPO.getMoney();
                            }
                        }
                    }
                }else if(realitySort == sort){//同等段位
                    if(realityStar > star){
                        payMoney = (realityStar - star) * money;
                    }else if(realityStar < star){//降星,双倍赔偿
                        payMoney = (realityStar - star) * money*2;
                    }
                }else{//降段降星
                    for (RankPO rankPO:rankPOList){
                        for(int i=0;i<sort-realitySort;i++){
                            if(rankPO.getSort() == realitySort+i){
                                payMoney += (rankPO.getStar()-star)*rankPO.getMoney()*2;
                            }
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
        CommonSearchBean csb=new CommonSearchBean("ddw_practice_game",null,"t1.gameId,t1.rankId,t1.appointment,ct0.gameName,ct1.rank ",null,null,searchCondition,
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

}
