package com.deejay.Standalone;



import com.deejay.encryptionfw.AESEncDec;

import java.io.*;
import java.util.HashMap;


public class SecureConfigUtil {

    private static void encryptFile(String strFile) {

        String strLine;

        File file = new File(strFile);
        AESEncDec aesEncDec = new AESEncDec();

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));

            HashMap<String, String> filekvmap = new HashMap<>();


            while ((strLine = br.readLine()) != null)  {
                String[] strTempArray = null;
                if (strLine.length() > 2) {
                    strTempArray = strLine.split("=", 2);
                    if ( strTempArray[1].trim().isEmpty() || strTempArray[1].trim().contains("SECURECONFIG")) {
                        filekvmap.put(strTempArray[0], strTempArray[1]);
                    }
                    else {
                        filekvmap.put(strTempArray[0], "${SECURECONFIG:" + aesEncDec.encryptString(strTempArray[1]) + "}");
                    }
                }
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            for (String eachKey: filekvmap.keySet()) {
                bw.write(eachKey + "=" + filekvmap.get(eachKey) );
                bw.newLine();
            }

            bw.close();
        }
        catch (FileNotFoundException e) {
            System.out.printf("File %s not found\n", strFile);
        }
        catch (IOException e) {
            System.out.printf("File %s is unable to read \n", strFile);
        }
    }


    // Standalone program for encryption and decryption
    public static void main(String[] args)  {


        Boolean args_status = (args.length != 2) ? true : false;

        if ( args_status) {

            System.out.println("Invalid number of parameters, use one of the following parameters");
            System.out.println("[ -enc <string  to encrypt> ]");
            System.out.println("[ -dec <string to decrypt> ]");
            System.out.println("[ -file <File containing key value pair to encrypt values> ]");
            System.exit(1);
        }

        AESEncDec aesEncDec = new AESEncDec();
        switch(args[0]) {
            case "-enc":
                System.out.println("Passing -enc " + args[1]);
                System.out.printf( "Encrypted String  : %s\n",aesEncDec.encryptString(args[1]));
                break;
            case "-dec":
                System.out.println("Passing -dec " + args[1]);
                System.out.printf( "Encrypted String  : %s\n",aesEncDec.decryptString(args[1]));
                break;
            case "-file":
                System.out.println("Passing -file");
                encryptFile(args[1]);
                break;
            default:
                System.out.println("Invalid parameter");
        }
    }

}
