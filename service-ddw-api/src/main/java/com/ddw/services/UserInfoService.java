package com.ddw.services;

import com.ddw.beans.PhotographPO;
import com.ddw.beans.UserInfoDTO;
import com.ddw.beans.UserInfoPO;
import com.ddw.beans.UserInfoUpdateDTO;
import com.ddw.util.IMApiUtil;
import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.beans.CommonChildBean;
import com.gen.common.beans.CommonSearchBean;
import com.gen.common.config.MainGlobals;
import com.gen.common.exception.GenException;
import com.gen.common.services.CommonService;
import com.gen.common.services.FileService;
import com.gen.common.util.BeanToMapUtil;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
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

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO save(UserInfoDTO userInfoDTO)throws Exception{
        UserInfoPO userInfoPO = new UserInfoPO();
        PropertyUtils.copyProperties(userInfoPO,userInfoDTO);
        userInfoPO.setId(null);
        userInfoPO.setGradeId(1);
        //TODO 生成邀请码
        userInfoPO.setInviteCode("");
        userInfoPO.setCreateTime(new Date());
        userInfoPO.setUpdateTime(new Date());
        ResponseVO re=this.commonInsert("ddw_userinfo",userInfoPO);
        if(re.getReCode()==1){
            boolean flag=IMApiUtil.importUser(userInfoPO,0);
            if(!flag){
                throw new GenException("IM导入账号openid"+userInfoPO.getOpenid()+"失败");
            }
        }
        return re;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO update(String openid,UserInfoUpdateDTO userInfoUpdateDTO)throws Exception{
        UserInfoPO userInfoPO = new UserInfoPO();
        UserInfoPO user = this.queryByOpenid(openid);
        PropertyUtils.copyProperties(userInfoPO,user);
        PropertyUtils.copyProperties(userInfoPO,userInfoUpdateDTO);
        userInfoPO.setUpdateTime(new Date());
        Map updatePoMap= BeanToMapUtil.beanToMap(userInfoPO);
        return this.commonUpdateBySingleSearchParam("ddw_userinfo",updatePoMap,"id",userInfoPO.getId());
    }

    public UserInfoPO query(String id)throws Exception{
//        return this.commonObjectBySingleParam("ddw_userinfo","id",id,new UserInfoPO().getClass());
        Map searchCondition = new HashMap<>();
        searchCondition.put("id",id);

        Map conditon=new HashMap();

        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo",null,"t1.*,ct0.gradeName ugradeName,ct0.level ulevel,ct1.gradeName ggradeName,ct1.level glevel,ct2.gradeName pgradeName,ct2.level plevel ",0,1,searchCondition,new CommonChildBean("ddw_grade","id","gradeId",conditon),
                new CommonChildBean("ddw_goddess_grade","id","goddessGradeId",conditon),new CommonChildBean("ddw_practice_grade","id","practiceGradeId",conditon));
        List list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && list.size()>0){
            UserInfoPO userInfoPO=new UserInfoPO();
            PropertyUtils.copyProperties(userInfoPO,list.get(0));
            return userInfoPO;
        }
        return null;
    }

    public UserInfoPO queryByOpenid(String openid)throws Exception{
//        Map searchCondition = new HashMap<>();
//        searchCondition.put("openid",openid);
//        return this.commonObjectBySearchCondition("ddw_userinfo",searchCondition,new UserInfoPO().getClass());
        Map searchCondition = new HashMap<>();
        searchCondition.put("openid",openid);

        Map conditon=new HashMap();

        CommonSearchBean csb=new CommonSearchBean("ddw_userinfo",null,"t1.*,ct0.gradeName ugradeName,ct0.level ulevel,ct1.gradeName ggradeName,ct1.level glevel,ct2.gradeName pgradeName,ct2.level plevel ",0,1,searchCondition,new CommonChildBean("ddw_grade","id","gradeId",conditon),
                new CommonChildBean("ddw_goddess_grade","id","goddessGradeId",conditon),new CommonChildBean("ddw_practice_grade","id","practiceGradeId",conditon));
        List list=this.getCommonMapper().selectObjects(csb);
        if(list!=null && list.size()>0){
            UserInfoPO userInfoPO=new UserInfoPO();
            PropertyUtils.copyProperties(userInfoPO,list.get(0));
            return userInfoPO;
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
        PhotographPO photographPO = new PhotographPO();
        for(Map map:list){
            PropertyUtils.copyProperties(photographPO,map);
            photographList.add(photographPO);
        }
        return photographList;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO uploadPhotograph(String id,MultipartFile[]photograph)throws Exception{
        PhotographPO photographPO = new PhotographPO();
        int code = 1;
        for(MultipartFile phto:photograph){
            String idcardFrontImgName= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( phto.getOriginalFilename());
            FileInfoVo fileInfoVo= UploadFileMoveUtil.move( phto,mainGlobals.getRsDir(), idcardFrontImgName);

            CommonBeanFiles f=this.fileService.createCommonBeanFiles(fileInfoVo);
            this.fileService.saveFile(f);

            photographPO.setUserId(Integer.valueOf(id));
            photographPO.setImgUrl(fileInfoVo.getUrlPath());
            photographPO.setImgName(idcardFrontImgName);
            photographPO.setCreateTime(new Date());
            photographPO.setUpdateTime(new Date());
            ResponseVO responseVO = this.commonInsert("ddw_photograph",photographPO);
            if(responseVO.getReCode()<0){
                code = 0;
            }
        }
        if(code == 1){
            return new ResponseVO(1,"上传成功",null);
        }else{
            return new ResponseVO(-1,"上传失败",null);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO deletePhotograph(String photograph)throws Exception{
        Map searchCondition = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        for(String photo:photograph.split(",")){
            sb = sb.append("'").append(photo).append("'").append(",");
        }
        if(sb.lastIndexOf(",")==sb.length()-1){
            sb = sb.deleteCharAt(sb.length()-1);
        }
        searchCondition.put("id ","in ("+sb.toString()+")");
        return this.commonDeleteByCombination("ddw_photograph",searchCondition);
    }
}
