package com.tcs.EformsTesting;

import java.util.ArrayList;

public class KevempuLogic 
{

	public String getData(String appSeqNumber,String orgId)
	{
	
		String formId = "1";		
		String appName="Akshay";
		String barCode="txtBarcodeField";
		String variable="";
		String barcodeField="";
		System.out.println("appSeqNumber:"+appSeqNumber);
				
		if(appSeqNumber.length()==7)
		{
			variable="CON";
			
		}
		if(appSeqNumber.length()==6)
		{
			variable="CON0";
			
		}
		if(appSeqNumber.length()==5)
		{
			variable="CON00";
			
		}
		if(appSeqNumber.length()==4)
		{
			variable="CON000";
			
		}
		if(appSeqNumber.length()==3)
		{
			variable="CON0000";
		}
		if(appSeqNumber.length()==2)
		{
			variable="CON00000";
		}
		if(appSeqNumber.length()==1)
		{
			variable="CON000000";
		}
		
		System.out.println("variable:"+variable);
		barcodeField=variable+appSeqNumber;	
		System.out.println("barcodeField:"+barcodeField);
		return barcodeField;
	}
	
}
	