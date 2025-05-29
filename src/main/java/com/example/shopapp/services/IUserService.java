package com.example.shopapp.services;

import com.example.shopapp.dtos.UpdateUserDTO;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.exception.DataNotFoundException;
import com.example.shopapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception;

    Page<User> findAllUsers(String keyword, Pageable pageable);

    void resetPassword(Long userId, String newPassword) throws Exception;

    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
}
