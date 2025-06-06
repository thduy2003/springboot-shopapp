package com.example.shopapp.controllers;

import com.example.shopapp.models.Role;
import com.example.shopapp.services.impl.RoleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleServiceImpl roleServiceImpl;
    @GetMapping("")
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleServiceImpl.getAllRoles();
        return ResponseEntity.ok(roles);
    }

}
