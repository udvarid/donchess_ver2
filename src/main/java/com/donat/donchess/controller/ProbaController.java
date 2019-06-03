package com.donat.donchess.controller;

import com.donat.donchess.service.EmailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbaController {

    private EmailService emailService;

    public ProbaController(EmailService emailService) {

        this.emailService = emailService;
    }

    @RequestMapping("/")
    public String index() {
        //emailService.sendMessage("udvarid@hotmail.com");
        return "Fooldal";
    }

    @RequestMapping("/secret")
    public String titkos() {
        return "Titkos";
    }

}
