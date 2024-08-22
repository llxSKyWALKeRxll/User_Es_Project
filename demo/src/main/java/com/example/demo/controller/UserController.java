package com.example.demo.controller;

import com.example.demo.dto.ResponseDtoV1;
import com.example.demo.dto.UserEsDtoV1;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/validate")
    public void validateUser(
            @RequestHeader(name = "jwt-token") String jwtToken
    ) {

        userService.validateUser(jwtToken);

    }

    @GetMapping(value = "/")
    public ResponseEntity<?> fetchUserFromEs(
            @RequestParam(name = "id") String id
    ) {

        ResponseDtoV1<?> response = userService.fetchUserFromEs(id);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping(value = "/")
    public void saveUserToEs(
            @RequestBody UserEsDtoV1 userEsDtoV1
            ) {

        userService.createUserEs(userEsDtoV1);

    }

}
