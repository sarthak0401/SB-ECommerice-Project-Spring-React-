package org.ecommerce.project.repositories;

import org.ecommerce.project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findAddressByAddressId(Long addressId);
}
