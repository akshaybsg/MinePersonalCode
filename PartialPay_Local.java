package com.tcs.EformsTesting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import com.tcs.EForms.DBManager;


public class PartialPay_Local {
	public static final Log logger = LogFactory.getLog(PartialPay_Local.class);
	public String PartialPay (String orgid)
	{
		logger.error("--------------Inside PartialPay from PartialPayLogic---------------");
		Integer orgId= Integer.parseInt(orgid);
		JSONArray Jsonarray=new JSONArray();
		Connection con = null;
		 try {
			
			 Class.forName("com.mysql.jdbc.Driver");
			 con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eforms_global","root", "password");
				PreparedStatement ps=null;
				ResultSet rs=null;
				try
				 {
					 	String query="select isRevalOpen from eforms_global.app_course_master  where rowstate > -1 and mst_org_id = '" + orgId + "' and course_id = 'PartialPay';";
					 	System.out.println("Query--"+query);
					 	ps = con.prepareStatement(query.toString());
					    try
				        {
				        	rs=ps.executeQuery();
				        	System.out.println("execute query");
				        	while(rs.next())
				        	{
				        	  		Jsonarray.add(rs.getString("isRevalOpen"));	
				        	}
				        	
				        }
				        catch(Exception e)
				        {
				        	logger.error(e);
				        }
				 }
				 catch(Exception ex)
				 {
					 logger.error("Exception Occured in getting data in PartialPay"+ex);	
				 }
		 }
		 catch(Exception e)
			{
				logger.error("Exception Occured in connecting PartialPay"+e);
				try
				{
					con.close();
				}
				catch (Exception ex)
				{
					logger.error(ex);
		            logger.error("Error in PartialPay method, closing db connection"+ex);
				}
			}
			finally
			{
				try
				{
					con.close();
				}
				catch (Exception e)
				{
					logger.error("Error in PartialPay method, closing db connection"+e);
				}
			}
			System.out.println(Jsonarray.toString());
			return Jsonarray.toString();
	}
}
