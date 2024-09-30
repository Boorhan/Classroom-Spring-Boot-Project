package com.demo.classroom.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.demo.classroom.Entity.User;
import com.demo.classroom.Repository.StudentRepository;
import com.demo.classroom.Repository.TeacherRepository;

import java.util.Collection;
import java.util.Collections;

public class MyUserDetails implements UserDetails {

    private final User user;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public MyUserDetails(User user, TeacherRepository teacherRepository, StudentRepository studentRepository) {
        this.user = user;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (teacherRepository.existsByUserId(user.getId())) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"));
        } else if (studentRepository.existsByUserId(user.getId())) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
        } else {
            return Collections.emptyList(); // No role assigned
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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
