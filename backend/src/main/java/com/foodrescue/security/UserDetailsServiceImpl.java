package com.foodrescue.security;

import com.foodrescue.entity.User;
import com.foodrescue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Connects Spring Security's authentication mechanism with our User entity.
 *
 * Spring Security calls loadUserByUsername() during login.
 * We load the user from the database and return it wrapped
 * in Spring Security's UserDetails format.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        // Check if user is blocked
        if (user.isBlocked()) {
            throw new UsernameNotFoundException("User account is blocked: " + email);
        }

        // Spring Security uses GrantedAuthority for roles.
        // We prefix with "ROLE_" so @PreAuthorize("hasRole('ADMIN')") works.
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),  // account enabled?
                true,              // account not expired
                true,              // credentials not expired
                !user.isBlocked(), // account not locked
                List.of(authority)
        );
    }
}
