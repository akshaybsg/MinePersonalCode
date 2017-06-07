package com.tcs.EformsTesting;
import java.io.BufferedReader;
import java.net.URL;
import java.*;
import javax.net.*;
import java.net.InetSocketAddress.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.*;
import org.apache.commons.httpclient.*;


public class test {
	
	private static Log logger =LogFactory.getLog(test.class);
	String responseXml="";
	int result=0;
	String strURL="";
	String userorgXml="";
	public String getOTP(String UserName,String password,String MobileNo,String SenderID,String CDMAHeader,String Message)
	{
		System.setProperty("java.net.useSystemProxies", "true");
	//	System.setProperty("https.proxyHost", "webproxy.tcs.com");
		System.setProperty("https.proxyPort", "8080"); 
		
	//	InetSocketAddress addr=(InetSocketAddress)myProxy.address();
		
		
		   System.out.println(UserName);
		   System.out.println(password);
		   System.out.println(MobileNo);
		   System.out.println(SenderID);
		   System.out.println(CDMAHeader);
		   System.out.println(Message);
		   
		   strURL="http://103.16.101.52:8080/sendsms/bulksms?username="+UserName+"&password="+password+"&type=0&dlr=1&destination="+MobileNo+"&source=GGDSDC&message="+Message;
		   PostMethod post = new PostMethod(strURL);
		   System.out.println("userorgXML:"+userorgXml);
			 if(!userorgXml.equals(""))
			 {
			 StringRequestEntity requestEntity = new StringRequestEntity(userorgXml);
		        post.setRequestEntity(requestEntity);
			 }
			 	StringRequestEntity requestEntity = new StringRequestEntity(userorgXml);
		        post.setRequestEntity(requestEntity);
		        post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
		        HttpClient httpclient = new HttpClient();
		        try {
		        	logger.error("strURL"+strURL);
		             result = httpclient.executeMethod(post);
		            responseXml = post.getResponseBodyAsString();
		
		            // Display status code
		           System.out.println("Response status code: " + result);
		
		            // Display response
		            System.out.println("Response body: ");
		            System.out.println(post.getResponseBodyAsString());
		
		        }
		        catch (HttpException ex) {
			    		logger.error(":::exception in PVCConfigurationAction::: ",ex);
		        	}
		        catch (Exception e) {
							logger.error(":::exception in PVCConfigurationAction::: ",e);
				}
			        finally {
			                   post.releaseConnection();
			        }
		        if(result!=200)
		        {
		        	responseXml="Error Occured : "+responseXml;
		        }
	
	return responseXml;
	}
	
}
