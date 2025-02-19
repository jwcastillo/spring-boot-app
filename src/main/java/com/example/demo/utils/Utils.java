package com.example.demo.utils;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class Utils {
    public static boolean isMailValid(String email){
        String regex = "^[a-z0-9-_.]{2,}@[a-z]{2,}[.][a-z]{2,}$";
        return Pattern.compile(regex).matcher(email).matches();
    }
}
