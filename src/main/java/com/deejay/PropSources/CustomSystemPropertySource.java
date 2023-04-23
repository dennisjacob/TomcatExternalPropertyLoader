package com.deejay.PropSources;

import com.deejay.HelperUtils.HelperUtils;
import com.deejay.HelperUtils.SecureUtilLogger;
import com.deejay.encryptionfw.AESEncDec;
import org.apache.tomcat.util.IntrospectionUtils;

import java.util.logging.Level;


public class CustomSystemPropertySource implements IntrospectionUtils.PropertySource {

    private String PropertyValue = "";
    private  String extractedPropValue ="";

    private String decryptedPropValue = null;

    public CustomSystemPropertySource() {

        AESEncDec aesEncDec = new AESEncDec();
        HelperUtils helperUtils = new HelperUtils();

       for (String prop: System.getProperties().stringPropertyNames()) {
           PropertyValue = System.getProperty(prop);

           // If a property value is not NULL, having a justifiable size and with SECURECONFIG string
           if ( (PropertyValue.length() > 10 &&  PropertyValue.contains("SECURECONFIG"))) {

               SecureUtilLogger.log(Level.INFO, "For the property " + prop + " Value before decryption is " +  PropertyValue );

               extractedPropValue = PropertyValue
                            .trim()
                            .replace("${SECURECONFIG:","")
                            .replace("}","");

               decryptedPropValue = aesEncDec.decryptString(extractedPropValue);

               if (! helperUtils.isNullOrEmpty(decryptedPropValue))
               {
                   SecureUtilLogger.log(Level.INFO, "Decrypted value for the key " +  prop);
                   SecureUtilLogger.log(Level.INFO, "Decrypted value for the key " +  decryptedPropValue);
                   System.setProperty(prop, decryptedPropValue);
               }

           }
        }


    }

    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }

    public void setProperty(String key, String value) {
        System.setProperty(key,value);
    }

}

