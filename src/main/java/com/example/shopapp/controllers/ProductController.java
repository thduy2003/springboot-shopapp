package com.example.shopapp.controllers;

import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.dtos.ProductImageDTO;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.responses.ProductListResponse;
import com.example.shopapp.responses.ProductResponse;
import com.example.shopapp.services.ProductService;
import com.example.shopapp.services.IProductRedisService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final IProductRedisService iProductRedisService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getAllProducts(@RequestParam(defaultValue = "") String keyword,
                                                              @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int limit) throws JsonProcessingException {
        PageRequest pageRequest = PageRequest.of(
                page -1,limit, Sort.by("id").ascending());
        int totalPages = 0;
        logger.info(String.format("keyword == %s, categoryid = %d, page = %d, limit = %d", keyword, categoryId, page, limit));
        List<ProductResponse> products = iProductRedisService.getAllProducts(keyword, categoryId, pageRequest);
        if (products == null) {
            Page<ProductResponse> productPages = productService.getAllProducts(keyword, categoryId, pageRequest);
            totalPages = productPages.getTotalPages();
            products = productPages.getContent();
            iProductRedisService.saveAllProducts(products, keyword, categoryId, pageRequest);
        }
        return ResponseEntity.ok(ProductListResponse.builder()
                .products(products)
                .totalPages(totalPages)
                .build());

    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(value= "")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult result)  {
        try{
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(value= "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public  ResponseEntity<?> uploadImages(@PathVariable("id") Long productId, @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size() > 5) {
                return ResponseEntity.badRequest().body("You can only upload maximum 5 images");
            }
            List<ProductImage> productImages = new ArrayList<>();
            for(MultipartFile file: files) {
                //Nếu không có file gửi lên thì bỏ qua vòng lặp này
                if(file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if(file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large! Maximum size is 10MB");
                }
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }

                String filename = storeFile(file);
                //Lưu vào đối tượng product trong DB
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(), ProductImageDTO.builder().imageUrl(filename).build());
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        //Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        //Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        //Kiểm tra và tạo thư mục nếu nó không tồn tại
        if(!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
           java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if(resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long productId, @Valid @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(productId, productDTO);
            return ResponseEntity.ok(ProductResponse.fromProduct(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", productId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts() {
        Faker faker = new Faker();
        for(int i = 0; i < 1000; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10, 90000000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(2, 4))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
            return ResponseEntity.ok("Fake products created successfully");
        }
    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        try {
            List<Long> productIds = Arrays
                    .stream(ids.split(",")).map(Long::parseLong).toList();
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
