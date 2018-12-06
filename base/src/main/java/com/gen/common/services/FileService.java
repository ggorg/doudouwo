package com.gen.common.services;

import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.util.PicUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class FileService extends CommonService {
    private final Logger logger = Logger.getLogger(FileService.class);


    @Async
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveFile(CommonBeanFiles file){

        return this.commonInsert("base_files",file);
    }
    @Async
    public void mergeImages(Set<String> fileStr, String targetFilePath){
        try {
            PicUtil.merge(fileStr,targetFilePath);
        }catch (Exception e){
            logger.error("FileService->mergeImages",e);
        }

    }
    public CommonBeanFiles createCommonBeanFiles(FileInfoVo vo){
        CommonBeanFiles file=new CommonBeanFiles();
        file.setBaseFileName(vo.getBaseName());
        file.setFileName(vo.getRandomName());
        file.setCreateTime(new Date());
        file.setLocalPath(vo.getFileSrc());
        file.setUrlPath(vo.getUrlPath());
        file.setSuffixName(vo.getSuffixName());
        return file;
    }
    public CommonBeanFiles getFileByUrl(String url)throws Exception{
        return this.commonObjectBySingleParam("base_files","UrlPath",url,CommonBeanFiles.class);
    }
}
