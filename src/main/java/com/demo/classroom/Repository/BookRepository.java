package com.demo.classroom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.classroom.Entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{
    
}
