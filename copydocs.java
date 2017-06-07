package com.tcs.EformsTesting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.*;
import sun.org.mozilla.javascript.internal.json.JsonParser;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.parser.*;



import org.json.simple.parser.JSONParser;

import java.lang.String;
import java.lang.Object;
import java.io.*;
import java.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


import com.tcs.cms.common.util.*;


public class copydocs {
	private static final Log LOGGER = LogFactory.getLog(copydocs.class);

	public String myjsonoutput(String orgId,String stuId,Double Amount,String FeeType)
	{
		String response="";
		String response2="";
		String flag="";
	      String len="";
	      String opt="";
	      JSONObject res_jsonobj=new JSONObject();
	      JSONArray res_jsonarray=new JSONArray();
	      
	      
	      JSONParser parser=new JSONParser();
	      try
	      {
	    	//  response= ccda.getFeeCollectionXMLForOnlineCollectionAPI(orgId,"9520", "1", stuId,"1");
	    	//  response= "{fee:{Feedetail:[{name:College Fee,amount:6300.0},{name:Development Fee,amount:0.0}]}}";
	    	  response= "{\"fee\":{\"Feedetail\":[{\"name\":\"College Fee\",\"amount\":6300.0},{\"name\":\"Development Fee\",\"amount\":0.0}]}}";
	    	  response2="[\"1\"]";
	    //	  response="[0,{\"fee\":{\"Feedetail\":[{\"name\":\"College Fee\",\"amount\":0.0},{\"name\":\"Development Fee\",\"amount\":0.0}]}}]";
	    	  LOGGER.error("response of getFeeCollectionXMLForOnlineCollectionAPI :::"+response);
	    	  LOGGER.error("response2 of getFeeCollectionXMLForOnlineCollectionAPI :::"+response2);
	    	  
	    	  
	    	  res_jsonobj= new JSONObject(response);  	 
	    	  String FeeString=res_jsonobj.getString("fee");
	    	  JSONObject jObject = new JSONObject(FeeString);
		    res_jsonarray = jObject.getJSONArray("Feedetail");
		    JSONArray res2_jsonarray=new JSONArray(response2);
		    opt=res2_jsonarray.getString(0);
		    
	    	System.out.println("Jsonobject:"+res_jsonobj);
	    	System.out.println("Jsonobject in string:"+res_jsonobj.getString("fee"));
	    	System.out.println("String of array:"+FeeString);
	    	System.out.println("Jarray:"+res_jsonarray);
	    	System.out.println("Output of response 2 in string:"+opt);
	    	
	    	for (int i = 0; i < res_jsonarray.length(); i++) 
	    	{
	    	        JSONObject jObj = res_jsonarray.getJSONObject(i);
	    	        String name =jObj.getString("name");
	    	        Double Amnt =jObj.getDouble("amount");
	    	        
	    	        if(name.equalsIgnoreCase(FeeType))
	    	        {
	    	        	if(opt.equalsIgnoreCase("0"))
	    	        	{
	    	        		System.out.println("Value of opt:"+opt);
	    	        		if(Amount.equals(Amnt))
		    	        	{
		    	        		flag="true";
		    	        	}
		    	        	else
		    	        		flag="false";
	    	        	}
	    	        	if(opt.equalsIgnoreCase("1"))
	    	        	{System.out.println("Value of opt:"+opt);
	    	        		if((Amount < Amnt) || Amount.equals(Amnt))
	    	        		{
	    	        			flag="true";
	    	        		}
	    	        		else
	    	        			flag="false";
	    	        		
	    	        	}
	    	        	
	    	        }
	    	        LOGGER.error(i + " name : " + name);
	    	        LOGGER.error(i + " amount : " + Amnt);
	      	}
	    	
	    	  
	      }
	      catch(Exception m)
	      {
	    	  LOGGER.error("exception occured in getFeeCollectionXMLforOnlineCollectionAPI :"+m);
	    	  m.printStackTrace();
	      }
	      LOGGER.error("Flag:" + flag);
	      return flag;
	}
	public void copyDocsApi()
	{
		Connection con=null;
	    PreparedStatement psSelect = null;
	    ResultSet rsSelect = null;
	    String app_seq_no="";
	    StringBuffer Filepath1=new StringBuffer("");
	    StringBuffer Filepath2=new StringBuffer("");
	    StringBuffer Filepath3=new StringBuffer("");
	    StringBuffer Filepath4=new StringBuffer("");
	    StringBuffer Filepath5=new StringBuffer("");
	    StringBuffer Filepath6=new StringBuffer("");
	    StringBuffer Filepath7=new StringBuffer("");
	    StringBuffer Filepath8=new StringBuffer("");
	    StringBuffer Filepath9=new StringBuffer("");
	    
	    try
		{
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_844","root", "password");
		}
		catch(Exception a)
		{
			LOGGER.error("not able to connect local:"+a);
		}
	    try
	    {
	      StringBuffer query = new StringBuffer("");
	      query.append("select app_seq_no,SUBSTRING_INDEX(txtPhotoPath,'/', -3) AS PHOTOPATH,SUBSTRING_INDEX(txtSignaturePath,'/', -3) AS SIGNPATH,");
	      query.append("SUBSTRING_INDEX(txtCourseSyllabus,'/', -3) AS coursylaPATH,SUBSTRING_INDEX(txtGradeSheets,'/', -3) AS gradecertPATH,");
	      query.append("SUBSTRING_INDEX(txtCategoryCertificate,'/', -3) AS catcertPATH,SUBSTRING_INDEX(txtDisabilityCertificate,'/', -3) AS discertPATH,");
	      query.append("SUBSTRING_INDEX(txtNationalityCertificate,'/', -3) AS natcertPATH,SUBSTRING_INDEX(txtOBCCertificate,'/', -3) AS obscertPATH,");
	      query.append("SUBSTRING_INDEX(txtBirthCertificate,'/', -3) AS birthcertPATH from `app_form3580_data` where rowstate > -1 ;");
	      psSelect = con.prepareStatement(query.toString());
	      rsSelect = psSelect.executeQuery();
	      
	      if (rsSelect != null)
	      {
	    	  while (rsSelect.next())
		        {
	    		  LOGGER.error("Query Executed");
	    		  app_seq_no = rsSelect.getString(1);
	    		  Filepath1=new StringBuffer("");
	    		  Filepath2=new StringBuffer("");
	    		  Filepath3=new StringBuffer("");
	    		  Filepath4=new StringBuffer("");
	    		  Filepath5=new StringBuffer("");	    		  
	    		  Filepath6=new StringBuffer("");
	    		  Filepath7=new StringBuffer("");
	    		  Filepath8=new StringBuffer("");
	    		  Filepath9=new StringBuffer("");
	    		  
	    		  Filepath1.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath2.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath3.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath4.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath5.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath6.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath7.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath8.append("D:/TEST/IIT Input/InputFolderWise/844/");
	    		  Filepath9.append("D:/TEST/IIT Input/InputFolderWise/844/");
    		  
		          Filepath1.append(rsSelect.getString(2));
		          Filepath2.append(rsSelect.getString(3));
		          Filepath3.append(rsSelect.getString(4));
		          Filepath4.append(rsSelect.getString(5));
		          Filepath5.append(rsSelect.getString(6));
		          Filepath6.append(rsSelect.getString(7));
		          Filepath7.append(rsSelect.getString(8));
		          Filepath8.append(rsSelect.getString(9));
		          Filepath9.append(rsSelect.getString(10));
		          
		          
		         // Filepath2=rsSelect.getString(3);
		          LOGGER.error("app_Seq:"+app_seq_no+"::Filepath1::"+Filepath1+"::filepath2:"+Filepath2);
		          LOGGER.error("Filepath3:"+Filepath3+"::Filepath4::"+Filepath4+"::filepath5:"+Filepath5);
		          LOGGER.error("Filepath6:"+Filepath6+"::Filepath7::"+Filepath7+"::filepath8:"+Filepath8 +"::Filepath9::"+Filepath9);
		          
			        File dir = new File("D:/TESTCOPY/IIT output via code new/"+app_seq_no);
			  		dir.mkdirs();
			  		LOGGER.error("Folder Created for application number-"+app_seq_no);
			  		File source1 = new File(Filepath1.toString());
			  		File source2 = new File(Filepath2.toString());
			  		File source3 = new File(Filepath3.toString());
			  		File source4 = new File(Filepath4.toString());
			  		File source5 = new File(Filepath5.toString());
			  		File source6 = new File(Filepath6.toString());
			  		File source7 = new File(Filepath7.toString());
			  		File source8 = new File(Filepath8.toString());
			  		File source9 = new File(Filepath9.toString());
			  		File dest = new File("D:/TESTCOPY/IIT output via code new/"+app_seq_no);
			  		try 
			  		{
			  			if(rsSelect.getString(2) != null && !rsSelect.getString(2).isEmpty())
			  				   FileUtils.copyFileToDirectory(source1, dest);
			  			if(rsSelect.getString(3) != null && !rsSelect.getString(3).isEmpty())
			  					FileUtils.copyFileToDirectory(source2, dest);
			  			if(rsSelect.getString(4) != null && !rsSelect.getString(4).isEmpty())
			  					FileUtils.copyFileToDirectory(source3, dest);
			  			if(rsSelect.getString(5) != null && !rsSelect.getString(5).isEmpty())
			  					FileUtils.copyFileToDirectory(source4, dest);
			  			if(rsSelect.getString(6) != null && !rsSelect.getString(6).isEmpty())
			  					FileUtils.copyFileToDirectory(source5, dest);
			  			if(rsSelect.getString(7) != null && !rsSelect.getString(7).isEmpty())
			  					FileUtils.copyFileToDirectory(source6, dest);
			  			if(rsSelect.getString(8) != null && !rsSelect.getString(8).isEmpty())
			  					FileUtils.copyFileToDirectory(source7, dest);
			  			if(rsSelect.getString(9) != null && !rsSelect.getString(9).isEmpty())
			  					FileUtils.copyFileToDirectory(source8, dest);
			  			if(rsSelect.getString(10) != null && !rsSelect.getString(10).isEmpty())
			  					FileUtils.copyFileToDirectory(source9, dest);
			  			
			  		   LOGGER.error("File Copied for application no.-"+app_seq_no);
			  		} 
			  		catch (IOException e) 
			  		{
			  		   LOGGER.error("Error occured in copying...."+e);
			  		   e.printStackTrace();
			  		}
			  		
		        }
	      }
	    }
	    catch(Exception e)
	    {
	    	LOGGER.error("Error in query:"+e);
	    }
	    finally
  		{
  			try
  			{
  				con.close();
  			}
  			catch (Exception e)
  			{
  				LOGGER.error("Not able to close connection"+e);
  			}
  			
  		}
	    
		
		
		
		
	}
}
