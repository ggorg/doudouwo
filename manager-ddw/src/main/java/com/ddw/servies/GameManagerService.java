package com.ddw.servies;

import com.ddw.beans.GameDTO;
import com.ddw.beans.GamePO;
import com.gen.common.beans.CommonBeanFiles;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacky on 2018/5/16.
 */
@Service
@Transactional(readOnly = true)
public class GameManagerService extends CommonService {
    @Autowired
    private MainGlobals mainGlobals;
    @Autowired
    private FileService fileService;

    public Page findPage(Integer pageNo)throws Exception{
        return super.commonPage("ddw_game","createTime desc",pageNo,10,new HashMap());
    }

    public List<GamePO> findList()throws Exception{
        return super.commonObjectsBySearchCondition("ddw_game",new HashMap());
    }

    public GamePO selectById(String id){
        try {
            return super.commonObjectBySingleParam("ddw_game","id",id,GamePO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveOrUpdate(GameDTO gameDTO)throws Exception{
        String picUrl = "";
        if(gameDTO.getFileImgShow() != null && !gameDTO.getFileImgShow().isEmpty()){
            MultipartFile photograph1 = gameDTO.getFileImgShow();
            String ImgName1= DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS")+"."+ FilenameUtils.getExtension( photograph1.getOriginalFilename());
            FileInfoVo fileInfoVo1= UploadFileMoveUtil.move( photograph1,mainGlobals.getRsDir(), ImgName1);
            picUrl = mainGlobals.getServiceUrl()+fileInfoVo1.getUrlPath();
            CommonBeanFiles f1=this.fileService.createCommonBeanFiles(fileInfoVo1);
            this.fileService.saveFile(f1);
        }
        if(gameDTO.getId() > 0 ){
            GamePO gamePO = new GamePO();
            PropertyUtils.copyProperties(gamePO,gameDTO);
            Map updatePoMap= BeanToMapUtil.beanToMap(gamePO);
            if(!StringUtils.isBlank(picUrl)){
                updatePoMap.replace("`picUrl`",picUrl);
            }
            updatePoMap.replace("`createTime`",new Date());
            return super.commonUpdateBySingleSearchParam("ddw_game",updatePoMap,"id",gameDTO.getId());
        }else{
            GamePO gamePO = new GamePO();
            PropertyUtils.copyProperties(gamePO,gameDTO);
            if(!StringUtils.isBlank(picUrl)){
                gamePO.setPicUrl(picUrl);
            }
            gamePO.setCreateTime(new Date());
            return super.commonInsert("ddw_game",gamePO);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO delete(String id){
        ResponseVO vo=new ResponseVO();
        int n = super.commonDelete("ddw_game","id",id);
        if(n>0){
            vo.setReCode(1);
            vo.setReMsg("删除成功");
        }else{
            vo.setReCode(-2);
            vo.setReMsg("删除失败");
        }
        return vo;
    }
}
