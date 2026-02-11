package org.ecommerce.project.service;

import org.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createNewAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    List<AddressDTO> getLoggedInUserAddress();

    AddressDTO getSpecificAddress(Long addressId);

    AddressDTO updateAddress(AddressDTO addressDTOp, Long addressId);

    String deleteAddress(Long addressId);
}
