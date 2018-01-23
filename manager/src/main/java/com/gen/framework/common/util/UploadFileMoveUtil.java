package com.gen.framework.common.util;

import com.gen.framework.common.controller.MainController;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

public class UploadFileMoveUtil {
    private static final Logger logger = LoggerFactory.getLogger(UploadFileMoveUtil.class);
    public static boolean move(MultipartFile file,String rsDir,String fileName){
        FileOutputStream fos=null;
        try{
            File rsDirFile=new File(rsDir);
            if(!rsDirFile.exists()){
                rsDirFile.mkdirs();
            }
            File destFile=new File(rsDirFile, StringUtils.isNotBlank(fileName)?fileName:file.getOriginalFilename());
            fos=new FileOutputStream(destFile);
            IOUtils.write(file.getBytes(),fos);
            return true;
        }catch (Exception e){
            logger.error("UploadFileMoveUtil->move",e);
        }finally {
            if(fos!=null)IOUtils.closeQuietly(fos);
        }
        return false;

    }
}
