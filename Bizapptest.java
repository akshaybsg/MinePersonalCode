package com.tcs.EformsTesting;
import com.tcs.EForms.DBManager;
import com.tcs.exception.DBException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.sql.DriverManager;
import java.sql.*;


public class Bizapptest {
	 private static final Log LOGGER = LogFactory.getLog(Bizapptest.class);

	  public ArrayList<HashMap<String, String>> getBizAppDetailsList(String BizAppId, String OrgId, String FormId, String AppKey, String AppValue) throws DBException
	  {
	    ArrayList BizAppList = new ArrayList();
	    DBManager dbManager = DBManager.init();
	    Connection con=null;
	    PreparedStatement psSelect = null;
	    ResultSet rsSelect = null;
	    try
		{
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/global","root", "password");
		}
		catch(Exception a)
		{
			LOGGER.error("not able to connect local:"+a);
		}
	    try
	    {
	    //  dbManager.doConnectToGlobal();
	      LOGGER.error("Inside try : ");
	      StringBuffer query = new StringBuffer("");
	      query.append("select BizAppId,OrgId,FormId,AppKey,AppValue from eforms_global.eforms_bizapp_parameter_configuration where ");
	      if ((BizAppId != null) && (BizAppId != ""))
	      {
	        query.append("BizAppId='" + BizAppId + "'");
	      }
	      LOGGER.error("select query formed is as 1:: " + query);
	      if ((OrgId != null) && (OrgId != ""))
	      {
	        if ((BizAppId != null) && (BizAppId != "")) {
	          query.append(" and ");
	        }
	        query.append("OrgId='" + OrgId + "'");
	      }
	      LOGGER.error("select query formed is as 2:: " + query);
	      if ((FormId != null) && (FormId != ""))
	      {
	        if (((BizAppId != null) && (BizAppId != "")) || ((OrgId != null) && (OrgId != ""))) {
	          query.append(" and ");
	        }
	        query.append("FormId='" + FormId + "'");
	      }
	      LOGGER.error("select query formed is as 3:: " + query);
	      if ((AppKey != null) && (AppKey != ""))
	      {
	        if (((BizAppId != null) && (BizAppId != "")) || ((OrgId != null) && (OrgId != "")) || ((FormId != null) && (FormId != ""))) {
	          query.append(" and ");
	        }
	        query.append("AppKey='" + AppKey + "'");
	      }
	      LOGGER.error("select query formed is as 4:: " + query);
	      if ((AppValue != null) && (AppValue != ""))
	      {
	        if (((BizAppId != null) && (BizAppId != "")) || ((OrgId != null) && (OrgId != "")) || ((FormId != null) && (FormId != "")) || ((AppKey != null) && (AppKey != ""))) {
	          query.append(" and ");
	        }
	        query.append(" AppValue='" + AppValue + "'");
	      }
	      LOGGER.error("select query formed is as 5:: " + query);
	      LOGGER.error(query.toString());
	      psSelect = con.prepareStatement(query.toString());
	      LOGGER.error("psSelect " + psSelect);
	      rsSelect = psSelect.executeQuery();
	      LOGGER.error("rsSelect " + rsSelect);
	      if (rsSelect != null)
	      {
	        while (rsSelect.next())
	        {
	          String getBizAppId = rsSelect.getString(1);
	          LOGGER.error("getBizAppId is as :: " + getBizAppId);
	          String getOrgId = rsSelect.getString(2);
	          LOGGER.error("getOrgId is as :: " + getOrgId);
	          String getFormId = rsSelect.getString(3);
	          LOGGER.error("getFormId is as :: " + getFormId);
	          String getAppKey = rsSelect.getString(4);
	          LOGGER.error("getAppKey is as :: " + getAppKey);
	          String getAppValue = rsSelect.getString(5);
	          LOGGER.error("getAppValue is as :: " + getAppValue);
	          HashMap BizAppDetails = new HashMap();

	          BizAppDetails.put("BizAppId", getBizAppId);
	          BizAppDetails.put("OrgId", getOrgId);
	          BizAppDetails.put("FormId", getFormId);
	          BizAppDetails.put("AppKey", getAppKey);
	          BizAppDetails.put("AppValue", getAppValue);
	          BizAppList.add(BizAppDetails);
	          LOGGER.error("getAppValue is as ::added in list");
	        }
	      }
	    }
	    catch (SQLException e)
	    {
	      LOGGER.error("Exception in GetBizAppDetails API - getBizAppDetailsList :" + e.getMessage());
	      try
	      {
	        if (psSelect != null) psSelect.close();
	        if (rsSelect != null) rsSelect.close();
	        if (dbManager != null)
	        {
	          dbManager.doDisconnectFromGlobal();
	        }
	      }
	      catch (Exception ex)
	      {
	        LOGGER.error("Exception in GetBizAppDetails API - getBizAppDetailsList :" + ex.getMessage());
	      }
	    }
	    finally
	    {
	      try
	      {
	        if (psSelect != null) psSelect.close();
	        if (rsSelect != null) rsSelect.close();
	        if (dbManager != null)
	        {
	          dbManager.doDisconnectFromGlobal();
	        }
	      }
	      catch (Exception e)
	      {
	        LOGGER.error("Exception in GetBizAppDetails API - getBizAppDetailsList :" + e.getMessage());
	      }
	    }
	    return BizAppList;
	  }
}
