package com.tcs.kvpy.validations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import com.tcs.kvpy.data.DBManager;
import com.tcs.EForms.DBManager;

import com.tcs.exception.DBException;
//import com.tcs.jsp.beans.OnlineApplicationController;
import com.tcs.EFormsjsp.beans.OnlineApplicationController;
public class EFormsForgotPwdMailOrMobile {

	
	private DBManager dbManager;
	
	private static final Log logger = LogFactory
			.getLog(EFormsForgotPwdMailOrMobile.class);

	//Earlier: public String forgotPassword(String app_seq_no,String strAppId,String formId,String orgId, String mailId, String mobileNo){
	public String forgotPassword(String app_seq_no,String formId,String orgId, String mailId, String mobileNo){
		String strAppId = "9518";
		String pwd="";
		String appName="";
		String app_seq_no1="";
		String mail="";
		String identifier="ForgotPwd";
		HashMap<String,String> mailHashMap= new HashMap <String,String>(); //Added by akshay
		PreparedStatement ps = null;
	    ResultSet rs=null;
	    //HashMap<String, String> mailHashMap=new HashMap<String, String>();
	//	logger.error("Inside New Forgot password Method: ");
		StringBuffer query = new StringBuffer();
				
		query.append("select txtAppName,txtEmail,user_pwd,app_seq_no from app_form"+formId+"_data where");
		if(!app_seq_no.equals("0")){
  		  query.append(" app_seq_no='"+app_seq_no+"' and");
	  	}
	  	
	  	if(!mailId.equals("0")){
	  		query.append(" txtEmail='"+mailId+"' and");
	  	}
	  	
	  	if(!mobileNo.equals("0")){
	  		query.append(" txtMobileNo='"+mobileNo+"' and");
	  	}
	  	
	  	query.append(" rowstate>-1");
		
	  //	logger.error("Forgot password Query:: "+query.toString());
	  	
	  	try{
	  //		logger.error("Inside try>>>>>>>>>");
	  		dbManager = DBManager.init();
	  //		logger.error("Forgot Pwd>>After DB init()");
			dbManager.doConnect(orgId,"1",strAppId);
		//	logger.error("Forgot Pwd>>After DB doConnect()");
			
	  		ps = dbManager.getPreparedStatement(query.toString());			
	  		
			rs= ps.executeQuery();
			while (rs.next())
			{
				appName = rs.getString("txtAppName");
				mail = rs.getString("txtEmail");
				pwd=(String) rs.getString("user_pwd");
				app_seq_no1 = (String) rs.getString("app_seq_no");
			}
		//	logger.error("Fetched Records:: txtAppName>>"+appName+" , txtEmail>>"+mail+" , Password>>"+pwd+" , App seq no>>"+app_seq_no1);
			
			mailHashMap.put("txtAppName", appName);
			mailHashMap.put("app_seq_no", app_seq_no1);
		//	mailHashMap.put("user_pwd", pwd);
			
		//	logger.error("Hasmap:"+mailHashMap);
			if(!"".equals(pwd)){
				 OnlineApplicationController oac = new OnlineApplicationController();
				// oac.sendMailScheduler(appName, mail, strAppId, orgId, formId, app_seq_no1, pwd, identifier);
				oac.sendMailSchedulerWithParameter(appName, mail, strAppId, orgId, formId, app_seq_no1, pwd, identifier, mailHashMap);
				//sendMailSchedulerWithParameter(String applicantName,String mailTo,String appId,String orgId,String formId,String app_seq_no,String conf,String identifier,HashMap<String,String> mailHashMap) 
			}
	  		
	  	} catch (Exception exc) {
			logger.error("ForgotPwdMailOrMobile New Method Exception: ", exc);

		} finally {
			try {
				if(dbManager != null)
					dbManager.doDisconnect();
				if(ps != null)
					ps.close();
				if(rs != null)
					rs.close();
			} catch (SQLException e) {
				
				logger.error("Unable to close objects::::: "+e);
			} catch (DBException e) {
				e.printStackTrace();
			}
		}
		return pwd;
	}
}
