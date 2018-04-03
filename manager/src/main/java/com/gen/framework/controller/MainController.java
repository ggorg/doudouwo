package com.gen.framework.controller;



import com.gen.common.config.MainGlobals;
import com.gen.common.exception.GenException;
import com.gen.common.util.UploadFileMoveUtil;
import com.gen.common.vo.FileInfoVo;
import com.gen.common.vo.ResponseVO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;

@Controller
@RequestMapping("/")
public class MainController {
    private final Logger logger = Logger.getLogger(MainController.class);
    @Autowired
    private MainGlobals mainGlobals;


    @GetMapping("/404")
    public String to404(){
        return "pages/manager/common/404";
    }

    @PostMapping("/rs/do-upload")
    @ResponseBody
    public ResponseVO doUpload(MultipartFile file){

        FileInfoVo fileInfoVo= UploadFileMoveUtil.move(file,mainGlobals.getRsDir(), null);
        if(fileInfoVo!=null){
            return new ResponseVO(1,"上传成功",fileInfoVo);
        }else{
            return new ResponseVO(-2,"上传失败",null);
        }


    }

    @GetMapping("/rs/{filename:.+}")
    public void toInputImg(@PathVariable String filename,HttpServletResponse res){
        commonInputImg(filename,null,null,res);
    }
    @GetMapping("/rs/{twoLevel}/{filename:.+}")
    public void toInputTwoLevelImg(@PathVariable String filename,@PathVariable  String twoLevel,HttpServletResponse res){
        commonInputImg(filename,twoLevel,null,res);
    }
    @GetMapping("/rs/{twoLevel}/{threeLevel}/{filename:.+}")
    public void toInputTwoLevelImg(@PathVariable String filename,@PathVariable  String twoLevel,@PathVariable  String threeLevel,HttpServletResponse res){
        commonInputImg(filename,twoLevel,threeLevel,res);
    }
    private void commonInputImg(String filename,String twoLevel, String threeLevel,HttpServletResponse res){
        OutputStream os=null;
        byte[] bytes=null;
        try {
            res.setContentType("image/jpeg");

            File rsDir=new File(mainGlobals.getRsDir());
            if(!rsDir.exists()){
                throw new GenException(rsDir.getAbsolutePath()+"根目录不存在");
            }
            if(StringUtils.isNotBlank(twoLevel)){
                rsDir=new File(rsDir,twoLevel);
                if(!rsDir.exists()){
                    //rsDir.mkdirs();
                    throw new GenException(rsDir.getAbsolutePath()+"二级目录不存在");
                }
                if(StringUtils.isNotBlank(threeLevel)){
                    rsDir=new File(rsDir,threeLevel);
                    if(!rsDir.exists()){
                        //rsDir.mkdirs();
                        throw new GenException(rsDir.getAbsolutePath()+"三级目录不存在");
                    }
                }
            }

            File file=new File(rsDir,filename);
            if(file.exists()){

                os=res.getOutputStream();
                bytes=FileUtils.readFileToByteArray(file);
                if(bytes!=null){
                    IOUtils.write(bytes,os);
                }
            }
        }catch (Exception e){
            logger.error("MainController->toInputImg",e);
        }finally {
            IOUtils.closeQuietly(os);
            bytes = null;

        }
    }
}
