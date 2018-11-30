package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewReviewerTypeEnum;
import com.gen.common.services.CommonService;
import com.gen.common.util.Page;
import com.gen.common.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成为代练审批
 */
@Service
@Transactional(readOnly = true)
public class ReviewPracticeService extends CommonService {

    public Page findPage(Integer pageNo,Map condtion)throws Exception{
        return this.commonPage("ddw_review","createTime desc",pageNo,10,condtion);
    }

    public Page findPracticePageByHq(Integer pageNo)throws Exception{
        Map condtion=new HashMap();
        condtion.put("drReviewerType",ReviewReviewerTypeEnum.ReviewReviewerType0.getCode());
        condtion.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
        return this.findPage(pageNo,condtion);
    }

    public ReviewPO getReviewById(int id)throws Exception{
        return this.commonObjectBySingleParam("ddw_review","id",id,ReviewPO.class);
    }

    public ReviewPO getReviewByCode(String drBusinessCode)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("drBusinessCode",drBusinessCode);
        return this.commonObjectBySingleParam("ddw_review","drBusinessCode",drBusinessCode,ReviewPO.class);

    }

    public ReviewPracticePO getReviewPracticeByCode(String drBusinessCode)throws Exception{
        return this.commonObjectBySingleParam("ddw_review_practice","drBusinessCode",drBusinessCode,ReviewPracticePO.class);

    }

    public PracticePO getPractice(Integer practiceId)throws Exception{
        Map conditoin=new HashMap();
        conditoin.put("userId",practiceId);
        return this.commonObjectBySearchCondition("ddw_practice",conditoin,PracticePO.class);
    }

    public ResponseVO updatePractice(Integer practiceId,Integer storeId)throws Exception{
        Map setParams = new HashMap<>();
        setParams.put("storeId",storeId);
        return this.commonUpdateBySingleSearchParam("ddw_practice",setParams,"userId",practiceId);
    }

    /**
     * 审核后回调更新会员资料
     * @param rb
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO updateReviewPractice(ReviewCallBackBean rb)throws Exception{
        //根据审核结果处理
        ReviewPO reviewPO = this.getReviewByCode(rb.getBusinessCode());
        if(rb.getReviewPO().getDrReviewStatus() == 1) {
            Map setParams = new HashMap();
            setParams.put("practiceGradeId", 1);
            setParams.put("practiceFlag", 1);
            Map searchCondition = new HashMap();
            searchCondition.put("id", reviewPO.getDrProposer());
            //判断是否存在代练表,不存在则插入代练表,已存在判断门店是否相同,不相同,更新代练门店,相同不做任何操作
            PracticePO practicePO = this.getPractice(reviewPO.getDrProposer());
            if (practicePO == null) {
                practicePO = new PracticePO();
                practicePO.setUserId(reviewPO.getDrProposer());
                practicePO.setStoreId(reviewPO.getDrBelongToStoreId());
                practicePO.setCreateTime(new Date());
                practicePO.setUpdateTime(new Date());
                this.commonInsert("ddw_practice", practicePO);
            } else if (practicePO.getStoreId() != reviewPO.getDrBelongToStoreId()) {
                this.updatePractice(reviewPO.getDrProposer(), reviewPO.getDrBelongToStoreId());
            }
            //插入代练与游戏关联表
            ReviewPracticePO reviewPracticePO = this.getReviewPracticeByCode(rb.getBusinessCode());
            //判断是否已存在同游戏,是则更新代练段位等级
            Map searchCondition2 = new HashMap();
            searchCondition2.put("userId", reviewPO.getDrProposer());
            searchCondition2.put("gameId", reviewPracticePO.getGameId());
            PracticeGamePO practiceGamePO = this.commonObjectBySearchCondition("ddw_practice_game", searchCondition2, PracticeGamePO.class);
            if (practiceGamePO == null) {
                PracticeGamePO pg = new PracticeGamePO();
                pg.setCreateTime(new Date());
                pg.setUserId(reviewPO.getDrProposer());
                pg.setGameId(reviewPracticePO.getGameId());
                pg.setRankId(reviewPracticePO.getRankId());
                pg.setAppointment(0);
                this.commonInsert("ddw_practice_game", pg);
            } else {
                Map setParams2 = new HashMap<>();
                setParams2.put("rankId", reviewPracticePO.getRankId());
                this.commonUpdateByParams("ddw_practice_game", setParams2, searchCondition2);
            }
            //更新代练审核表
            Map searchCondition3 = new HashMap();
            searchCondition3.put("drBusinessCode", rb.getBusinessCode());
            Map setParams3 = new HashMap<>();
            setParams3.put("status",1 );//审核状态，0未审核，1审核通过，2审核拒绝
            setParams3.put("`describe`",reviewPO.getDrReviewDesc() );//审核说明
            this.commonUpdateByParams("ddw_review_practice", setParams3, searchCondition3);
            return this.commonUpdateByParams("ddw_userinfo", setParams, searchCondition);
        }else{
            Map searchCondition = new HashMap();
            searchCondition.put("drBusinessCode", rb.getBusinessCode());
            Map setParams = new HashMap<>();
            setParams.put("status",2 );//审核状态，0未审核，1审核通过，2审核拒绝
            setParams.put("`describe`",reviewPO.getDrReviewDesc() );//审核说明
            this.commonUpdateByParams("ddw_review_practice", setParams, searchCondition);
        }
        return new ResponseVO(1,"成功",null);
    }

    public List<Map> gameList(){
        return this.commonList("ddw_game",null,1,9999,new HashMap<>());
    }
    public List<Map> rankList(){
        return this.commonList("ddw_rank",null,1,9999,new HashMap<>());
    }
}
