package com.donat.donchess.user;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.UserDto;
import com.donat.donchess.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class userTest extends AbstractApiTest {

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




}
