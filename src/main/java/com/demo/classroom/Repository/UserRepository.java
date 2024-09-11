package com.demo.classroom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
