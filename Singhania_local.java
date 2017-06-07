package com.tcs.EformsTesting;

import com.tcs.cms.common.util.CmsCdmsDataApi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.sql.Timestamp;
import java.lang.Long;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Singhania_local {


	private static Log logger =LogFactory.getLog(Singhania_local.class);
	
	public void executeSchedular()
	{
		logger.error("<<<<<<<<<<<<<<<<<   START   FeePaymentSchedular_Singhania    >>>>>>>>>>>>>>>>>");
		Map<String,String> OrgFormDetailsMap=getorgformidsfree();
		
		Set<String> orgSet = OrgFormDetailsMap.keySet();
	    Iterator<String> iterator = orgSet.iterator();
	    while (iterator.hasNext()) {
	      String key = (String)iterator.next();
	      int orgid = Integer.parseInt(key.trim());
	      String orgId = Integer.toString(orgid);    
	      String formId = (String)OrgFormDetailsMap.get(key);
	      logger.error("<<<<<<<<<<<<<<<<<   START   FeePaymentSchedular_Singhania for Org:: " + orgId + " Form Id:: " + formId + "  >>>>>>>>>>>>>>>>>");

	      String eformsAppId = "9518";
	      String cmsAppId = "9520";
	      String pUserId = "1";
	      String studDcnId = "1";
	      String pRemarks = null;
	      String columnName = "";
	   /*   columnName.concat(" stuId,UserId,txt_Amount,txtPGid,loginid,txtFeeType,app_seq_no,paymentTransactionNo,txtAmountPaid,");
	      columnName.concat("payment_mode,txtPaymentDate,txtInstrumentDate,txtInstrumentNumber,txtInstrumentBankName,txtBankId,txtBankMasterId,payment_status,"); 
	      columnName.concat("fee_dtl_fees0,fee_dtl_fees1,fee_dtl_fees2,fee_dtl_fees3,fee_dtl_fees4,fee_dtl_fees5,fee_dtl_fees6,fee_dtl_fees7,fee_dtl_fees8,");
	      columnName.concat("fee_dtl_fees9,fee_dtl_fees10,fee_dtl_fees11,fee_dtl_fees12,fee_dtl_fees13,fee_dtl_fees14,");
	      columnName.concat("fee_dtl_obj0,fee_dtl_obj1,fee_dtl_obj2,fee_dtl_obj3,fee_dtl_obj4,fee_dtl_obj5,fee_dtl_obj6,fee_dtl_obj7,fee_dtl_obj8,");
	  	  columnName.concat("fee_dtl_obj9,fee_dtl_obj10,fee_dtl_obj11,fee_dtl_obj12,fee_dtl_obj13,fee_dtl_obj14");*/
	      columnName=columnName+"stuId,UserId,txt_Amount,txtPGid,loginid,txtFeeType,app_seq_no,paymentTransactionNo,txtAmountPaid,txtPGname,";
	      columnName=columnName+"payment_mode,txtPaymentDate,txtInstrumentDate,txtInstrumentNumber,txtInstrumentBankName,txtBankId,txtBankMasterId,payment_status,"; 
	      columnName=columnName+"fee_dtl_fees0,fee_dtl_fees1,fee_dtl_fees2,fee_dtl_fees3,fee_dtl_fees4,fee_dtl_fees5,fee_dtl_fees6,fee_dtl_fees7,fee_dtl_fees8,";
	      columnName=columnName+"fee_dtl_fees9,fee_dtl_fees10,fee_dtl_fees11,fee_dtl_fees12,fee_dtl_fees13,fee_dtl_fees14,";
	      columnName=columnName+"fee_dtl_obj0,fee_dtl_obj1,fee_dtl_obj2,fee_dtl_obj3,fee_dtl_obj4,fee_dtl_obj5,fee_dtl_obj6,fee_dtl_obj7,fee_dtl_obj8,";
	      columnName=columnName+"fee_dtl_obj9,fee_dtl_obj10,fee_dtl_obj11,fee_dtl_obj12,fee_dtl_obj13,fee_dtl_obj14";
	      
//	      String selectQuery = "select * from app_form" + formId + "_data where payment_status in ('S','M') and (cmsReceiptNumber is null or cmsReceiptNumber ='') and rowstate > -1";
	      String selectQuery = "select "+columnName+" from app_form" + formId + "_data where payment_status in ('S','M') and (cmsReceiptNumber is null or cmsReceiptNumber ='') and rowstate > -1";
	      Connection con = null ;

	      CmsCdmsDataApi ccda = new CmsCdmsDataApi();
	      try 
	      {
	    	  
	        //  db = DBManager.init();
	          //db.doConnect(orgId, "1", eformsAppId);
	          Class.forName("com.mysql.jdbc.Driver");
	          con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_935","root", "password");
				
	         
	        PreparedStatement ps;
	        ps = con.prepareStatement(selectQuery.toString());
	        try
	       	{
	        	  		ResultSet rs = ps.executeQuery();
	          
	          while (rs.next())
	          {
	        	  String studentId = rs.getString("stuId");
	              pUserId = rs.getString("UserId");
	              String amountReceived = rs.getString("txt_Amount");
	              pRemarks = "Amount Received";
	              String pGatewayId = rs.getString("txtPGname");
	              String pLoginId = rs.getString("loginid");
	              String lLovItemName = rs.getString("txtFeeType");
	              String app_seq = rs.getString("app_seq_no");
	              String pTrxId = rs.getString("paymentTransactionNo");
	              String TotalPaid=rs.getString("txtAmountPaid");
	              
	              String Spaymode=rs.getString("payment_mode");
	              Integer paymode=3;
	              String Spaydate =rs.getString("txtPaymentDate");
	              logger.error("Pay Date:"+Spaydate);
	              Timestamp Tpaydate=rs.getTimestamp("txtPaymentDate");
	              logger.error("Pay Date Timestamp:"+Tpaydate);
	              String SinstrumentDate=rs.getString("txtInstrumentDate");
	              logger.error("Instrument date:"+SinstrumentDate);
	              Timestamp TinstrumentDate=rs.getTimestamp("txtInstrumentDate");
	              logger.error("Pay Date Timestamp:"+TinstrumentDate);
	              String SinstrumentNum=rs.getString("txtInstrumentNumber");
	              logger.error("Instrument number:"+SinstrumentNum);
	              String SinstrumentBankName=rs.getString("txtInstrumentBankName");
	              logger.error("Instrument bank name:"+SinstrumentBankName);
	              String SbankId=rs.getString("txtBankId");
	              logger.error("BankID:"+SbankId);
	              String SbankMasterId=rs.getString("txtBankMasterId");
	              logger.error("Bank master Id long:"+SbankMasterId);
	            
	              
	              if(Spaymode.equalsIgnoreCase("cash"))
	            	  paymode=0;
	              if(Spaymode.equalsIgnoreCase("cheque"))
	            	  paymode=1;
	              if(Spaymode.equalsIgnoreCase("ECS"))
	            	  paymode=2;
	              if(Spaymode.equalsIgnoreCase("online"))
	            	  paymode=3;
	              if(Spaymode.equalsIgnoreCase("DD"))
	            	  paymode=4;
	              if(Spaymode.equalsIgnoreCase("PO"))
	            	  paymode=5;
	              if(Spaymode.equalsIgnoreCase("BOE"))
	            	  paymode=6;
	              if(Spaymode.equalsIgnoreCase("Bank Advice"))
	            	  paymode=7;
	        	  HashMap<String,Object> params = new HashMap<String,Object>();
	        	  
	        	  if(rs.getString("payment_status").equalsIgnoreCase("M"))
	        	  {
	        		  pTrxId=app_seq;
	        		if(rs.getString("payment_mode") != null && !rs.getString("payment_mode").isEmpty())
	          		  params.put("payment_mode",paymode);
	          	  if(rs.getString("txtPaymentDate") != null && !rs.getString("txtPaymentDate").isEmpty())
	          		  params.put("payment_date",Tpaydate);
	          	  if(rs.getString("txtInstrumentDate") != null && !rs.getString("txtInstrumentDate").isEmpty())
	          		  params.put("instrument_date",TinstrumentDate);
	          	  if(rs.getString("txtInstrumentNumber") != null && !rs.getString("txtInstrumentNumber").isEmpty())
	          		  params.put("instrument_number", SinstrumentNum);
	          	  if(rs.getString("txtInstrumentBankName") != null && !rs.getString("txtInstrumentBankName").isEmpty())
	          		  params.put("instrument_bank_name", SinstrumentBankName);
	          	  if(rs.getString("txtBankId") != null && !rs.getString("txtBankId").isEmpty())
	          	  { 
	          		Long bankId=Long.parseLong(SbankId);
	          		params.put("bank_id", bankId);
	          		logger.error("Bank Id long:"+bankId);
		          }
	          	if(rs.getString("txtBankMasterId") != null && !rs.getString("txtBankMasterId").isEmpty())
	          	{
	          	  Long bankMasterId=Long.parseLong(SbankMasterId);
	              logger.error("Bank master Id long:"+bankMasterId);
	              params.put("bank_master_id", bankMasterId);
	          	}
	          	if(rs.getString("app_seq_no") != null && !rs.getString("app_seq_no").isEmpty())
	          		  params.put("ack_number", app_seq);
	        	  }
	        	  
	        	   logger.error("HashMap params: " + params);
	        	  if(Spaymode.equals("3"))
	        		  params.clear();

	                    	  
	              HashMap<Long,Double> dataForCollection = new HashMap<Long,Double>();
	             
	              
	              for(int i=0;i<15;i++ )
	              {
	            	if (rs.getString("fee_dtl_fees"+i) == null ||  rs.getString("fee_dtl_fees"+i).equals("") )
	            	 {
	            		// dataForCollection.put(Long.parseLong("-1"),Double.parseDouble("-1"));
	            	 }
	            	 else
	            	 {
	            		  	dataForCollection.put(Long.parseLong(rs.getString("fee_dtl_obj"+i)),Double.parseDouble(rs.getString("fee_dtl_fees"+i)));
	            	  }
	            	  
	            	
	              } 
	              logger.error("HashMap: " + dataForCollection);
	              
	              System.out.println("Printing Complete Hasmap"); 
	               System.out.println(dataForCollection);                       
	               String[] response = (String[])null;
	               try
	               {
	            	   logger.error("params passed: Userid:"+pUserId+":studentId:"+studentId+":ptxiD:"+pTrxId+":params:"+params);
	            //	   if(Spaymode.equalsIgnoreCase("3"))
	            //		   response = ccda.feeCollectionEformFromSch(orgId, cmsAppId, pUserId, studentId, studDcnId, dataForCollection, pRemarks, pGatewayId, pLoginId, lLovItemName, pTrxId);
	            //	   else
	            	//	   response= ccda.feeCollectionEformFromSch(orgId, cmsAppId, pUserId, studentId, studDcnId, dataForCollection, pRemarks, pGatewayId, pLoginId, lLovItemName, pTrxId, params);
	                   response = new String[] {"1","9893125942-9769161732"};
	            	  logger.error("Response from feeCollectionEformFromSch: " + response);
	            	  
	            	  
	            	  if (response[0] == "1")
	                  {
	                    String updateQuery = "UPDATE app_form" + formId + "_data SET cmsReceiptNumber =? WHERE app_seq_no=?";
	                    try {
	                        ps = con.prepareStatement(updateQuery.toString());
	                        ps.setString(1, response[1]);
	                        ps.setString(2, app_seq);
	                        int rs1 = ps.executeUpdate();
	                        logger.error("CMS receipt number for App_Seq_no : " + app_seq + ":updated in Eforms DB");
	                        if (rs1 >= 0) {
	                       //   continue;
	                          logger.error("CMS receipt number for App_Seq_no : " + app_seq);
	                        }
	                        logger.error("CMS receipt number for App_Seq_no : " + app_seq + "updated in Eforms DB");
	                      }
	                      catch (Exception e) {
	                        logger.error("Exception in updating cms receipt number for App_Seq_no : " + app_seq);
	                        logger.error("exception updating eform table:" + e.getMessage());
	                      }
	                  }
	            	  else
	            		  logger.error("Error in function method in CMS for AppseqNo:" + app_seq + " ExceptionCause:" + response[1]);
	              }
	              catch (Exception e) 
	              {
	                  logger.error(" Failed for functin:Method is as: function(orgId:" + orgId + ", pAppId:" + cmsAppId + ",pUserId:" + pUserId + ", pStudId:" + studentId + ", pStudDcnId:" + studDcnId + ", pAmountToBePaid:" + TotalPaid + ", pAmountReceived:" + amountReceived + ", pRemarks:" + pRemarks + ", pGatewayId:" + pGatewayId + ", pLoginId:" + pLoginId + ",lLovItemName:" + lLovItemName + ", pTrxId:" + pTrxId + "):" + e.getMessage());
	               }
	          }
	      }
	      catch (Exception ex)
        {
          logger.error(ex);
        } 
	          
	      }
	      catch (Exception e)
	      {
	          logger.error(e);
	          try
	          {
	          
	            con.close();
	          }
	          catch (Exception ex)
	          {
	            logger.error(ex);
	          }
	       }
	    }
		
		
	}
	
	private static Map<String, String> getorgformidsfree()
	{
		Map<String,String> OrgFormDetailsMapl = new HashMap<String,String>();
		OrgFormDetailsMapl.put("935","1467");
		logger.error("Getting orgid and formid in OrgFormDetailsMap");
		return OrgFormDetailsMapl;
	}
	
	
}

