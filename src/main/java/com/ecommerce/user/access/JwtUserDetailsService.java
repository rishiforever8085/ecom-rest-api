package com.ecommerce.user.access;

import com.ecommerce.jwt.JwtRequest;
import com.ecommerce.jwt.UserAuthorityRepository;
import com.ecommerce.user.account.UserDTO;
import com.ecommerce.user.account.UserEntity;
import com.ecommerce.user.account.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailAndActive(email, true);
        if (user == null) {
            throw new DisabledException("No Active user with email: " + email);
        }
        List<UserAuthority> authorities = userAuthorityRepository.findByUserId(user.getId());
        return new User(user.getEmail(), user.getPassword(),
                authorities.stream().map(auth -> new SimpleGrantedAuthority(auth.getAuthorityName() + "")).collect(Collectors.toList()));
    }

    public UserEntity save(UserDTO userDTO) {
        UserEntity user = new UserEntity();
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFcmToken(userDTO.getFcmToken());
        user.setDeviceType(userDTO.getDeviceType());
        user.setActive(true);
        return userRepository.save(user);
    }

    public LoggedInUser getUserDetails(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
        List<UserAuthority> authorities = userAuthorityRepository.findByUserId(user.getId());
        String role = authorities.size() > 0 ? authorities.get(0).getAuthorityName() : "USER";
        LoggedInUser loggedInUser = new LoggedInUser();
        loggedInUser.setEmail(user.getEmail());
        loggedInUser.setDeviceType(user.getDeviceType());
        loggedInUser.setFcmToken(user.getFcmToken());
        loggedInUser.setUserId(user.getId());
        //todo bug in the below logic - need to sort out.
        loggedInUser.setRole(role);
        loggedInUser.setFirstName(user.getFirstname());
        loggedInUser.setLastName(user.getLastname());

        return loggedInUser;
    }

    public void deActivateUser(String email) {
        UserEntity entity = userRepository.findByEmail(email);
        entity.setActive(false);
        userRepository.save(entity);
    }

    @Transactional
    public void updateByEmail(JwtRequest jwtRequest) {
        userRepository.updateFcmTokenAndDeviceType(jwtRequest.getFcmToken(), jwtRequest.getDeviceType(), jwtRequest.getEmail());
    }
}
