/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 *
 * @author Daniel
 */
public class Algorithm {
    
    private static Algorithm instance;

    private Algorithm() {

    }
    
    public static Algorithm getInstance() {
      if (instance == null)
         instance = new Algorithm();
      return instance;
    }

    public static String stringHexa(byte[] bytes) {
        StringBuilder  hexString = new StringBuilder();
    	for (int i=0;i<bytes.length;i++) {
            int unsigned = bytes[i] & 0xff;
            if (unsigned < 0x10)
             hexString.append("0");
            hexString.append(Integer.toHexString((unsigned)));
    	}
        return hexString.toString();
    }
  
    public static byte[] gerarHash(String frase, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            md.reset();
            md.update(frase.getBytes());
            byte[] digest = md.digest();
            return digest;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
