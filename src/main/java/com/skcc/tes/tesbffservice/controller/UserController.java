package com.skcc.tes.tesbffservice.controller;

import com.skcc.tes.tesbffservice.vo.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController("/data")
@CrossOrigin(methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS})
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final RestTemplate restTemplate;

    @Value("${tes.url.user}")
    private String userServiceUrl;

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        log.info("========== redirecting ::: " + userServiceUrl);
        List list = restTemplate.getForObject(String.format("%s%s", userServiceUrl, "/users"), List.class);
        log.info("========== result ::: " + list.size());
        return list;
    }
}
