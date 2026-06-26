package com.example.crm.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.crm.repositories.UserRepository;
import com.example.crm.models.User;

import java.util.List;

/*
    Connection bettwen JPA and Spring Security
 */

@Service 
public class JpaUserDetailsService implements UserDetailsService{
    
    // Instance the User repository
    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    
    /*
        Method to load user detail by username.
        If its find, parse User to an Spring Security object and build
     */
    @Override
    public UserDetails loadUserByUsername(String username){
        // Check if username exists in DB
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("username %s not found", username)));

        List<GrantedAuthority> authorities = 
                     user.getRoles().stream() //Take roles from `user_roles`
                     /*
                        Pase roles ADMIN, USER into Spring authorities 
                                       ->  ROLE_ADMIN, ROLE_USER
                      */
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(   
                                "ROLE_" + role.getName()))
                    .toList();


        return org.springframework.security.core.userdetails.
            User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(authorities)
                .build();

    

    }

}
