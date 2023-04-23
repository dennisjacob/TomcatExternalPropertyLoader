package com.deejay.encryptionfw;

public class EncryptionFWConfig {
    public static final int ENC_ITERATION_COUNT = 1024;
    public static final int ENC_KEY_LENGTH = 256;
    public static final String ENC_CIPHER = "AES/GCM/NoPadding";
    public static final String KEYGEN_ALG = "PBKDF2WithHmacSHA256";
    public static final String ENC_SECRET_KEY_ENCODING_ALG = "AES";

    public static final String ENC_SECRET_KEY_DIGEST_SIZE = "SHA-384";
}
