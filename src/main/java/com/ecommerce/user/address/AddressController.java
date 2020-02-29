package com.ecommerce.user.address;

import com.ecommerce.ResponseWithStatus;
import com.ecommerce.Status;
import com.ecommerce.aspect.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {
    @Autowired
    private AddressService addressService;

    @Track
    @GetMapping("user/{userId}/address")
    public ResponseEntity getAll(@PathVariable("userId") long userId) {
        List<AddressEntity> out = addressService.getAllAddress(userId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @GetMapping("/address/{addressId}")
    public ResponseEntity getOne(@PathVariable("userId") long userId, @PathVariable("addressId") long addressId) {
        AddressEntity out = addressService.getAddress(userId, addressId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @PostMapping("user/{userId}/address")
    public ResponseEntity updateOne(@PathVariable("userId") long userId,
                                    @RequestBody AddressDTO addressDTO) {
        AddressEntity out = addressService.saveAddress(addressDTO, userId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), out)
        );
    }

    @Track
    @DeleteMapping("user/{userId}/address")
    public ResponseEntity deleteOne(@PathVariable("userId") long userId,
                                    @RequestBody AddressDTO addressDTO) {
        addressService.deleteAddress(addressDTO, userId);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "Request completed successfully"), null)
        );
    }
}
