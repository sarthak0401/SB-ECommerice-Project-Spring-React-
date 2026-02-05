package org.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
//@ToString   // @Data of lombok already contains the TwoString method, which displays the whole object info related to class in string
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min = 4, message = "Product name must contain atleast 4 characters")
    private String productName;
    private String image;

    @NotBlank
    @Size(min = 6, message = "Description must contain atleast 6 characters")
    private String description;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;


    // Relationship
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // Category of the product


    // We are adding a column to associate seller(user having Seller role) with the Products table, so many products can have one seller, therefore (M:1)
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;


    // Link between cart Items and product, IMP : We always create Bidirectional mapping to maintain consistency
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<CartItem> products = new ArrayList<>();
}
