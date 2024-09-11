package com.demo.classroom.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.classroom.Entity.Student;
import com.demo.classroom.Entity.Teacher;
import com.demo.classroom.Repository.StudentRepository;
import com.demo.classroom.Repository.TeacherRepository;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @PostMapping("/students/{studentId}/upload")
    public ResponseEntity<String> uploadStudentImage(@PathVariable Long studentId, @RequestParam("file") MultipartFile file) {
        return handleFileUpload(studentId, file, "student");
    }

    @PostMapping("/teachers/{teacherId}/upload")
    public ResponseEntity<String> uploadTeacherImage(@PathVariable Long teacherId, @RequestParam("file") MultipartFile file) {
        return handleFileUpload(teacherId, file, "teacher");
    }

    private ResponseEntity<String> handleFileUpload(Long entityId, MultipartFile file, String entityType) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload");
        }

        try {
            // Create the directory if it does not exist
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Save the file to the upload directory
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.write(path, file.getBytes());

            // Construct the URL (modify as needed for your setup)
            String fileUrl = "/files/" + file.getOriginalFilename();

            // Update the entity with the image URL
            if (entityType.equals("student")) {
                Student student = studentRepository.findById(entityId).orElseThrow(() -> new RuntimeException("Student not found"));
                student.setImageUrl(fileUrl);
                studentRepository.save(student);
            } else if (entityType.equals("teacher")) {
                Teacher teacher = teacherRepository.findById(entityId).orElseThrow(() -> new RuntimeException("Teacher not found"));
                teacher.setImageUrl(fileUrl);
                teacherRepository.save(teacher);
            }

            return ResponseEntity.ok("File uploaded and URL saved successfully: " + fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir + filename);
        byte[] fileContent = Files.readAllBytes(filePath);
        return ResponseEntity.ok().body(fileContent);
    }
}
