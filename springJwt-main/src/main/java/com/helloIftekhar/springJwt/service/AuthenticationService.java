package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.AuthenticationResponse;
import com.helloIftekhar.springJwt.model.Role;
import com.helloIftekhar.springJwt.model.Token;
import com.helloIftekhar.springJwt.model.User;
import com.helloIftekhar.springJwt.repository.TokenRepository;
import com.helloIftekhar.springJwt.repository.UserRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 TokenRepository tokenRepository,
                                 AuthenticationManager authenticationManager) {
        this.repository = repository;
       this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthenticationResponse(null, "User already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setPhone_number(request.getPhone_number());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
       user.setPassword(passwordEncoder.encode(request.getPassword()));
        // user.setPassword(request.getPassword());
       // user.setRole(request.getRole());

        // Set the default profile picture for the user
        Blob defaultProfilePicture = loadDefaultProfilePictureBlob();
        user.setImage(defaultProfilePicture);

        user = repository.save(user);

        String jwt = jwtService.generateToken(user);

        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "User registration was successful");
    }

    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), // Use email for authentication
                        request.getPassword()
                )
        );

        User user = repository.findByEmail(request.getEmail()).orElseThrow(); // Find user by email
        String jwt = jwtService.generateToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "User login was successful");
    }




    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    public void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }
    private Blob loadDefaultProfilePictureBlob() {
        try {
            // Load the default profile picture from a predefined location or resource
            InputStream inputStream = getClass().getResourceAsStream("/images/default watsapp 2.jpg");
            if (inputStream != null) {
                byte[] bytes = IOUtils.toByteArray(inputStream);
                return new SerialBlob(bytes); // Convert byte array to Blob
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if default picture is not found or cannot be loaded
    }

}
