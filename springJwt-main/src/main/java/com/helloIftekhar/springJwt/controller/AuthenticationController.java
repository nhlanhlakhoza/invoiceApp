package com.helloIftekhar.springJwt.controller;

import com.helloIftekhar.springJwt.model.AuthenticationResponse;
import com.helloIftekhar.springJwt.model.User;
import com.helloIftekhar.springJwt.repository.UserRepository;
import com.helloIftekhar.springJwt.service.AuthenticationService;
import com.helloIftekhar.springJwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@CrossOrigin(origins ="http://localhost:4200/")
public class AuthenticationController {


    @Autowired
    private UserRepository repository;
    @Autowired
    UserService userDetailsService;
    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) {
        Optional<User> existingUser = repository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            // User already exists, return an error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }

        // User does not exist, proceed with registration
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPasswordProcess(@RequestBody User usersSign) {
        Optional<User> existingUser = repository.findByEmail(usersSign.getEmail());
        if (existingUser.isPresent()) {
            // User exists, send email for password reset
            userDetailsService.sendEmail(existingUser.get());
            return ResponseEntity.ok().body("{\"message\": \"Email sent successfully\"}");
        } else {
            // User does not exist, return an error response
            return ResponseEntity.badRequest().body("{\"message\": \"User not found\"}");
        }
    }
    @PostMapping("/update-image")
    public ResponseEntity<?> updateImageByEmail(@RequestParam String email) {
        try {
            authService.updateImageByEmail(email);
            return ResponseEntity.ok().body("{\"message\": \"Image updated successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error updating image\"}");
        }
    }
}
