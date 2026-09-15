package com.lesya.inventory.controller;

import com.lesya.inventory.dto.category.CategoryRequest;
import com.lesya.inventory.dto.category.CategoryResponse;
import com.lesya.inventory.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/categories")
// Provide RESTendpoints for category management.
public class CategoryController {

    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    // EN: Returns all product categories.
    public ResponseEntity<List<CategoryResponse>> getCategories() {

        return ResponseEntity.ok(
                categoryService.findAllCategories()
        );
    }


    @GetMapping("/{id}")
    // Return a category via its database identifier.
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                categoryService.findById(id)
        );
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    // Create a new category. ONLY administrator privilege
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {

        CategoryResponse response =
                categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Update an existing category. ONLY administrator privilege
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {

        return ResponseEntity.ok(
                categoryService.updateCategory(
                        id,
                        request
                )
        );
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    // Delete a category. ONLY administrator privilege
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}