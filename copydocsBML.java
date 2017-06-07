package com.tcs.EformsTesting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.*;

import java.io.*;
import java.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class copydocsBML {

	private static final Log LOGGER = LogFactory.getLog(copydocsBML.class);
	public void copyDocsApi()
	{
		Connection con=null;
	    PreparedStatement psSelect = null;
	    ResultSet rsSelect = null;
	    String app_seq_no="";
	    StringBuffer Filepath1=new StringBuffer("");
	   
	    
	    try
		{
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_996","root", "password");
		}
		catch(Exception a)
		{
			LOGGER.error("not able to connect local:"+a);
		}
	    try
	    {
	      StringBuffer query = new StringBuffer("");
	      query.append("select app_seq_no,SUBSTRING_INDEX(txtPhotoPath,'/', -3) AS PHOTOPATH from `app_form1681_data` where rowstate > -1;");
	      //query.append("select app_seq_no,SUBSTRING_INDEX(txtPhotoPath,'/', -3) AS PHOTOPATH from `app_form1859_data` where rowstate > -1;");
	      psSelect = con.prepareStatement(query.toString());
	      rsSelect = psSelect.executeQuery();
	      
	      if (rsSelect != null)
	      {
	    	  while (rsSelect.next())
		        {
	    		  LOGGER.error("Query Executed");
	    		  app_seq_no = rsSelect.getString(1);
	    		  Filepath1=new StringBuffer("");
	    		  Filepath1.append("D:/TEST/BML Input/applications/Online/0/996/");
	    		      		  
		          Filepath1.append(rsSelect.getString(2));
		          
		          
		         // Filepath2=rsSelect.getString(3);
		          LOGGER.error("app_Seq:"+app_seq_no+"::Filepath1::"+Filepath1);
		         
		          
			        File dir = new File("D:/TESTCOPY/BML output via code/"+app_seq_no);
			  		dir.mkdirs();
			  		LOGGER.error("Folder Created for application number-"+app_seq_no);
			  		File source = new File(Filepath1.toString());
			  		File dest = new File("D:/TESTCOPY/BML output via code/"+app_seq_no);
			  		try 
			  		{
			  		   FileUtils.copyFileToDirectory(source, dest);
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
