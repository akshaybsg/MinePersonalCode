package com.tcs.EformsTesting;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.xml.internal.ws.Closeable;
import com.tcs.EForms.DBManager;
import java.sql.*;

public class getDataBMLPDM {
	public static final Log LOGGER = LogFactory.getLog(getDataBMLPDM.class);
	public String getBMLPDMdata(String param1,String param2)
	{
		JSONObject Jsonobj=new JSONObject();
		JSONArray Jsonarray=new JSONArray();
		String column="";
		String condition="";
		String formId="";
		String orgId="";
		//DBManager dm = DBManager.init();
		Connection con=null;
		if(param1.equalsIgnoreCase("1681"))
		 {
			 column="app_seq_no,user_id,user_pwd";
			 condition=" entity_id="+param2+" and rowstate > -1;";
			 formId="1681";
			 orgId="996";
		 }
		if(param1.equalsIgnoreCase("1859"))
		{
			 column="app_seq_no,user_id,user_pwd";
			 condition=" entity_id="+param2+" and rowstate > -1;";
			 formId="1859";
			 orgId="996";
		}
		if(param1.equalsIgnoreCase("1129"))
		{
			 column="app_seq_no,user_id,user_pwd";
			 condition=" entity_id="+param2+" and rowstate > -1;";
			 formId="1129";
			 orgId="667";
		}
		try {
			//	dm.doConnect(orgId,"1","9518");
				
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/global","root", "password");
				PreparedStatement ps=null;
				ResultSet rs=null;
				 try
				 {
					 	String query="select " + column + " from alm_1049.app_form"+formId+"_data  where " +condition;
					 	System.out.println(query);
					 	ps = con.prepareStatement(query.toString());
					 	String[] formField = column.split(",");
					 	try
				        {
				        	rs=ps.executeQuery();
				        	while (rs.next())
				        	{
				        		for (int i=0;i<3;i++)
				        		{
				           			Jsonobj.put(formField[i],(rs.getString(formField[i])));
				        			Jsonarray.add(rs.getString(formField[i]));
				        		}
				        	}
				        }	
				        catch(Exception e)
				        {
				        	LOGGER.error(e);
				        }		
				 }
				 catch(Exception ex)
				 {
					 LOGGER.error("Exception Occured in getting data in getDataBMLPDM"+ex);	
				 }
			}
		catch(Exception e)
		{
			LOGGER.error("Exception Occured in connecting getDataBMLPDM"+e);
			try
			{
				//dm.doDisconnect();
				con.close();
			}
			catch (Exception ex)
			{
				LOGGER.error(ex);
	            LOGGER.error("Error in getDataBMLPDM method, closing db connection"+ex);
			}
		}
		finally
		{
			try
			{
				//dm.doDisconnect();
				con.close();
			}
			catch (Exception e)
			{
				LOGGER.error("Error in getDataBMLPDM method, closing db connection"+e);
			}
		}
		System.out.println(Jsonarray.toString());
		return Jsonarray.toString();
	}
}
