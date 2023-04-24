package com.deejay.Listeners;

import com.deejay.HelperUtils.HelperLogger;
import com.deejay.encryptionfw.AESEncDec;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.tomcat.util.digester.Digester;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyFileListener implements LifecycleListener {

    private String fileList = null;
    private Boolean overwrite = true;

    public String getFileList() {
        return fileList;
    }

    public void setFileList(String fileList) {
        this.fileList = fileList;
    }

    public Boolean getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    public PropertyFileListener() {
        HelperLogger.log(Level.INFO,"Entered the Property File Listener");
    }
    @Override
    public void lifecycleEvent(LifecycleEvent event) {



        if (Lifecycle.START_EVENT.equals(event.getType())) {
             String[] fileList = getFileList().split(",");
            for (String fileName : fileList) {


                HelperLogger.log(Level.INFO,"Reading the properties from file " + fileName);

                Digester.replaceSystemProperties();

                Properties properties = new Properties();

                try {
                    FileInputStream fis = new FileInputStream(fileName);
                    properties.load(fis);
                    fis.close();
                    for (String prop: properties.stringPropertyNames()) {

                        // If key exists
                        if (System.getProperties().containsKey(prop)) {
                            // If key exists and overwrite is True
                            HelperLogger.log(Level.INFO,"Key exists , key :" + prop);
                            if (getOverwrite()) {
                                HelperLogger.log(Level.INFO,"Property : " + prop + " already exists and will be overwritten ");

                                String propValue = replacePlaceholders(properties.getProperty(prop));
                                HelperLogger.log(Level.INFO,"Setting Property : " + prop + "  Value :" + propValue);
                                System.setProperty(prop, propValue);
                            }
                        }
                        // If key not exists, just set the system prop
                        else {
                            HelperLogger.log(Level.INFO,"Key " + prop + " not exists");
                            String propValue = replacePlaceholders(properties.getProperty(prop));
                            HelperLogger.log(Level.INFO,"Setting Property : " + prop + "  Value :" + propValue);

                            if (!propValue.equals("")) {
                                System.setProperty(prop, propValue);
                            }
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    public static String replacePlaceholders(String input) {
        //identify properties in ${}, and get corresponding System Prop if it exists.
        HelperLogger.log(Level.INFO,"In replacePlaceholders for the string " + input);
        String regex = "\\$\\{([^\\}]+)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            // if placeholder contains SECURECONFIG
/*            if (placeholder.contains("SECURECONFIG")) {
                //
                HelperLogger.log(Level.INFO,"Received the input string inside matcher" + placeholder);
                String parseEncString =  placeholder
                                            .trim()
                                            .replace("SECURECONFIG:","");
                HelperLogger.log(Level.INFO,"Parsed enc string " +  parseEncString);
                AESEncDec aesEncDec = new AESEncDec();
                String decString = aesEncDec.decryptString(parseEncString);
                HelperLogger.log(Level.INFO,"Decrypted string :" + decString);
                if (!decString.isEmpty()) { sb.append(decString); }
                break;
            }*/

            String replacement = System.getProperty(placeholder);
            HelperLogger.log(Level.INFO,"Replacement string :" +  replacement);


            if (replacement != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                HelperLogger.log(Level.INFO,"Inside replacement is not null");
            }
        }

        matcher.appendTail(sb);
        HelperLogger.log(Level.INFO,"Return string is :" + sb.toString());
        return sb.toString();
    }

}
