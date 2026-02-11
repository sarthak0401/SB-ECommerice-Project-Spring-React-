package org.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String street;
    private String houseName;
    private String cityName;
    private String state;
    private String country;
    private String pincode;
}
