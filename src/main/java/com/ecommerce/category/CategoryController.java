package com.ecommerce.category;

import com.ecommerce.ResponseWithStatus;
import com.ecommerce.Status;
import com.ecommerce.aspect.Track;
import com.ecommerce.category.entity.ProductCategoryEntity;
import com.ecommerce.category.entity.ProductSubCategoryEntity;
import com.ecommerce.category.model.CategoryRequest;
import com.ecommerce.category.model.SubCategoryRequest;
import com.ecommerce.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    Validator groupValidator;
    @Autowired
    private CategoryService categoryServiceImpl;
    @Autowired
    private StorageService storageService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(groupValidator);
    }

    @Track
    @GetMapping
    public ResponseEntity<?> allCategories() {
        List<ProductCategoryEntity> list = categoryServiceImpl.getAllCategories();
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), list)
        );
    }

//    @GetMapping("/subcategories") // todo this API is probably not needed?
    public List<ProductSubCategoryEntity> allSubCategories() {
        List<ProductSubCategoryEntity> list = categoryServiceImpl.getAllSubCategories();
        return list;
    }

    @Track
    @GetMapping("/{categoryId}")
    public ResponseEntity getOneProductCategory(@PathVariable("categoryId") long categoryId) {
        ProductCategoryEntity category = categoryServiceImpl.getCategoryById(categoryId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), category)
        );
    }

    @Track
    @GetMapping("subcategory/{subCategoryId}")
    public ResponseEntity getOneProductSubCategory(@PathVariable("subCategoryId") long subCategoryId) {
        ProductSubCategoryEntity subCategory =  categoryServiceImpl.getSubCategoryById(subCategoryId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), subCategory)
        );
    }

    @Track
    @PostMapping
    public ResponseEntity createCategory(CategoryRequest category, @RequestParam("file") MultipartFile file) {
        ProductCategoryEntity out = categoryServiceImpl.createCategory(category, file);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping("/subcategory")
    public ResponseEntity<?> createSubCategory(
            SubCategoryRequest subCategoryRequest,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        ProductSubCategoryEntity out =  categoryServiceImpl.createSubCategory(subCategoryRequest, file);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping("/{categoryId}")
    public ResponseEntity updateCategory(@PathVariable(value = "categoryId") long categoryId,
                                                CategoryRequest categoryRequest,
                                                @RequestParam(value = "file", required = false) MultipartFile file) {

        ProductCategoryEntity out =  categoryServiceImpl.updateCategory(categoryId, categoryRequest, file);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping("/subcategory/{subCategoryId}")
    public ResponseEntity editSubCategory(@PathVariable(value = "subCategoryId") long subCategoryId,
                                                    SubCategoryRequest subCategoryRequest,
                                                    @RequestParam(value = "file", required = false) MultipartFile file) {
        ProductSubCategoryEntity out =  categoryServiceImpl.updateSubCategory(subCategoryId, subCategoryRequest, file);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @GetMapping("/image/{categoryId}")
    @ResponseBody
    public ResponseEntity<Resource> serveCategoryImage(@PathVariable("categoryId") long categoryId) {
        return categoryServiceImpl.getCategoryImage(categoryId);
    }

    @Track
    @GetMapping("/subcategory/image/{subCategoryId}")
    @ResponseBody
    public ResponseEntity<Resource> serveSubCategoryImage(@PathVariable("subCategoryId") long subCategoryId) {
        return categoryServiceImpl.getSubCategoryImage(subCategoryId);
    }

    @Track
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "categoryId") long categoryId) {
        categoryServiceImpl.deleteCategory(categoryId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), null)
        );
    }

    @Track
    @DeleteMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<?> deleteSubCategory(@PathVariable(value = "subCategoryId") long subCategoryId) {
        categoryServiceImpl.deleteSubCategory(subCategoryId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), null)
        );
    }
}