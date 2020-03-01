package com.ecommerce.category;

import com.ecommerce.category.entity.ProductCategoryEntity;
import com.ecommerce.category.entity.ProductSubCategoryEntity;
import com.ecommerce.category.model.CategoryRequest;
import com.ecommerce.category.model.SubCategoryRequest;
import com.ecommerce.category.repository.ProductCategoryRepository;
import com.ecommerce.category.repository.ProductSubCategoryRepository;
import com.ecommerce.exception.DatabaseException;
import com.ecommerce.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private static final String CATEGORY_IMAGE_ROOT = "category-images/";
    private static final String SUB_CATEGORY_IMAGE_ROOT = "sub-category-images/";
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductSubCategoryRepository productSubCategoryRepository;
    @Autowired
    private StorageService storageService;
    @Value("${app.host.address}")
    private String hostAddress;

    @Override
    public List<ProductCategoryEntity> getAllCategories() {
        List<ProductCategoryEntity> categories = productCategoryRepository.findAll();
        categories.stream().forEach(category -> {
            category.setImage(hostAddress + "/categories/image/" + category.getId());
        });
        return categories;
    }

    @Override
    public List<ProductSubCategoryEntity> getAllSubCategories() {
        List<ProductSubCategoryEntity> subCategories = productSubCategoryRepository.findAll();
        subCategories.forEach(subCategory -> {
            subCategory.setImage(hostAddress + "/categories/subcategory/image/" + subCategory.getId());
        });
        return subCategories;
    }

    @Override
    public ProductCategoryEntity getCategoryById(long categoryId) {
        ProductCategoryEntity category = productCategoryRepository.findById(categoryId).get();
        category.setImage(hostAddress + "/categories/image/" + category.getId());
        return category;
    }

    @Override
    public ProductSubCategoryEntity getSubCategoryById(long subCategoryId) {
        ProductSubCategoryEntity entity = productSubCategoryRepository.findById(subCategoryId).get();
        entity.setImage(hostAddress + "/categories/subcategory/image/" + entity.getId());
        return entity;
    }

    public ProductCategoryEntity getOneProductCategoryById(long categoryId) {
        return productCategoryRepository.findById(categoryId).get();
    }

    public ProductCategoryEntity saveCategory(ProductCategoryEntity productCategory) {
        return productCategoryRepository.save(productCategory);
    }

    @Transactional
    @Override
    public ProductCategoryEntity createCategory(CategoryRequest categoryRequest, MultipartFile file) {
        log.info("Creating product category with the request : {}", categoryRequest);
        ProductCategoryEntity entity = new ProductCategoryEntity();
        entity.setCategoryName(categoryRequest.getCategoryName());
        entity.setActive(true);
        ProductCategoryEntity out = productCategoryRepository.save(entity);

        log.info("Start uploading impage for given category");
        uploadCategoryImageImage(out.getId(), file);
        return null; // todo better handling
    }

    @Override
    public ProductCategoryEntity updateCategory(long categoryId, CategoryRequest categoryRequest, MultipartFile file) {
        ProductCategoryEntity productCategory = getOneProductCategoryById(categoryId);

        if (productCategory == null) {
            throw new IllegalArgumentException("Invalid Product Category ID");
        }

        if (categoryRequest.getCategoryName() != null) {
            productCategory.setCategoryName(categoryRequest.getCategoryName());
        }
        productCategoryRepository.save(productCategory);
        if (file != null) {
            // todo remove old file first ?
            uploadCategoryImageImage(productCategory.getId(), file);
        }
        return productCategory;
    }

    private List<ProductSubCategoryEntity> prepareSubCategoryList(ProductCategoryEntity entity, List<SubCategoryRequest> subCategories) {
        List<ProductSubCategoryEntity> list = new ArrayList<>();
        subCategories.forEach(subCategory -> {
            ProductSubCategoryEntity subCategoryEntity = new ProductSubCategoryEntity();
            subCategoryEntity.setSubCategoryName(subCategory.getSubCategoryName());
            subCategoryEntity.setActive(true);
            list.add(subCategoryEntity);
        });
        return list;
    }

    @Override
    public ProductSubCategoryEntity updateSubCategory(long subCategoryId, SubCategoryRequest subCategoryRequest, MultipartFile file) {
        ProductSubCategoryEntity entity = productSubCategoryRepository.findById(subCategoryId).get();

        if (entity == null) {
            throw new IllegalArgumentException("Invalid Sub CategoryId");
        }
        if (subCategoryRequest.getCategoryId() != null) {
            ProductCategoryEntity productCategoryEntity = productCategoryRepository.findById(subCategoryRequest.getCategoryId()).get();
            if (productCategoryEntity == null) {
                throw new IllegalArgumentException("Invalid Category name provided in request");
            }
            entity.setProductCategory(productCategoryEntity);
        }
        if (subCategoryRequest.getSubCategoryName() != null) {
            entity.setSubCategoryName(subCategoryRequest.getSubCategoryName());
        }
        ProductSubCategoryEntity out = productSubCategoryRepository.save(entity);
        if (file != null) {
            // todo delete the existing image first.
            uploadSubCategoryImageImage(out.getId(), file);
        }
        return out;
    }

    @Override
    public ProductSubCategoryEntity createSubCategory(SubCategoryRequest subCategoryRequest, MultipartFile file) {
        ProductSubCategoryEntity subCategoryEntity = new ProductSubCategoryEntity();
        subCategoryEntity.setSubCategoryName(subCategoryRequest.getSubCategoryName());
        subCategoryEntity.setActive(subCategoryRequest.getActive());

        ProductCategoryEntity productCategoryEntity = productCategoryRepository.findById(subCategoryRequest.getCategoryId()).get();

        if (productCategoryEntity == null) {
            throw new IllegalArgumentException("Invalid Product Category Name");
        }
        subCategoryEntity.setProductCategory(productCategoryEntity);

        ProductSubCategoryEntity out = productSubCategoryRepository.save(subCategoryEntity);
        uploadSubCategoryImageImage(out.getId(), file);
        return out;
    }

    public String uploadCategoryImageImage(long categoryId, MultipartFile file) {
        String path = CATEGORY_IMAGE_ROOT + categoryId;
        String filename = storageService.store(file, path);
        log.info("File has been stored at path = {}, filename = {}", path, filename);
        ProductCategoryEntity entity = productCategoryRepository.findById(categoryId).get();
        entity.setImagePath(filename);
        log.info("inserting filename in product category table");
        productCategoryRepository.save(entity);
        return filename;
    }

    public String uploadSubCategoryImageImage(long categoryId, MultipartFile file) {
        String path = SUB_CATEGORY_IMAGE_ROOT + categoryId;
        String filename = storageService.store(file, path);
        ProductSubCategoryEntity entity = productSubCategoryRepository.findById(categoryId).get();
        entity.setImagePath(filename);
        productSubCategoryRepository.save(entity);
        return filename;
    }

    @Override
    public ResponseEntity<Resource> getCategoryImage(long categoryId) {

        String imagePath = productCategoryRepository.findById(categoryId).get().getImagePath();

        String path = CATEGORY_IMAGE_ROOT + categoryId + "/";

        Resource file = storageService.loadAsResource(path + imagePath);
        String mimeType = "image/png";
        try {
            mimeType = file.getURL().openConnection().getContentType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(file);
    }

    @Override
    public ResponseEntity<Resource> getSubCategoryImage(long subCategoryId) {
        String imagePath = productSubCategoryRepository.findById(subCategoryId).get().getImagePath();

        String path = SUB_CATEGORY_IMAGE_ROOT + subCategoryId + "/";

        Resource file = storageService.loadAsResource(path + imagePath);
        String mimeType = "image/png";
        try {
            mimeType = file.getURL().openConnection().getContentType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(file);
    }

    @Override
    public List<ProductSubCategoryEntity> getSubCategoryByCategoryId(long categoryId) {
        List<ProductSubCategoryEntity> entityList =  productCategoryRepository.findById(categoryId).get().getProductSubCategories();
        entityList.forEach( entity -> {
            entity.setImage(hostAddress + "/categories/subcategory/image/" + entity.getId());
        });
        return entityList;
    }

    @Override
    public void deleteCategory(long categoryId) {
        try {
            productCategoryRepository.deleteById(categoryId);
        } catch (NoSuchElementException e) {
            throw new DatabaseException("CATEGORY_NOT_PRESENT_ERROR", "Given category id is not present in the system");
        }
        // todo delete the image from file system
    }

    public void deleteSubCategory(long subCategoryId) {
        try {
            productSubCategoryRepository.deleteById(subCategoryId);
        } catch (NoSuchElementException e) {
            throw new DatabaseException("SUBCATEGORY_NOT_PRESENT_ERROR", "Given sub category id is not present in the system");
        }
        // todo delete the image from file system
    }
}
