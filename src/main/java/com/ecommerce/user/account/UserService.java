package com.ecommerce.user.account;

import com.ecommerce.exception.DatabaseException;
import com.ecommerce.jwt.UserAuthorityRepository;
import com.ecommerce.user.access.AuthorityTypes;
import com.ecommerce.user.access.AuthorityTypesRepository;
import com.ecommerce.user.access.UserAuthority;
import com.ecommerce.user.access.UserAuthorityRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private AuthorityTypesRepository authorityTypesRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserRepository userRepository;

    public Object save(AuthorityTypes authorityTypes) {
        AuthorityTypes out = null;
        try {
            out = authorityTypesRepository.save(authorityTypes);
        } catch (Exception e) {
            throw new DatabaseException("DB_001", e.getMessage());
        }
        return out;
    }

    public Object saveRole(UserAuthorityRequest userAuthorityRequest) {
        UserEntity userEntity = userRepository.findByEmail(userAuthorityRequest.getUsername());
        if (userEntity == null) {
            throw new DatabaseException("DB_002", "Invalid User");
        }
        AuthorityTypes authorityTypes = authorityTypesRepository.findByName(userAuthorityRequest.getRolename());
        if (authorityTypes == null) {
            throw new DatabaseException("DB_003", "Invalid Role");
        }
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setUserId(userEntity.getId());
        userAuthority.setAuthorityName(authorityTypes.getName());
        try {
            userAuthority = userAuthorityRepository.save(userAuthority);
        } catch (ConstraintViolationException e) {
            throw new DatabaseException("DB_ROLE_CREATION_FAILED", "User already has a role assigned - can not assign two roles at a time");
        }
        return userAuthority;
    }
}
