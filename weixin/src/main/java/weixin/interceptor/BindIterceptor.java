package weixin.interceptor;

import com.gen.framework.common.exception.GenException;
import com.gen.framework.common.util.MyEncryptUtil;
import com.gen.framework.common.util.Tools;
import com.ty.config.Globals;
import com.ty.services.TyBindService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component

public class BindIterceptor extends HandlerInterceptorAdapter {
    private final Logger logger = LoggerFactory.getLogger(BindIterceptor.class);
    @Value("${gen.framework.wap.bind.filter.urls}")
    private String filterUrls;
   // private List filterList;

    @Value("${gen.framework.wap.url.prefix}")
    private String wapPrefixUrls;
   // private List managerPrefixUrlsList;
    @Autowired
    private TyBindService tyBindService;

    @Autowired
    private Globals globals;



    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception{
        String path=request.getRequestURI();
        String prefixPath=path.replaceAll("^(/[^/]+).*$","$1");
        if(filterUrls.indexOf("*")>-1 && filterUrls.indexOf(".")==-1){
            filterUrls=filterUrls.replaceAll("[*]",".*");
        }
        String openid=Tools.setOpenidByThreadLocal(StringUtils.isBlank(globals.getTestOpenid())?request.getParameter("token"): MyEncryptUtil.encry(globals.getTestOpenid()));
        if(prefixPath.matches("^("+wapPrefixUrls+")$") && !path.equals("/wap/to-error")){
            if(StringUtils.isBlank(openid)){
                response.sendRedirect("/wap/to-error");
                return false;
            }
            if(!path.matches("^("+filterUrls+")$") && tyBindService.checkIsBind(openid)==null){
                response.sendRedirect("/wap/to-bind-telphone?token="+request.getParameter("token"));
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
       try {
           throw new GenException("openRedPacket->充值红包失败->全公用事业");
       }catch (Exception e){
           String[] str=e.getMessage().split("->");
           System.out.println(str.length>1?str[str.length-1]:"领取红包失败");

       }
    }

}
