package com.mrlocalhost.mrspotifyclone.handlers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mrlocalhost.mrspotifyclone.Constants;

public class LogWriter implements Serializable {

	private static final Level logLevel = Constants.LOG_LEVEL;
	private static final long serialVersionUID = -6162196162517554693L;
	private transient Logger logger;
	
	public LogWriter(String className) {
		logger = Logger.getLogger(className);
		logger.setLevel(logLevel);
	}
	public void log(String msg) {
		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, msg);
	}
	public void error(String msg) {
		if (logger.isLoggable(Level.SEVERE))
			logger.log(Level.SEVERE, msg);
	}
	public void warning(String msg) {
		if (logger.isLoggable(Level.WARNING))
			logger.log(Level.WARNING, msg);
	}
	public void errorTrace(Exception e) {
		if (logger.isLoggable(Level.SEVERE))
			logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
	}
}