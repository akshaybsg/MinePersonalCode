package com.tcs.EformsTesting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.tcs.EForms.DBManager;

public class getGGDSDDatatest {

	public static final Log LOGGER = LogFactory.getLog(getGGDSDDatatest.class);
	
	public String getGoswamiData(String param1, String param2)
	{
		LOGGER.error("We are inside getGoswamiData");
		JSONObject Jsonobj=new JSONObject();
		JSONArray Jsonarray=new JSONArray();
		String column="";
		String condition="";
		String formId="";
		DBManager dm = DBManager.init();
			 
			 if(param1.equalsIgnoreCase("getYearMScBioInfoData"))
			 {
				 column="txtIstYrStartDate , txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMScBioInfoSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearMScBiotechData"))
			 {
				 column="txtIstYrStartDate , txtIstYrEndDate";
				 condition=" rowstate > -1 and (txtMScBioTechSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearMScAppChemData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMScAppChemSem1!='' and txtMScAppChemSem1!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearMScPhyData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMScPhySem1!='' and txtMScPhySem1!='')";
				 formId="2353";
			}
			 if(param1.equalsIgnoreCase("getYearMScITSem1Data"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMScITSem1!='' and txtMScITSem1!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearMEFBData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMEFBSem1!='' and txtMEFBSem1!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearMBEData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMBESem1!='' and txtMBESem1!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearMCOMData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMCOMSem1!='' and txtMCOMSem1!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearMAECOData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtMAECOSem1!='' and txtMAECOSem1!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearDMLTData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtDMLTSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearPGDCAData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtPGDCASubject!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearPGDCGAData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtPGDCGASem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearPGDPM_LWData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtPGDPM_LWSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearPGDMMData"))
			 {
				 column="txtIstYrStartDate , txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtPGDMMSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearPGDMCData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtPGDMCSubject!='' and txtIstYrStartDate!='')";
				 formId="2353";
				 
			 }
			 
			 if(param1.equalsIgnoreCase("getYearBVocFoodData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBVocFoodSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
				 
			 }
			 if(param1.equalsIgnoreCase("getYearBScBioInfoData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBScBioInfoSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
				 
			 }
			 if(param1.equalsIgnoreCase("getYearBScBiotechData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBScBiotechSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearBSCNMData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBScNMSubject!='' and txtIstYrStartDate!='')";
				 formId="2353"; 
			 }
			 if(param1.equalsIgnoreCase("getYearBSCMData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBScMedSem2!='' and txtIstYrStartDate!='')";
				 formId="2353"; 
			 }
			 if(param1.equalsIgnoreCase("getYearBCAData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBCASem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearBVocRetailData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBVocRetailSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearBBAData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBBASem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearBCMData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBCOMSem1!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getYearBAData"))
			 {
				 column="txtIstYrStartDate,txtIstYrEndDate";
				 condition=" rowstate > -1 and ( txtBASubject!='' and txtIstYrStartDate!='')";
				 formId="2353";
			 }
			 if(param1.equalsIgnoreCase("getScratchCardValue"))
			 {
				 column="txtScratchCardAmnt,txtScratchCardNo";
				 condition=" txtScratchCardNo="+param2+" and rowstate > -1";
				 formId="2439";
			 }
			 try {
					
				 Connection con = null;
				 Class.forName("com.mysql.jdbc.Driver");
				 con = DriverManager.getConnection("jdbc:mysql://localhost:3306/global","root", "password");
				 PreparedStatement ps = null;
			     ResultSet rs = null;
			     	 
			 
					 try
					 {
						 	String query="select " + column + " from app_form"+formId+"_data  where" +condition;
						 	ps = con.prepareStatement(query.toString());
						 	String[] formField = column.split(",");
		
							
					        try
					        {
					        	rs=ps.executeQuery();
					        	while (rs.next())
					        	{
					        		for (int i=0;i<2;i++)
					        		{
					           			Jsonobj.put(formField[i],(rs.getString(formField[i])));
					        			Jsonarray.add(rs.getString(formField[i]));
					        		}
					        	}
					        }	
					        catch(Exception e)
					        {
					        	LOGGER.error("Error in execute query in getGoswamiData:"+e);
					        }		
					 }
					 catch(Exception ex)
					 {
						 LOGGER.error("Exception Occured in getting data in getGoswamiData"+ex);	
					 }
		}
		catch(Exception e)
		{
			LOGGER.error("Exception Occured in connecting getGoswamiData"+e);
			try
			{
				
			}
			catch (Exception ex)
			{
				LOGGER.error(ex);
	            LOGGER.error("Error in getGoswamiData method, closing db connection"+ex);
			}
		}
		System.out.println(Jsonarray.toString());
		return Jsonarray.toString();
		
	}


}
