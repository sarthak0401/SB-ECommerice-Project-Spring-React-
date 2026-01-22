package org.ecommerce.project.service;

import org.ecommerce.project.exceptions.APIException;
import org.ecommerce.project.exceptions.ResourceNotFoundException;
import org.ecommerce.project.model.Category;
import org.ecommerce.project.payload.CategoryDTO;
import org.ecommerce.project.payload.CategoryResponse;
import org.ecommerce.project.repositories.CategoryRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImplementation implements CategoryService {

    // Creating the variable of repository interface
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();


        // NOTE : we can pass the object of Sort to the PageRequest.of() method
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        // Creating an object of Pageable type, Pageable : Interface, PageRequest : class implementing Pageable interface, which is needed to get the required Page

        Page<Category> categoryPage = categoryRepo.findAll(pageDetails);
        // Passing the Pageable object as an argument to the finaAll method of JPA Repo, which will return a Page (We specify the type of Page in LHS)
        // categoryPage is the paginated object which spring data JPA is helping us find

        // List<Category> categories = categoryRepo.findAll();   // This code was finding all the categories, all of which are NOT needed at once

        // categoryPage.getContent() -> this gives the list of categories from the particular page
        List<Category> categories = categoryPage.getContent();

        List<CategoryDTO> categoryDTO = categories.stream().map(category -> modelMapper.map(category,
                CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTO); // This is setting the list of Categories

        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());


        if (categories.isEmpty()) {
            throw new APIException("No category added till now!");
        }
        return categoryResponse;
    }
    // getting all the categories from the database

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepo.findByCategoryName(categoryDTO.getCategoryName());
        if (existingCategory != null) {
            throw new APIException("Category with name " + existingCategory.getCategoryName() + " already exists!!");
        }

        Category category = modelMapper.map(categoryDTO, Category.class);

        // This save() method from JPA repo, returns the instance of the entity which was saved/updated
        // So we could do something like this as well : Category savedCategory = categoryRepo.save(category);
        // And used this savedCategory object which will be updated and latest to return, using model mapper convert it into CategoryDTO's type
        Category savedCategory = categoryRepo.save(category);
        System.out.println("Category added successfully");

        // THis was we will have the data from the database, also we will get the properly defined id. Note the user is not passing the id here, its automatically generating
        // from the database for each entry, so its input as null initially and when its input in db its given some value, we want to return that
        // Therefore we took the savedCategory and first converetd it into categoryDTO and then returned it
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(long categoryId) {

        // creating a local variable using the object of categoryRepo interface
        Category category =
                categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category",
                        "categoryId", categoryId));
//        CategoryDTO categoryToBeDeletedDTO = modelMapper.map(category, CategoryDTO.class);

        categoryRepo.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId) {

        Category category = modelMapper.map(categoryDTO, Category.class);

        category.setCategoryID(categoryId);
        Category savedCategory = categoryRepo.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
