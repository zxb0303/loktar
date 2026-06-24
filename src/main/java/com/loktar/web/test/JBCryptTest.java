package com.loktar.web.test;


import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class JBCryptTest {
    public static void main(String[] args) {
        String username = "111";
        String password = "222";
        String filePath = "F:/11/htpasswd";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(username + ":" + hashedPassword);
            writer.newLine();
            log.info("{}", "Password has been hashed and stored.");
        } catch (IOException e) {
            log.error("{}","Error writing to htpasswd file: " +e.getMessage());
         }
    }
}
