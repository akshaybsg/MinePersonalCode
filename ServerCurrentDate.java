package com.tcs.EformsTesting;

import  java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerCurrentDate {
	
	private static Log logger =LogFactory.getLog(ServerCurrentDate.class);
	String result_date;
	public String getServerDate()
	{
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try
		{
			result_date = f.format(date);
		}
		catch(Exception e)
		{
			logger.error("Error in getServerDate method,"+e);
		}
		System.out.println(result_date);
		return result_date;
	}

}
