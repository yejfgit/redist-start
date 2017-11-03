package com.yejinfu.redis.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class LoggerUtil{
	
	private static final String ERROR = "error";
	private static final String INFO = "info";
	private static final String DEBUG = "debug";
	private static final String WARN = "warn";
	  /**
     * 错误输入日志
     */
    public static final Logger log = LoggerFactory.getLogger(Object.class);

    public static void error(String msg,Exception e) {
        logError(msg,e);
        //printError(Logger.getLogger("errorLogger"),msg,e);
    }

    public static void info(Object msg) {
        logInfo(String.valueOf(msg));
        //Logger.getLogger("rootLogger").info(msg);
    }


    /**
     * 记录一直 info信息
     *
     * @param message
     */
    public static void logInfo(String message) {
        StringBuilder s = new StringBuilder();
        s.append((message));
        log.info(s.toString());
        logSolr(INFO,s.toString(),null);
    }

    public static void logInfo(String message, Throwable e) {
        StringBuilder s = new StringBuilder();
        s.append(("exception : -->>"));
        s.append((message));
        log.info(s.toString(), e);
        logSolr(INFO,s.toString(),e);
    }

    public static void logWarn(String message) {
        StringBuilder s = new StringBuilder();
        s.append((message));

        log.warn(s.toString());
        logSolr(WARN,s.toString(),null);
    }

    public static void logWarn(String message, Throwable e) {
        StringBuilder s = new StringBuilder();
        s.append(("exception : -->>"));
        s.append((message));
        log.warn(s.toString(), e);
        logSolr(WARN,s.toString(),e);
    }

    public static void logDebug(String message) {
        StringBuilder s = new StringBuilder();
        s.append((message));
        log.debug(s.toString());
        
        logSolr(DEBUG,s.toString(),null);
    }

    public static void logDebug(String message, Throwable e) {
        StringBuilder s = new StringBuilder();
        s.append(("exception : -->>"));
        s.append((message));
        log.debug(s.toString(), e);

        logSolr(DEBUG,s.toString(),e);
    }

    public static void logError(String message) {
        StringBuilder s = new StringBuilder();
        s.append(message);
        log.error(s.toString());
        logSolr(ERROR,s.toString(),null);
    }

    /**
     * 记录日志错误信息
     *
     * @param message
     * @param e
     */
    public static void logError(String message, Throwable e) {
        StringBuilder s = new StringBuilder();
        s.append(("exception : -->>"));
        s.append((message));
        log.error(s.toString(), e);
        
        logSolr(ERROR,s.toString(),e);
    }

    public static void logSolr(String loggerType,String msg,Throwable te){
    	try {
    		String errorMsg = "";
            if(te != null){
    		    ByteArrayOutputStream  buf = new ByteArrayOutputStream();  
    		    te.printStackTrace(new PrintWriter(buf,true));  
    	        errorMsg = buf.toString();
    		}
    		
			//SolrLoggerClient.buildLoggerIndex(MyDateUtils.formatHmsTime(new Date(), null)+" "+msg+errorMsg, loggerType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
