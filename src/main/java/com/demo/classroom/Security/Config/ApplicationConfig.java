package com.demo.classroom.Security.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.StudentRepository;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                return new UserDetails() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        if (teacherRepository.existsByUserId(user.getId())) {
                            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"));
                        } else if (studentRepository.existsByUserId(user.getId())) {
                            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
                        } else {
                            return Collections.emptyList();
                        }
                    }
                    @Override
                    public String getPassword() {
                        return user.getPassword();
                    }

                    @Override
                    public String getUsername() {
                        return user.getUsername();
                    }
                };
            }
            
        };
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
