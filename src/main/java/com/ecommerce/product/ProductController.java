package com.ecommerce.product;

import com.ecommerce.ResponseWithStatus;
import com.ecommerce.Status;
import com.ecommerce.aspect.Track;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImageEntity;
import com.ecommerce.product.models.ProductRequest;
import com.ecommerce.product.models.ProductResponse;
import com.ecommerce.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/product")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class.getName());
    @Autowired
    private ProductService productService;
    @Track
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseWithStatus> createProduct(@RequestPart ProductRequest product,
                                                            @RequestPart(required = false) MultipartFile[] files) {

        Product out = productService.saveProduct(product, files);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseWithStatus> get(@PathVariable("id") long id) {
        Product out = productService.getProduct(id);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @GetMapping
    public ResponseEntity getAll(@RequestParam(value = "subcategoryId", required = false) Long subcategoryId) {
        List<Product> out = null;
        if (subcategoryId != null){
            out = productService.getAllProductsBySubCategory(subcategoryId);
        }else {
            out = productService.getAllProducts();
        }
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity updateProduct(@PathVariable("id") long id, @RequestBody @Valid ProductRequest product) {
        Product out = productService.updateProduct(id, product);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping(value = "/{id}/uploadimage")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseWithStatus> handleFileUpload(@PathVariable("id") Long id, @RequestParam("file") MultipartFile[] files) {
        List<ProductImageEntity> outList = productService.uploadProductImages(id, files);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), outList)
        );
    }

    @ApiIgnore
    @Track
    @GetMapping("/{id}/images")
    public ResponseEntity viewImages(@PathVariable("id") String productId) {
        List<ProductImageEntity> out = productService.getProductImages(Long.parseLong(productId));
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @ApiIgnore
    @Track
    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("id") long imageId) {
        return productService.serveProductImage(imageId);
    }
}
