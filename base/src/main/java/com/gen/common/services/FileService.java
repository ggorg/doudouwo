package com.gen.common.services;

import com.gen.common.beans.CommonBeanFiles;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;

@Service
public class FileService extends CommonService {


    @Async
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public ResponseVO saveFile(CommonBeanFiles file){

        return this.commonInsert("base_files",file);
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
}
