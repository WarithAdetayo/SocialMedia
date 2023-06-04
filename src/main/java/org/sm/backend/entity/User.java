package org.sm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Date;

@Entity
@Table(name = "SMUSER")
public class User {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "passwordHash", length = 128)
    private String passwordHash;

    @Column(name = "salt")
    private String salt;

    @Column(name = "dateJoined")
    private Date dateJoined;

    public User() {
        this.dateJoined = new Date();
    }

    public User(String username) {
        this();
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Date getDateJoined() {
        return this.dateJoined;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean checkPassword(String password) {

        if (this.passwordHash == null || this.salt == null)
            return false;

        try {
            String passwordHash = getPasswordHash(password, this.salt.getBytes());
            return passwordHash.equals(this.passwordHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public User setPassword(String password) {
        try {
            byte[] salt = generateSalt();
            this.salt = this.byteToString(salt);
            this.passwordHash = getPasswordHash(password, this.salt.getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    private String getPasswordHash(String clearPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeySpec spec = new PBEKeySpec(clearPassword.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        return this.byteToString(hash);
    }

    private byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("User<%s>", username);
    }

}
