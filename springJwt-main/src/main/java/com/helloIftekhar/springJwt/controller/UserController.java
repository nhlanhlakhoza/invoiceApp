package com.helloIftekhar.springJwt.controller;

import com.helloIftekhar.springJwt.model.AuthenticationResponse;
import com.helloIftekhar.springJwt.model.User;
import com.helloIftekhar.springJwt.repository.UserRepository;
import com.helloIftekhar.springJwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // Remove trailing slash
@RequestMapping("/user")
public class UserController {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public UserController(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Object> passwordResetProcess(@RequestBody User userDTO) {
        Optional<User> existingUserOptional = repository.findByEmail(userDTO.getEmail());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
            existingUser.setPassword(hashedPassword);
            repository.save(existingUser);
            return ResponseEntity.ok().body("{\"message\": \"Password reset successful\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"User not found\"}");
        }
    }

    @PostMapping("/updateProfile")
    public Object updateProfile(
            @RequestParam("username") String username,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone_number") String phoneNumber,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "file", required = false) MultipartFile file) {


        try {

            Blob profileImageBlob = null; // Initialize as null

            if (file != null && !file.isEmpty()) {
                // If file is provided, read bytes and create Blob
                byte[] bytes = file.getBytes();
                profileImageBlob = new javax.sql.rowset.serial.SerialBlob(bytes);
            }
            // Update user profile and get the JWT token
            String jwtToken = userService.updateProfile(username, fullName, email, phoneNumber, password, profileImageBlob);

            // Create an AuthenticationResponse object with JWT token and success message
            AuthenticationResponse response = new AuthenticationResponse(jwtToken, "Profile updated successfully");

            // Return the response entity with the custom object
            return ResponseEntity.ok(response);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UpdateResponse("Error updating profile"));
        }
    }

    // Custom response object
    public static class UpdateResponse {
        private String message;

        public UpdateResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @GetMapping("/displayProfileImage")
    public ResponseEntity<byte[]> displayProfileImage(@RequestParam("email") String email) {
        try {
            Optional<User> user = userService.findByEmail(email);

            // Ensure the user is not null
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Blob profileImageBlob = user.get().getImage();
            byte[] profileImageBytes;

            if (profileImageBlob != null) {
                int blobLength = (int) profileImageBlob.length();
                profileImageBytes = profileImageBlob.getBytes(1, blobLength);
            } else {
                // Handle case where no profile image is found
                // You might want to return a default image or some message
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(profileImageBytes);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/resetPassProfile")
    public ResponseEntity<Object> passwordReset(@RequestBody User userDTO) {
        Optional<User> existingUserOptional = repository.findByEmail(userDTO.getEmail());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
            existingUser.setPassword(hashedPassword);
            repository.save(existingUser);
            return ResponseEntity.ok().body("{\"message\": \"Password reset successful\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"User not found\"}");
        }
    }
    @PostMapping("/verifyOldPassword")
    public ResponseEntity<Object> verifyOldPassword(@RequestBody User request) {
        String email = request.getEmail();
        String password = request.getPassword();

        Optional<User> userOptional = repository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPassword = user.getPassword();

            // Verify old password against the encoded password
            if (passwordEncoder.matches(password, encodedPassword)) {
                // Old password matches, allow password reset
                return ResponseEntity.ok().body("{\"message\": \"Old password verified\"}");
            } else {
                // Old password does not match
                return ResponseEntity.badRequest().body("{\"message\": \"Old password is incorrect\"}");
            }
        } else {
            // User not found
            return ResponseEntity.badRequest().body("{\"message\": \"User not found\"}");
        }
    }
    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestBody User changePasswordDTO) {
        String email = changePasswordDTO.getEmail();
        String newPassword = changePasswordDTO.getPassword();

        Optional<User> userOptional = repository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"message\": \"User not found\"}");
        }

        User user = userOptional.get();

        // Encode the new password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        repository.save(user);

        return ResponseEntity.ok().body("{\"message\": \"Password changed successfully\"}");
    }


}
