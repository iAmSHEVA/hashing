package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class PasswordHashing {
    public static void main(String[] args) throws SQLException {
        String username = "Baraa";
        String password = "secret123";
        //Method that generate hashed password and , and store the salt value and hashed password in database
        hashAndSaltPassword(username, password);
        //Method that check the entered password with the username's password at database with the same salt at DB
        DBaction.checkPassword(username, password);
        DBaction users = new DBaction("", "", "");
        users.retrieveAllusers();

    }

    public static void hashAndSaltPassword(String username, String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Concatenate password and salt
            String saltedPassword = password + new String(salt);

            // Hash the salted password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltedPassword.getBytes());
            byte[] hashedPassword = md.digest();

            // Encode the salt and hashed password into base64 strings
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashedPasswordBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            // Store the hashed password and salt in the database
            Connection dbConnection = DBConnection.getInstance().getConnection();
            PreparedStatement pstmt = dbConnection.prepareStatement("INSERT INTO users (username, password, salt) VALUES (?, ?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPasswordBase64);
            pstmt.setString(3, saltBase64);
            pstmt.executeUpdate();
        } catch (NoSuchAlgorithmException | SQLException e) {
            e.printStackTrace();
        }
    }


}
