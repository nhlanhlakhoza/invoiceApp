package com.helloIftekhar.springJwt.SaveFile;

import com.helloIftekhar.springJwt.model.User;
import com.helloIftekhar.springJwt.repository.UserRepository;
import com.helloIftekhar.springJwt.service.AuthenticationService;
import com.helloIftekhar.springJwt.service.JwtService;
import com.helloIftekhar.springJwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl extends UserService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl(AuthenticationService authenticationService, UserRepository repository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        super(authenticationService, repository, passwordEncoder, jwtService);
    }

    @Override
    public Optional<User> findByEmail(String email) {

        return userRepository.findByEmail(email);
    }
    // Implement other methods for user management
}
