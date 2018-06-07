package com.ddw.token;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.ResponseApiVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Configuration
public class TokenInterceptorConfig extends WebMvcConfigurerAdapter {
    private final Logger logger = Logger.getLogger(TokenInterceptorConfig.class);

    @Value("${tokenLiveHour}")
    private Integer tokenLiveHour;
    public void addInterceptors(InterceptorRegistry registry) {
        //System.out.println(	123);
        registry.addInterceptor(new TokenInterceptor());

    }
    class TokenInterceptor extends HandlerInterceptorAdapter {

        public void toWriteResponseVo(HttpServletResponse response,Integer code,String msg)throws Exception{
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.print(JSONObject.toJSON(new ResponseApiVO(code,msg,null)));
            out.close();
            out.flush();
        }
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            if(handler instanceof HandlerMethod){
                HandlerMethod method = (HandlerMethod) handler;
                if(method.hasMethodAnnotation(Token.class) && method.hasMethodAnnotation(Idemp.class)){
                    Map<String,String> map=(Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                    String base64Token=map.get("token");
                    TokenUtil.putIdempotent(base64Token,"");
                }
            }

        }
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {

            if(handler instanceof HandlerMethod){
                HandlerMethod method = (HandlerMethod) handler;
                if(method.hasMethodAnnotation(Token.class)){
                   Map<String,String> map=(Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

                   if(map==null || !map.containsKey("token")){
                       toWriteResponseVo(response,-1000,"参数异常");
                       return false;
                   }

                   String base64Token=map.get("token");
                    logger.info("base64Token:"+base64Token);
                   if(base64Token==null){
                       toWriteResponseVo(response,-1000,"token异常");
                       return false;
                   }
                    String baseToken=TokenUtil.getBaseToken(base64Token);
                    if(baseToken==null){
                        toWriteResponseVo(response,-1000,"token异常");
                        return false;
                    }
                    logger.info("baseToken:"+baseToken);
                    if(!TokenUtil.validToken(baseToken)){
                        toWriteResponseVo(response,-1000,"token异常");
                        return false;
                    }
                    if(tokenLiveHour>-1){
                       if(TokenUtil.isOverTime(baseToken,tokenLiveHour)){
                           toWriteResponseVo(response,-1000,"token超时");
                           return false;
                       }
                    }
                    if(!TokenUtil.hasToken(base64Token)){
                        toWriteResponseVo(response,-1000,"token失效");
                        return false;
                    }
                    if(method.hasMethodAnnotation(Idemp.class)){
                        String idempStr=TokenUtil.getIdemp(base64Token);
                        if(StringUtils.isBlank(idempStr)){
                            toWriteResponseVo(response,-20,"无效的操作");
                            return false;
                        }else if("do".equals(idempStr)){
                            TokenUtil.putIdempotent(base64Token,"doing");
                            return true;
                        }else if("doing".equals(idempStr)){
                            toWriteResponseVo(response,-21,"处理中，请耐性等待");
                            return false;
                        }
                    }
                    return true;
                }

            }
            return true;
        }


    }
}
