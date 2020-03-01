package com.ecommerce.product;

import com.ecommerce.category.entity.ProductSubCategoryEntity;
import com.ecommerce.category.repository.ProductSubCategoryRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImageEntity;
import com.ecommerce.product.models.ProductRequest;
import com.ecommerce.product.models.ProductResponse;
import com.ecommerce.product.repository.ProductImageRepository;
import com.ecommerce.product.repository.ProductRepository;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private static final String PRODUCT_IMAGES_LOCATION = "product-images/";

    @Value("${app.host.address}")
    private String hostAddress;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductSubCategoryRepository productSubCategoryRepository;
    @Autowired
    private StorageService storageService;

    public Product updateProduct(long id, ProductRequest product) {
        Product updatedProduct = productRepository.findById(id).get();
        updatedProduct.setName(product.getName());
        updatedProduct.setPrice(product.getPrice());
        updatedProduct.setDescription(product.getDescription());
        updatedProduct.setProductCode(product.getProductCode());
        if (product.getSubCategoryId() != null) {
            ProductSubCategoryEntity subCategoryEntity = productSubCategoryRepository.findById(product.getSubCategoryId()).get();
            if (subCategoryEntity == null) {
                throw new IllegalArgumentException("Invalid Sub Category name");
            } else {
                updatedProduct.setSubCategoryId(subCategoryEntity.getId());
            }
        }
        return productRepository.save(updatedProduct);
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach( product -> {
            setProductImageURLs(product.getProductImages());
        });
        return products;
    }

    public Product getProduct(long id) {
        Product product = productRepository.findById(id).get();
        setProductImageURLs(product.getProductImages());
        return product;
    }

    private void setProductImageURLs(List<ProductImageEntity> productImages) {
        productImages.forEach(
                productImage -> setProductImageURL(productImage)
        );
    }

    private void setProductImageURL(ProductImageEntity productImage) {
        productImage.setImage(hostAddress + "/product/image/" + productImage.getId());
    }

    //todo createProduct method does the same right ? remove this then.
    public Product saveProduct(ProductRequest productRequest, MultipartFile[] files) {
        ProductSubCategoryEntity subCategoryEntity = productSubCategoryRepository.findById(productRequest.getSubCategoryId()).get();
        if (subCategoryEntity == null) {
            throw new IllegalArgumentException("Invalid Sub Category ID");
        }
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setSubCategoryId(subCategoryEntity.getId());
        product.setUnitOfMeasure(productRequest.getUnitOfMeasure());
        product.setPrice(productRequest.getPrice());
        product.setOffer(getSafeDouble(productRequest.getOffer()));
        product.setOfferPrice(getSafeDouble(productRequest.getOfferPrice()));
        product.setType(productRequest.getType());
        product.setAdditionalInfo(productRequest.getAdditionalInfo());

        Product out = productRepository.save(product);
        uploadProductImages(out.getId(), files);

        return out;
    }

    private Double getSafeDouble(String offerPrice) {
        return offerPrice == null ? null : Double.valueOf(offerPrice);
    }

    public ProductImageEntity addProductImage(Long id, String filename) {
        ProductImageEntity image = new ProductImageEntity();
        image.setProduct(productRepository.findById(id).get());
        image.setPath(filename);
        return productImageRepository.save(image);
    }

    public List<ProductImageEntity> getProductImages(long productId) {
        List<ProductImageEntity> images = productImageRepository.findByProductId(productId);
        images = images.stream().filter(image -> image.getProduct().getId() == productId).collect(Collectors.toList());
        images.forEach(image -> image.setImage(hostAddress + "/product/image/" + image.getId()));
        return images;
    }

    public ProductImageEntity getImage(long imageId) {
        return productImageRepository.findById(imageId).get();
    }

    public List<Product> getAllProductsBySubCategory(Long subcategoryId) {
        List<Product> products =  productRepository.findBySubCategoryId(subcategoryId);
        products.forEach( product -> {
            setProductImageURLs(product.getProductImages());
        });
        return products;
    }

    public List<ProductImageEntity> uploadProductImages(Long id, MultipartFile[] files) {
        String path = PRODUCT_IMAGES_LOCATION + id;
        List<ProductImageEntity> outList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String filename = storageService.store(files[i], path);
            ProductImageEntity imageEntity = addProductImage(id, filename);
            outList.add(imageEntity);
        }
        return outList;
    }

    public ResponseEntity<Resource> serveProductImage(long imageId) {
        ProductImageEntity image = getImage(imageId);
        // Relative path to StorageProperties.rootLocation
        String path = PRODUCT_IMAGES_LOCATION + image.getProduct().getId() + "/";

        Resource file = storageService.loadAsResource(path + image.getPath());
        String mimeType = "image/png";
        try {
            mimeType = file.getURL().openConnection().getContentType();
        } catch (IOException e) {
            log.error("Can't get file mimeType. " + e.getMessage());
        }
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(file);
    }
}
