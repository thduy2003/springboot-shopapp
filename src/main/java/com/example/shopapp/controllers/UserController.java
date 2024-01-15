package com.example.shopapp.controllers;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.UpdateUserDTO;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.dtos.UserLoginDTO;
import com.example.shopapp.models.User;
import com.example.shopapp.responses.LoginResponse;
import com.example.shopapp.responses.RegisterResponse;
import com.example.shopapp.responses.UserResponse;
import com.example.shopapp.services.IUserService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizeMessage(MessageKeys.PASSWORD_NOT_MATCH));
            }
            User user = userService.createUser(userDTO);
            return ResponseEntity.ok(RegisterResponse.builder()
                            .message(localizationUtils.getLocalizeMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .user(user)
                    .build()
            );
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(localizationUtils.getLocalizeMessage(MessageKeys.REGISTER_FAILED, e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
                    String token = userService.login(userLoginDTO.getPhoneNumber(),
                            userLoginDTO.getPassword(),
                            userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
                    );
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizeMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(LoginResponse.builder()
                    .message(localizationUtils.getLocalizeMessage(MessageKeys.LOGIN_FAILED
                            , e.getMessage())).build()
            );
        }
    }
    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/details/{userId}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
            ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            if(user.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updateUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updateUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
