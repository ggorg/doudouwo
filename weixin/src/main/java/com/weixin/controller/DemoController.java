package com.weixin.controller;


import com.gen.common.util.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.File;

@Controller
@RequestMapping(value = "/demo")
public class DemoController {

    @GetMapping("/to")
    public String toTest(HttpSession session){
        System.out.println(session.getId());

        Tools.setSession("openid","Wegweg");
            return "pages/manager/weixin/userEdit";
    }

    public static void main(String[] args) {
        try {
            System.out.println(new File("classpath:pages/manager/weixin").exists());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
