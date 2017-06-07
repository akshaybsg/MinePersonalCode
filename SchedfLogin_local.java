package com.tcs.EformsTesting;

import com.tcs.ionschedulerframework.bean.IonSchedulerBean;
import com.tcs.ionschedulerframework.contentmodel.IonSchedulerCM;
import com.tcs.EForms.DBManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SchedfLogin_local {
	public static final Log LOGGER = LogFactory.getLog(SchedfLogin_local.class);
	 public void getlogdetail_sched()
	  {
		 LOGGER.error("<<<<<<<<<<<<<<<<<   START   getlogdetail_sched of SchedfLogin:: >>>>>>>>>>>>>>>>>");
		// 	Connection lConnection = null;
		    Connection lGBLConnection = null;
		    
		    String lResultString = "false";
	//	    DBManager db=null;
		    String eformsAppId = "9518";
		    String orgId="945";
		    String formId="2807";
		//    String userType="2";
		    //User lUser = new User();
		    //IN CMS--Common---dynamic dropdown--getAllAccessableLibraries
		    try
		    {
		    	 Class.forName("com.mysql.jdbc.Driver");
				 lGBLConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/global","root", "password");
			/*	 String lQueryToGetUser ="INSERT INTO app_form"+formId+"_data (masterform_id,orgid,dcnid,payment_mode,payment_status,paymentTransactionNo,paymentReceiptNo,pvcUserCreatedFlag,last_updated_by,"+
		    	"last_updated_date,app_seq_no,user_id,user_pwd,app_status,txtAppName,userid,loginid,usrsalutation,usrfirstname,usrlastname,usrdisplayname,usrorgid,usrtype)" +
		    							"SELECT ('"+formId+"','"+orgId+"','1',NULL,NULL,NULL,NULL,'Y','559544',uslastupdatedat,usrloginid,usrid,'12345','Pending',usrfirstname,usrid,loginid,usrsalutation,usrfirstname,usrlastname,usrdisplayname,usrorgid,usrtype) from global.gblusers;";
		    */
				 String lQueryToGetUser="INSERT INTO app_form2807_data (masterform_id,orgid,dcnid,payment_mode,payment_status,paymentTransactionNo,paymentReceiptNo,logged_by,logged_date,last_updated_by,last_updated_date,app_seq_no,user_id,user_pwd,app_status,pvcUserCreatedFlag,txtAppName,usrlastname,userid,usrfirstname,loginid)" +
				 						"SELECT '2807','945','1',NULL,NULL,NULL,NULL,'559544',uslastupdatedat,'559544',uslastupdatedat,usrloginid,usrid,'12345','Pending','Y',usrfirstname,usrlastname,usrid,usrfirstname,usrloginid from gblusers;";
				 
				 LOGGER.error(lQueryToGetUser);
		    	PreparedStatement lPreparedStatement = lGBLConnection.prepareStatement(lQueryToGetUser);
		    	LOGGER.error(lPreparedStatement);
		 //   	lPreparedStatement.setLong(1, Long.parseLong(orgId));
		    //    ResultSet lResultSet = lPreparedStatement.executeQuery();
		    	lPreparedStatement.executeUpdate(lQueryToGetUser);
		    	
		    //    LOGGER.error();
		        
		    }
		    catch (NumberFormatException pException1)
		    {
		      LOGGER.error("Exception in getlogdetail_sched:", pException1);
		    }
		    catch (Exception pException2)
		    {
		      LOGGER.error("Exception in getlogdetail_sched:", pException2);
		    }
		    finally
		    {
		        try
		        {
		 //         db.doDisconnect();
		//          if (lConnection != null)
		 //           lConnection.close();
		          if (lGBLConnection != null)
		            lGBLConnection.close();
		        }
		        catch (Exception pException3)
		        {
		          LOGGER.error("getlogdetail_sched : Disconnect Failed", pException3);
		        }
		      }
		    
	  }
	
}
