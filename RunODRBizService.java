package com.tcs.WithLoggers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class RunODRBizService {

	public static final Log logger = LogFactory.getLog(RunODRBizService.class);
	private static String gLoginId = "webservice.user@opjindaluniv.com";
	private static String gPwd = "OpJindal@123";
	private static String gServiceKey = "nSRj4S6TVFnGXmKdrHOEFA%3D%3D";
	private static String gUniqueCode = "sSy%2Bi2u6wso4nZ0WZq58rg%3D%3D";	

	private static String gDomain = "https://www.tcsion.com/" ;//(https://www.tcsion.com/%27)
	private static String gURI_GetToken = "iONBizServices/Authenticate";
	private static String gURI_RunService = "iONBizServices/iONWebService";

	private static Proxy lTCSProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.tcs.com", 8080));

	public static void main(String[] args) {

		String lUrlParameterString = "servicekey="+gServiceKey+"&s="+gUniqueCode;
		String lURLOutput=null;
		System.setProperty("https.protocols", "TLSv1");

		try {

			RunODRBizService lRunODRBizService = new RunODRBizService();
			String lReturnVal = lRunODRBizService.getToken(gLoginId, gPwd);
			Long lTokenId = 0L;

			try{
				lTokenId = Long.parseLong(lReturnVal);
				logger.error("lTokenId:"+lReturnVal);
				lUrlParameterString = lUrlParameterString+"&tokenid="+lTokenId;
				System.out.println("lUrlParameterString:"+lUrlParameterString);
			}catch(Exception e){
				throw new Exception("Error in token fetch : "+lReturnVal);
			}

			//Hit Data Service URL with token
			String[]lResponse = lRunODRBizService.runURL(gDomain, gURI_RunService, lUrlParameterString, "POST");
			int lResponseCode = Integer.parseInt(lResponse[0]);
			lURLOutput = lResponse[1];

			//Parse JSON Output
			if(lResponseCode==200){

				try {
					JSONParser lParser = new JSONParser();
					Object lJSONObj = lParser.parse(lURLOutput);
				//	logger.error("lURLOutput:"+lURLOutput);
				//	logger.error("lJSONObj:"+lJSONObj);
					JSONArray lRecordArray = (JSONArray)(lJSONObj);
					System.out.println("lRecordArray.size():"+lRecordArray.size());
					for(int ctr=0;ctr<5;ctr++){
						JSONObject lRowElement = (JSONObject)lRecordArray.get(ctr);
						System.out.println(lRowElement.get("Site"));
					}
				
				/*	for(int ctr=0;ctr<lRecordArray.size();ctr++){
						JSONObject lRowElement = (JSONObject)lRecordArray.get(ctr);
						System.out.println(lRowElement.get("Site"));
					}*/
				} catch (ParseException e) {
					System.out.println(lURLOutput);
					e.printStackTrace();
				}
			}

		} catch(ParseException pe){
			pe.printStackTrace();
		}catch(Exception exp) {
			System.out.println(exp.getMessage());
			exp.printStackTrace();
		}

	}


	private String getToken(String pUserId, String pPassword) throws Exception{

		String lUrlParameterString = "usrloginid="+pUserId+"&usrpassword="+pPassword;
		String lURLOutput=null;
		String lReturnResponse;

		try {

			//Hit Token Service URL
			String[]lResponse = runURL(gDomain, gURI_GetToken, lUrlParameterString, "POST");
			int lResponseCode = Integer.parseInt(lResponse[0]);
			lURLOutput = lResponse[1];

			//Parse Output XML
			DocumentBuilder lDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource lStreamSource = new InputSource();
			lStreamSource.setCharacterStream(new StringReader(lURLOutput));

			Document lXMLDoc = lDocBuilder.parse(lStreamSource);
			NodeList lNodes = lXMLDoc.getElementsByTagName("RESULT");
			NamedNodeMap lAttribMap = lNodes.item(0).getAttributes();
			if(lResponseCode==200){
			   	lReturnResponse = lAttribMap.getNamedItem("TOKENID").getNodeValue();
			}else{
			   	lReturnResponse = lAttribMap.getNamedItem("MSG").getNodeValue();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} 

		return lReturnResponse;
	}

	private String[] runURL(String pDomain, String pURI, String pParamString, String pMethod) throws Exception {

		URLConnection lConnection = null;
		String lUrl = pDomain+pURI;
		String lURLOutput=null;
		BufferedReader lReaderObj=null;
		DataOutputStream lOpStream=null;
		StringBuilder lReturnResponse=new StringBuilder();;
		String[] lReturnVals = new String [2];

		try {

			URL lURLObj = new URL(lUrl);
			lConnection = lURLObj.openConnection(lTCSProxy);
			//lConnection = lURLObj.openConnection(Proxy.NO_PROXY); //Use the commented line in case system has direct internet access

			//Create Request Header
			((HttpsURLConnection) lConnection).setRequestMethod(pMethod);
			lConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			lConnection.setRequestProperty("Content-Length", "" + pParamString.length());
			lConnection.setDoOutput(true);

			//Hit URL with Params
			lOpStream = new DataOutputStream(lConnection.getOutputStream());
			lOpStream.writeBytes(pParamString.toString());
			lOpStream.flush();

			//FetchResponse
			lReturnVals[0] = String.valueOf(((HttpsURLConnection)lConnection).getResponseCode());
			lReaderObj = new BufferedReader(new InputStreamReader(lConnection.getInputStream()));
			while ((lURLOutput = lReaderObj.readLine()) != null) {
				lReturnResponse.append(lURLOutput);
			}

			lReturnVals[1] = lReturnResponse.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new Exception("URL String not formed properly : "+lUrl);

		} catch (ProtocolException e) {
			e.printStackTrace();
			throw new Exception("Protocol not supported by URL : "+lUrl);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Error in fetching data : "+lUrl);
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("System/Network Error in fetching data : "+lUrl);
		}finally{

			if(lOpStream!=null){lOpStream.close();}
			if(lReaderObj!=null){lReaderObj.close();}
		}

		return lReturnVals;
	}
}
 
