package com.ecommerce.user.account;

import com.ecommerce.ResponseWithStatus;
import com.ecommerce.Status;
import com.ecommerce.aspect.Track;
import com.ecommerce.user.access.AuthorityTypes;
import com.ecommerce.user.access.JwtUserDetailsService;
import com.ecommerce.user.access.UserAuthorityRequest;
import com.ecommerce.user.address.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;

    @Track
    @PostMapping(value = "/deactivate")
    public ResponseEntity<?> deactivate(String email) {
        userDetailsService.deActivateUser(email);
        return ResponseEntity.ok(new ResponseWithStatus(new Status(true, "Request completed successfully"), null));
    }

    @Track
    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO userDTO) {
        //todo Return proper error when user already exists
        UserEntity userEntity = userDetailsService.save(userDTO);
        return ResponseEntity.ok(new ResponseWithStatus(
                new Status(true, "User registered successfully"), userEntity)
        );
    }

    @Track
    @PostMapping("/authority")
    public ResponseEntity<?> createAuthority(@RequestBody AuthorityTypes authorityTypes) {
        //todo proper error when role already exist
        return ResponseEntity.ok(new ResponseWithStatus(new Status(true, "authority created successfully"),
                userService.save(authorityTypes)));
    }

    @Track
    @PostMapping("/role")
    public ResponseEntity<?> createRole(@RequestBody UserAuthorityRequest userAuthorityRequest) {
        //todo proper error handling.
        return ResponseEntity.ok(new ResponseWithStatus(new Status(true, "role assigned successfully")
                , userService.saveRole(userAuthorityRequest)));
    }
}
