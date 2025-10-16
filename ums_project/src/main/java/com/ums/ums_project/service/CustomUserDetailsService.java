package com.ums.ums_project.service;

import com.ums.ums_project.model.User;
import com.ums.ums_project.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String collegeId) throws UsernameNotFoundException {
        User user = userRepository.findByCollegeId(collegeId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with collegeId: " + collegeId));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getCollegeId(),  // use collegeId for login
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
