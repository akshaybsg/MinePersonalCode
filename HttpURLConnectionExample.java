package com.tcs.EformsTesting;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.*;

public class HttpURLConnectionExample {
	
	private final String USER_AGENT = "Mozilla/5.0";
	

	// systemSettings.put("proxySet", "true");
     //systemSettings.put("http.proxyHost","proxy.tcs.com");
   //  systemSettings.put("http.proxyPort", "8080");
	
	public void sendGet() throws Exception {
		
		System.setProperty("java.net.useSystemProxies", "true");
	//	System.setProperty("https.proxyHost", "webproxy.tcs.com");
		System.setProperty("https.proxyPort", "8080"); 
		String MobileNo="9167030505";
	//	String url = "https://www.google.com";
	//	String url="http://103.16.101.52:8080/sendsms/bulksms?username=gtic-ggdsd32&password=shamim12&type=0&dlr=1&destination=9167030505&source=GGDSDC&message=hellodude";
		String url="http://103.16.101.52:8080/sendsms/bulksms?username=gtic-ggdsd32&password=shamim12&type=0&dlr=1&destination="+MobileNo+"&source=GGDSDC&message=hibuddy";
		//url.concat("&source=GGDSDC&message=hibuddy");
		System.out.println(url);
		URL obj = new URL(url);
		System.out.println("URL obj:"+obj);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		System.out.println("Conn obj:"+con);
		BufferedReader in;
		StringBuffer response;
		try
	       {
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", USER_AGENT);
				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
		 
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				response = new StringBuffer();
		 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				System.out.println(response.toString());
       }
       catch(Exception e)
       {
    	   System.out.println("exception"+e); 
       }
       finally
       {  
    	   System.out.println("executed finally"); 
       }
		
 
	}

}
