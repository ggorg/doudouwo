package weixin.controller;


import com.gen.common.util.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {
    @GetMapping("/to")
    public String toTest(HttpSession session){
        System.out.println(session.getId());
        Tools.setSession("openid","Wegweg");
            return "redirect:/wap/to-bind-telphone";
    }
}
