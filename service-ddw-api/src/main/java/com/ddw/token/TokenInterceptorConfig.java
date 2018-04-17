package com.ddw.token;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.ResponseVO;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

@Configuration
public class TokenInterceptorConfig extends WebMvcConfigurerAdapter {

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
            out.print(JSONObject.toJSON(new ResponseVO(code,msg,null)));
            out.close();
            out.flush();
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
                   String baseToken=TokenUtil.getBaseToken(base64Token);
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

                    return true;
                }

            }
            return true;
        }


    }
}
