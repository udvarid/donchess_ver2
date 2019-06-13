package com.donat.donchess;

import com.donat.donchess.dto.UserDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class UserApiOperation {

    private RestTemplate restTemplate;
    private String endpointUrl;

    public UserApiOperation(RestTemplate restTemplate, String endpointUrl) {
        this.restTemplate = restTemplate;
        this.endpointUrl = endpointUrl;
    }

    public List<UserDto> getAll() {

        ResponseEntity<List<UserDto>> result = restTemplate.exchange(
                endpointUrl + "/listOfUsers",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>() {
                });

        return result.getBody();
    }



}
