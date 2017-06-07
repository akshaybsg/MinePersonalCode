package com.tcs.EformsTesting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.*;
import java.lang.String;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;







public class ChallengeAccepted {
	private static final Log LOGGER = LogFactory.getLog(ChallengeAccepted.class);
	
	public static boolean isFileExists(String filename) 
	{
			boolean exists = new File(filename).exists();
	       return exists;
	}
	
	public void copyDocsApiComp()
	{
		Connection con=null;
	    PreparedStatement psSelect = null;
	    ResultSet rsSelect = null;
	    String app_seq_no="";
	    ArrayList<String> AppSeq = new ArrayList<String>(); 
	    StringBuffer Filepath1=new StringBuffer("");
	    StringBuffer Filepath2=new StringBuffer("");
	    StringBuffer Filepath3=new StringBuffer("");
	    StringBuffer Filepath4=new StringBuffer("");
	    StringBuffer Filepath5=new StringBuffer("");
	    StringBuffer Filepath6=new StringBuffer("");
	    StringBuffer Filepath7=new StringBuffer("");
	    StringBuffer Filepath8=new StringBuffer("");
	   
	    
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
	      query.append("select app_seq_no from `app_form3580_data` where rowstate > -1 ;");
	      psSelect = con.prepareStatement(query.toString());
	      rsSelect = psSelect.executeQuery();
	      
	      if (rsSelect != null)
	      {
	    		System.out.println("Query Executed");
	    	  while (rsSelect.next())
		        {
	    		 
	    		  app_seq_no = rsSelect.getString(1);
	    		  AppSeq.add(app_seq_no);
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
	    for (String appno : AppSeq) 
	    {
            	System.out.println("Application Number:"+appno);
	    		  Filepath1=new StringBuffer("");
	    		  Filepath2=new StringBuffer("");
	    		  Filepath3=new StringBuffer("");
	    		  Filepath4=new StringBuffer("");
	    		  Filepath5=new StringBuffer("");	    		  
	    		  Filepath6=new StringBuffer("");
	    		  Filepath7=new StringBuffer("");
	    		  Filepath8=new StringBuffer("");
	    	
	    		  
	    		  Filepath1.append("D:/IIT/Input_Source/Category_Certificate/");
	    		  Filepath2.append("D:/IIT/Input_Source/Course_Syllabus/");
	    		  Filepath3.append("D:/IIT/Input_Source/Disability_Certificate/");
	    		  Filepath4.append("D:/IIT/Input_Source/Grade_Sheets/");
	    		  Filepath5.append("D:/IIT/Input_Source/Nationality_Certificate/");
	    		  Filepath6.append("D:/IIT/Input_Source/OBC_NCL_Certificate/");
	    		  Filepath7.append("D:/IIT/Input_Source/Photograph/");
	    		  Filepath8.append("D:/IIT/Input_Source/Signature/");
	    	
    		  
	    		  File source1 = new File(Filepath1+appno+".pdf");
	    		  File source2 = new File(Filepath2+appno+".pdf");
	    		  File source3 = new File(Filepath3+appno+".pdf");
	    		  File source4 = new File(Filepath4+appno+".pdf");
	    		  File source5 = new File(Filepath5+appno+".pdf");
	    		  File source6 = new File(Filepath6+appno+".pdf");
	    		  File source7 = new File(Filepath7+appno+".jpg");
	    		  File source8 = new File(Filepath8+appno+".jpg");
	    		  
	    		  if(source1.exists())
	    			  	System.out.println("found.Complete path of file is :"+source1);
	    		  if(source2.exists())
	    				System.out.println("found.Complete path of file is :"+source2);
	    		  if(source3.exists())
	    				System.out.println("found.Complete path of file is :"+source3);
	    		  if(source4.exists())
	    				System.out.println("found.Complete path of file is :"+source4);
	    		  if(source5.exists())
	    				System.out.println("found.Complete path of file is :"+source5);
		    	  if(source6.exists())
		    			System.out.println("found.Complete path of file is :"+source6);
		    	  if(source7.exists())
	    				System.out.println("found.Complete path of file is :"+source7);
		    	  if(source8.exists())
		    			System.out.println("found.Complete path of file is :"+source8);
		    		  
	    		  try
	    		  {
	    			  if(source1.exists() || source2.exists() || source3.exists() || source4.exists() || source5.exists() || source6.exists() || source7.exists() || source8.exists()) 
			          {
			        	File dir = new File("D:/IIT/Output/"+appno);
				  		dir.mkdirs();
				  		LOGGER.error("Folder Created for application number-"+appno);
			          }
	    		  }
	    		  catch(Exception ex)
	    		  {
	    			  LOGGER.error("Error Occured in creating folders"+ex);
	    		  }
	    	      
			  	
			  		File dest = new File("D:/IIT/Output/"+appno);
			  		try 
			  		{
			  			 if(source1.exists())
				          {
			  				FileUtils.copyFileToDirectory(source1, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".pdf");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_CatCerti"+".pdf");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  			if(source2.exists())
				          {
			  				FileUtils.copyFileToDirectory(source2, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".pdf");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_Cour_Syl"+".pdf");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  			if(source3.exists())
				          {
			  				FileUtils.copyFileToDirectory(source3, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".pdf");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_DisabltCerti"+".pdf");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  			if(source4.exists())
				          {
			  				FileUtils.copyFileToDirectory(source4, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".pdf");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_GrdSheet"+".pdf");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  			if(source5.exists())
				          {
			  				FileUtils.copyFileToDirectory(source5, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".pdf");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_NatCerti"+".pdf");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  			if(source6.exists())
				          {
			  				FileUtils.copyFileToDirectory(source6, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".pdf");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_OBC_NCL_Certi"+".pdf");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  			if(source7.exists())
				          {
			  				FileUtils.copyFileToDirectory(source7, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".jpg");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_Photo"+".jpg");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  			if(source8.exists())
				          {
			  				FileUtils.copyFileToDirectory(source8, dest);
			  				System.out.println("File Copied for application no.-"+appno);
			  				File destnow = new File("D:/IIT/Output/"+appno+"/"+appno+".jpg");
			  				System.out.println(destnow);
			  				File newName = new File("D:/IIT/Output/"+appno+"/"+appno+"_Sign"+".jpg");
			  				System.out.println(newName);
			  				destnow.renameTo(newName);
			  				System.out.println("File renamed");
				          }
			  		   
			  		} 
			  		catch (IOException e) 
			  		{
			  		   LOGGER.error("Error occured in copying...."+e);
			  		   e.printStackTrace();
			  		}
			  		
	    } 
	}
}

