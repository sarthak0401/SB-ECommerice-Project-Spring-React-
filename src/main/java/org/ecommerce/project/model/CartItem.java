package org.ecommerce.project.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
public class CartItem {

    // think of this as the table of cart, all products there as the rows, all rows have cartItemId -> unique id for the item in cart, product -> which is there in the cart, cart -> to which cart that product belong to (cart_id is different for each user)

    // And quantity,discount and price of product, all of these things corresponds to a single row of item

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    // Many CartItem entity have one product , like lets say user1 have cartItem1 as his cart, user2 have cartItem2 as his cart. Both added same iphone, So a single product can be added to multiple cartItems.

    // cartItems means the cart entity contains items that belongs to a single user, multiple cartItems means multiple user's different carts. They can have same product in it. Therefore @ManytoOne


    /*
    private double discount = product.getDiscount();
    private double productPrice = product.getPrice();
    private int quantity = product.getQuantity();
    */

    // Many cart items can have one cart
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
