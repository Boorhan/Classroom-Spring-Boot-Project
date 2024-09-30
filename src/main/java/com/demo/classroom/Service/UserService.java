package com.demo.classroom.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.TeacherRepository;
import com.demo.classroom.Repository.StudentRepository;

import com.demo.classroom.Repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public UserService(UserRepository userRepository, TeacherRepository teacherRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

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

}
