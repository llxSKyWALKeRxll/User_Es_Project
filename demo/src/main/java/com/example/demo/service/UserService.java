package com.example.demo.service;

import com.example.demo.dto.ResponseDtoV1;
import com.example.demo.dto.UserEsDtoV1;

public interface UserService {

    ResponseDtoV1<?> validateUser(
            String jwtToken
    );

    ResponseDtoV1<?> fetchUserFromEs(
            String id
    );

    boolean createUserEs(
            UserEsDtoV1 userDto
    );

}
