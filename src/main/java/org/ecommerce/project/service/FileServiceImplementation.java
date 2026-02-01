package org.ecommerce.project.service;

import org.ecommerce.project.model.Product;
import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.payload.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImplementation implements FileService {

    @Autowired
    ModelMapper modelMapper;

    // Uploading the image for the product
    public String uploadImage(String path, MultipartFile file) throws IOException {

        // Getting the original name of the image
        String originalFileName = file.getOriginalFilename();  // This will return the whole name of the file including extension, file.getName() gives only the name without extension name

        // Generating unique name for the file
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        // this will REMOVE the name of the original file till the . (so it basically gives us the . and the extension of the file)

        String filepath = path + File.separator + fileName;

        // Checking if path exists if not Create
        File folder = new File(path);
        if (!folder.exists()) folder.mkdir();

        // Upload it to server
        Files.copy(file.getInputStream(), Path.of(filepath));

        // return the fileName of the image
        return fileName;
    }

    public ProductResponse settingPaginationResponse(List<Product> products, Page<Product> productPage){
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLast(productPage.isLast());
        productResponse.setPageSize(productPage.getSize());

        return productResponse;
    }
}
