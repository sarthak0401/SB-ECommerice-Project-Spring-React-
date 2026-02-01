package org.ecommerce.project.service;

import org.ecommerce.project.exceptions.APIException;
import org.ecommerce.project.exceptions.ResourceNotFoundException;
import org.ecommerce.project.model.Category;
import org.ecommerce.project.model.Product;
import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.payload.ProductResponse;
import org.ecommerce.project.repositories.CategoryRepo;
import org.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImplementation implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image_path}")
    private String path;


    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        //  Checking if product is already present or not
        Product existingProduct = productRepository.findProductByProductNameIgnoreCase(modelMapper.map(productDTO, Product.class).getProductName().strip());

        if(existingProduct==null){
            Category category = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

            // Product product1 = new Product(); // We dont need to create a new object, we can directly use the argument object product


            // THis below thing gives ERROR -> because the productDTO is NOT saved to the database and therefore productId is NOT generated, its null as of now
            // Product product = productRepository.findById(productDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found", "Product", productDTO.getProductId()));


            // Calculating discount & Setting the discounted price to special price variable
            double discount = productDTO.getDiscount();
            double price = productDTO.getPrice();
            double splPrice = price - price * discount / 100;
            String productName = productDTO.getProductName().strip(); // Making sure it doesnt have any leading and trailing whitespaces

            // Setting the category and the specialPrice to the product

            Product product = modelMapper.map(productDTO, Product.class);

            product.setProductName(productName);
            product.setCategory(category);
            product.setSpecialPrice(splPrice);
            product.setImage("default.png");


            Product productSaved = productRepository.save(product);
            return modelMapper.map(productSaved, ProductDTO.class);
        }
        else throw new APIException("Product Already exists");
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productPage =  productRepository.findAll(pageDetails);

        List<Product> products = productPage.getContent();

        // Checking if the list is empty (i.e, no products added yet)
        if(!products.isEmpty()){
            List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

            ProductResponse productResponse = new ProductResponse();
            productResponse.setContent(productDTOS);
            productResponse.setLast(productPage.isLast());
            productResponse.setPageNumber(productPage.getNumber());
            productResponse.setTotalPages(productPage.getTotalPages());
            productResponse.setPageSize(productPage.getSize());
            productResponse.setTotalElements(productPage.getTotalElements());

            return productResponse;
        }
        else{
             throw new APIException("No Products added till now!");
        }
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Implementing Pagination logic

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findProductByCategory_CategoryIDOrderByPriceAsc(categoryId, pageDetails);

        List<Product> products = productPage.getContent();

        // Checking if there exists any product with specified category
        if(!products.isEmpty()){
            return fileService.settingPaginationResponse(products, productPage);
        }
        else throw new APIException("No products exits with the specified category");

    }

    @Override
    public ProductResponse getAllProductsWrtKeywordSearch(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Implementing pagination
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productPage = productRepository.findProductByProductNameContainingIgnoreCase(keyword, pageDetails);
        List<Product> products = productPage.getContent();

        if(!products.isEmpty()){
            return fileService.settingPaginationResponse(products, productPage);
        }
        else throw new APIException("No product found with searched Keyword");

    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        //Checking if the productRetrieved exists, i.e, the productId is valid or not. It will throw the exception if product is NOT found
        Product productRetrieved = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found", "ID", productId));

        productRetrieved.setProductName(productDTO.getProductName());
        productRetrieved.setDescription(productDTO.getDescription());
        productRetrieved.setPrice(productDTO.getPrice());
        productRetrieved.setDiscount(productDTO.getDiscount());
        productRetrieved.setSpecialPrice(productDTO.getPrice() - productDTO.getPrice() * productDTO.getDiscount() / 100);
        productRetrieved.setQuantity(productDTO.getQuantity());

        Product savedProd = productRepository.save(productRetrieved);
        return modelMapper.map(savedProd, ProductDTO.class);

    }

    @Override
    public String deleteProduct(Long productId) {
        Product productRetrieved = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found", "ID", productId));
        // With the above code, if the product is not found it'll throw ResourceNotFoundException

        productRepository.deleteById(productId);
        return "Product with id " + productId + " deleted successfully!";
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Getting the product from the database
        Product productFromDatabase = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Not found", "ID", productId));

        // Upload image to server (saving the image in the code filesystem)
        String filename = fileService.uploadImage(path, image);
        // get the filename of the uploaded image
        // Updating the new filename to the product (using the setter)

        productFromDatabase.setImage(filename);
        // save product and return DTO
        Product updatedProduct = productRepository.save(productFromDatabase);

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
