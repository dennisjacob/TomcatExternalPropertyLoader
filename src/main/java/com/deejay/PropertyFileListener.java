package com.deejay;


import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.res.StringManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyFileListener implements LifecycleListener {

    private static final Log logger = LogFactory.getLog(PropertyFileListener.class);
    private static final StringManager sm = StringManager.getManager(PropertyFileListener.class);

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
        logger.info(sm.getString("Entered the Property File Listener"));
    }
    @Override
    public void lifecycleEvent(LifecycleEvent event) {



        if (Lifecycle.START_EVENT.equals(event.getType())) {
             String[] fileList = getFileList().split(",");
            for (String fileName : fileList) {
                logger.info(sm.getString("Reading the properties from file " + fileName));

                Digester.replaceSystemProperties();

                Properties properties = new Properties();

                try {
                    FileInputStream fis = new FileInputStream(fileName);
                    properties.load(fis);
                    fis.close();
                    for (String prop: properties.stringPropertyNames()) {

                        if (System.getProperties().containsKey(prop)) {

                            if (getOverwrite()) {
                                logger.info(sm.getString("Property : " + prop + " already exists and will be overwritten "));

                                String propValue = replacePlaceholders(properties.getProperty(prop));
                                logger.info(sm.getString("Setting Property : " + prop + "  Value :" + propValue));
                                System.setProperty(prop, propValue);
                            }
                        }
                        else {

                            String propValue = replacePlaceholders(properties.getProperty(prop));
                            logger.info(sm.getString("Setting Property : " + prop + "  Value :" + propValue));
                            
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
        String regex = "\\$\\{([^\\}]+)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = System.getProperty(placeholder);
            if (replacement != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

}
