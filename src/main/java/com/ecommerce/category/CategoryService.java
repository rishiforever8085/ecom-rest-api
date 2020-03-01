package com.ecommerce.category;

import com.ecommerce.category.entity.ProductCategoryEntity;
import com.ecommerce.category.entity.ProductSubCategoryEntity;
import com.ecommerce.category.model.CategoryRequest;
import com.ecommerce.category.model.SubCategoryRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    List<ProductCategoryEntity> getAllCategories();

    ProductCategoryEntity getCategoryById(long categoryId);

    ProductCategoryEntity createCategory(CategoryRequest categoryRequest, MultipartFile file);

    ProductCategoryEntity updateCategory(long categoryId, CategoryRequest categoryRequest, MultipartFile file);

    void deleteCategory(long categoryId);

    ResponseEntity<Resource> getCategoryImage(long categoryId);

    List<ProductSubCategoryEntity> getAllSubCategories();

    ProductSubCategoryEntity getSubCategoryById(long categoryId);

    ProductSubCategoryEntity createSubCategory(SubCategoryRequest subCategoryRequest, MultipartFile file);

    ProductSubCategoryEntity updateSubCategory(long categoryId, SubCategoryRequest subCategoryRequest, MultipartFile file);

    void deleteSubCategory(long categoryId);

    ResponseEntity<Resource> getSubCategoryImage(long subCategoryId);

    List<ProductSubCategoryEntity> getSubCategoryByCategoryId(long categoryId);
}
