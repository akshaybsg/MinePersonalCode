package com.tcs.EformsTesting;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SequenceIdGenerator 
{

	private static Log LOGGER = LogFactory.getLog(SequenceIdGenerator.class);
	private HttpServletRequest request;
	private static SequenceIdGenerator ref;
	
	public SequenceIdGenerator() {
		// no code req'd
	}

	public SequenceIdGenerator(HttpServletRequest request) {
		this.request = request;
	}

	public static SequenceIdGenerator getSequenceIdGenerator() {
		if (ref == null)
			ref = new SequenceIdGenerator();
		return ref;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
		// that'll teach 'em
	}
	
	
	public String manavRachnaSeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
	{
		String app_seq_no="";	
		com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
		ent_id=seqgen.ApplicationSeqGenerator(seqPatternOnline,"app_seq_no", orgID, appId, formId);

		try
		{
			LOGGER.error("ent_id:::" + ent_id);
			String append = "";
		    if (ent_id.length() == 1) append = "0000" + ent_id;
		    else if (ent_id.length() == 2) append = "000" + ent_id;
		    else if (ent_id.length() == 3) append = "00" + ent_id;
		    else if (ent_id.length() == 4) append = "0" + ent_id;
		    else
		    	append = ent_id;
		    
		    LOGGER.error("append:::" + append);
		    app_seq_no = seqPatternOnline +  append;
		}
		catch (Exception ex)
		{
			LOGGER.error("Exception in Manav Rachna SeqGenerator:::" + ex.getMessage());
		}
		return app_seq_no;
	}	

	public String manavRachnaSeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
	{
		String app_seq_no="";
		String appId="9518";
		com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
		ent_id=seqgen.ApplicationSeqGenerator(seqPatternOffline,"app_seq_no", orgId, appId, formId);
	 
		try 
		{
			LOGGER.error("ent_id:::" + ent_id);
		    String append = "";
		    if (ent_id.length() == 1) append = "0000" + ent_id;
		    else if (ent_id.length() == 2) append = "000" + ent_id;
		    else if (ent_id.length() == 3) append = "00" + ent_id;
		    else if (ent_id.length() == 4) append = "0" + ent_id;
		    else 
		    	append = ent_id;
		    
		    LOGGER.error("append:::" + append);
		    app_seq_no = seqPatternOffline +  append;
		        	       
		}
		catch (Exception ex)
		{
			LOGGER.error("Exception in Manav Rachna SeqGenerator:::" + ex.getMessage());
		}
		return app_seq_no;
	}
	
	public String timesNewSeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
	{
		String app_seq_no="";	
		com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
		ent_id=seqgen.ApplicationSeqGenerator(seqPatternOnline,"app_seq_no", orgID, appId, formId);

		try
		{
			String site_id = "";
			site_id = FieldMap.get("site_id").toString();
			LOGGER.error("ent_id:::" + ent_id);
			int i=ent_id.length();
			String str="0";
		    if (i<6)
		    {
				for(int n=1;n<=5-i;n++){	
					str=str.concat("0");		
				}	
				app_seq_no = seqPatternOnline + site_id + str + ent_id;
			}		    
		    else
		    {	
			    app_seq_no = seqPatternOnline + site_id + ent_id;
			}
		}
		catch (Exception ex)
		{
			LOGGER.error("Exception in Times New SeqGenerator:::" + ex.getMessage());
		}
		return app_seq_no;
	}	

	public String timesNewSeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
	{
		int index1=-1;
		index1=-2;
		String app_seq_no="";
		String appId="9518";
	//	com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
	//	ent_id=seqgen.ApplicationSeqGenerator(seqPatternOffline,"app_seq_no", orgId, appId, formId);
	 
		try 
		{
			String site_id = "";
			LOGGER.error("Value of index1:"+index1);
			if(!(index1 == -1))
			{
				LOGGER.error("Hi");
			}	
			index1 = strFieldList.indexOf("site_id");
			site_id = str_array[index1];
			LOGGER.error("ent_id:::" + ent_id);
			int i=ent_id.length();
			String str="0";
		    if (i<6)
		    {		
				for(int n=1;n<=5-i;n++){	
					str=str.concat("0");	
				}
				    app_seq_no = seqPatternOffline + site_id + str + ent_id;
			}    
		    else
		    {	
			    app_seq_no = seqPatternOffline + site_id + ent_id;
			}
		}
		catch (Exception ex)
		{
			LOGGER.error("Exception in Times New SeqGenerator:::" + ex.getMessage());
		}
		return app_seq_no;
	}
	
	public String BMLMunjalSeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
	  {
	    String Prefrence = "";

	    String app_seq_no = "";

	    LOGGER.error("inside BMLMunjalSeqGenerator");
	    LOGGER.error("FieldMap::" + FieldMap);
	    LOGGER.error("seqPatternOnline::" + seqPatternOnline);
	    try
	    {
	      LOGGER.error("inside try");
	      Prefrence = FieldMap.get("txtPrefrence1").toString();

	      LOGGER.error("ent_id:::" + ent_id);
	      String append = "";
	      if (ent_id.length() == 1) append = "1000" + ent_id;
	      if (ent_id.length() == 2) append = "100" + ent_id;
	      if (ent_id.length() == 3) append = "10" + ent_id;
	      if (ent_id.length() == 4) append = "1" + ent_id;
	      if (ent_id.length() == 5) append = ent_id;

	      LOGGER.error("append:::" + append);

	      if (Prefrence.equals("MBA(Regular)")) {
	        LOGGER.error("inside MBA(Regular)");

	        app_seq_no = seqPatternOnline + "MR" + append;
	      }

	      if (Prefrence.equals("MBA in Business Analytics (in association with IBM)")) {
	        LOGGER.error("inside MBA in Business Analytics (in association with IBM)");

	        app_seq_no = seqPatternOnline + "MA" + append;
	      }

	      if (Prefrence.equals("MBA in Accounting and Finance(in association with KPMG)")) {
	        LOGGER.error("inside MBA in Accounting and Finance(in association with KPMG)");

	        app_seq_no = seqPatternOnline + "MF" + append;
	      }

	    }
	    catch (Exception ex)
	    {
	      LOGGER.error("Exception in BMLSeqGenerator:::" + ex.getMessage());
	    }
	    LOGGER.error("app_seq_no::::" + app_seq_no);
	    return app_seq_no;
	  }

	  public String BMLMunjalSeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
	  {
	    int index1 = -1;

	    String app_seq_no = "";
	    try {
	      index1 = strFieldList.indexOf("txtPrefrence1");
	      String Prefrence = str_array[index1];

	      String append = "";
	      if (ent_id.length() == 1) append = "1000" + ent_id;
	      if (ent_id.length() == 2) append = "100" + ent_id;
	      if (ent_id.length() == 3) append = "10" + ent_id;
	      if (ent_id.length() == 4) append = "1" + ent_id;
	      if (ent_id.length() == 5) append = ent_id;

	      if (Prefrence.equals("MBA(Regular)"))
	      {
	        app_seq_no = seqPatternOffline + "MR" + append;
	      }

	      if (Prefrence.equals("MBA in Business Analytics (in association with IBM)"))
	      {
	        app_seq_no = seqPatternOffline + "MA" + append;
	      }

	      if (Prefrence.equals("MBA in Accounting and Finance(in association with KPMG)"))
	      {
	        app_seq_no = seqPatternOffline + "MF" + append;
	      }

	    }
	    catch (Exception ex)
	    {
	      LOGGER.error("Exception in BMLSeqGenerator:::" + ex.getMessage());
	    }
	    return app_seq_no;
	  }
	  
		public String AgnelDelhiSeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
		{
			String app_seq_no="";	
			com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
			ent_id=seqgen.ApplicationSeqGenerator(seqPatternOnline,"app_seq_no", orgID, appId, formId);

			try
			{
				
				LOGGER.error("ent_id:::" + ent_id);
				int i=ent_id.length();
				String str="";
			    if (i<5)
			    {
					for(int n=1;n<=5-i;n++){	
						str=str.concat("0");		
					}	
					app_seq_no = seqPatternOnline + str + ent_id;
				}		    
			    else
			    {	
				    app_seq_no = seqPatternOnline + ent_id;
				}
			}
			catch (Exception ex)
			{
				LOGGER.error("Exception in Agnel New SeqGenerator:::" + ex.getMessage());
			}
			return app_seq_no;
		}	

		public String AgnelDelhiSeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
		{
			int index1=-1;
			String app_seq_no="";
			String appId="9518";
			com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
			ent_id=seqgen.ApplicationSeqGenerator(seqPatternOffline,"app_seq_no", orgId, appId, formId);
		 
			try 
			{
				LOGGER.error("ent_id:::" + ent_id);
				int i=ent_id.length();
				String str="";
			    if (i<5)
			    {
					for(int n=1;n<=5-i;n++){	
						str=str.concat("0");		
					}	
					app_seq_no = seqPatternOffline + str + ent_id;
				}		    
			    else
			    {	
				    app_seq_no = seqPatternOffline + ent_id;
				}
			}
			catch (Exception ex)
			{
				LOGGER.error("Exception in Agnel New SeqGenerator:::" + ex.getMessage());
			}
			return app_seq_no;
		}
		
		public String KalraEnquirySeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
		{
			String app_seq_no="";	
			com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
			ent_id=seqgen.ApplicationSeqGenerator(seqPatternOnline,"app_seq_no", orgID, appId, formId);

			try
			{
				
				LOGGER.error("ent_id:::" + ent_id);
				int i=ent_id.length();
				String str="";
			    if (i<3)
			    {
					for(int n=1;n<=3-i;n++){	
						str=str.concat("0");		
					}	
					app_seq_no = str + ent_id;
				}		    
			    else
			    {	
				    app_seq_no = ent_id;
				}
			}
			catch (Exception ex)
			{
				LOGGER.error("Exception in KalraEnquiry SeqGenerator:::" + ex.getMessage());
			}
			return app_seq_no;
		}	

		public String KalraEnquirySeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
		{
			int index1=-1;
			String app_seq_no="";
			String appId="9518";
			com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
			ent_id=seqgen.ApplicationSeqGenerator(seqPatternOffline,"app_seq_no", orgId, appId, formId);
		 
			try 
			{
				LOGGER.error("ent_id:::" + ent_id);
				int i=ent_id.length();
				String str="";
			    if (i<3)
			    {
					for(int n=1;n<=3-i;n++){	
						str=str.concat("0");		
					}	
					app_seq_no = str + ent_id;
				}		    
			    else
			    {	
				    app_seq_no =  ent_id;
				}
			}
			catch (Exception ex)
			{
				LOGGER.error("Exception in KalraEnquiry SeqGenerator::::" + ex.getMessage());
			}
			return app_seq_no;
		}
		public String ManavRachanaWithdrawalSeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
		  {
		    String app_seq_no = "";
		    com.tcs.EFormsutil.SequenceIdGenerator seqgen = new com.tcs.EFormsutil.SequenceIdGenerator();
		    ent_id = seqgen.ApplicationSeqGenerator(seqPatternOnline, "app_seq_no", orgID, appId, formId);
		    try
		    {
		      String txtWithdrawalFormId = "";
		      txtWithdrawalFormId = FieldMap.get("txtWithdrawalFormId").toString();
		      LOGGER.error("ent_id:::" + ent_id);
		      LOGGER.error("Withdrawal form ID:::" + txtWithdrawalFormId);
		     		      
		        String sequence = "WF160" +ent_id;

		        if (txtWithdrawalFormId.equals("340"))
		        {
		          app_seq_no = "MRIU" + sequence;

		          LOGGER.error("Sequence for MRIU:::" + app_seq_no);
		        }
		        else if (txtWithdrawalFormId.equals("50"))
		        {
		          app_seq_no = "MRU" + sequence;
		          LOGGER.error("Sequence for MRU:::" + app_seq_no);
		        }
		        else if (txtWithdrawalFormId.equals("49"))
		        {
		          app_seq_no = "MRDC" + sequence;
		          LOGGER.error("Sequence for MRDC:::" + app_seq_no);
		        }
		        else if (txtWithdrawalFormId.equals("18"))
		        {
		          app_seq_no = "MRCED" + sequence;
		          LOGGER.error("Sequence for MRCED:::" + app_seq_no);
		        }
		        else {
		          app_seq_no = "Withdrawal"+ent_id;
		          LOGGER.error("Sequence for all others:::" + app_seq_no);
		        }
		      }
		      
		    catch (Exception ex)
		    {
		      LOGGER.error("Exception in Withdrawal Form Manav Rachna New SeqGenerator:::" + ex.getMessage());
		    }
		    return app_seq_no;
		  }

		  public String ManavRachanaWithdrawalSeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
		  {
		    int index1 = -1;
		    String app_seq_no = "";
		    String appId = "9518";
		    com.tcs.EFormsutil.SequenceIdGenerator seqgen = new com.tcs.EFormsutil.SequenceIdGenerator();
		    ent_id = seqgen.ApplicationSeqGenerator(seqPatternOffline, "app_seq_no", orgId, appId, formId);
		    try
		    {
		      String txtWithdrawalFormId = "";
		      index1 = strFieldList.indexOf("txtWithdrawalFormId");
		      txtWithdrawalFormId = str_array[index1];
		      LOGGER.error("ent_id:::" + ent_id);
		  
		        String sequence = "WF160" + ent_id;

		        if (txtWithdrawalFormId.equals("340"))
		        {
		          app_seq_no = "MRIU" + sequence;

		          LOGGER.error("Sequence for MRIU:::" + app_seq_no);
		        }
		        else if (txtWithdrawalFormId.equals("50"))
		        {
		          app_seq_no = "MRU" + sequence;
		          LOGGER.error("Sequence for MRU:::" + app_seq_no);
		        }
		        else if (txtWithdrawalFormId.equals("49"))
		        {
		          app_seq_no = "MRDC" + sequence;
		          LOGGER.error("Sequence for MRDC:::" + app_seq_no);
		        }
		        else if (txtWithdrawalFormId.equals("18"))
		        {
		          app_seq_no = "MRCED" + sequence;
		          LOGGER.error("Sequence for MRCED:::" + app_seq_no);
		        }
		        else {
		          app_seq_no = "Withdrawal"+ent_id;
		          LOGGER.error("Sequence for all others:::" + app_seq_no);
		        }
		      
		        }
		    catch (Exception ex)
		    {
		      LOGGER.error("Exception in Withdrawal Form Manav Rachna New SeqGenerator:::" + ex.getMessage());
		    }
		    return app_seq_no;
		  }
		  
		  public String ManavRachanaWithdrawalNewSeqGen(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
		  {
		    String txtWithdrawalFormId = "";

		    String app_seq_no = "";

		    LOGGER.error("inside ManavRachanaWithdrawalNewSeqGen");
		    LOGGER.error("FieldMap::" + FieldMap);
		    LOGGER.error("seqPatternOnline::" + seqPatternOnline);
		    try
		    {
		      LOGGER.error("inside try");
		      txtWithdrawalFormId = FieldMap.get("txtWithdrawalFormId").toString();
		      
		      LOGGER.error("WithdrawalFormId::" + txtWithdrawalFormId);
		      
		      LOGGER.error("ent_id:::" + ent_id);
		      String append = "";
		      if (ent_id.length() == 1) append = "160000" + ent_id;
		      if (ent_id.length() == 2) append = "16000" + ent_id;
		      if (ent_id.length() == 3) append = "1600" + ent_id;
		      if (ent_id.length() == 4) append = "160" + ent_id;
		      if (ent_id.length() == 5) append = ent_id;

		      LOGGER.error("append:::" + append);

		      if (txtWithdrawalFormId.equals("340")) {
		        LOGGER.error("inside MRIU Form");

		        app_seq_no = seqPatternOnline + "MRIUWF" + append;
		        LOGGER.error("inside MRIU Form app_seq_no::::" + app_seq_no);
		      }

		      else if (txtWithdrawalFormId.equals("50")) {
		        LOGGER.error("inside MRU Form");

		        app_seq_no = seqPatternOnline + "MRUWF" + append;
		        LOGGER.error("inside MRU Form app_seq_no::::" + app_seq_no);
		      }

		      else if (txtWithdrawalFormId.equals("49")) {
		        LOGGER.error("inside MRCD Form");

		        app_seq_no = seqPatternOnline + "MRDCWF" + append;
		        LOGGER.error("inside MRCD Form app_seq_no::::" + app_seq_no);
		      }
		      
		      else if (txtWithdrawalFormId.equals("18")) {
			        LOGGER.error("inside MRCED Form");

			        app_seq_no = seqPatternOnline + "MRCEDWF" + append;
			        LOGGER.error("inside MRCED Form app_seq_no::::" + app_seq_no);
			      }
		   else{
		    	  
			   app_seq_no = seqPatternOnline+ "Withdrawal" + ent_id;
			   LOGGER.error("inside Else app_seq_no::::" + app_seq_no);
			   
		      }

		    }
		    catch (Exception ex)
		    {
		      LOGGER.error("Exception in ManavRachanaWithdrawalNewSeqGen:::" + ex.getMessage());
		    }
		    LOGGER.error("app_seq_no::::" + app_seq_no);
		    return app_seq_no;
		  }

		  public String ManavRachanaWithdrawalNewSeqGen(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
		  {
		    int index1 = -1;

		    String app_seq_no = "";
		    try {
		      index1 = strFieldList.indexOf("txtWithdrawalFormId");
		      String txtWithdrawalFormId = str_array[index1];

		      String append = "";
		      if (ent_id.length() == 1) append = "160000" + ent_id;
		      if (ent_id.length() == 2) append = "16000" + ent_id;
		      if (ent_id.length() == 3) append = "1600" + ent_id;
		      if (ent_id.length() == 4) append = "160" + ent_id;
		      if (ent_id.length() == 5) append = ent_id;

		      if (txtWithdrawalFormId.equals("340"))
		      {
		        app_seq_no = seqPatternOffline + "MRIUWF" + append;
		      }
		      else   if (txtWithdrawalFormId.equals("50"))
		      {
		        app_seq_no = seqPatternOffline + "MRUWF" + append;
		      }

		      else  if (txtWithdrawalFormId.equals("49"))
		      {
		        app_seq_no = seqPatternOffline  + "MRDCWF" + append;
		      }

		      else   if (txtWithdrawalFormId.equals("18"))
		      {
		        app_seq_no = seqPatternOffline +  "MRCEDWF" + append;
		      }
		      else{
		    	  
				   app_seq_no = seqPatternOffline + "Withdrawal" +ent_id; 
			      }

		    }
		    catch (Exception ex)
		    {
		      LOGGER.error("Exception in ManavRachanaWithdrawalNewSeqGen:::" + ex.getMessage());
		    }
		    return app_seq_no;
		  }
		  
		  public String GoswamiSeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
			{
				String app_seq_no="";	
				com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
				ent_id=seqgen.ApplicationSeqGenerator(seqPatternOnline,"app_seq_no", orgID, appId, formId);

				try
				{

					LOGGER.error("ent_id:::" + ent_id);
					String str="_";

						app_seq_no = seqPatternOnline+str+ent_id;

				}
				catch (Exception ex)
				{
					LOGGER.error("Exception in Goswami SeqGenerator:::" + ex.getMessage());
				}
				return app_seq_no;
			}	

			public String GoswamiSeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
			{
				int index1=-1;
				String app_seq_no="";
				String appId="9518";
				com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
				ent_id=seqgen.ApplicationSeqGenerator(seqPatternOffline,"app_seq_no", orgId, appId, formId);

				try 
				{
					LOGGER.error("ent_id:::" + ent_id);
					String str="_";

						app_seq_no = seqPatternOffline+str+ent_id;

				}
				catch (Exception ex)
				{
					LOGGER.error("Exception in Goswami SeqGenerator::::" + ex.getMessage());
				}
				return app_seq_no;
			} 
			
			public String SankaraSeqGenerator(String htmlFieldNameCSV, String htmlFieldValueCSV, String ent_id, String seqPatternOnline, String orgID, String appId, String formId, HashMap FieldMap)
			{
				String app_seq_no="";	
				com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
				ent_id=seqgen.ApplicationSeqGenerator(seqPatternOnline,"app_seq_no", orgID, appId, formId);

				try
				{

					LOGGER.error("ent_id:::" + ent_id);
					int i=ent_id.length();
					String str="";
				    if (i<3)
				    {
						for(int n=1;n<=3;n++){	
							str=str.concat("0");		
						}	
						app_seq_no = seqPatternOnline+str+ent_id;
					}		    
				    else
				    {	
					    app_seq_no = seqPatternOnline+ent_id;
					}
				}
				catch (Exception ex)
				{
					LOGGER.error("Exception in Sankara SeqGenerator:::" + ex.getMessage());
				}
				return app_seq_no;
			}	

			public String SankaraSeqGenerator(String ent_id, String seqPatternOffline, String[] str_array, List strFieldList, String orgId, String formId)
			{
				int index1=-1;
				String app_seq_no="";
				String appId="9518";
				com.tcs.EFormsutil.SequenceIdGenerator seqgen=new com.tcs.EFormsutil.SequenceIdGenerator();
				ent_id=seqgen.ApplicationSeqGenerator(seqPatternOffline,"app_seq_no", orgId, appId, formId);

				try 
				{
					LOGGER.error("ent_id:::" + ent_id);
					int i=ent_id.length();
					String str="";
				    if (i<3)
				    {
						for(int n=1;n<=3;n++){	
							str=str.concat("0");		
						}	
						app_seq_no = seqPatternOffline+str+ent_id;
					}		    
				    else
				    {	
					    app_seq_no =  seqPatternOffline+ent_id;
					}
				}
				catch (Exception ex)
				{
					LOGGER.error("Exception in Sankara SeqGenerator::::" + ex.getMessage());
				}
				return app_seq_no;
			}
		
}