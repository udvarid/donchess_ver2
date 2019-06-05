package com.donat.donchess.controller;

import com.donat.donchess.dto.RegisterDto;
import com.donat.donchess.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDto registerDto) throws Exception {
        userService.registerUser(registerDto);
        return new ResponseEntity(HttpStatus.OK);
    }


    //TODO autentikációs végpontot megírni
}
