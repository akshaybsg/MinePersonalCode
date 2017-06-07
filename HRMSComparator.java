package com.tcs.EformsTesting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.json.simple.JSONArray;

public class HRMSComparator 
{
	
	//Integer orgId= Integer.parseInt();
	JSONArray Jsonarray=new JSONArray();
	Connection con = null;
	String formId;
	 
		
		 Class.forName("com.mysql.jdbc.Driver");
		 con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eforms_hrms","root", "password");
			PreparedStatement ps=null;
			ResultSet rs=null;
			HashMap<String,String> lMap=new HashMap<String,String>();
			try
			 {
					formId="4662";
				 	String query="select* from app_form"+formId+"_data;";
				 	ps = con.prepareStatement(query.toString());
				    try
			        {
			        	rs=ps.executeQuery();
			        	System.out.println("execute query");
			        	while(rs.next())
			        	{
			        		
							lMap.put("HRMSState", rs.getString("txtHRMSState")==null? "":rs.getString("txtHRMSState").trim());
			        	
			        	}
			        	
			        }
			        catch(Exception e)
			        {
			       // 	logger.error(e);
			        }
			 }
			 catch(Exception ex)
			 {
				 //logger.error("Exception Occured in getting data in PartialPay"+ex);	
			 }
	 }

}
