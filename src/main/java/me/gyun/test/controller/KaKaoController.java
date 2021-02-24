package me.gyun.test.controller;

import me.gyun.test.service.KaKaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class KaKaoController {

    @Autowired
    private final KaKaoService kaKaoService;

    public KaKaoController(KaKaoService kaKaoService) {
        this.kaKaoService = kaKaoService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam("code") String code) {
        String access_Token = kaKaoService.getAccessToken(code);
        System.out.println("controller access_token : " + access_Token);

        Map<String, Object> userInfo = kaKaoService.getUserInfo(access_Token);
        System.out.println("login Controller : " + userInfo);

        return "index";
    }
}


