package com.ddw.services;

import com.ddw.beans.*;
import com.ddw.dao.PhotographMapper;
import com.ddw.enums.ReviewBusinessTypeEnum;
import com.ddw.enums.ReviewStatusEnum;
import com.ddw.util.IMApiUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.config.MainGlobals;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.CacheUtil;
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
 * 会员
 * Created by Jacky on 2018/4/12.
 */
@Service
public class UserInfoService extends CommonService {
    @Autowired
    private FileService fileService;
    @Autowired
    private MainGlobals mainGlobals;
    @Autowired
    private PhotographMapper photographMapper;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(UserInfoDTO userInfoDTO)throws Exception{
        UserInfoPO userInfoPO = new UserInfoPO();
        PropertyUtils.copyProperties(userInfoPO,userInfoDTO);
        userInfoPO.setId(null);
        userInfoPO.setGradeId(1);
        userInfoPO.setGoddessGradeId(1);
        userInfoPO.setPracticeGradeId(1);
        userInfoPO.setGoddessFlag(0);
        userInfoPO.setPracticeFlag(0);
        //TODO 生成邀请码
        userInfoPO.setInviteCode("");
        userInfoPO.setCreateTime(new Date());
        userInfoPO.setUpdateTime(new Date());
        ResponseVO re=this.commonInsert("ddw_userinfo",userInfoPO);
        if(re.getReCode()==1){
            boolean flag= IMApiUtil.importUser(userInfoPO,0);
            if(!flag){
                throw new GenException("IM导入账号openid"+userInfoPO.getOpenid()+"失败");
            }
        }
        return re;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(String openid,UserInfoUpdateDTO userInfoUpdateDTO)throws Exception{
        UserInfoPO userInfoPO = new UserInfoPO();
        UserInfoVO user = this.queryByOpenid(openid);
        PropertyUtils.copyProperties(userInfoPO,user);
        PropertyUtils.copyProperties(userInfoPO,userInfoUpdateDTO);
        userInfoPO.setUpdateTime(new Date());
        Map updatePoMap= BeanToMapUtil.beanToMap(userInfoPO);
        return this.commonUpdateBySingleSearchParam("ddw_userinfo",updatePoMap,"id",userInfoPO.getId());
    }

    public UserInfoVO query(String id)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("id",id);

        Map conditon=new HashMap();

        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo",null,"t1.*,ct0.gradeName ugradeName,ct0.level ulevel,ct1.gradeName ggradeName,ct1.level glevel,ct2.gradeName pgradeName,ct2.level plevel ",0,1,searchCondition,new CommonChildBean("ddw_grade","id","gradeId",conditon),
                new CommonChildBean("ddw_goddess_grade","id","goddessGradeId",conditon),new CommonChildBean("ddw_practice_grade","id","practiceGradeId",conditon));
        List list=this.getCommonMapper().selectObjects(csb);
        return this.setFlag(list);
    }

    public UserInfoVO queryByOpenid(String openid)throws Exception{
        Map searchCondition = new HashMap<>();
        searchCondition.put("openid",openid);

        Map condition=new HashMap();
        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo",null,"t1.id,t1.openid,t1.userName,t1.userPwd,t1.realName,t1.nickName," +
                "t1.headImgUrl,t1.phone,t1.label,t1.starSign,t1.signature,t1.province,t1.city,t1.area,t1.sex,t1.registerType,t1.idcard," +
                "t1.idcardFrontUrl,t1.idcardOppositeUrl,t1.inviteCode,t1.goddessFlag,t1.practiceFlag,t1.gradeId,t1.goddessGradeId,t1.practiceGradeId,t1.createTime," +
                "ct0.gradeName ugradeName,ct0.level ulevel,ct1.gradeName ggradeName,ct1.level glevel," +
                "ct2.gradeName pgradeName,ct2.level plevel ",0,1,searchCondition,new CommonChildBean("ddw_grade","id","gradeId",condition),
                new CommonChildBean("ddw_goddess_grade","id","goddessGradeId",condition),new CommonChildBean("ddw_practice_grade","id","practiceGradeId",condition));
        List list=this.getCommonMapper().selectObjects(csb);
        return this.setFlag(list);
    }

    public UserInfoVO setFlag(List list) throws Exception{
        if(list!=null && list.size()>0){
            UserInfoVO userInfoVO=new UserInfoVO();
            PropertyUtils.copyProperties(userInfoVO,list.get(0));
            //实名认证状态
            if(StringUtils.isBlank(userInfoVO.getIdcard())){
                //判断审核缓存是否存在
                if(CacheUtil.get("review","realname"+userInfoVO.getId()) == null){
                    Map condition1=new HashMap();
                    condition1.put("drProposer",userInfoVO.getId());
                    condition1.put("drBusinessType", ReviewBusinessTypeEnum.ReviewBusinessType4.getCode());
                    condition1.put("drReviewStatus",ReviewStatusEnum.ReviewStatus2.getCode());
                    List<Map> list1 = this.getCommonMapper().selectObjects(new CommonSearchBean("ddw_review",condition1));
                    if(list1.size()>0){
                        CacheUtil.put("review","realname"+userInfoVO.getId(),3);
                        userInfoVO.setRealnameFlag(3);
                    }else {
                        userInfoVO.setRealnameFlag(0);
                    }
                }else {
                    userInfoVO.setRealnameFlag((Integer) CacheUtil.get("review","realname"+userInfoVO.getId()));
                }
            }else{
                userInfoVO.setRealnameFlag(1);
            }
            //女神状态
            if(userInfoVO.getGoddessFlag() !=null && userInfoVO.getGoddessFlag() != 1){
                //判断审核缓存是否存在
                if(CacheUtil.get("review","goddess"+userInfoVO.getId()) == null){
                    Map condition1=new HashMap();
                    condition1.put("drProposer",userInfoVO.getId());
                    condition1.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType5.getCode());
                    condition1.put("drReviewStatus", ReviewStatusEnum.ReviewStatus2.getCode());
                    List<Map> list1 = this.getCommonMapper().selectObjects(new CommonSearchBean("ddw_review",condition1));
                    if(list1.size()>0){
                        CacheUtil.put("review","goddess"+userInfoVO.getId(),3);
                        userInfoVO.setGoddessFlag(3);
                    }else{
                        userInfoVO.setGoddessFlag(0);
                    }
                }else {
                    userInfoVO.setGoddessFlag((Integer) CacheUtil.get("review","goddess"+userInfoVO.getId()));
                }
            }
            //代练状态
            if(userInfoVO.getPracticeFlag() !=null && userInfoVO.getPracticeFlag() != 1){
                //判断审核缓存是否存在
                if(CacheUtil.get("review","practice"+userInfoVO.getId()) == null){
                    Map condition1=new HashMap();
                    condition1.put("drProposer",userInfoVO.getId());
                    condition1.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType6.getCode());
                    condition1.put("drReviewStatus",ReviewStatusEnum.ReviewStatus2.getCode());
                    List<Map> list1 = this.getCommonMapper().selectObjects(new CommonSearchBean("ddw_review",condition1));
                    if(list1.size()>0){
                        CacheUtil.put("review","practice"+userInfoVO.getId(),3);
                        userInfoVO.setPracticeFlag(3);
                    }else {
                        userInfoVO.setPracticeFlag(0);
                    }
                }else {
                    userInfoVO.setPracticeFlag((Integer) CacheUtil.get("review","practice"+userInfoVO.getId()));
                }
            }
            //直播状态
            //判断审核缓存是否存在
            if(CacheUtil.get("review","liveRadio"+userInfoVO.getId()) == null){
                Map condition1=new HashMap();
                condition1.put("drProposer",userInfoVO.getId());
                condition1.put("drBusinessType",ReviewBusinessTypeEnum.ReviewBusinessType3.getCode());
                condition1.put("drReviewStatus",ReviewStatusEnum.ReviewStatus2.getCode());
                List<Map> list1 = this.getCommonMapper().selectObjects(new CommonSearchBean("ddw_review",condition1));
                if(list1.size()>0){
                    CacheUtil.put("review","liveRadio"+userInfoVO.getId(),3);
                    userInfoVO.setLiveRadioFlag(3);
                }else{
                    userInfoVO.setLiveRadioFlag(0);
                }
            }else {
                userInfoVO.setLiveRadioFlag((Integer) CacheUtil.get("review","liveRadio"+userInfoVO.getId()));
            }
            return userInfoVO;
        }
        return null;
    }

    /**
     * 查询会员相册
     * @param id 会员id
     * @return
     * @throws Exception
     */
    public List<PhotographPO>queryPhotograph(String id)throws Exception{
        List<PhotographPO> photographList = new ArrayList<PhotographPO>();
        List<Map> list = this.commonObjectsBySingleParam("ddw_photograph","userId",id);
        for(Map map:list){
            PhotographPO photographPO = new PhotographPO();
            PropertyUtils.copyProperties(photographPO,map);
            photographList.add(photographPO);
        }
        return photographList;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO uploadPhotograph(String id,MultipartFile[]photograph)throws Exception{
        PhotographPO photographPO = new PhotographPO();
        int code = 1;
        HashSet<String> hs = new HashSet<String>();
        List<PhotographPO> list = new ArrayList<PhotographPO>();
        for(MultipartFile phto:photograph){
            String idcardFrontImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( phto.getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move( phto,mainGlobals.getRsDir(), idcardFrontImgName);

            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

            photographPO.setUserId(Integer.valueOf(id));
            photographPO.setImgUrl(mainGlobals.getServiceUrl() + fileInfoVo.getUrlPath());
            photographPO.setImgName(idcardFrontImgName);
            photographPO.setCreateTime(new Date());
            photographPO.setUpdateTime(new Date());
            ResponseVO responseVO = this.commonInsert("ddw_photograph",photographPO);
            if(responseVO.getReCode()<0){
                code = 0;
            }else{
                hs.add(idcardFrontImgName);
            }
        }
        if(!hs.isEmpty()){
            list = photographMapper.findListByNames(hs);
        }
        if(code == 1){
            return new ResponseVO(1,"上传成功",list);
        }else{
            return new ResponseVO(-1,"上传失败",list);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deletePhotograph(String photograph)throws Exception{
        HashSet<String> hs = new HashSet<String>();
        hs.add(photograph);
        List<PhotographPO> photographPOList = photographMapper.findListByIds(hs);
        //删除本地图片
        for(PhotographPO photographPO : photographPOList){
            UploadFileMoveUtil.delete(mainGlobals + photographPO.getImgName());
        }
        Map searchCondition = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        for(String photo:photograph.split(",")){
            sb = sb.append("'").append(photo).append("'").append(",");
        }
        if(sb.lastIndexOf(",")==sb.length()-1){
            sb = sb.deleteCharAt(sb.length()-1);
        }
        searchCondition.put("id ","in ("+sb.toString()+")");
        ResponseVO responseVO = this.commonDeleteByCombination("ddw_photograph",searchCondition);
        return responseVO;
    }
}
