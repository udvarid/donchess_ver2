package com.donat.donchess.controller;

import com.donat.donchess.dto.User.RegisterDto;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.dto.User.UserLoginDto;
import com.donat.donchess.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Transactional
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/listOfUsers")
    public Set<UserDto> makeListOfUsers() {
        return userService.prepareList();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/confirmation")
    public ResponseEntity confirmation(@RequestParam("token") String token) {
        userService.confirmUserByToken(token);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/resource")
    public Map<String,Object> home() {
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }

    //TODO session rotate és Csrf token bekötése
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        userService.login(userLoginDto, request);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/user")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }


}
