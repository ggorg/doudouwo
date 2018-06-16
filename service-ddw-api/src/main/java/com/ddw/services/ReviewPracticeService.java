package com.ddw.services;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.*;
import com.ddw.beans.vo.AppIndexPracticeVO;
import com.ddw.dao.PracticeMapper;
import com.ddw.dao.UserInfoMapper;
import com.ddw.enums.*;
import com.ddw.token.TokenUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.config.MainGlobals;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
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
    private UserInfoMapper userInfoMapper;
    @Autowired
    private PracticeMapper practiceMapper;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseApiVO apply(Integer id,String userName, String gameId, String rankId, MultipartFile photograph1,MultipartFile photograph2,MultipartFile photograph3)throws Exception{
        if(StringUtils.isBlank(gameId) || StringUtils.isBlank(rankId) ||
                photograph1 == null || photograph2 == null ||photograph3 == null ||
                photograph1.isEmpty() || photograph2.isEmpty() ||photograph3.isEmpty()){
            return new ResponseApiVO(-1,"参数不正确",null);
        }else{
            Map conditionMap = new HashMap<>();
            conditionMap.put("drProposer",id);
            conditionMap.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
            //查询状态审核未通过
            conditionMap.put("drReviewStatus,!=",2);
            ReviewRealNamePO realPO = this.commonObjectBySearchCondition("ddw_review",conditionMap,new ReviewRealNamePO().getClass());
            if(realPO != null){
                return new ResponseApiVO(-2,"不允许重复提交申请",null);
            }
            //更新会员代练状态为审核中
            Map setConditionMap = new HashMap<>();
            setConditionMap.put("practiceFlag",2);
            this.commonUpdateBySingleSearchParam("ddw_userinfo",setConditionMap,"id",id);
            //插入审批表
            ReviewPO reviewPO=new ReviewPO();
            String bussinessCode = String.valueOf(new Date().getTime());
            reviewPO.setDrBusinessCode(bussinessCode);
            reviewPO.setCreateTime(new Date());
            reviewPO.setDrProposerName(userName);
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

                String ImgName2= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph1.getOriginalFilename());
                FileInfoVo fileInfoVo2= UploadFileMoveUtil.move( photograph1,mainGlobals.getRsDir(), ImgName1);
                reviewPracticePO.setPicUrl2(mainGlobals.getServiceUrl()+fileInfoVo2.getUrlPath());
                CommonBeanFiles f2=this.fileService.createCommonBeanFiles(fileInfoVo2);
                this.fileService.saveFile(f2);

                String ImgName3= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph1.getOriginalFilename());
                FileInfoVo fileInfoVo3= UploadFileMoveUtil.move( photograph1,mainGlobals.getRsDir(), ImgName3);
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
        MyAttentionVO myAttentionVO = myAttentionService.queryPracticeByUserId(userId);
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

}
