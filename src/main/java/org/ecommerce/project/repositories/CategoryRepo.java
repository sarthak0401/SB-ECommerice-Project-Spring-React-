package org.ecommerce.project.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.ecommerce.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// JpaRepo is an interface itself
@Repository

// <Category,Long> here Category is the name of the model class
// and Long is the type of primary key
public interface CategoryRepo extends JpaRepository<Category,Long> {
    Category findByCategoryName(String categoryName);
}
