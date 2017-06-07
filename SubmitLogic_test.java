package com.tcs.EformsTesting;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.EForms.DBManager;

public class SubmitLogic_test {
	public static final Log LOGGER = LogFactory.getLog(SubmitLogic_test.class);
	public String ChkDuplicate(String InputObj,String UserId, String InputCheck)
	{
		String check="true";
		 Connection con = null ;
		 String query,whereCondition;
		 try
		 {
		 Class.forName("com.mysql.jdbc.Driver");
	     con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_935","root", "password");
		 }
		 catch(Exception m)
		 {
			 LOGGER.error("class not found");
		 }
		  
	     
		try {
			
			String userid=UserId;
			String s1[];
			String s2[];
			ArrayList<String> inputobj =  new ArrayList<String>();
			ArrayList<String> inputcheck =  new ArrayList<String>();
			  
			s1=InputObj.split(",");
			s2=InputCheck.split(",");
			Integer lens1=s1.length-1;
			Integer lens2=s2.length-1;
			for(int n=0;n<lens1;n++)
			{
				inputobj.add(s1[n]);
				inputcheck.add(s2[n]);
			}
			
		//	LOGGER.error("Input Arraylist of Sched obj:"+inputobj);
		//	LOGGER.error("Input Arraylist of Sched corresponding checks:"+inputcheck);	
					
		//	whereCondition="payment_status='S' and userid="+userid+"";
			whereCondition="payment_status in ('S','M') and stuId= '"+userid+"'";
			query=" select * from app_form1467_data where "+whereCondition+" and rowstate>-1";
			
			
			PreparedStatement ps;
		    ps = con.prepareStatement(query.toString());
			try
			{
					ResultSet  rs=ps.executeQuery();
					LOGGER.error("Prepared Statement:"+ps);
					LOGGER.error("Query is"+query);
					ResultSetMetaData md = rs.getMetaData();  
							

		outsideloop:  while (rs.next())
					  {
						 System.out.println("Result Set:"+rs);
						  Map rowData = new HashMap();
						  rowData.put(md.getColumnName(1), rs.getObject(1));
						  rowData.put(md.getColumnName(1), rs.getObject(6));
						  rowData.put(md.getColumnName(1), rs.getObject(7));
						  rowData.put(md.getColumnName(1), rs.getObject(15));
						  rowData.put(md.getColumnName(1), rs.getObject(26));
						  rowData.put(md.getColumnName(1), rs.getObject(35));
						  for (int i = 44; i <=76; i++) 
						  {  rowData.put(md.getColumnName(i), rs.getObject(i));  
						  } 
				//		  LOGGER.error("Result Set List:"+rowData);
						   ArrayList<String> dbobj =  new ArrayList<String>();
						   ArrayList<String> dbcheck =  new ArrayList<String>();
						   int EntityId = Integer.parseInt(rs.getString("entity_id"));
						   for(int j=0;j<15;j++)
						   {
						
							   String d=rs.getString("fee_dtl_obj"+j);
							   String e=rs.getString("fee_dtl_check"+j);
							   dbobj.add(d);
							   dbcheck.add(e);
							   for (int m=0;m<inputobj.size();m++)
							   {
								   if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && d!=null && e!=null && !d.trim().equalsIgnoreCase("NULL") && !e.trim().equalsIgnoreCase("NULL"))
								   {
								   if(d.equalsIgnoreCase(inputobj.get(m)) && e.equalsIgnoreCase(inputcheck.get(m)))
								   {
									   LOGGER.error("Values are matched at input fee_dtl_obj value:"+inputobj.get(m)+" with entityId:"+EntityId+" fee_dtl_obj value.");
									   check="false";
									   break outsideloop;
								   }
								
								   }
								
							   }
							 
						   }
						 }
					LOGGER.error("User can continue doing payment.Values not matched.");
					
			}	
			catch (Exception e)
			{
				LOGGER.error("Unable to fetch data for submitlogicforsinghania :", e);
			}
		}
		catch (Exception ey)
		{
			LOGGER.error(ey);
		} 
		  
		finally
		{
			try
	          {
				con.close();
	          }
	          catch (Exception ex)
	          {
	        	  LOGGER.error(ex);
	        	  LOGGER.error("Error in submitlogicforsinghania, closing db connection"+ex);
	          }
		}
		return check;
}
}

