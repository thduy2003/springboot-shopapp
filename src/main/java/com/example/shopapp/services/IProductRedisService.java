package com.example.shopapp.services;

import com.example.shopapp.responses.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductRedisService {
    void clear();

    List<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) throws JsonProcessingException;

    void saveAllProducts(List<ProductResponse> products, String keyword, Long categoryId, PageRequest pageRequest) throws JsonProcessingException;
}
