package com.tcs.EformsTesting;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.EForms.DBManager;

public class MunjalMBAPostSubmitLogic {
	public static final Log LOGGER = LogFactory.getLog(MunjalMBAPostSubmitLogic.class);
	private DBManager dbManager;
public MunjalMBAPostSubmitLogic(){
		
	}
public void execute(List lstParameters,HashMap fieldValue){		
		
		String appSeqNumber=(String)lstParameters.get(0);
		String orgId = (String)lstParameters.get(1);
		String appId = (String)lstParameters.get(2);
		String formId = (String)lstParameters.get(3);		
		String appStatus = (String)fieldValue.get("app_status");
		String applicantName = (String)fieldValue.get("txtAppFirstName");
		String toMailId = (String)fieldValue.get("txtEmail");
		String mailRequired = (String)fieldValue.get("txtMailRequired");
		String conf = "1111";
		String identifier=(String)fieldValue.get("txtidentifier");
		
		HashMap<String,String> mailHashMap=new HashMap<String,String>();

		LOGGER.error("<<<<<<<<<<<<<< Inside MunjalMBAPostSubmitLogic::execute appStatus"+appStatus+" applicantName"+applicantName+" toMailId"+toMailId+" mailRequired"+mailRequired+">>>>>>>>>>>");
		try{	
			
			if(appStatus != null && (appStatus.equals(identifier)) && mailRequired != null && (mailRequired.equals("Y")))
			{
				LOGGER.error("<<<<<<<<<<<<<< sendMailScheduler is getting called in MunjalMBAPostSubmitLogic::execute >>>>>>>>>>>");
				sendMailScheduler(applicantName,toMailId,appId,orgId,formId,appSeqNumber,conf,identifier,mailHashMap);
				
			}	
		}catch(Exception e){
			LOGGER.error("Exception in MunjalMBAPostSubmitLogic::execute"+e.getMessage());
		}		
	}
private void sendMailScheduler(String applicantName,String mailTo,String appId,String orgId,String formId,String app_seq_no,String conf,String identifier,HashMap<String,String> mailHashMap) {
	
	PreparedStatement ps = null;
	ResultSet rs = null;

	String tempKey="";
	String tempValue="";
	String key="";
	String value="";
	
	Iterator iterator = mailHashMap.keySet().iterator();
	while(iterator. hasNext()){
		tempKey=iterator.next().toString();
		tempValue = mailHashMap.get(tempKey);
		
		key=key+tempKey+"-@@ion@@-";
		value=value+tempValue+"-@@ion@@-";
	}	
	
	String query3 = "insert into alm_common.app_scedhuler_detail(form_id,app_seq_no,user_name,user_pwd,is_mail_sent,is_sms_sent,orgid,dcnid,logged_by,logged_date,rowstate,mail_to,sms_to,identifier,keyString,valueString) values (?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?)";

	String userName = applicantName;
	
	dbManager = DBManager.init();
	
	try{
	dbManager.doConnectForLogicalDBName("1","1","9518","alm_common");
	
	ps = dbManager.getPreparedStatement(query3);
	ps.setInt(1, Integer.parseInt(formId));
	ps.setString(2, app_seq_no);
	ps.setString(3, userName);
	ps.setString(4, conf);
	ps.setBoolean(5, false);
	ps.setString(6, "N");
	ps.setInt(7, Integer.parseInt(orgId));
	ps.setInt(8, Integer.parseInt(formId));
	ps.setInt(9, 1);
	ps.setString(10, "0");
	ps.setString(11,mailTo);
	ps.setString(12, "");
	ps.setString(13, identifier);
	ps.setString(14, key);
	ps.setString(15, value);
	ps.executeUpdate();
	
	LOGGER.error("<<<<<<<<<<<<<< insert query execueted in MunjalMBAPostSubmitLogic::sendMailScheduler >>>>>>>>>>>");
	}
	catch (Exception exc)
	{ 
		LOGGER.error("Exception while insertion in alm_common.app_scedhuler_detail table", exc);
	
	}finally {
		try
		{
			dbManager.doDisconnect();
			if (rs != null) {rs.close();}
			if (ps != null) {ps.close();}
		}
		catch(Exception exc)
		{
			// exc1.printStackTrace();
			LOGGER.error("Exception in MunjalMBAPostSubmitLogic::sendMailSchedulerP "+exc.getMessage());
		}
		
	}
			   
}
}

