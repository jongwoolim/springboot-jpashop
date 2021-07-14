package me.jongwoo.jpashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HelloController {

//    @GetMapping("/hello")
//    public String hello(){
//        return "hello";
//    }

    @GetMapping("/hello")
    public String hello2(Model model){
        model.addAttribute("data", "hello~");
        return "/hello";
    }
}
