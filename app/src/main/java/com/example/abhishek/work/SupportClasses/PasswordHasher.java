package com.example.abhishek.work.SupportClasses;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    String password = "";
    int log_rounds;

    public PasswordHasher(String password){
        password = password;
    }
    public PasswordHasher(String password, int log_rounds){
        password = password;
        this.log_rounds = log_rounds;
    }

    public String hashPassword(){
        String salt = BCrypt.gensalt(log_rounds);
        String hashed_pass = BCrypt.hashpw(password,salt);

        return hashed_pass;
    }

}
