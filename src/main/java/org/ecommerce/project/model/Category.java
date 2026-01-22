package org.ecommerce.project.model;

// This is the model of the category(Its structure)
// NOTE: Each entity when defined creates a table in the database
// AND Each Instance of the Entity (Object of the entity) corresponds to a row in that table
// Ex: IF we have a table for storing info about books, each book object would be a row


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "categories")
@Data // This is because of lombok
@NoArgsConstructor // This is needed for the entity in the database , lombok
@AllArgsConstructor // lombok
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // THis will keep ID as unique -> primary key, but NOTE This strategy is NOT supported by all the databases
    private Long categoryID;

    @NotBlank    // This will make sure the name is not empty
    @Size(min = 5, message = "Category name must contain atleast 5 characters")
    private String categoryName;

    /*
    public Category(String categoryName, Long categoryID) {
        this.categoryName = categoryName;
        this.categoryID = categoryID;
    }



    // Default no-arg constructor is recommended while creating the table
    public Category() {}

    public Long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

     */

    // We used lombok with this application
}
