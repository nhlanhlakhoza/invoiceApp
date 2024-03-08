package com.helloIftekhar.springJwt.service;


import com.helloIftekhar.springJwt.model.AuthenticationResponse;
import com.helloIftekhar.springJwt.model.User;
import com.helloIftekhar.springJwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.util.Base64;
import java.util.Optional;

@Service
public abstract class UserService {


    @Autowired
    JavaMailSender javaMailSender;

    //email settings
    @Autowired
    private MailSender mailSender;
    @Autowired
    private JavaMailSender jmSender;

    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserService(AuthenticationService authenticationService, UserRepository repository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
@Autowired
private UserRepository repository;
    public String sendEmail(User user) {
        try {


            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("nhlanhlakhoza05@gmail.com");// input the senders email ID
            msg.setTo(user.getEmail());

            msg.setSubject("Password Reset Request");
            msg.setText("Hello " + user.getUsername()+ ",\n\n"
                    + "You have requested to reset your password. Here is your Email Address:\n\n"
                    + "Email: " + user.getEmail() + "\n"
                    + "Please click on the following link to reset your password: " + "http://localhost:4200/change_password" + "\n\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Regards,\n"
                    + "Invoice System");

            javaMailSender.send(msg);

            System.out.println("Mail Send...");

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }

    @Autowired
    private UserRepository userRepository;

    public String updateProfile(String username, String fullName, String email, String phoneNumber, String password, Blob profileImageBlob) {
        // Retrieve the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));

        // Update user's profile information
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone_number(phoneNumber);


        // Update password only if provided
        if (password != null ) {
            String hashedPassword = passwordEncoder.encode(password);
            user.setPassword(hashedPassword);
        }


        // Update profile image only if provided
        if (profileImageBlob != null) {
            user.setImage(profileImageBlob);
        }

        // Save the updated user object
        try {
            userRepository.save(user);
            String jwt = jwtService.generateToken(user);

            // Save or update user's token
            authenticationService.saveUserToken(jwt, user);

            return jwt;

        } catch (Exception e) {
            // Handle exceptions appropriately
            throw new RuntimeException("Failed to update user profile", e);
        }
    }



    public abstract Optional<User> findByEmail(String email);
}

