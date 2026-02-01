package org.ecommerce.project.service;

import org.ecommerce.project.model.Product;
import org.ecommerce.project.payload.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    String uploadImage(String path, MultipartFile file) throws IOException;
    ProductResponse settingPaginationResponse(List<Product> products, Page<Product> productPage);
}
