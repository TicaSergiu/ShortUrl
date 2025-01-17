package com.shorturl.Service;

import com.shorturl.Models.UserPO;
import com.shorturl.Repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(String username, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        UserPO userPO = new UserPO();
        userPO.setUsername(username);
        userPO.setPassword(encodedPassword);
        userRepository.save(userPO);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPO userPO = userRepository.findByUsername(username);
        if(userPO == null) {
            throw new UsernameNotFoundException(username);
        }

        return org.springframework.security.core.userdetails.User.withUsername(username).password(userPO.getPassword())
                                                                 .build();
    }

    public boolean existsUser(String username) {
        return userRepository.existsByUsername(username);
    }
}
