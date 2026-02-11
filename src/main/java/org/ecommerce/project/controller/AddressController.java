package org.ecommerce.project.controller;

import org.ecommerce.project.payload.AddressDTO;
import org.ecommerce.project.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createNewAddress(@RequestBody AddressDTO addressDTO){
        AddressDTO savedAddress = addressService.createNewAddress(addressDTO);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddressess(){
        List<AddressDTO> allAddress = addressService.getAllAddresses();
        return new ResponseEntity<>(allAddress, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddress(){
        List<AddressDTO> addressDTOList = addressService.getLoggedInUserAddress();
        return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getSpecificAddress(@PathVariable Long addressId){
        AddressDTO addressDTO = addressService.getSpecificAddress(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@RequestBody AddressDTO addressDTOp, @PathVariable Long addressId){
        AddressDTO addressDTO = addressService.updateAddress(addressDTOp, addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String message = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
