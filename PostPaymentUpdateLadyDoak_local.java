package com.tcs.EformsTesting;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;

import com.tcs.EForms.DBManager;
import com.tcs.exception.DBException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PostPaymentUpdateLadyDoak_local 
{
	private static Log LOGGER = LogFactory.getLog(PostPaymentUpdateLadyDoak_local.class);
	Connection con = null;
	public void updateAfterPaymentLadyDoak(String app_seq_no, String orgId, String formId,String payment_gateway_name, String payment_status) 
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eforms_global","root", "password");
			PreparedStatement ps = null;
		
		
		 LOGGER.error("payment_status::::"+payment_status);
			if (payment_status.equals("S")) 
			{
										LOGGER.error("inside if::::");
										String query1="select * from alm_1200.app_form4370_data where app_seq_no = '"+app_seq_no+"';";
										String Columnname=getColname(query1,con);
										LOGGER.error("Columnname::::"+Columnname);
										String[] tokens = Columnname.split(",");
										LOGGER.error("tokens.length::::"+tokens.length);
										try
										{
												for(int i=1;i<tokens.length;i++)
												{
													String coltoupdate=tokens[i];
													LOGGER.error("coltoupdate::::"+coltoupdate);
													String query2 = "update alm_1200.app_form4370_data set app_status=? ,"+coltoupdate+"='S'  where app_seq_no=?";	
													ps = con.prepareStatement(query2.toString());
													ps.setString(1, "Submitted");
													ps.setString(2, app_seq_no);
													int status=ps.executeUpdate();
													LOGGER.error("status::::"+status);
												}
										}
										catch (Exception e) 
										{
											e.printStackTrace();
											LOGGER.error("Exception in updateAfterPaymentLadyDoak((If)):::" + e.getMessage());
										}
										
										finally
										{
												try
												{
													if (ps != null) 
														ps.close();
												}
												catch (SQLException e)
												{
													e.printStackTrace();
												}
											
											try
											{
												con.close();
											}
											catch(Exception e)
											{
												LOGGER.error("Exception in closing connection object(If)):::" + e.getMessage());
											}
							
							
										}


		}

		else 
		{

			try{

				LOGGER.error("inside else::::");				
			
					String query3 = "update alm_1200.app_form4370_data set app_status=? where app_seq_no=?";
		
					ps = con.prepareStatement(query3.toString());
					ps.setString(1, "Pending");
					ps.setString(2, app_seq_no);
					int status=ps.executeUpdate();
					LOGGER.error("status::::"+status);
			}
			
			catch (Exception e)
			{
				e.printStackTrace();
				LOGGER.error("Exception in updateAfterPaymentLadyDoak((Else)):::" + e.getMessage());
			}


			finally
			{

					try
					{
						if (ps != null) 
							ps.close();
						
						
					}
					catch (SQLException exc) 
					{
						exc.printStackTrace();
					}
						try
						{
							con.close();
						}
						catch(Exception e)
						{
							LOGGER.error("Exception in closing connection object(If)):::" + e.getMessage());
						}
					
			}
		}
	}
		catch (Exception e)
		{
			e.printStackTrace();
			LOGGER.error("Exception in updateAfterPaymentLadyDoak((Else)):::" + e.getMessage());
		}
	}
  public String getColname(String query1 ,Connection con)
  {
	PreparedStatement ps1 = null;
	ResultSet  rs1 =null;
	String Colname="";
	try
	{
		ps1 = con.prepareStatement(query1.toString());
		rs1=ps1.executeQuery();
		
		
		while(rs1.next())
		{
// ----------------------------------------------For UG--------------------------------------------						
			for(int i=1;i<7;i++)
			   {
				 String d=rs1.getString("pref_AidedUG"+i);
				 
				 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
				 {
					 Colname=Colname.concat(",").concat("pref_AidedUG"+i+"status");
				 }
			   }
				for(int i=1;i<7;i++)
			   {
				 String d=rs1.getString("pref_SelfUG"+i);
				 
				 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
				 {
					 Colname=Colname.concat(",").concat("pref_SelfUG"+i+"status");
				 }
			   }
				for(int i=1;i<3;i++)
				   {
					 String d=rs1.getString("pref_SelfCerti"+i);
					 
					 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
					 {
						 Colname=Colname.concat(",").concat("pref_SelfCerti"+i+"status");
					 }
				   }
				for(int i=1;i<3;i++)
				   {
					 String d=rs1.getString("pref_SelfDip"+i);
					 
					 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
					 {
						 Colname=Colname.concat(",").concat("pref_SelfDip"+i+"status");
					 }
				   }
	//----------------------------------------------For PG--------------------------------------------
				for(int i=1;i<4;i++)
				   {
					 String d=rs1.getString("pref_AidedPG"+i);
					 
					 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
					 {
						 Colname=Colname.concat(",").concat("pref_AidedPG"+i+"status");
					 }
				   }
				for(int i=1;i<4;i++)
				   {
					 String d=rs1.getString("pref_SelfPG"+i);
					 
					 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
					 {
						 Colname=Colname.concat(",").concat("pref_SelfPG"+i+"status");
					 }
				   }
				for(int i=1;i<4;i++)
				   {
					 String d=rs1.getString("pref_SelfPG"+i);
					 
					 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
					 {
						 Colname=Colname.concat(",").concat("pref_SelfPG"+i+"status");
					 }
				   }
				for(int i=1;i<4;i++)
				   {
					 String d=rs1.getString("pref_SelfMphil"+i);
					 
					 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
					 {
						 Colname=Colname.concat(",").concat("pref_SelfMphil"+i+"status");
					 }
				   }
				for(int i=1;i<3;i++)
				   {
					 String d=rs1.getString("pref_SelfPGD"+i);
					 
					 if(d!=null && !d.isEmpty() && !d.trim().equalsIgnoreCase("") && !d.trim().equalsIgnoreCase("NULL"))
					 {
						 Colname=Colname.concat(",").concat("pref_SelfPGD"+i+"status");
					 }
				   }
				/*if(rs1.getString("txtHostelAdd").equalsIgnoreCase("Yes"))
				{
					Colname=Colname.concat(",").concat("txtHostelAddstatus");
				}*/
				
		}
		
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		LOGGER.error("Exception in getting data from alm_1200.app_form4370_data((If)):::" + ex.getMessage());
	}
	
	return Colname;
}
}


 
