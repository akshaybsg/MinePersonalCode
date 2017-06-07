package com.tcs.EformsTesting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.EForms.DBManager;
//import com.tcs.FeePaymentSummaryBizApp.CMSFeePaymentScheduler;
import com.tcs.cms.common.util.CmsCdmsDataApi;
import com.tcs.exception.DBException;
import com.tcs.scheduler.BaseScheduler;

public class CMSFeePaymentScheduler_local {
	private static Log logger = LogFactory.getLog(CMSFeePaymentScheduler_local.class);

	public void run() {

	}

	public static void main(String args[]) {
		new CMSFeePaymentScheduler_local().executeScheduler();
	}
	public void executeScheduler()
	{

		logger.error("<<<<<<<<<<<<<<<<<   START   CMSFeePaymentScheduler_local    >>>>>>>>>>>>>>>>>");
		Map<String, String> orgFormMapForFee = getOrgFormsIdForFeeCollection();
		// Put Iterator here to iterate set
		Set<String> orgSet = orgFormMapForFee.keySet();
		Iterator<String> iterator = orgSet.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			int orgId = Integer.parseInt(key.trim());
			String formId = (String) orgFormMapForFee.get(key);
			logger.error("<<<<<<<<<<<<<<<<<   START   CMSFeePaymentScheduler for Org:: "+ orgId + " Form Id:: " + formId + "  >>>>>>>>>>>>>>>>>");
			// getting all form detail
			// int orgId = 744;
			// String formId = "1107";
			String eformsAppId = "9518";
			String cmsAppId = "9520";
			String pUserId = "1";
			String studentId;
			String studDcnId = "1";
			String amountToBePaid;
			String amountReceived;
			String remarks = null;
			String gatewayId;
			String loginId;
			String lovItemName;
			String app_seq;
			String transactionId;
			Connection con = null;
			
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/alm_1029","root", "password");
			 PreparedStatement ps = null;
		     ResultSet rs = null;
			String selectQuery = "select entity_id,txtPGid,stuId,txtPendingAmount,txt_Amount,txtPGname,loginid,txtFeeType,app_seq_no,paymentTransactionNo from app_form" + formId + "_data where payment_status='S' and (cmsReceiptNumber is null or cmsReceiptNumber ='') and rowstate > -1";
			try {
				
			     ps = con.prepareStatement(selectQuery.toString());
			     rs = ps.executeQuery();
			     while (rs.next()) {
					studentId = rs.getString("stuId");
					amountToBePaid = rs.getString("txtPendingAmount");
					amountReceived = rs.getString("txt_Amount");
					remarks = "Amount Received";
					gatewayId = rs.getString("txtPGid");
					loginId = rs.getString("loginid");
					lovItemName = rs.getString("txtFeeType");
					app_seq = rs.getString("app_seq_no");
					transactionId = rs.getString("paymentTransactionNo");
					String[] response = null;
					try {
					//	response = ccda.feeCollectionSaveOnlineEform(orgId + "", cmsAppId, pUserId, studentId, studDcnId, amountToBePaid, amountReceived, remarks, gatewayId, loginId, lovItemName, transactionId);
						response[0]="0";
						response[1]="sms/04-03-2014/FEE AND FINE"; 
						if (response[0] == "1") {
							//String updateQuery = "UPDATE alm_" + orgId + ".app_form" + formId + "_data SET cmsReceiptNumber =? WHERE app_seq_no=?";
							String updateQuery = "UPDATE app_form" + formId + "_data SET cmsReceiptNumber =? WHERE app_seq_no=?";
							try {
								ps = con.prepareStatement(updateQuery.toString());
								ps.setString(1, response[1]);
								ps.setString(2, app_seq);
								int rs1 = ps.executeUpdate();
								if (rs1 >= 0) {
									//logger.error("Successfully Updated");
								} else {
									logger.error("Error in updating cms receipt number for App_Seq_no : " + app_seq);
								}
							} catch (Exception e) {
								logger.error("Exception in updating cms receipt number for App_Seq_no : " + app_seq);
								logger.error("exception updating eform table:" + e.getMessage());
							}
						} else {
							logger.error("Error in feeCollectionSaveOnlineEform method in CMS for AppseqNo:" + app_seq + " ExceptionCause:" + response[1]);
						}
					} catch (Exception e) {
						logger.error(" failed for feeCollectionSaveOnlineEform:Method is as: feeCollectionSaveOnlineEform(orgId:" + orgId + ", pAppId:" + cmsAppId + ",pUserId:" + pUserId + ", pStudId:" + studentId + ", pStudDcnId:" + studDcnId + ", pAmountToBePaid:" + amountToBePaid + ", pAmountReceived:" + amountReceived + ", pRemarks:" + remarks + ", pGatewayId:" + gatewayId + ", pLoginId:" + loginId + ",lLovItemName:" + lovItemName + ", pTrxId:" + transactionId + "):" + e.getMessage());
					}
				}
			}
			catch (SQLException en) {
				logger.error(en);
			} 
			finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException exc) {
					logger.error("CMSFeePaymentScheduler:: Error in finally block", exc);
				}
				try {
					con.close();
				}
				catch (SQLException em) {
					logger.error(em);
				}
				catch (Exception ey) {
					logger.error(ey);
				}
			}
			
		}
		catch(Exception e)
		{
			logger.error(e);
		}
			
				
			
			logger.error("<<<<<<<<<<<<<<<<<   END   CMSFeePaymentScheduler for Org:: " + orgId + "   Form Id:: " + formId + "  >>>>>>>>>>>>>>>>>");
		}
		logger.error("<<<<<<<<<<<<<<<<<   END   CMSFeePaymentScheduler    >>>>>>>>>>>>>>>>>");
	
	}
	private static Map<String, String> getOrgFormsIdForFeeCollection() {
		Map<String, String> orgFormIdMapForFeeCollection = new HashMap<String, String>();
		orgFormIdMapForFeeCollection.put("744", "1107");
		orgFormIdMapForFeeCollection.put("422", "1307");
		orgFormIdMapForFeeCollection.put("686", "1354");
		orgFormIdMapForFeeCollection.put("176", "1383");
		orgFormIdMapForFeeCollection.put("368", "1402");
		//logger.error("Returning orgId and FormId by method getOrgFormsIdForFeeCollection");
		return orgFormIdMapForFeeCollection;
	}
}
