package br.com.javamastery.util;

import java.util.Random;

public class ValidationUtils {
    public static String generateRamdomCode(int length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomCode = new StringBuilder();
        Random random = new Random();
        while (randomCode.length() < length){
            randomCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomCode.toString();
    }
}
