package com.donat.donchess.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbaController {

    @RequestMapping("/")
    public String index() {
        return "Fooldal";
    }

    @RequestMapping("/secret")
    public String titkos() {
        return "Titkos";
    }

}
