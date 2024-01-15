package com.example.shopapp.services;

import com.example.shopapp.dtos.UpdateUserDTO;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.exception.DataNotFoundException;
import com.example.shopapp.models.User;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception;

}
