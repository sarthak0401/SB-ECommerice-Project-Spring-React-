package org.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String userName;

    @NotBlank
    @Size(max=50)
    @Email
    @Column(name = "email")
    private String email;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"), // This refers to the primary key of the User entity
        inverseJoinColumns = @JoinColumn(name = "role_id"))  // This refers to the column which points to the primary key of the Role entity
    private Set<Role> roles = new HashSet<>();



    // This we are doing for the Seller side, the user having the Seller role, will have a set of products which is listed by them, 1:M -> One seller can have multiple products listed
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    @ToString.Exclude
    private Set<Product> products = new HashSet<>();



    // user is the owner of the relationship, since join column is there in User entity's side
    @Getter
    @Setter
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Address> addressList = new ArrayList<>();


    // each user can have one cart associated with them, therefore OnetoOne
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Cart cart;
}

// In 1:m , m:1 owner is at the Many side, we specify it using JoinColumn annotation, And the non-owning side uses mapped-by attribute in mapping
