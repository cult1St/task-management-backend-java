package com.backend.fintech.application.core.helpers;

import lombok.AllArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class CodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateSixDigit(){
        return String.valueOf(secureRandom.nextInt(900000) + 100000);
    }
    public static String generateDateStamp(){
        //should return something like 20260423150029 + 1892(random number)
        //yyyymmddhhiiss
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return timestamp + random;
    }
}
