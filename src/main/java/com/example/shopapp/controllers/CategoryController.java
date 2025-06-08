package com.example.shopapp.controllers;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.components.converters.CategoryMessageConverter;
import com.example.shopapp.dtos.CategoryDTO;
import com.example.shopapp.models.Category;
import com.example.shopapp.responses.ResponseObject;
import com.example.shopapp.services.ICategoryService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;
    private final LocalizationUtils localizationUtils;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllCategories(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        List<Category> categories = categoryService.getAllCategories();
        /*kafka get all category*/
        this.kafkaTemplate.send("get-all-categories", categories);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(categories)
                .message("Get all categories successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(
            @PathVariable("id") Long categoryId
    ) {
        Category existingCategory = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ResponseObject.builder()
                        .data(existingCategory)
                        .message("Get category information successfully")
                        .status(HttpStatus.OK)
                .build());
    }
    @PostMapping("")
    public ResponseEntity<?> insertCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result) {
        if(result.hasErrors()) {

            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(localizationUtils.getLocalizeMessage(MessageKeys.FAILED_CREATE_CATEGORY
                    + errorMessages));
        }
        Category newCategory = categoryService.createCategory(categoryDTO);
        // send kafka categoryAdd commentMore actions
        this.kafkaTemplate.send("insert-a-category", newCategory); // producer
        this.kafkaTemplate.setMessageConverter(new CategoryMessageConverter());
        return ResponseEntity.ok(localizationUtils.getLocalizeMessage(MessageKeys.CREATE_CATEGORY_SUCCESSFULLY ));
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(localizationUtils.getLocalizeMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(localizationUtils.getLocalizeMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY + id));
    }
}
