package com.donat.donchess.user;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.RegisterDto;
import com.donat.donchess.dto.UserDto;
import com.donat.donchess.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class userTest extends AbstractApiTest {

    public static final String MAIL_FOR_REGISTRATION = "donat.udvari@ponte.hu";
    @Autowired
    UserRepository userRepository;

    @Test
    public void createUserTest() {
        List<UserDto> userDtos = userApi.getAll();
        List<User> users = userRepository.findAll();

        assertEquals(userDtos.size(), users.size());
        users.forEach(user -> {
            assertTrue(userDtos
                    .stream()
                    .anyMatch(userDto -> userDto.getId().equals(user.getId())));
        });
    }

    @Test
    public void registerUserWithConfirmationTest() {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("donat.udvari@ponte.hu");
        registerDto.setFullName(MAIL_FOR_REGISTRATION);
        registerDto.setPassword("1234");

        userApi.register(registerDto);

        User unconfirmedUser = userRepository.findByEmail(registerDto.getEmail());
        assertNotNull(unconfirmedUser);
        assertEquals(registerDto.getFullName(), unconfirmedUser.getFullname());
        assertFalse(unconfirmedUser.isEnabled());
        assertFalse(unconfirmedUser.getAuthenticationToken().isEmpty());

        userApi.confirmRegistration(unconfirmedUser.getAuthenticationToken());


    }

    @Test
    public void confirmUserRegistration() {
        User confirmedUser = userRepository.findByEmail(MAIL_FOR_REGISTRATION);
        assertNotNull(confirmedUser);
        assertTrue(confirmedUser.getAuthenticationToken() == null);
        assertTrue(confirmedUser.isEnabled());

    }




}
