package com.helloIftekhar.springJwt.SaveFile;

import com.helloIftekhar.springJwt.model.User;

public interface UserServices {
    User findByEmail(String email);
}
