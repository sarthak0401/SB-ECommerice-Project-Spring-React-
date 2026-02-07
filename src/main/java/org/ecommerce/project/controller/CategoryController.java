//Controller layer contains all the handling of requests and response

package org.ecommerce.project.controller;

import jakarta.validation.Valid;
import org.ecommerce.project.config.AppConstants;
import org.ecommerce.project.payload.CategoryDTO;
import org.ecommerce.project.payload.CategoryResponse;
import org.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
// with this we dont need to write /api/ in any endpoint, it will be considered by default
public class CategoryController {
    // This is a List of categories -> Type is Category (name of the category class)

    @Autowired
    private CategoryService categories;
//    CategoryService categories = new CategoryServiceImplementation();
    // This was done to implement loose coupling

    //Creating the object of CategoryServiceImplementation class using the interface its implementing


    // We can leverage the use of @RequestMapping annotation -> it can be done on class level as well as on method level


    //    @GetMapping("/api/public/categories")  // Removed since we used class level @RequestMapping
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder) {
        CategoryResponse categories1 = categories.getAllCategory(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categories1, HttpStatus.OK);
    }

    @GetMapping("/echo")
    public ResponseEntity<String> echoMessage(@RequestParam(name = "message", defaultValue = "Hello meow") String message) {
        return new ResponseEntity<>("Echoed the message: " + message, HttpStatus.OK);
    }

    // Below is the implementation of @RequstMapping on method level, it takes two parameters, value and method

    //  @PostMapping("/public/categories")
//    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/public/categories", method = RequestMethod.POST)
    public ResponseEntity<CategoryDTO> insertCategory(@Valid @RequestBody CategoryDTO categoryDTO) {


        CategoryDTO categoryDTO_response_forNewCategory = categories.createCategory(categoryDTO);
        return new ResponseEntity<>(categoryDTO_response_forNewCategory, HttpStatus.CREATED);
    }

    // We are returning the deleted object
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable long categoryId) {
        CategoryDTO deletedCategoryDTO = categories.deleteCategory(categoryId);
        return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);
    }

    // This will be the update mapping
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable long categoryId) {
        CategoryDTO savedCategoryDTO = categories.updateCategory(categoryDTO, categoryId);


        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }
}

// IMP NOTE about mapping of the models, Earlier we were using the Category Model directly to fetch any details
// from the database -> Now since we want to show the data in different representation in response, so use created
// another DTO model, which is CategoryDTO_request class. Now since both the Category and CategoryDTO_request class
// do the same thing, we need to MAP Category into CategoryDTO class, since all the code is using Category class to
// fulfill the request   -> So we used "model mapper" -> Added its dependency in pom.xml


