package com.tcs.EformsTesting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EFormActivationException extends Exception 
{
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(EFormActivationException.class);

	public EFormActivationException(String message) 
	{
		super(message);
		logger.error(message);
	}
}