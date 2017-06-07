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
import java.net.URLEncoder ;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Set;
import java.util.Iterator;
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



public class ViewComedK_CMSDetails {

//	private String gLoginId = "batchseat@erafi.com";
//	private String gPwd = "iON@2016";
//	private static String gServiceKey = "2KWT8Fdd32l4hIvFJyvqRQ%3D%3D";
//	private String gUniqueCodeBatch ="flQQvDh69GQrkLzqADphzg%3D%3D";
//	private String gUniqueCodeSeat ="6yIfHh91YxD0LXapGQNQMA%3D%3D";
//	private String gDomain = "https://www.tcsion.com/" ;
	
//	private String gLoginId = "qa_batchseat@erafi.com";
//	private String gPwd = "Smb@2016";
//	private static String gServiceKey = "2KWT8Fdd32l4hIvFJyvqRQ%3D%3D";
//	private String gUniqueCodeBatch ="EU0t%2Bj6yHiM7jg5SYydrLQ%3D%3D";
//	private String gUniqueCodeSeat ="gSVTxY2v3iE7zJl3oZemKw%3D%3D";
//	private String gDomain = "https://qahf.tcsion.com/" ;	
	
	
	private static String gLoginId=null;
	private static String gPwd=null;
	private static String gServiceKey=null;
	private static String uniquecodebatch=null;
	private static String uniquecodeseat=null;
	private static String gDomain=null;
	
	private static String gURI_GetToken = "iONBizServices/Authenticate";
	private static String gURI_RunService = "iONBizServices/iONWebService";
	
	private static HashMap<String,ArrayList<Timestamp>> TokenMapTimestamp  = new HashMap<String,ArrayList<Timestamp>>();
	public static Integer count=0;
	private Date CurDate = new Date();
	private Timestamp timeszero=new Timestamp(CurDate.getTime());
	private ArrayList<Timestamp> ts= new ArrayList<Timestamp>();
	
	private static Proxy lTCSProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.tcs.com", 8080));
	
	public static final Log logger = LogFactory.getLog(ViewComedK_CMSDetails.class);
	
	public String getDataforCOMEDK(String BatchName,String Domain)
	{
	/*	if((gLoginId.equalsIgnoreCase("") || gLoginId.isEmpty()==true || gLoginId==(null)) || (gPwd.equalsIgnoreCase("") || gPwd.isEmpty()==true) 
				|| (gServiceKey.equalsIgnoreCase("") || gServiceKey.isEmpty()==true) || (gUniqueCode.equalsIgnoreCase("") || gUniqueCode.isEmpty()==true)
				|| (gDomain.equalsIgnoreCase("") || gDomain.isEmpty()==true))*/
		if((uniquecodeseat == null) || (uniquecodebatch == null))
		{
			getTableDetails("1022","4977",BatchName);
		}
		else
		{
			logger.error("Static var of login details called.");
		}
		
		
		logger.error("Inside ViewComedK_CMSDetails  :::::  getDataforCOMEDK");
		System.out.println("counter:"+count);
		System.out.println("cur time in minutes:"+getMindiff(timeszero));
		JSONArray Jsonarray=new JSONArray();
		String unqcode=null;
		if(BatchName.equalsIgnoreCase("BatchList") && BatchName !=null)
		{
			unqcode=uniquecodebatch;
		}
		if(BatchName.equalsIgnoreCase("SeatList") && BatchName !=null)
		{
			unqcode=uniquecodeseat;
		}	
		String lUrlParameterString = "servicekey="+gServiceKey+"&s="+unqcode;
	/*	System.out.println(gLoginId);
		System.out.println(gPwd);
		System.out.println(gUniqueCode);
		System.out.println(gDomain);
		System.out.println(gURI_RunService);
		System.out.println(lUrlParameterString);*/
		String lURLOutput=null;
		System.setProperty("https.protocols", "TLSv1");
		
		try {
			
			ViewComedK_CMSDetails lViewComedK_CMSDetails = new ViewComedK_CMSDetails();
			Long lTokenId = 0L;
			String lReturnVal="";
			System.out.println(TokenMapTimestamp.size());
//--------------Check for old token-------------------------------------------------------------------------------
		if((TokenMapTimestamp.size()== 0))
		{
			TokenMapTimestamp.clear();
			lReturnVal = lViewComedK_CMSDetails.getToken(gLoginId, gPwd , gDomain);
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
			System.out.println("First 1:::"+getMindiff(timesone));
			System.out.println("First 2:::"+getMindiff(timestwo));
	
			if ((getMindiff(timesone) >100) ||  (getMindiff(timestwo)>4))
			{
				TokenMapTimestamp.clear();
				lReturnVal = lViewComedK_CMSDetails.getToken(gLoginId, gPwd , gDomain);
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
						System.out.println("lTokenId:"+lReturnVal);
						System.out.println("ts:::ArrayList:::"+ts);
						System.out.println("TokenMapTimestamp:::"+TokenMapTimestamp);
						lUrlParameterString = lUrlParameterString+"&tokenid="+lTokenId;
						//String  batchnametopass="ALLIANCE COLLEGE OF ENGINEERING AND DESIGN. ALLIANCE UNIVERSITY";
						//String q = "<DYNAMIC_FILTERS ENTITYTYPEID='10120179'><FILTERS><FIELD ID='204210261' VALUE = '"+batchnametopass+"' BLANK_DATA_FLAG='0' IS_NEGATIVEFILTER='0' DICE='undefined'  IS_DATE_MASTER='0' DATE_MASTER_FIELD='' ></FIELD></FILTERS></DYNAMIC_FILTERS>";
						//String url = "http://example.com/query?q=" + URLEncoder.encode(q, "UTF-8"); //https://docs.oracle.com/javase/7/docs/api/java/net/URLEncoder.html
						
						
					}
					catch(Exception e)
					{
						throw new Exception("Error in token fetch : "+lReturnVal);
					}
					System.out.println(gLoginId);
					System.out.println(gPwd);
					System.out.println(uniquecodebatch);
					System.out.println(uniquecodeseat);
					System.out.println(gDomain);
					System.out.println(gURI_RunService);
					System.out.println("lUrlParameterString:"+lUrlParameterString);
					//Hit Data Service URL with token
					String[]lResponse = lViewComedK_CMSDetails.runURL(gDomain, gURI_RunService, lUrlParameterString, "POST");
					System.out.println("Response Array::"+ lResponse);
					for (int i=0; i <lResponse.length; i++)
					{
						System.out.println("Response["+i+"]= "+lResponse[i]);
					}
					int lResponseCode = Integer.parseInt(lResponse[0]);
					lURLOutput = lResponse[1];

			//Parse JSON Output
			if(lResponseCode==200){

				try 
				{
					JSONParser lParser = new JSONParser();
					Object lJSONObj = lParser.parse(lURLOutput);
					JSONArray lRecordArray = (JSONArray)(lJSONObj);
					Jsonarray=lRecordArray;
					System.out.println("lRecordArray.size():"+lRecordArray.size());
					System.out.println("lRecordArray:"+lRecordArray);
					for(int ctr=0;ctr<lRecordArray.size();ctr++)
					{
						JSONObject lRowElement = (JSONObject)lRecordArray.get(ctr);
						Object lRowElemantObject = lRecordArray.get(ctr);
					}
				
				} 
				catch (ParseException e) 
				{
					System.out.println(lURLOutput);
					e.printStackTrace();
				}
			}

		}
		catch(ParseException pe)
		{
			System.out.println(pe.getMessage());
			pe.printStackTrace();
		}
		catch(Exception exp) 
		{
			System.out.println(exp.getMessage());
			exp.printStackTrace();
		}
		System.out.println("Jsonarray:"+Jsonarray);
		return Jsonarray.toString();
		
	}

	private String getToken(String pUserId, String pPassword, String gDomain) throws Exception{

		String lUrlParameterString = "usrloginid="+pUserId+"&usrpassword="+pPassword;
		String lURLOutput=null;
		String lReturnResponse;
		System.out.println(":Inside get Token:");
	
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
			if(lResponseCode==200)
			{
			   	lReturnResponse = lAttribMap.getNamedItem("TOKENID").getNodeValue();
			}
			else
			{
			   	lReturnResponse = lAttribMap.getNamedItem("MSG").getNodeValue();
			}
			System.out.println(":lReturnResponse:"+lReturnResponse);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} 

		return lReturnResponse;
	}

	private synchronized String[] runURL(String pDomain, String pURI, String pParamString, String pMethod) throws Exception 
	{
		System.out.println("Inside getRun");
		URLConnection lConnection = null;
		String lUrl = pDomain+pURI;
		String lURLOutput=null;
		BufferedReader lReaderObj=null;
		DataOutputStream lOpStream=null;
		StringBuilder lReturnResponse=new StringBuilder();;
		String[] lReturnVals = new String [2];
		System.out.println("lUrl = "+lUrl);
		System.out.println("pParamString = "+pParamString);
		/*if(pURI.equalsIgnoreCase("iONBizServices/iONWebService"))
		{
		//	String  batchnametopass="A.C.S. COLLEGE OF ENGINEERING";
			String  batchnametopass="57";
			batchnametopass=replaceSpaceInString(batchnametopass);
			System.out.println(batchnametopass);
			String filterDataXML = "<DYNAMIC_FILTERS ENTITYTYPEID='10120179'><FILTERS><FIELD ID='204210261' VALUE='"+batchnametopass+"' BLANK_DATA_FLAG='0' IS_NEGATIVEFILTER='0' DICE='undefined'  IS_DATE_MASTER='0' DATE_MASTER_FIELD='' ></FIELD></FILTERS></DYNAMIC_FILTERS>";
			System.out.println(lUrl);
			System.out.println(pParamString);
			pParamString=pParamString+"&filterDataXML="+URLEncoder.encode(filterDataXML, "UTF-8");
			System.out.println("After Concat::"+pParamString);
		}
		if(pURI.equalsIgnoreCase("iONBizServices/iONWebService"))
		{
			
			Iterator iter = TokenMapTimestamp.entrySet().iterator();
	        if (TokenMapTimestamp.containsKey("lTokenId"))
	        {   
	            Map.Entry mEntry = (Map.Entry) iter.next();
	            ts.set(1,timeszero);
	            TokenMapTimestamp.put("lTokenId",ts);
	            System.out.println(mEntry.getKey() + " : " + mEntry.getValue());
	        }
					   
		}*/
		try {

			URL lURLObj = new URL(lUrl);
			lConnection = lURLObj.openConnection(lTCSProxy);
			
		//	lConnection = lURLObj.openConnection(Proxy.NO_PROXY); //Use the commented line in case system has direct internet access

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
		logger.error("Inside get table details.");
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
					uniquecodebatch = rs.getString("txtUniqueCodeBatch")==null? "":rs.getString("txtUniqueCodeBatch").trim();
				}
				if(BatchName.equalsIgnoreCase("SeatList"))
				{
					uniquecodeseat = rs.getString("txtUniqueCodeSeat")==null? "":rs.getString("txtUniqueCodeSeat").trim();
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
	public String replaceSpaceInString(String s){
	    int i;
	    for (i=0;i<s.length();i++){
	          if (s.charAt(i)==(int)32){
	            s=s.substring(0, i)+"%20"+s.substring(i+1, s.length());
	            i=i+2;              
	            }
	    }
	    return s;
	    }
	public long getMindiff(Timestamp v)
	{
		Date CurDate1 = new Date();
		Timestamp timeszero1=new Timestamp(CurDate1.getTime());
		
			long milliseconds1 = v.getTime();
			long milliseconds2 = timeszero1.getTime();
			long diff = milliseconds2 - milliseconds1;
			long diffMinutes = diff / (60 * 1000);
		  
		return diffMinutes;
	}
		
}

