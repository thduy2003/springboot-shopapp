package com.example.shopapp.services;

import com.example.shopapp.components.JwtTokenUtils;
import com.example.shopapp.dtos.UpdateUserDTO;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.exception.DataNotFoundException;
import com.example.shopapp.exception.PermissionDenyException;
import com.example.shopapp.models.Role;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.RoleRepository;
import com.example.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
//tự khởi tạo constructor nếu có thuộc tính bên trong class
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        //Kiểm tra xem sđt đã tồn tại hay chưa
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if(role.getName().equals(Role.ADMIN)) {
            throw new PermissionDenyException("You cannot register an admin account");
        }
        //Convert from userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .active(true)
                .build();

        newUser.setRole(role);
        //Kiểm tra nếu có accountID , không yêu cầu password
        if(userDTO.getGoogleAccountId() == 0 && userDTO.getFacebookAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("Invalid phone number/ password");
        }
        User existingUser = optionalUser.get();
        //check password
        if(existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0) {
            if(!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrong phonenumber or password");
            }
        }
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException("Role does not exist");
        }
        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException("User is locked");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                (phoneNumber, password, existingUser.getAuthorities());
        //authentication with JavaSpring security
        authenticationManager.authenticate(authenticationToken);

        return jwtTokenUtils.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }

    }
    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception {
        // Retrieve the existing user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        //check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if(!existingUser.getPhoneNumber().equals(newPhoneNumber) && userRepository.existsByPhoneNumber(newPhoneNumber)) {
           throw new DataIntegrityViolationException("Phone number already exists");
        }

        // Update user details based on the DTO
        if(updateUserDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if(updateUserDTO.getFullName() != null) {
            existingUser.setFullName(updateUserDTO.getFullName());
        }
        if(updateUserDTO.getAddress() != null) {
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if(updateUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
        if(updateUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }
        if(updateUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        }
        if(updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isBlank()) {
            if(!updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
        }

        // If no social account IDs are provided, update the password
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            String newPassword = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }

        // Save the updated user
        return userRepository.save(existingUser);
    }
}
