package com.lancefallon.services;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public boolean matchPassword(String input, String hash){
        return BCrypt.checkpw(input, hash);
    }

}
