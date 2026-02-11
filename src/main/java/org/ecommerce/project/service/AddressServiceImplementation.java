package org.ecommerce.project.service;

import jakarta.transaction.Transactional;
import org.ecommerce.project.exceptions.APIException;
import org.ecommerce.project.model.Address;
import org.ecommerce.project.model.User;
import org.ecommerce.project.payload.AddressDTO;
import org.ecommerce.project.repositories.AddressRepository;
import org.ecommerce.project.repositories.UserRepository;
import org.ecommerce.project.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImplementation implements AddressService {

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createNewAddress(AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);

        // Fetch the currently logged-in user
        User user = authUtils.getLoggedInUser();

        // In OneToMany/ManyToOne, the owning side is the @ManyToOne side (Address).
        // So we must set the user inside Address BEFORE saving,
        // because the foreign key (user_id) lives in the Address table.
        address.setUser(user);

        // Maintain bidirectional consistency in memory.
        // This updates the inverse side collection in User.
        user.getAddressList().add(address);

        // Save the owning side (Address).
        // Since Address owns the relationship, this will correctly
        // insert the row with the proper user_id foreign key.
        Address savedAddress = addressRepository.save(address);

        // No need to explicitly save user if this method is @Transactional,
        // because Hibernate will automatically detect changes (dirty checking).
        // Also, cascade from User is not required here since Address is the owning side.

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();
        List<AddressDTO> addressDTOS = addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

        return addressDTOS;
    }

    @Override
    public List<AddressDTO> getLoggedInUserAddress() {
        User user = userRepository.findUserByUserName(authUtils.getLoggedInUser().getUserName()).orElseThrow(()-> new APIException("User not found"));

        // getting the address related to a particular user
        List<Address> addressList = user.getAddressList();

        List<AddressDTO> addressDTOS = addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

        return addressDTOS;
    }

    @Override
    public AddressDTO getSpecificAddress(Long addressId) {
        Address address = addressRepository.findAddressByAddressId(addressId);
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTOp, Long addressId) {
        Address address = addressRepository.findAddressByAddressId(addressId);
        address.setState(addressDTOp.getState());
        address.setPincode(addressDTOp.getPincode());
        address.setCountry(addressDTOp.getCountry());
        address.setStreet(addressDTOp.getStreet());
        address.setHouseName(addressDTOp.getHouseName());
        address.setCityName(addressDTOp.getCityName());

        Address updatedAddr = addressRepository.save(address);
        User user = address.getUser();
        user.getAddressList().removeIf(add -> add.getAddressId().equals(addressId));
        user.getAddressList().add(updatedAddr);

        userRepository.save(user);

        return modelMapper.map(updatedAddr, AddressDTO.class);
    }

    @Transactional
    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findAddressByAddressId(addressId);
        User user = address.getUser();

        user.getAddressList().remove(address);

        // addressRepository.delete(address);
        return "Address with " + addressId + " deleted successfully!";
    }
}
