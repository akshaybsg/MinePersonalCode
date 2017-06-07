package com.tcs.EformsTesting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GetdataAjaxcall {

	private static Log logger =LogFactory.getLog(GetdataAjaxcall.class);
	public String getDatafromDB(String fields,String entityId)
	{
		String entity_id=entityId;
		String setoffields=fields;
		System.out.println("<----fields--->"+fields+"<---setoffields--->"+setoffields);
		String condition="";
		String[] FormField = setoffields.split("@@");
		int nooffields=FormField.length;
		System.out.println("FormFiledArray---"+FormField+"--length--"+nooffields+"setoffields.split"+setoffields.split("@@"));
		String column="";
		condition="entity_id="+entity_id+" and rowstate > -1";
		JSONObject Jsonobj=new JSONObject();
		JSONArray Jsonarray=new JSONArray();
		
				for (int i=0;i< nooffields;i++)
				{
				if (i==nooffields-1)
					column+=FormField[i];
				else
					column+=FormField[i]+",";
				}
				
		 Connection con = null;
		 try
			{
						
			 String query="select"+" "+column+" from app_form1467_data where "+condition+"";
			Class.forName("com.mysql.jdbc.Driver");
	        con = DriverManager.getConnection("jdbc:mysql://172.17.219.157:3306/eforms_global","root", "password");
	        PreparedStatement ps;
	        ps = con.prepareStatement(query.toString());
	        System.out.println("Printing Prepared statement:"+ps);
	        try
			  {
						  ResultSet  rs=ps.executeQuery();
						  System.out.println("Printing Result Set:"+rs);
						  while (rs.next())
						  {
							for (int i=0;i< nooffields;i++)
							{
							  System.out.println("Printing Formfield:"+FormField[i]);
							  Jsonobj.put(FormField[i],(rs.getString(FormField[i])));
							  Jsonarray.add(rs.getString(FormField[i]));
							}
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
	            logger.error("Error in getDatafromDB method, closing db connection"+ex);
	          }
	       }
		 System.out.println("Output Jsonobj String:"+Jsonobj.toString());
		 System.out.println("Output Jsonobj Array:"+Jsonarray.toString());
		 return Jsonarray.toString();
	}
	
}
