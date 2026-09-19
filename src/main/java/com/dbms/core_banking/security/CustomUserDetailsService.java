package com.dbms.core_banking.security;

import com.dbms.core_banking.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        try {
            Map<String, Object> user =
                    userRepository.findByUsername(username);

            return User.withUsername((String) user.get("username"))
                    .password((String) user.get("password"))
                    .roles((String) user.get("role"))
                    .build();

        } catch (Exception e) {
            throw new UsernameNotFoundException(
                    "User not found: " + username
            );
        }
    }
}
