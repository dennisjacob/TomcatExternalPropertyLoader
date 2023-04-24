package com.deejay.HelperUtils;



import java.io.IOException;
import java.util.logging.*;

public class HelperLogger {

    static Logger logger;
    public Handler fileHandler;
    Formatter plainText;
    public HelperLogger() throws IOException {
        logger = Logger.getLogger(HelperLogger.class.getName());
    }

    private static Logger getLogger(){
        if(logger == null){
            try {  new HelperLogger();    }
            catch (IOException e) { e.printStackTrace();  }
        }
        return logger;
    }
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
    }



}
