package com.loktar.web.test;

import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class JBCryptTest {
    public static void main(String[] args) {
        String username = "111";
        String password = "222";
        String filePath = "F:/htpasswd";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(username + ":" + hashedPassword);
            writer.newLine();
            System.out.println("Password has been hashed and stored.");
        } catch (IOException e) {
            System.err.println("Error writing to htpasswd file: " + e.getMessage());
        }
    }
}
