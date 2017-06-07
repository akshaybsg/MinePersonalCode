package com.tcs.EformsTesting;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.*;


public class SMS_OTP {
	private static Log logger =LogFactory.getLog(SMS_OTP.class);
	String OTPresult="";
	int result=0;
	String strURL="";
	String userorgXml="";
	
	public String getOTP(String stringURL,String UserName,String password,String MobileNo,String Source,String SenderID,String CDMAHeader,String Message)
	{
		
		System.setProperty("java.net.useSystemProxies", "true");
		System.setProperty("https.proxyHost", "webproxy.tcs.com");
		System.setProperty("https.proxyPort", "8080"); 
			
		logger.error("Parameters passed: stringUrl:"+stringURL+" username:"+UserName+" Password:"+password+" MobileNO:"+MobileNo);
		logger.error("Source:"+Source+" SenderID:"+SenderID+" CDMAHeader:"+CDMAHeader+" Message:"+Message);
					
		if(stringURL.equals("0"))
		{
			  strURL="http://103.16.101.52:8080/sendsms/bulksms?username="+UserName+"&password="+password+"&type=0&dlr=1&destination="+MobileNo+"&source="+Source+"&message="+Message;	
		}
		else
		{
			strURL=stringURL;
		}

		System.out.println(strURL);
				
				PostMethod post = new PostMethod(strURL);
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
		              OTPresult = post.getResponseBodyAsString();
		           //  System.out.println(OTPresult);
		            System.out.println("Response status code: " + result);
		             System.out.println("Response body: ");
		             System.out.println(post.getResponseBodyAsString());
		
		        }
		        catch (HttpException ex) {
			    		logger.error("::::Exception in SMS_OTP class of getOTP()::: ",ex);
		        	}
		        catch (Exception e) {
							logger.error(":::Exception in SMS_OTP class of getOTP()::: ",e);
				}
			        finally {
			                   post.releaseConnection();
			                   System.out.println("finally closed");
			        }
		        if(result!=200)
		        {
		        	OTPresult="Error Occured : "+OTPresult;
		        }
	
	return OTPresult;
	}
	
	
}
