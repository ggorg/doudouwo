package com.ddw.token;

import com.alibaba.fastjson.JSONObject;
import com.ddw.beans.ResponseApiVO;
import com.gen.common.util.ThreadLocalUtil;
import com.gen.common.util.Tools;
import com.gen.common.util.TydicDES;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

@Configuration
public class TokenInterceptorConfig extends WebMvcConfigurerAdapter {
    private final Logger logger = Logger.getLogger(TokenInterceptorConfig.class);

    @Value("${tokenLiveHour:-1}")
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
                    String base64Token=null;
                    if(map!=null && map.containsKey("token")){
                        base64Token=map.get("token");
                    }else{
                        JSONObject jsonObj=(JSONObject)ThreadLocalUtil.get();
                        if(jsonObj!=null){
                            base64Token=jsonObj.getString("t");
                            ThreadLocalUtil.clear();
                        }
                    }
                    String name=method.getMethodAnnotation(Idemp.class).value();
                    String idempName=StringUtils.isBlank(name)?"idemp":name;
                    TokenUtil.putIdempotent(base64Token,idempName,null);
                }

            }

        }
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {

            if(handler instanceof HandlerMethod){
                HandlerMethod method = (HandlerMethod) handler;
                if(method.hasMethodAnnotation(Token.class)){
                    String cookenStr=Tools.getCookie("shopToken");
                    logger.info("shopToken:"+cookenStr);
                    String base64Token=null;
                    if(StringUtils.isBlank(cookenStr)){
                        Map<String,String> map=(Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                        if(map==null || !map.containsKey("token")){
                            toWriteResponseVo(response,-1000,"参数异常");
                            return false;
                        }
                        base64Token=map.get("token");

                    }else{
                        try {
                            JSONObject obj=JSONObject.parseObject(TydicDES.decodedecodeValue(URLDecoder.decode(cookenStr,"utf-8")));

                            base64Token=obj.getString("t");
                            ThreadLocalUtil.set(obj);
                        }catch (Exception e){
                            toWriteResponseVo(response,-1000,"参数异常");
                            return false;
                        }

                    }

                    //logger.info("base64Token:"+base64Token);
                   if(base64Token==null){
                       toWriteResponseVo(response,-1000,"token异常");
                       return false;
                   }
                    String baseToken=TokenUtil.getBaseToken(base64Token);
                    if(baseToken==null){
                        toWriteResponseVo(response,-1000,"token异常");
                        return false;
                    }
                  //  logger.info("baseToken:"+baseToken);
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
                        String name=method.getMethodAnnotation(Idemp.class).value();
                        String idempName=StringUtils.isBlank(name)?"idemp":name;
                        String idempStr=TokenUtil.getIdemp(base64Token,idempName);

                        if(StringUtils.isBlank(name)){
                            if(StringUtils.isBlank(idempStr)){
                                toWriteResponseVo(response,-20,"无效的操作");
                                return false;
                            }else if("do".equals(idempStr)){
                                TokenUtil.putIdempotent(base64Token,idempName,"doing");
                                return true;
                            }else if("doing".equals(idempStr)){
                                toWriteResponseVo(response,-21,"处理中，请耐性等待");
                                return false;
                            }
                        }else{
                            if(StringUtils.isBlank(idempStr)){
                                TokenUtil.putIdempotent(base64Token,idempName, DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
                                return true;
                            }else{
                                long currentSeconds=System.currentTimeMillis();
                                long idempTime=DateUtils.parseDate(idempStr,"yyyy-MM-dd HH:mm:ss").getTime();
                                long v=currentSeconds-idempTime;
                                if(v<30000){
                                    toWriteResponseVo(response,-21,"处理中，请耐性等待");
                                    return false;
                                }else{
                                    TokenUtil.putIdempotent(base64Token,idempName, DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
                                    return true;
                                }
                            }
                        }

                    }
                    return true;
                }

            }
            return true;
        }


    }

    public static void main(String[] args) throws Exception{


        //String str="tuGlDe2Q3hRjjuKup3vO%2FTvEHotAFt59B%2FJI2Jy6eZbpFLh4HPjkkcaQkS7VefayAR%2Fe0%2F8A0%2FuW%0D%0A0m%2BHk7f0Mkz5zHqIQeNphSbSgJ5p2ak%3D";
        String str="tuGlDe2Q3hRjjuKup3vO%252FTvEHotAFt59B%252FJI2Jy6eZbpFLh4HPjkkcaQkS7VefayAR%252Fe0%252F8A0%252FuW%250D%250A0m%252BHk7f0Mkz5zHqIQeNphSbSgJ5p2ak%253D";
        JSONObject obj=JSONObject.parseObject(TydicDES.decodedecodeValue(URLDecoder.decode(URLDecoder.decode(str,"utf-8"),"utf-8")));
        System.out.println(obj);

    }
}
