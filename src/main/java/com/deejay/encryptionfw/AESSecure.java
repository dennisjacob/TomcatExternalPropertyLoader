package com.deejay.encryptionfw;

public interface AESSecure {
    public String encryptString(String pText);
    public String decryptString(String encText);
}