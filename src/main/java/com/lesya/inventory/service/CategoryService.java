package com.lesya.inventory.service;

import com.lesya.inventory.dto.category.CategoryRequest;
import com.lesya.inventory.dto.category.CategoryResponse;
import com.lesya.inventory.entity.category.Category;
import com.lesya.inventory.exception.DuplicateResourceException;
import com.lesya.inventory.exception.ResourceNotFoundException;
import com.lesya.inventory.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// BUSINESS LOGIC OF PROJECT
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }



    // Return all product categories.
    public List<CategoryResponse> findAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // Return a category via its database identifier.
    public CategoryResponse findById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + id
                        )
                );

        return mapToResponse(category);
    }


    // Create new category
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {

        // Check category name
        if (categoryRepository.existsByName(request.getName())) {

            throw new DuplicateResourceException(
                    "Category with name "
                            + request.getName()
                            + " already exists"
            );
        }

        // Create new Category entity
        Category category = new Category();
        category.setName(request.getName());

        // Save category
        Category savedCategory = categoryRepository.save(category);

        // Return response DTO
        return mapToResponse(savedCategory);
    }


    // Update the name of an existing product category.
    @Transactional
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request
    ) {

        // Find category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + id
                        )
                );

        // Check category name
        Category categoryWithSameName = categoryRepository
                .findByName(request.getName())
                .orElse(null);

        if (categoryWithSameName != null
                && !categoryWithSameName.getId().equals(category.getId())) {

            throw new DuplicateResourceException(
                    "Category with name "
                            + request.getName()
                            + " already exists"
            );
        }

        // Update category
        category.setName(request.getName());

        // Save updated category
        Category updatedCategory = categoryRepository.save(category);

        // Return response DTO
        return mapToResponse(updatedCategory);
    }


    // Delete category ONLY  when no products are assigned to it.
    @Transactional
    public void deleteCategory(Long id) {

        // Find category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: " + id
                        )
                );

        // Check assigned products
        if (!category.getProducts().isEmpty()) {

            throw new IllegalStateException(
                    "Category cannot be deleted while products are assigned to it"
            );
        }

        // Delete category
        categoryRepository.delete(category);
    }


    // Map entity into the API response DTO.
    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getProducts().size()
        );
    }
}