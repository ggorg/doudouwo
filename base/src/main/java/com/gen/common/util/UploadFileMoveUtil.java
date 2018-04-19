package com.gen.common.util;

import com.gen.common.vo.FileInfoVo;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

public class UploadFileMoveUtil {
    private static final Logger logger = Logger.getLogger(UploadFileMoveUtil.class);
    public static FileInfoVo move(MultipartFile file,String rsDir,String fileName){
        FileOutputStream fos=null;
        try{
            FileInfoVo fileInfoVo=new FileInfoVo();
            File rsDirFile=new File(rsDir);
            if(!rsDirFile.exists()){
                rsDirFile.mkdirs();
            }
            File destFile=new File(rsDirFile, StringUtils.isNotBlank(fileName)?fileName:file.getName());
            if(FilenameUtils.getExtension(destFile.getName()).toLowerCase().matches("^(jpg|gif|png|jpeg)$")){
                Thumbnails.of(file.getInputStream()).scale(1).outputQuality(0.5f).toFile(destFile);
            }else{
                fos=new FileOutputStream(destFile);
                IOUtils.write(file.getBytes(),fos);
            }
            fileInfoVo.setBaseName(file.getOriginalFilename());
            fileInfoVo.setRandomName(destFile.getName());
            if(rsDir.matches("(.*)(/rs/.*)")){
                fileInfoVo.setUrlPath(rsDir.replaceAll("(.*)(/rs/.*)","$2")+destFile.getName());
            }
            fileInfoVo.setFileSize(file.getSize());
            fileInfoVo.setFileSrc(destFile.getPath());
          //  fileInfoVo.setUrlPath(+"/rs/"+destFile.getName());
            return fileInfoVo;
        }catch (Exception e){

            logger.error("UploadFileMoveUtil->move",e);
        }finally {
            if(fos!=null)IOUtils.closeQuietly(fos);
        }
        return null;

    }

}
