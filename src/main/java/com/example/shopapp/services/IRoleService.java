package com.example.shopapp.services;

import com.example.shopapp.models.Role;
import com.example.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IRoleService {
    List<Role> getAllRoles();

}
