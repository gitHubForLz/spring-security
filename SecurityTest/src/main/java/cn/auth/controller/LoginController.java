package cn.auth.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public String userLogin() {
        return "demo-sign";
    }

    @RequestMapping("/login-error")
    public String  loginError() {
        return "login-error";

    }

    @RequestMapping("/hello")
    @ResponseBody
    public String helloWorld() {
        return "spring security hello world";
    }

    @RequestMapping("/whoim")
   @ ResponseBody
    public Object whoIm()
    {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}