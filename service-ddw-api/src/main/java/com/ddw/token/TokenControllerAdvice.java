package com.ddw.token;

import com.ddw.beans.RequestDTO;
import com.ddw.beans.ResponseVO;
import com.gen.common.exception.GenException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//@ControllerAdvice(annotations = {Token.class})
public class TokenControllerAdvice {

    @ModelAttribute
    public String validToken( @PathVariable String token)throws GenException{

       /* if(args!=null && args.getToken()!=null){
          //  RequestDTO requestDTO=entity.getBody();
          ////  if(StringUtils.isBlank(requestDTO.getToken())){
          //      throw new GenException("token异常");
         //   }
         //   return requestDTO;
        }
        return args;*/
        System.out.println(token+"-----");
       return token;
    }
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseVO toException(Exception e){
        if(e instanceof GenException){
            System.out.println("----------haha");
            new ResponseVO(-1,e.getMessage(),null);
        }
        return new ResponseVO(-1,"系统异常",null);
    }
}
