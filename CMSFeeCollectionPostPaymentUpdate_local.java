package com.tcs.EformsTesting;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.tcs.EForms.DBManager;
import com.tcs.cms.common.util.CmsCdmsDataApi;
import com.tcs.exception.DBException;

public class CMSFeeCollectionPostPaymentUpdate_local {

	private static Log LOGGER = LogFactory.getLog(CMSFeeCollectionPostPaymentUpdate_local.class);
	
	public String updateAfterPayment(String app_seq_no, String orgId, String formId,String payment_gateway_name, String payment_status) 
	{
		PreparedStatement ps = null;
		 LOGGER.error("payment_status::::"+payment_status);
							if (payment_status.equals("S"))
							{
								getcmsreceipt(app_seq_no,orgId,formId);
							}
							else
							{
								LOGGER.error("Payment_status is not successfull.Hence ::::"+payment_status);
							}
							return "true";
}


public void getcmsreceipt(String app_seq_no, String orgId, String formId)
{
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
	try
	{
	 Connection con = null;
	 Class.forName("com.mysql.jdbc.Driver");
	 con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/alm_1029","root", "password");
	 PreparedStatement ps = null;
     ResultSet rs = null;
     
     String wherecondition=""+"app_seq_no= '"+app_seq_no+"' and"+" payment_status = 'S' and (cmsReceiptNumber is null or cmsReceiptNumber ='') and rowstate > -1";
 	String selectQuery = "select entity_id,txtPGid,stuId,txtPendingAmount,txt_Amount,txtPGname,loginid,txtFeeType,app_seq_no,paymentTransactionNo from app_form" + formId + "_data where "+wherecondition+"";
	CmsCdmsDataApi ccda = new CmsCdmsDataApi();
	 ps = con.prepareStatement(selectQuery.toString());
		
	try {
		rs = ps.executeQuery();
							while(rs.next())
							{
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
								response = new String[2];
								try {
									//response = ccda.feeCollectionSaveOnlineEform(orgId + "", cmsAppId, pUserId, studentId, studDcnId, amountToBePaid, amountReceived, remarks, gatewayId, loginId, lovItemName, transactionId);
								response[0]="0";
								response[1]="sms/04-03-2014/FEE AND FINE"; 
								LOGGER.error(response[0]+","+response[1]);
								
								
									if (response[0] == "1") {
										String updateQuery = "UPDATE app_form" + formId + "_data SET cmsReceiptNumber =? , status_receipt=?  WHERE app_seq_no=?";
										try {
											ps = con.prepareStatement(updateQuery.toString());
											ps.setString(1, response[1]);
											ps.setString(2, "CMS Receipt Number Updated");
											ps.setString(3, app_seq);
											
											int rs1 = ps.executeUpdate();
											if (rs1 >= 0) {
												//logger.error("Successfully Updated");
											} else {
												LOGGER.error("Error in updating cms receipt number for App_Seq_no : " + app_seq);
											}
										} catch (Exception e) {
											LOGGER.error("Exception in updating cms receipt number for App_Seq_no : " + app_seq);
											LOGGER.error("exception updating eform table:" + e.getMessage());
										}
									} 
									else {
											LOGGER.error("Incorect Response received with  ExceptionCause:" + response[1]);
											String updateQuery = "UPDATE app_form" + formId + "_data SET status_receipt =? WHERE app_seq_no=?";
											try {
												ps = con.prepareStatement(updateQuery.toString());
												ps.setString(1, "CMS_Receipt_Number not Updated due to incorrect response");
												ps.setString(2, app_seq);
												int rs1 = ps.executeUpdate();
												if (rs1 >= 0) {
													//logger.error("Successfully Updated");
												} else {
													LOGGER.error("Error in updating status of receipt " + app_seq);
												}
											} catch (Exception e) {
												LOGGER.error("Exception in updating status of receipt : " + app_seq);
											}
										
									}
								} catch (Exception e) {
									LOGGER.error(" failed for feeCollectionSaveOnlineEform:Method is as: feeCollectionSaveOnlineEform(orgId:" + orgId + ", pAppId:" + cmsAppId + ",pUserId:" + pUserId + ", pStudId:" + studentId + ", pStudDcnId:" + studDcnId + ", pAmountToBePaid:" + amountToBePaid + ", pAmountReceived:" + amountReceived + ", pRemarks:" + remarks + ", pGatewayId:" + gatewayId + ", pLoginId:" + loginId + ",lLovItemName:" + lovItemName + ", pTrxId:" + transactionId + "):" + e.getMessage());
								}
							
							}
		}
	 catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	catch(ClassNotFoundException ey)
	{
		LOGGER.error(ey);
	}
	catch(SQLException ez)
	{
		LOGGER.error(ez);
	}
}

}
