package com.tcs.EformsTesting;


	
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.util.*;
	import java.lang.Long;
	import org.apache.commons.logging.Log;
	import org.apache.commons.logging.LogFactory;
	import java.io.File;
	import java.io.IOException;
	import java.io.StringReader;
	import java.io.StringWriter;
	import java.io.Writer;
	//import java.lang.reflect.Method;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.ResultSetMetaData;
	import java.sql.SQLException;
	import java.text.CharacterIterator;
	import java.text.DateFormat;
	import java.text.SimpleDateFormat;
	import java.text.StringCharacterIterator;
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.LinkedHashMap;
	import java.util.List;
	import java.util.Vector;
	import java.net.URLEncoder;
	import java.util.Timer;
	import java.util.TimeZone;
	import java.util.TimerTask;
	

	import javax.servlet.http.HttpServletRequest;
	import javax.xml.parsers.DocumentBuilder;
	import javax.xml.parsers.DocumentBuilderFactory;
	import javax.xml.parsers.ParserConfigurationException;
	import javax.xml.transform.OutputKeys;
	import javax.xml.transform.Transformer;
	import javax.xml.transform.TransformerFactory;
	import javax.xml.transform.dom.DOMSource;
	import javax.xml.transform.stream.StreamResult;

	import org.apache.commons.logging.Log;
	import org.apache.commons.logging.LogFactory;
	import org.json.simple.JSONArray;
	import org.json.simple.JSONObject;
import org.mortbay.jetty.servlet.PathMap.Entry;
	import org.w3c.dom.CharacterData;
	import org.w3c.dom.Document;
	import org.w3c.dom.Element;
	import org.w3c.dom.Node;
	import org.w3c.dom.NodeList;
	import org.xml.sax.InputSource;
	import org.xml.sax.SAXException;
	//import ExceptionMonitoring.ExceptionMonitor;
  	import com.tcs.base.helpers.cache.MultitenantCache;
	import com.tcs.beans.integrationvo.ApplicationDataVo;
	import com.tcs.beans.integrationvo.HallTicketDataVo;
	import com.tcs.beans.integrationvo.LoginDataVo;
	import com.tcs.beans.integrationvo.OnlineFormMasterDataVo;
	import com.tcs.beans.integrationvo.OnlineFormMetaDataVo;
	import com.tcs.constants.EFormsConstants;
	import com.tcs.data.DBManager;
	import com.tcs.data.DataSet;
	import com.tcs.exception.DataException;
import com.tcs.jsp.beans.OnlineApplicationController;



	
	
//public class Eforms_Auto extends TimerTask{
	public class Eforms_Auto{
		
		private static Log logger =LogFactory.getLog(Eforms_Auto.class);
		
		public void run()
		{
			new ViewComedK_CMSDetails().getDataforCOMEDK("BatchList","0");
			new ViewComedK_CMSDetails().getDataforCOMEDK("SeatList","0");
		//	new ViewComed_2().getDataforCOMEDK("SeatList","0");
		}
		
		public static void main(String args[])
		{
			
			try{
	//		new Efroms_Auto_json_local().getEforms_Auto_Details("5","935","1467","1");
			//new GetdataAjaxcall().getDatafromDB("orgid@@masterform_id@@dcnid@@payment_mode@@payment_status","3");
		//	new CMSFeeCollectionPostPaymentUpdate_local().updateAfterPayment("4","744","1107","Akshay","S");
		//	new ServerCurrentDate().getServerDate();
		//	new SMS_OTP().getOTP("0","gtic-ggdsd32", "shamim12", "9167030505","GGDSDC", "gtic-ggdsd32", "gtic-ggdsd32", "TheOTPforconfirmingthisis");
		//	new HttpURLConnectionExample().sendGet();
		//		new testURL().excutePost("https://www.google.co.in/", "");
		//		new test().gettestUrl("https://www.google.co.in", "");
		//	new test().getOTP("gtic-ggdsd32","shamim12","9167030505","gtic-ggdsd32","gtic-ggdsd32","The OTP for confirming this is");
			//submitlogicforsinghania A = new submitlogicforsinghania();
			//A.ChkDuplicate("1000,1001,1002,760,,76,end","1","Y,,Y,,,,end");
		//		new	getGGDSDDatatest().getGoswamiData("getScratchCardValue","8674545542319810");
		//	new getDataBMLPDM().getBMLPDMdata("1129","11");
		//		new CMSFeeCollectionPostPaymentUpdate_local().updateAfterPayment("422-1", "", "", payment_gateway_name, payment_status);
		//		new copydocs().copyDocsApi();
		//		new copydocs().myjsonoutput("945","1",6300.0,"College Fee");
		//		new Singhania_local().executeSchedular();
				//List strFieldList = Arrays.asList("sup1", "sup2", "site_id");
			//	String[] str_array={"a","b","c","d"c,"e","f","i"};
			//	List<String> strFieldList = new ArrayList<String>();
			//	List<String> strFieldList = Arrays.asList("sup1", "sup2", "sup3");
			//	strFieldList.add("sup1");
			//	strFieldList.add("sup2");
			//	strFieldList.add("site_id");
			//	new SequenceIdGenerator().timesNewSeqGenerator("1" ,"Offine",str_array, strFieldList, "176", "144");
			//	new ViewComedK_CMSDetails().getDataforCOMEDK("BatchList","0");
				
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("ques5","sunakshi");
				map.put("ques1","akshay");
				map.put("ques4","sudeep");
				map.put("ques3", "mayur");
				map.put("ques2","sanket");
				
				
				System.out.println("From for each");
				for(String key : map.keySet())
				{
					
					System.out.println("::Key ="+key+":::Value ="+map.get(key));
				}
				System.out.println("From Iterator");
				Iterator<String> itr = map.keySet().iterator();
				while(itr.hasNext())
				{
					String Key1=itr.next();
					System.out.println("::Key ="+Key1+":::Value ="+map.get(Key1));
				}
				System.out.println("Size of Hashmap:"+map.size()+": to clear hasmap use map.clear()");
				System.out.println("Map contains value=akshay?" +map.containsValue("akshay"));
				System.out.println("Synchronize map:" +Collections.synchronizedMap(map));
				System.out.println("Map contains key=2?" +map.containsKey(2));
				System.out.println("Now Sorted Hashmap in ascending");
				
				TreeMap tm = new TreeMap(map);
				Iterator<String> itrtm = tm.keySet().iterator();
				while(itrtm.hasNext())
				{
					String Key1=itrtm.next();
					System.out.println("::Key ="+Key1+":::Value ="+tm.get(Key1));
				}
				Map Submap =tm.subMap("ques2", "ques9");
				Submap.put("ques6","Saloni");
				tm.put("ques9", "Ritu");
				System.out.println("Now Sorted Hashmap in Descending");
				Iterator<String> itrtmd = tm.descendingKeySet().iterator();
				while(itrtmd.hasNext())
				{
					String Key1=itrtmd.next();
					System.out.println("::Key ="+Key1+":::Value ="+tm.get(Key1));
				}
				System.out.println("Now Sorted Submap");
				Iterator<String> itrtmds = Submap.keySet().iterator();
				while(itrtmds.hasNext())
				{
					String Key1=itrtmds.next();
					System.out.println("::Key ="+Key1+":::Value ="+Submap.get(Key1));
				}
				System.out.println("From Entry Set");
				/*Set<Map.Entry<Integer,String>> es = map.entrySet();*/
				/*for(Entry e : es)
				{
					
					System.out.println("::Key ="+e.getKey()+":::Value ="+e.getValue());
				}*/
					
					//getDataforCOMEDK("SeatList","0");
		//		 TimerTask tasknew = new Eforms_Auto();
		//		Timer timer = new Timer(true);
			// scheduling the task at interval
		//		timer.scheduleAtFixedRate(tasknew, 0, 60*1000);   
				
		//		try 
		//		{
		  //          Thread.sleep(360000);
		  //      } catch (InterruptedException e) {
		   //         e.printStackTrace();
		    //    }
		  //      timer.cancel();
		//		new SchedfLogin_local().getlogdetail_sched();
				//new ChallengeAccepted().copyDocsApiComp();
				//new FileMove_mayur().FileMoveMayur();
			//	TestInterface test;
			//	System.out.println(TestInterface.testid);
			//	TestAbstract testabs;
		//		TestAbstract testab= new Eforms_Auto();
			//	private void setnames()
			//	{
			//		System.out.println("You are in setname:");
			//	}
		//		System.out.print(testab.getClass());
		//		new GetImage_test().captureScreen("imagetest");
		//		new SubmitLogic_test().ChkDuplicate("stuId", "105' or 1=1", "3865");
		//		new ReadGmail().readGmail();
		//		new PartialPay_Local().PartialPay("945");
		//		new copydocsBML().copyDocsApi();
		//		new KevempuLogic().getData("1", "1");
		//		new PostPaymentUpdateLadyDoak_local().updateAfterPaymentLadyDoak("202","1200","4370","PayU","S");
			}
			catch(Exception e)
			  {
				  logger.error("unable to fetch data");
				  logger.error(e);
			  }
			/*		String xmldata = "";
					JSONObject eformsautojsonobj=new JSONObject();
					JSONArray eformsautojsonarray=new JSONArray();
				//	private DBManager dbManager;
					//private HttpServletRequest request;
					
					
			
				logger.error("<<<<<<<<<<<<<<<<<   START   Eforms_Auto    >>>>>>>>>>>>>>>>>");
				
				  String userId = "1";
			      String organisation_id = "935"; 
			      String form_id = "1467";
			      String eformsAppId = "9518";
			      int noofmetadataoptions=5;
			    
			      
			      DataSet dataSet = null;
			      Writer writer = null;
			      writer = new java.io.StringWriter();
			      XmlWriter xmlwriter = new XmlWriter(writer);
			      String strDependentDataXML = "";
			    //  Vector rows = null;
			      
			      Connection con = null ; 
			      
			      try {
			    	  
			    	  
						xmlwriter.writeEntity(EFormsConstants.XML_RESPONSE);
						String query;
						query ="select * from app_form1467_data where user_id="+userId+" and masterform_id="+form_id+" and orgid="+organisation_id+" and rowstate>-1";
							Class.forName("com.mysql.jdbc.Driver");
					          con = DriverManager.getConnection("jdbc:mysql://172.17.219.157:3306/eforms_global","root", "password");
							  PreparedStatement ps;
							  ps = con.prepareStatement(query.toString());
							  System.out.println("Printing Prepared statement:"+ps);
							
							  try
							  {
										  ResultSet  rs=ps.executeQuery();
										  int j=1;
										  System.out.println("Printing Resultset:"+rs);
										  
										  while (rs.next())
										  {
												  String Dropdown=rs.getString("txtdropdown1");
												  System.out.println("Initially Printing Dropdown value :"+Dropdown);
												  ArrayList<String> Option = new ArrayList<String>();
												  												  
												 
												  for(int i=1;i<=noofmetadataoptions;i++ )
									              {				            	  
									            	 if (rs.getString("txtoption"+i) == null ||  rs.getString("txtoption"+i).equals("") )
									            	 {
									            		// dataForCollection.put(Long.parseLong("-1"),Double.parseDouble("-1"));
									            	 }
									            	 else
									            	 {
									            		 Option.add(rs.getString("txtoption"+i));
									            		 JSONObject eformsautojsonobjtwo=new JSONObject();
									            		 eformsautojsonobjtwo.put("Options",rs.getString("txtoption"+i));
									            		 eformsautojsonarray.add(eformsautojsonobjtwo);
									              	  }
									            	
									              } 
												  System.out.println("Printing Options values array : "+Option);
												  System.out.println("Printing Dropdown value :"+Dropdown);
										  }
							   }
							  catch(Exception e)
							  {
								  logger.error("unable to fetch data");
								  logger.error(e);
							  }
						
						
						
			      }
			      catch (Exception e)
			      {
			          logger.error(e);
			          try
			          {
			          
			            con.close();
			          }
			          catch (Exception ex)
			          {
			            logger.error(ex);
			          }
			       }
			      eformsautojsonobj.put("eformsautojsonobj:",eformsautojsonarray);
					logger.error("Eforms Automate object is " + eformsautojsonobj);
				//	return eformsautojsonobj.toString();


			     
				
	*/	}
}
