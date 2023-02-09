package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class DBaction {
    private String username;
    private String password;
    private String hashPass;
    public static String salty;
    private String salt;

    public DBaction(String name, String password, String salt) {
        this.username = name;
        this.password = password;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashPass() {
        return hashPass;
    }

    public void setHashPass(String hashPass) {
        this.hashPass = hashPass;
    }

    public String getSalt(String username) {
        try {
            Connection dbConnection = DBConnection.getInstance().getConnection();
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT salt FROM users WHERE username = '" + username + "';");
            while (rs.next()) {
                salty = rs.getString("salt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return salty;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void insertUser() {
        try {
            Connection dbConnection = DBConnection.getInstance().getConnection();
            Statement stmt = dbConnection.createStatement();
            PreparedStatement insertStmt =
                    dbConnection.prepareStatement("INSERT INTO users (username, password, salt) VALUES (?, ?, ?);");
            insertStmt.setString(1, this.username);
            insertStmt.setString(2, (this.password));
            insertStmt.setString(3, (this.salt));
            int rows = insertStmt.executeUpdate();
            System.out.println("Rows affected: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void retrieveAllusers() {
        try {
            Connection dbConnection = DBConnection.getInstance().getConnection();
            Statement stmt = dbConnection.createStatement();
            String query = "SELECT username, password, salt FROM users";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                //Display values
                String row = "Username: " + rs.getString("username") +
                        " Hash: " + rs.getString("password") +
                        " Salt: " + rs.getString("salt") + "\n";
                System.out.print(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean checkPassword(String username, String enteredPassword) {
        try {
            Connection dbConnection = DBConnection.getInstance().getConnection();
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT password, salt FROM users WHERE username = '" + username + "';");
            while (rs.next()) {
                String storedPassword = rs.getString("password");
                String saltString = rs.getString("salt");
                byte[] salt = Base64.getDecoder().decode(saltString);
                String saltedPassword = enteredPassword + new String(salt);
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(saltedPassword.getBytes());
                byte[] resultByteArray = md.digest();
                StringBuilder sb1 = new StringBuilder();
                for (byte b : resultByteArray) {
                    sb1.append(String.format("%02x", b));
                }
                if (storedPassword.equals(sb1.toString())) {
                    System.out.println("true1");
                    System.out.println();
                    return true;
                } else {
                    System.out.println("false1");
                    return false;
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("error");
        }
        System.out.println("false2");
        return false;
    }



}
