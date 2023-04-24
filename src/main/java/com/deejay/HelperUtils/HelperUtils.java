package com.deejay.HelperUtils;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

public  class HelperUtils {
    public static InetAddress ip;


    // Get IP Address of the system
    public static String getIPAddress() {
        String ipAddress = null;
        try {
            ip = InetAddress.getLocalHost();
            ipAddress = ip.getHostAddress();
        } catch (UnknownHostException e) {
            HelperLogger.log(Level.SEVERE, "Unable to get the IP Address" );
            e.printStackTrace();
        }
        return ipAddress;
    }


    // Get the MAC Address of the system
    public static String getMacAddress() {
        String macAddress = null;
        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            macAddress = sb.toString();
        } catch (UnknownHostException e) {
            HelperLogger.log(Level.SEVERE, "Unknown Host" );
            e.printStackTrace();
        } catch (SocketException e) {
            HelperLogger.log(Level.SEVERE, "Unable to get the Network Address" );
            e.printStackTrace();
        }
        return macAddress;
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    // Get hostname, "localhost" is default
    public static String getHostName() {
        String hostname="localhost";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            HelperLogger.log(Level.SEVERE, "Unable to identify the hostname" );
            e.printStackTrace();
        }
        return hostname;
    }

    public boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}