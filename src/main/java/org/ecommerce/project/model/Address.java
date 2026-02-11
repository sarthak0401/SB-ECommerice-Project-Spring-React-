package org.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name must be least 5 characters")
    private String street;

    @NotBlank
    @Size(min = 5, max = 20, message = "House name must be between 5 to 20 characters")
    private String houseName;

    @NotBlank
    @Size(min =3, message = "City name must be least 3 characters" )
    private String cityName;


    @NotBlank
    @Size(min =3, message = "State name must be least 3 characters" )
    private String state;

    @NotBlank
    @Size(min =3, message = "Country name must be least 3 characters" )
    private String country;

    @NotBlank
    @Size(min =6, message = "Pincode name must be least 6 characters" )
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String houseName, String cityName, String state, String country, String pincode) {
        this.street = street;
        this.houseName = houseName;
        this.cityName = cityName;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}
