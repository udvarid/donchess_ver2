package com.donat.donchess.controller;

import com.donat.donchess.dto.RegisterDto;
import com.donat.donchess.dto.UserDto;
import com.donat.donchess.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/user")
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
    public ResponseEntity register(@RequestBody RegisterDto registerDto) throws Exception {
        userService.registerUser(registerDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/confirmation")
    public ResponseEntity confirmation(@RequestParam("token") String token) throws Exception {
        userService.confirmUserByToken(token);
        return new ResponseEntity(HttpStatus.OK);
    }


}
