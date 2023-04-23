package com.deejay.HelperUtils;



import java.io.IOException;
import java.util.logging.*;

public class SecureUtilLogger {

    static Logger logger;
    public Handler fileHandler;
    Formatter plainText;
    public SecureUtilLogger() throws IOException {
        logger = Logger.getLogger(SecureUtilLogger.class.getName());
    }

    private static Logger getLogger(){
        if(logger == null){
            try {  new SecureUtilLogger();    }
            catch (IOException e) { e.printStackTrace();  }
        }
        return logger;
    }
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
    }



}
