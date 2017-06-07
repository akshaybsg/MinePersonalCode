package com.tcs.EformsTesting;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.net.URLEncoder ;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource; 

import com.tcs.EForms.DBManager;

public class ViewComed_2 {


	private String gLoginId;
	private String gPwd;
	private String gServiceKey;
	private String gUniqueCode;
	private String gDomain;
	
	private static String gURI_GetToken = "iONBizServices/Authenticate";
	private static String gURI_RunService = "iONBizServices/iONWebService";
	
	private static HashMap<String,ArrayList<Timestamp>> TokenMapTimestamp  = new HashMap<String,ArrayList<Timestamp>>();
	public static Integer count=0;
	private Date CurDate = new Date();
	private Timestamp timeszero=new Timestamp(CurDate.getTime());
	private ArrayList<Timestamp> ts= new ArrayList<Timestamp>();
	
	private static Proxy lTCSProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.tcs.com", 8080));
	
	public static final Log logger = LogFactory.getLog(ViewComed_2.class);
	
	public String getDataforCOMEDK(String BatchName,String Domain)
	{
		getTableDetails("1022","4977",BatchName);
		
		logger.error("Inside ViewComedK_CMSDetails  :::::  getDataforCOMEDK");
		JSONArray Jsonarray=new JSONArray();
		
		String lUrlParameterString = "servicekey="+gServiceKey+"&s="+gUniqueCode;
		String lURLOutput=null;
		System.setProperty("https.protocols", "TLSv1");

		
		try {
			
			ViewComed_2 lViewComed_2 = new ViewComed_2();
			Long lTokenId = 0L;
			String lReturnVal="";
			logger.error(TokenMapTimestamp.size());
//--------------Check for old token-------------------------------------------------------------------------------
		if((TokenMapTimestamp.size()== 0))
		{
			TokenMapTimestamp.clear();
			lReturnVal = lViewComed_2.getToken(gLoginId, gPwd , gDomain);
			ArrayList<Timestamp> listts= new ArrayList<Timestamp>();
		 	listts.add(0,timeszero);
		   	listts.add(1,timeszero);
		   	ts.addAll(listts);
		   	TokenMapTimestamp.put(lReturnVal,listts);
		}
		else 
		{
			Timestamp timesone=null;
			Timestamp timestwo=null;
			ArrayList<Timestamp> valtimelist = new ArrayList<Timestamp>();
			String val=null;
			for ( String key : TokenMapTimestamp.keySet()) 
			{
			    val=key;
			}
			valtimelist=TokenMapTimestamp.get(val);
			timesone=valtimelist.get(0);
			timestwo=valtimelist.get(1);
			logger.error("First 1:::"+getMindiff(timesone));
			logger.error("First 2:::"+getMindiff(timestwo));
	
			if ((getMindiff(timesone) >3) ||  (getMindiff(timestwo)>1))
			{
				TokenMapTimestamp.clear();
				lReturnVal = lViewComed_2.getToken(gLoginId, gPwd , gDomain);
				ArrayList<Timestamp> listts= new ArrayList<Timestamp>();
				listts.add(0,timeszero);
			 	listts.add(1,timeszero);
			 	ts.addAll(listts);
			   	TokenMapTimestamp.put(lReturnVal,listts);
			}
		
			else 
			{
				lReturnVal=val;
				TokenMapTimestamp.get(val).set(1, timeszero);
			}
		}
		
//--------------------------------------------------------------------------------------------------------------------------					

					try
					{
						lTokenId = Long.parseLong(lReturnVal);
						logger.error("lTokenId:"+lReturnVal);
						lUrlParameterString = lUrlParameterString+"&tokenid="+lTokenId;
										
					}
					catch(Exception e)
					{
						throw new Exception("Error in token fetch : "+lReturnVal);
					}
		
			//Hit Data Service URL with token
					String[]lResponse = lViewComed_2.runURL(gDomain, gURI_RunService, lUrlParameterString, "POST");
			
			
					int lResponseCode = Integer.parseInt(lResponse[0]);
					lURLOutput = lResponse[1];

			//Parse JSON Output
			if(lResponseCode==200){

				try {
					JSONParser lParser = new JSONParser();
					Object lJSONObj = lParser.parse(lURLOutput);
					JSONArray lRecordArray = (JSONArray)(lJSONObj);
					Jsonarray=lRecordArray;
					System.out.println("lRecordArray.size():"+lRecordArray.size());
					System.out.println("lRecordArray:"+lRecordArray);
			
					for(int ctr=0;ctr<lRecordArray.size();ctr++){
						JSONObject lRowElement = (JSONObject)lRecordArray.get(ctr);
						Object lRowElemantObject = lRecordArray.get(ctr);
					}
				
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
	//	System.out.println("Jsonarray:"+Jsonarray);
		return Jsonarray.toString();
		
	}

	private String getToken(String pUserId, String pPassword, String gDomain) throws Exception{

		String lUrlParameterString = "usrloginid="+pUserId+"&usrpassword="+pPassword;
		String lURLOutput=null;
		String lReturnResponse;
		logger.error(":Inside get Token:");
		count++;
		System.out.println(":counter:"+count);
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

	private String[] runURL(String pDomain, String pURI, String pParamString, String pMethod) throws Exception 
	{
		
		URLConnection lConnection = null;
		String lUrl = pDomain+pURI;
		String lURLOutput=null;
		BufferedReader lReaderObj=null;
		DataOutputStream lOpStream=null;
		StringBuilder lReturnResponse=new StringBuilder();;
		String[] lReturnVals = new String [2];
		

		try {

			URL lURLObj = new URL(lUrl);
			lConnection = lURLObj.openConnection(lTCSProxy); // Comment when deploy the jar
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
	public void getTableDetails(String orgId,String formId , String BatchName)
	{
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		DBManager dm = DBManager.init();
		Connection con = null ;
		try 
		{
		//	dm.doConnect(Integer.parseInt(orgId), "1", "9518");
			String query = "Select txtLoginID,txtPassword,txtServiceKey,txtUniqueCodeBatch,txtUniqueCodeSeat,txtDomain from app_form" + formId + "_data where rowstate>-1;";
		//	ps = dm.getPreparedStatement(query.toString());
		//    rs=ps.executeQuery();
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_1022","root", "password");
	        ps = con.prepareStatement(query);
	        rs=ps.executeQuery();
			while (rs.next()) 
			{
				gLoginId = rs.getString("txtLoginID")==null? "":rs.getString("txtLoginID").trim();
				gPwd = rs.getString("txtPassword")==null? "":rs.getString("txtPassword").trim();
				gServiceKey = rs.getString("txtServiceKey")==null? "":rs.getString("txtServiceKey").trim();
				if(BatchName.equalsIgnoreCase("BatchList"))
				{
					gUniqueCode = rs.getString("txtUniqueCodeBatch")==null? "":rs.getString("txtUniqueCodeBatch").trim();
				}
				if(BatchName.equalsIgnoreCase("SeatList"))
				{
					gUniqueCode = rs.getString("txtUniqueCodeSeat")==null? "":rs.getString("txtUniqueCodeSeat").trim();
				}		
				gDomain = rs.getString("txtDomain")==null? "":rs.getString("txtDomain").trim();
			}
			
		} 
		catch (Exception e)
		{
			logger.error("Exception in getTableDetails: " + e);
		} 
		
		finally
		{
			try {
					dm.doDisconnect();
					
				} 
			catch (Exception e1)
			{
				logger.error("Error while disconnecting " + e1.getMessage());
			}
		}
		

	}
	public long getMindiff(Timestamp v)
	{
		/*long now = System.currentTimeMillis(); // See note below
		long then = v.getTime();

		long minutes = then - now;*/
		Date CurDate1 = new Date();
		Timestamp timeszero1=new Timestamp(CurDate1.getTime());
		
			long milliseconds1 = v.getTime();
			long milliseconds2 = timeszero1.getTime();
			long diff = milliseconds2 - milliseconds1;
			long diffMinutes = diff / (60 * 1000);
		  
		return diffMinutes;
	}
	
}

