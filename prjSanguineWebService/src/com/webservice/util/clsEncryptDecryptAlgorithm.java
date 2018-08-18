/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webservice.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author PRASHANT
 */
public class clsEncryptDecryptAlgorithm {
    
    
    public static String getKey(String token) {
        try {
            MessageDigest digest = null;
            digest = MessageDigest.getInstance("SHA-256");
            byte[] key = digest.digest(token.getBytes());
            String keyStr = bin2hex(key).toLowerCase();
            digest = MessageDigest.getInstance("MD5");
            key = digest.digest(keyStr.getBytes());
            keyStr = bin2hex(key).toLowerCase();
            if (keyStr.length() > 16) {
                return keyStr.substring(0, 16);
            } else {
                return keyStr;
            }
        } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        }
        return "";
    }
    
    
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,data));
    }
 
    
    public static String encrypt(String value) {
        try {
            byte[] key = getKey("04081977").getBytes();
            String initVector = "xxxxyyyyzzzzwwww";
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    
    public static String decrypt(String encrypted) {
        try {
            String token="04081977";
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] key = digest.digest(token.getBytes());
            digest = MessageDigest.getInstance("MD5");

            key = getKey(token).getBytes();
            String initVector = "xxxxyyyyzzzzwwww";
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            System.out.println("Exception ************" + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
    
    
}
