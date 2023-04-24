package com.deejay.encryptionfw;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.logging.Level;
import com.deejay.HelperUtils.*;

public class AESEncDec implements AESSecure{

    private  String secretKey = null;
    private String salt = null;

    // Require 16 byte array for initialisation vector for AES
    private byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public AESEncDec() {

        // default constructor
        // Use the ipAddress and MAC Address to create the unique secretKey for a host
        InetAddress ip;
        String ipAddress = HelperUtils.getIPAddress();
        String macAddress = HelperUtils.getMacAddress();

        if (!ipAddress.isEmpty() && !macAddress.isEmpty()) {
            String secretBuilder = ipAddress + "->" +macAddress;
            HelperLogger.log(Level.INFO, "Secret Key : " + secretBuilder  );

            try {
                // Generate a secret using ENC_SECRET_KEY_DIGEST_SIZE hash of IP Address and MAC
                MessageDigest digest = MessageDigest.getInstance(EncryptionFWConfig.ENC_SECRET_KEY_DIGEST_SIZE);
                byte[] encodedHash = digest.digest(secretBuilder.getBytes(StandardCharsets.UTF_8));
                setSecretKey(HelperUtils.bytesToHex(encodedHash));

                // Just added the epoch start date to salt ( hostname ) , just to keep it a little longer
                // Salt is hostname and epoch start date
                setSalt(HelperUtils.getHostName() + "01011970");

            } catch (NoSuchAlgorithmException e) {

                HelperLogger.log(Level.SEVERE, "No MAC Algorithm found" );
                e.printStackTrace();

            }
        }
        else {
            HelperLogger.log(Level.SEVERE, "Unable to determine IP Or MAC of the system");
        }
    }



    @Override
    public String encryptString(String pText) {
        try {

           // IvParameterSpec Not required. Used only if AES/CBC/PKCS5Padding cipher is used for init
           // IvParameterSpec ivspec = new IvParameterSpec(iv);

            //Using PBKDF2 for Keygen
            SecretKeyFactory factory = SecretKeyFactory.getInstance(EncryptionFWConfig.KEYGEN_ALG);



            // Higher iterations makes Secretkey gen slower, but less secure.
            //HelperLogger.log(Level.FINE, "Iteration Count : " + ITERATION_COUNT );
            //HelperLogger.log(Level.FINE,"Key Length : " + KEY_LENGTH);

            KeySpec spec = new PBEKeySpec(
                                getSecretKey().toCharArray(),
                                getSalt().getBytes(),
                                EncryptionFWConfig.ENC_ITERATION_COUNT,
                                EncryptionFWConfig.ENC_KEY_LENGTH);


            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), EncryptionFWConfig.ENC_SECRET_KEY_ENCODING_ALG);

            //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Cipher cipher = Cipher.getInstance(EncryptionFWConfig.ENC_CIPHER);

            // Required only if we use the CBC mode.
            //cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            //Supported values are {128, 120, 112, 104, 96}
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(pText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            HelperLogger.log(Level.SEVERE, "Error in encryption. ");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String decryptString(String encText) {
        try {

            // IvParameterSpec Not required. Used only if AES/CBC/PKCS5Padding cipher is used for init
            // IvParameterSpec ivspec = new IvParameterSpec(iv);


            //Using PBKDF2 for Keygen
            SecretKeyFactory factory = SecretKeyFactory.getInstance(EncryptionFWConfig.KEYGEN_ALG);

            //HelperLogger.log(Level.FINE, "Iteration Count : " + ITERATION_COUNT );
            //HelperLogger.log(Level.FINE,"Key Length : " + KEY_LENGTH);

            // Higher iterations makes Secretkey gen slower, but less secure.
            KeySpec spec = new PBEKeySpec(
                                    getSecretKey().toCharArray(),
                                    getSalt().getBytes(),
                                    EncryptionFWConfig.ENC_ITERATION_COUNT,
                                    EncryptionFWConfig.ENC_KEY_LENGTH);

            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), EncryptionFWConfig.ENC_SECRET_KEY_ENCODING_ALG);

            Cipher cipher = Cipher.getInstance(EncryptionFWConfig.ENC_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            return new String(cipher.doFinal(Base64.getDecoder().decode(encText)));
        } catch (Exception e) {
            HelperLogger.log(Level.SEVERE, "Error in decryption.");
            e.printStackTrace();
        }
        return null;
    }
}