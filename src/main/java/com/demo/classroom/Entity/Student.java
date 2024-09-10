package com.demo.classroom.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Lob  // This will store the image as a BLOB in the database
    private byte[] image;  // The image data is stored as a byte array
    
    private String name;
    private String rollNo;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
