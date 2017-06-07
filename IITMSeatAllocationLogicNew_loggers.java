//package com.tcs.Model;
package com.tcs.WithLoggers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import com.tcs.ionschedulerframework.data.DBManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IITMSeatAllocationLogicNew_loggers
{
	public static final Log logger = LogFactory.getLog(IITMSeatAllocationLogicNew_loggers.class);

	//-----This map is used for storing Applicants Data.------//
	private static HashMap<String, HashMap<String, String>> pApplicantsDataMap = new HashMap<String, HashMap<String,String>>();
	//-----This map is used for storing information about offers to be made in Each program under various categories-------//
	private static HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>> pOfferDetailsMap = new HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>>();
	//-----This map is used for storing information about offers to be made in Each program under various categories-------//
	private static HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>> pDuplicateOfferDetailsMap = new HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>>();
	//-----This map contains Program code with GATE paper Eligibility.--------//
	private static HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>> pCoursewiseGATEPaperEligibilityMap = new HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>>();
	//-----This map will contain data to be updated in database for all Applicants.---------//
	private static HashMap<String, HashMap<Integer, String>> pUpdateValueMap = new HashMap<String, HashMap<Integer, String>>();
	//-----This is used to know for which round Seat Allocation will be done -----//
	private static Integer roundNumber = 0;
	//-----This map is used for storing information about updating offers details-------//
	private static HashMap<String, Integer> updateOfferDetailsMap = new HashMap<String, Integer>();
	
	public static void main(String[] args) 
	{ 
		
		Connection con = null ;
		try
		{	
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_iit","root", "password");
			//con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/eforms_global", "root", "password");
			getApplicantsDetails(con);
		}
	//	System.out.println("Hi");
		catch(Exception e)
		{
			logger.error("Error in getting Applicants details from Database.");
		}

//		try
//		{
//			Class.forName("com.mysql.jdbc.Driver");
//			//con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eforms","root", "root");
//			con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/eforms_global", "root", "password");
//			getOfferDetails(con);  
//		}
//		catch(Exception e)
//		{
//			logger.error("Error in getting Offer Details from Database.");
//		}
//		
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_iit","root", "password");
		//	con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/eforms_global", "root", "password");
		getOfferDetails(con);  
		}
		catch(Exception e)
		{
			logger.error("Error in getting Offer Details from Database.");
		}
		
		generateUpdateValuesForEformApplication();

		//----- Now we will update the database with allocation result of applicants.----//	
		try
		{	
			String columnName = "";
			logger.error("Inside class : IITMSeatAllocation -- Status : Started updating Applicants Data.");
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_iit","root", "password");
			//con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/eforms_global", "root", "password");
			PreparedStatement ps2 = null;
			for(String appNo:pUpdateValueMap.keySet())
			{	
				HashMap<Integer, String> lReturnMap = new HashMap<Integer, String>();
				lReturnMap = pUpdateValueMap.get(appNo);
				//----- The offered choice number flag for e.g - if 2nd choice is offered, txtOfferedChoiceFlag2 with value 'Y' will be updated. ----//
				columnName = "txtOfferedChoiceFlag" + lReturnMap.get(2);
				ps2 = con.prepareStatement("Update app_form3580_data_avishkar set txtOnlineOfferedCourse=? ,"+columnName+"='Y' ,seatCategoryR"+roundNumber+"=? ,choiceOfferedR"+roundNumber+"=? , txtRoundNumber=?, previousDiscipline=? where app_seq_no=? ;");		
				ps2.setString(1, lReturnMap.get(1));//----- The offered course for that round will be updated. ----//
				ps2.setString(2, lReturnMap.get(3));//----- Seat Category number for that round will be updated. 1 for General, 2 for OBC, 3 for SC and 4 for ST ----//
				ps2.setString(3, lReturnMap.get(2));//----- The offered choice number for that round will be updated. ----//
				ps2.setString(4, roundNumber+"");//----- The round number for which scheduler has been run.
				ps2.setString(5, lReturnMap.get(4));//----- The discipline under which seat has been offered for that round will be updated. ----//
				ps2.setString(6, appNo);
				ps2.executeUpdate();
			}
			logger.error("Inside class : IITMSeatAllocation -- Status : Started updating Offer Data.");
			PreparedStatement ps3 = null;
			logger.error("updateOfferDetailsMap"+updateOfferDetailsMap);
			for(String course:updateOfferDetailsMap.keySet())
			{
				String[] temp = course.split("@@");
				columnName = temp[2];
				Integer seatCount = updateOfferDetailsMap.get(course);
				ps3 = con.prepareStatement("Update app_form1095_data_avishkar set "+columnName+"="+seatCount+" where txtPrgcode=? and txtdisciplinecode=?;"); 		
				ps3.setString(1, temp[0]);//-----Course for which seats are updated ----//
				ps3.setString(2, temp[1]);//-----Course Discipline for which seats are updated ----//
				ps3.executeUpdate();
			}
			logger.error("Inside class : IITMSeatAllocation -- Status : Finished updating Offer Data.");
			
			try
			{
				if (ps2 != null)
			          ps2.close();
				
				if (ps3 != null)
			          ps3.close();
				
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after Updating database.");
			}
		}
		catch(Exception e)
		{
			logger.error("Error in updating Database");
		}
//added by akshay		
		try
		{
			 logger.error("calling the testFeasabilityLogic Method");
		}
		catch(Exception ex)
		{
			logger.error("Error in testFeasabilityLogic Method ");
		}
//end by akshay		
		finally
		{
			pApplicantsDataMap.clear();
			pOfferDetailsMap.clear();
			pDuplicateOfferDetailsMap.clear();
			pCoursewiseGATEPaperEligibilityMap.clear();
			pUpdateValueMap.clear();
			roundNumber = 0;
			updateOfferDetailsMap.clear();
			
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after Updating database.");
			}
		}
		
		System.out.println("Hi");
		
	}

	//------ Function to get all Applicants data from database ----- //
	public static void getApplicantsDetails(Connection con)
	{
		ResultSet rs=null;	
		PreparedStatement ps = null;
		String query = "SELECT * FROM app_form3580_data WHERE form_status in ('ScrutinyPass','AcceptWU','RejectWU','Reregistered') and txtAppNationality='No' and rowstate > -1 ;";	   
		try
		{
			ps = con.prepareStatement(query);
			rs=ps.executeQuery();
			while(rs.next())
			{
				HashMap<String,String> lMap=new HashMap<String,String>();
				lMap.put("txtGATE2013Score", rs.getString("txtGATE2013Score")==null? "":rs.getString("txtGATE2013Score").trim());
				lMap.put("txtGATE2014Score", rs.getString("txtGATE2014Score")==null? "":rs.getString("txtGATE2014Score").trim());
				lMap.put("txtGATE2013PaperCode", rs.getString("txtGATE2013PaperCode")==null? "":rs.getString("txtGATE2013PaperCode").trim());
				lMap.put("txtGATE2014PaperCode", rs.getString("txtGATE2014PaperCode")==null? "":rs.getString("txtGATE2014PaperCode").trim());
				lMap.put("txtOfflineOfferedCourse", rs.getString("txtOfflineOfferedCourse")==null? "":rs.getString("txtOfflineOfferedCourse").trim());
				lMap.put("txtOnlineOfferedCourse", rs.getString("txtOnlineOfferedCourse")==null? "":rs.getString("txtOnlineOfferedCourse").trim());
				lMap.put("txtCategoryValid", rs.getString("txtCategoryValid")==null? "":rs.getString("txtCategoryValid").trim());
				lMap.put("txtAppCategory", rs.getString("txtAppCategory")==null? "":rs.getString("txtAppCategory").trim());
				lMap.put("txtDisabilityValid", rs.getString("txtDisabilityValid")==null? "":rs.getString("txtDisabilityValid").trim());
				lMap.put("txtQualifyingDiscpline", rs.getString("txtQualifyingDiscpline")==null? "":rs.getString("txtQualifyingDiscpline").trim());
				lMap.put("txtCourseChoice1", rs.getString("txtCourseChoice1")==null? "":rs.getString("txtCourseChoice1").trim());
				lMap.put("txtCourseChoice2", rs.getString("txtCourseChoice2")==null? "":rs.getString("txtCourseChoice2").trim());
				lMap.put("txtCourseChoice3", rs.getString("txtCourseChoice3")==null? "":rs.getString("txtCourseChoice3").trim());
				lMap.put("txtCourseChoice4", rs.getString("txtCourseChoice4")==null? "":rs.getString("txtCourseChoice4").trim());
				lMap.put("txtCourseChoice5", rs.getString("txtCourseChoice5")==null? "":rs.getString("txtCourseChoice5").trim());
				lMap.put("txtCourseChoice6", rs.getString("txtCourseChoice6")==null? "":rs.getString("txtCourseChoice6").trim());
				lMap.put("txtCourseChoice7", rs.getString("txtCourseChoice7")==null? "":rs.getString("txtCourseChoice7").trim());
				lMap.put("txtCourseChoice8", rs.getString("txtCourseChoice8")==null? "":rs.getString("txtCourseChoice8").trim());
				lMap.put("txtOfferedChoiceFlag1", rs.getString("txtOfferedChoiceFlag1")==null? "":rs.getString("txtOfferedChoiceFlag1").trim());
				lMap.put("txtOfferedChoiceFlag2", rs.getString("txtOfferedChoiceFlag2")==null? "":rs.getString("txtOfferedChoiceFlag2").trim());
				lMap.put("txtOfferedChoiceFlag3", rs.getString("txtOfferedChoiceFlag3")==null? "":rs.getString("txtOfferedChoiceFlag3").trim());
				lMap.put("txtOfferedChoiceFlag4", rs.getString("txtOfferedChoiceFlag4")==null? "":rs.getString("txtOfferedChoiceFlag4").trim());
				lMap.put("txtOfferedChoiceFlag5", rs.getString("txtOfferedChoiceFlag5")==null? "":rs.getString("txtOfferedChoiceFlag5").trim());
				lMap.put("txtOfferedChoiceFlag6", rs.getString("txtOfferedChoiceFlag6")==null? "":rs.getString("txtOfferedChoiceFlag6").trim());
				lMap.put("txtOfferedChoiceFlag7", rs.getString("txtOfferedChoiceFlag7")==null? "":rs.getString("txtOfferedChoiceFlag7").trim());
				lMap.put("txtOfferedChoiceFlag8", rs.getString("txtOfferedChoiceFlag8")==null? "":rs.getString("txtOfferedChoiceFlag8").trim());
				lMap.put("txtSuitabilityTestRequired1", rs.getString("txtSuitabilityTestRequired1")==null? "":rs.getString("txtSuitabilityTestRequired1").trim());
				lMap.put("txtSuitabilityTestRequired2", rs.getString("txtSuitabilityTestRequired2")==null? "":rs.getString("txtSuitabilityTestRequired2").trim());
				lMap.put("txtSuitabilityTestRequired3", rs.getString("txtSuitabilityTestRequired3")==null? "":rs.getString("txtSuitabilityTestRequired3").trim());
				lMap.put("txtSuitabilityTestRequired4", rs.getString("txtSuitabilityTestRequired4")==null? "":rs.getString("txtSuitabilityTestRequired4").trim());
				lMap.put("txtSuitabilityTestRequired5", rs.getString("txtSuitabilityTestRequired5")==null? "":rs.getString("txtSuitabilityTestRequired5").trim());
				lMap.put("txtSuitabilityTestRequired6", rs.getString("txtSuitabilityTestRequired6")==null? "":rs.getString("txtSuitabilityTestRequired6").trim());
				lMap.put("txtSuitabilityTestRequired7", rs.getString("txtSuitabilityTestRequired7")==null? "":rs.getString("txtSuitabilityTestRequired7").trim());
				lMap.put("txtSuitabilityTestRequired8", rs.getString("txtSuitabilityTestRequired8")==null? "":rs.getString("txtSuitabilityTestRequired8").trim());
				lMap.put("txtSuitabilityTestResult1", rs.getString("txtSuitabilityTestResult1")==null? "":rs.getString("txtSuitabilityTestResult1").trim());
				lMap.put("txtSuitabilityTestResult2", rs.getString("txtSuitabilityTestResult2")==null? "":rs.getString("txtSuitabilityTestResult2").trim());
				lMap.put("txtSuitabilityTestResult3", rs.getString("txtSuitabilityTestResult3")==null? "":rs.getString("txtSuitabilityTestResult3").trim());
				lMap.put("txtSuitabilityTestResult4", rs.getString("txtSuitabilityTestResult4")==null? "":rs.getString("txtSuitabilityTestResult4").trim());
				lMap.put("txtSuitabilityTestResult5", rs.getString("txtSuitabilityTestResult5")==null? "":rs.getString("txtSuitabilityTestResult5").trim());
				lMap.put("txtSuitabilityTestResult6", rs.getString("txtSuitabilityTestResult6")==null? "":rs.getString("txtSuitabilityTestResult6").trim());
				lMap.put("txtSuitabilityTestResult7", rs.getString("txtSuitabilityTestResult7")==null? "":rs.getString("txtSuitabilityTestResult7").trim());
				lMap.put("txtSuitabilityTestResult8", rs.getString("txtSuitabilityTestResult8")==null? "":rs.getString("txtSuitabilityTestResult8").trim());
				lMap.put("choiceOfferedR1", rs.getString("choiceOfferedR1")==null? "":rs.getString("choiceOfferedR1").trim());
				lMap.put("choiceOfferedR2", rs.getString("choiceOfferedR2")==null? "":rs.getString("choiceOfferedR2").trim());
				lMap.put("choiceOfferedR3", rs.getString("choiceOfferedR3")==null? "":rs.getString("choiceOfferedR3").trim());
				lMap.put("choiceOfferedR4", rs.getString("choiceOfferedR4")==null? "":rs.getString("choiceOfferedR4").trim());
				lMap.put("choiceOfferedR5", rs.getString("choiceOfferedR5")==null? "":rs.getString("choiceOfferedR5").trim());
				lMap.put("choiceOfferedR6", rs.getString("choiceOfferedR6")==null? "":rs.getString("choiceOfferedR6").trim());
				lMap.put("choiceOfferedR7", rs.getString("choiceOfferedR7")==null? "":rs.getString("choiceOfferedR7").trim());
				lMap.put("choiceOfferedR8", rs.getString("choiceOfferedR8")==null? "":rs.getString("choiceOfferedR8").trim());
				lMap.put("seatCategoryR1", rs.getString("seatCategoryR1")==null? "":rs.getString("seatCategoryR1").trim());
				lMap.put("seatCategoryR2", rs.getString("seatCategoryR2")==null? "":rs.getString("seatCategoryR2").trim());
				lMap.put("seatCategoryR3", rs.getString("seatCategoryR3")==null? "":rs.getString("seatCategoryR3").trim());
				lMap.put("seatCategoryR4", rs.getString("seatCategoryR4")==null? "":rs.getString("seatCategoryR4").trim());
				lMap.put("seatCategoryR5", rs.getString("seatCategoryR5")==null? "":rs.getString("seatCategoryR5").trim());
				lMap.put("seatCategoryR6", rs.getString("seatCategoryR6")==null? "":rs.getString("seatCategoryR6").trim());
				lMap.put("seatCategoryR7", rs.getString("seatCategoryR7")==null? "":rs.getString("seatCategoryR7").trim());
				lMap.put("seatCategoryR8", rs.getString("seatCategoryR8")==null? "":rs.getString("seatCategoryR8").trim());
				lMap.put("seatCategoryR9", rs.getString("seatCategoryR9")==null? "":rs.getString("seatCategoryR9").trim());
				lMap.put("seatCategoryR10", rs.getString("seatCategoryR10")==null? "":rs.getString("seatCategoryR10").trim());
				lMap.put("form_status", rs.getString("form_status")==null? "":rs.getString("form_status").trim());
				pApplicantsDataMap.put(rs.getString("app_seq_no").trim(), lMap);
			}
			logger.error("Inside class : IITMSeatAllocation -- Status : Data Map Ready");
		//	logger.error("::::pApplicantDataMap:::"+pApplicantsDataMap);
			try
			{
				if (rs != null)
			          rs.close();
				
				if (ps != null)
			          ps.close();
			}
			catch (SQLException exc)
		      {
		        logger.error("exception in after getting Applicant's data in closing ps rs", exc);
		      }
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after getting Applicant's data");
			}
		}
		catch(Exception e)
		{
			logger.error("Error in getting Applicant's Data");
		}
		finally
		{
			try
			{
				if (rs != null)
			          rs.close();
				
				if (ps != null)
			          ps.close();
			}
			catch (SQLException exc)
		      {
		        logger.error("exception in finally block in closing ps rs", exc);
		      }
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after getting Applicant's data");
			}
		}
	}
	
	//------ Function to get Offer data from database ----- //
	public static void getOfferDetails(Connection con)
	{	
		ResultSet rs1=null;
		PreparedStatement ps1 = null;
		String query1 = "SELECT * FROM app_form1095_data WHERE rowstate>-1 ;";

		try
		{
			ps1 = con.prepareStatement(query1);
			rs1=ps1.executeQuery();	    
			Set<ArrayList<String>> disciSet = null;
			Iterator<ArrayList<String>> it = null;
			while(rs1.next())
			{
				String Course = "";
				Integer maxSeats = 0 ;
				HashMap<ArrayList<String>, ArrayList<String>> offerMap=new HashMap<ArrayList<String>, ArrayList<String>>();
				HashMap<ArrayList<String>, ArrayList<String>> eligibilityMap=new HashMap<ArrayList<String>, ArrayList<String>>();
				//----This array list will store amount of offers to be made, mapped against each discipline list of each course.----//
				ArrayList<String> seatList= new ArrayList<String>();

				Course = rs1.getString("txtPrgCode")==null? "":rs1.getString("txtPrgCode").trim();
			//	logger.error("1:");
				roundNumber = Integer.parseInt(rs1.getString("txtRoundNumber")==null? "":rs1.getString("txtRoundNumber").trim());
			//	logger.error("2:");
				//----- This flag tell us that whether the discipline list is start marked or not. ----//
				seatList.add(rs1.getString("txtcombinedmeritlist")==null? "N":rs1.getString("txtcombinedmeritlist").trim());
			//	logger.error("3:");
				//----- Number of General Seats for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtGeneral")==null? "0":rs1.getString("txtGeneral").trim());
			//	logger.error("4:");
				//----- Number of OBC Seats for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtOBC")==null? "0":rs1.getString("txtOBC").trim());
			//	logger.error("5:");
				//----- Number of SC Seats for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtSC")==null? "0":rs1.getString("txtSC").trim());
			//	logger.error("6:");
				//----- Number of ST Seats for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtST")==null? "0":rs1.getString("txtST").trim());
			//	logger.error("7:");
				//----- Number of General Seats(PH quota) for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtPWDGeneral")==null? "0":rs1.getString("txtPWDGeneral").trim());
				//----- Number of OBC Seats(PH quota) for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtPWDOBC")==null? "0":rs1.getString("txtPWDOBC").trim());
				//----- Number of SC Seats(PH quota) for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtPWDSC")==null? "0":rs1.getString("txtPWDSC").trim());
				//----- Number of ST Seats(PH quota) for that discipline list under a particular course. ----//
				seatList.add(rs1.getString("txtPWDST")==null? "0":rs1.getString("txtPWDST").trim());
			//	logger.error("8:"+seatList);
				//----- Now we will add all these individual category seats to get total number of seats. ----//	    	
				for(int i=1;i<9;i++)
					maxSeats = maxSeats + Integer.parseInt(seatList.get(i));
			//	logger.error("9:");
				seatList.add(maxSeats+"");
				seatList.add(maxSeats+"");

				//----- we are adding category seats again in this list to have account of seats given to other discipline in case of star marked discipline. ----//
				seatList.add(rs1.getString("txtGeneral")==null? "0":rs1.getString("txtGeneral").trim());
				seatList.add(rs1.getString("txtOBC")==null? "0":rs1.getString("txtOBC").trim());
			//	logger.error("10:");
				seatList.add(rs1.getString("txtSC")==null? "0":rs1.getString("txtSC").trim());
			//	logger.error("11:");
				seatList.add(rs1.getString("txtST")==null? "0":rs1.getString("txtST").trim());
				seatList.add(rs1.getString("txtPWDGeneral")==null? "0":rs1.getString("txtPWDGeneral").trim());
				seatList.add(rs1.getString("txtPWDOBC")==null? "0":rs1.getString("txtPWDOBC").trim());
				seatList.add(rs1.getString("txtPWDSC")==null? "0":rs1.getString("txtPWDSC").trim());
				seatList.add(rs1.getString("txtPWDST")==null? "0":rs1.getString("txtPWDST").trim());
			//	logger.error("11:");
				//----This array list will store discipline mapped against each course----//
				ArrayList<String> disciplinesList= new ArrayList<String>();
				if(rs1.getString("txtdisciplinecode").contains(","))
					disciplinesList = getArrayList(rs1.getString("txtdisciplinecode").trim());
				else
					disciplinesList.add(rs1.getString("txtdisciplinecode").trim());

				//----This array list will store eligible gate paper codes mapped against each course----//	
				ArrayList<String> lEligibilityList = new ArrayList<String>();
				if(rs1.getString("txtEligibleGatePaperCodes").contains(","))
					lEligibilityList = getArrayList(rs1.getString("txtEligibleGatePaperCodes").trim());
				else
					lEligibilityList.add(rs1.getString("txtEligibleGatePaperCodes").trim());
				
				logger.error("lEligibilityList:"+lEligibilityList);
				//----- Checking if Offer map already contains this course. ----//
				if(pOfferDetailsMap.containsKey(Course))
				{
					ArrayList<String> seatListPrev= new ArrayList<String>();
					ArrayList<String> disciListPrev= new ArrayList<String>();
					offerMap = pOfferDetailsMap.get(Course);
					eligibilityMap = pCoursewiseGATEPaperEligibilityMap.get(Course);
					logger.error("eligibilityMap:"+eligibilityMap);
					disciSet = offerMap.keySet();
					it = disciSet.iterator();
					//----- Checking if the discipline is star marked. ----//
					if(seatList.get(0).equalsIgnoreCase("Y"))
					{
						//----- If the discipline is star marked then its total seats will be added to total seats of all other discipline lists under that course. ----//
						while(it.hasNext())
						{
							disciListPrev = it.next();
							seatListPrev= offerMap.get(disciListPrev);
							Integer temp = Integer.parseInt(seatListPrev.get(9));
							seatListPrev.set(9,Integer.parseInt(seatList.get(10))+ temp +"");
							offerMap.put(disciListPrev, seatListPrev);
							//----- We will update total seat of this seat list too with total number of pool seats. ----//
							if(seatListPrev.get(0).equalsIgnoreCase("Y"))
								seatList.set(9, seatListPrev.get(9));
						}
					}
					else
					{
						//----- If the discipline is not star marked then its total seats will be updated to total pool seats of a star marked discipline under that course. ----//
						NonStar : while(it.hasNext())
						{
							disciListPrev = it.next();
							seatListPrev= offerMap.get(disciListPrev);
							if(seatListPrev.get(0).equalsIgnoreCase("Y"))
							{
								Integer temp = Integer.parseInt(seatListPrev.get(9));	
								seatList.set(9, Integer.parseInt(seatList.get(9))+temp +"");
								break NonStar;
							}
						}
					}		
					eligibilityMap.put(disciplinesList, lEligibilityList);
					pCoursewiseGATEPaperEligibilityMap.put(Course, eligibilityMap);
					
					offerMap.put(disciplinesList, seatList);
					pOfferDetailsMap.put(Course, offerMap);
				}
				else
				{
					offerMap.put(disciplinesList, seatList);
					pOfferDetailsMap.put(Course, offerMap);
					eligibilityMap.put(disciplinesList, lEligibilityList);
					pCoursewiseGATEPaperEligibilityMap.put(Course, eligibilityMap);
				}
				logger.error("pCoursewiseGATEPaperEligibilityMap:"+pCoursewiseGATEPaperEligibilityMap);
			}
			//----- Creating duplicate Offer map to know the actual seat counts for Dynamic Update -----//
			pDuplicateOfferDetailsMap.putAll(pOfferDetailsMap) ;
			
			
			logger.error("Inside class : IITMSeatAllocation -- Status : OfferData Map Ready " + pOfferDetailsMap);
			try
			{
				if (rs1 != null)
			          rs1.close();
				
				if (ps1 != null)
			          ps1.close();
			}
			catch (SQLException exc)
		     {
		        logger.error("exception in finally block in closing ps1 rs1", exc);
		     }
			
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after getting Offer data.");
			}
		}
		catch(Exception e)
		{
			logger.error("Error in getting Offer Data");
		}
		finally
		{
			try
			{
				if (rs1 != null)
			          rs1.close();
				
				if (ps1 != null)
			          ps1.close();
			}
			catch (SQLException exc)
		      {
		        logger.error("exception in finally block in closing ps1 rs1", exc);
		      }
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after getting Offer data.");
			}
		}
	}

	//------ Function to split string and store values in ArrayList ----- //
	public static ArrayList<String> getArrayList(String temp)
	{
		String[] strArr=temp.split(",");
		ArrayList<String> returnList=new ArrayList<String>();
		for(int i=0;i<strArr.length;i++)
		{
			returnList.add(strArr[i].trim());
		}	
		return returnList;
	}

	//------ Function to allocate offers to Applicants.----- //
	public static void generateUpdateValuesForEformApplication()
	{
		logger.error("Inside class : IITMSeatAllocation -- Status : In generateUpdateValuesForEformApplication");
		//-----This map is used to store GATE Score as key and Registration no. as value in TreeSet for ranking Applicants on the basis of GATE Score.----//
		TreeMap<Integer, TreeSet<String>> lMeritListMap = new TreeMap<Integer, TreeSet<String>>();
		Integer lGATEScoreCurrent = 0;
		Integer lGATEScorePrevious = 0;
		String lGATECurrentPaperCode = "";
		String lGATEPreviousPaperCode = "";
		try
		{
			lMeritListMap = createMeritList(lMeritListMap);
			logger.error(":lMeritListMap:::"+lMeritListMap);
			//----This number indicates the unique GATE Scores present in Merit List.----//
			Integer lMapSize = lMeritListMap.size(); 
			Double lChoice = 0.0 ;
			Double lAlloted = 0.0 ;
			Double lChoiceAllotmentRatio = 0.0;
			Integer lGATEScr ;	
			//-----Now we will run loop on MeritListMap and will allocate seat to Applicants-----//
			for(int s=1;s<lMapSize+1;s++)
				
			{	
				/*chk if status is Accept*/
								
				lGATEScr=lMeritListMap.lastKey();
				logger.error("GATE Score considered for admission "+lGATEScr);
				if(lGATEScr == 504)
				{
					logger.error("Hi u r 504");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 858)
				{
					logger.error("Hi u r 858");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 683)
				{
					logger.error("Hi u r 683");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 684)
				{
					logger.error("Hi u r 684");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 730)
				{
					logger.error("Hi u r 730");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 729)
				{
					logger.error("Hi u r 729");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 815)
				{
					logger.error("Hi u r 815");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 636)
				{
					logger.error("Hi u r 636");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 568)
				{
					logger.error("Hi u r 568");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 573)
				{
					logger.error("Hi u r 573");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 650)
				{
					logger.error("Hi u r 650");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 651)
				{
					logger.error("Hi u r 651");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 683)
				{
					logger.error("Hi u r 683");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 774)
				{
					logger.error("Hi u r 774");
					logger.error("Hi u r ::"+lGATEScr);
				}
				if(lGATEScr == 775)
				{
					logger.error("Hi u r 775");
					logger.error("Hi u r ::"+lGATEScr);
				}
				lGATECurrentPaperCode = "" ;
				lGATEPreviousPaperCode = "";
				lGATEScoreCurrent = 0;
				lGATEScorePrevious = 0;
				//-----It will store starting choice number for an applicant which he will get.------//
				HashMap<String , Integer> lStartChoiceNoMap = new HashMap<String, Integer>();
				HashMap<ArrayList<String>, ArrayList<String>> lDisciplineOfferMap = new HashMap<ArrayList<String>, ArrayList<String>>();
				ArrayList<String> lLoopList = new ArrayList<String>();

				TreeSet<String> lTempSet = lMeritListMap.get(lGATEScr);	
				if(lTempSet.size()== 1)//----if there is no tie case-----//
				{
					lLoopList.addAll(lTempSet);
					lStartChoiceNoMap.put(lLoopList.get(0), 1);
				}
				else//--- In case of tie---------//
				{
					logger.error("::params for resolvetie::lLoopList:"+lLoopList+": lTempSet:"+ lTempSet+":lGATEScr:"+ lGATEScr+":lStartChoiceNoMap:"+lStartChoiceNoMap);
					lStartChoiceNoMap = resolveTieCondition(lLoopList, lTempSet, lGATEScr, lStartChoiceNoMap);
		
				}
				logger.error("::lStartChoiceNoMap::"+lStartChoiceNoMap);
				Integer lOfferedChoiceNo = 0;
				Integer lLoopVariable = 0;
				Integer lSeatTypeNumber = 1;
				Integer lSeatTypeNumberakshay = 1;
				boolean lSeatOfferedFlag = false;
				Integer lSeatCategoryFlag = 0;	
				Integer lSeatCategoryFlagakshay = 0;
				Integer lOfferedChoiceLoopNo ;
				Integer lStartingChoiceLoopNo ;
																
				for(String lApp:lLoopList)
				{
					/*chk if status is Accept, if accept then loop{chk last rounds seatcategory, }else{}*/
				
					if(lGATEScr == 858)
						System.out.println("858 score ");
					ArrayList<String> lSeatQuantityList = new ArrayList<String>();
					ArrayList<String> lSeatQuantityListOrg = new ArrayList<String>();
					HashMap<Integer,String> lReturnValueMap = new HashMap<Integer, String>();
					ArrayList<String> lDisciplineList = new ArrayList<String>();
					ArrayList<String> lDisciplineListOrg = new ArrayList<String>();
					String lCourseOffered = "";
					String previouslyOffered = "";
					String lDisciplineCode = "";
					String lOtherDiscipline = "";
					String lDisciplineForUpdate = "";
					String lApplicantCategory = "";
					Integer lCourseOffers = 0;
					Integer lCategoryCourseOffers = 0 ;
					Integer lCategoryCourseOffersakshay = 0 ;
					Integer lCategoryCourseOffersOrg = 0 ;
					Integer lMaxOfferOfDisciplines = 0;
					lOfferedChoiceLoopNo = 9 ;
					lStartingChoiceLoopNo = 1;
					lCourseOffered = "";
					lOfferedChoiceNo = 0;
					lSeatOfferedFlag = false;

					logger.error("::pUpdateValueMap::"+pUpdateValueMap);
					if(pUpdateValueMap.containsKey(lApp))
					{	
						lOfferedChoiceLoopNo = Integer.parseInt(pUpdateValueMap.get(lApp).get(2));
						logger.error("::lOfferedChoiceLoopNo::"+lOfferedChoiceLoopNo);
					}
					else
					{
						if(pApplicantsDataMap.get(lApp).get("txtOfflineOfferedCourse")!=null && !pApplicantsDataMap.get(lApp).get("txtOfflineOfferedCourse").equals(""))
						{
							lCourseOffered = pApplicantsDataMap.get(lApp).get("txtOfflineOfferedCourse");
							logger.error(":lCourseOfferedOffline:"+lCourseOffered);
						}
							
						if(pApplicantsDataMap.get(lApp).get("choiceOfferedR"+roundNumber)==null || pApplicantsDataMap.get(lApp).get("choiceOfferedR"+roundNumber).equals(""))
						{
							if(pApplicantsDataMap.get(lApp).get("txtOnlineOfferedCourse")!=null && !pApplicantsDataMap.get(lApp).get("txtOnlineOfferedCourse").equals(""))
							{
								lCourseOffered = pApplicantsDataMap.get(lApp).get("txtOnlineOfferedCourse");
								logger.error(":lCourseOfferedOnline:"+lCourseOffered);
								logger.error(":roundnumber:"+roundNumber);
							}
							
						}
					}

					if(lCourseOffered != null && !lCourseOffered.equals(""))//Checking if already he had been offered courses in previous rounds or in offline mode
					{
						Temp:for(int j=1;j<9;j++)
						{
							previouslyOffered = pApplicantsDataMap.get(lApp).get("txtCourseChoice" + j)==null?"":pApplicantsDataMap.get(lApp).get("txtCourseChoice" + j).toString();
							if(previouslyOffered.equals(lCourseOffered))
							{
								lOfferedChoiceLoopNo = j;		
								previouslyOffered = lCourseOffered;
								logger.error(":previouslyOffered:"+previouslyOffered);
								break Temp;
							}
						}
					}
					lStartingChoiceLoopNo = lStartChoiceNoMap.get(lApp);
					logger.error(":lStartingChoiceLoopNo:"+lStartingChoiceLoopNo);
					logger.error("roundNumber:"+roundNumber);
					logger.error("lOfferedChoiceLoopNo:"+lOfferedChoiceLoopNo);
//old akshay changes were here for category update loop now moved down after update value map is set
//---------------------------Chnages by akshay-------------------------------------------------------------------------------------------
					
			if(!pApplicantsDataMap.get(lApp).get("form_status").equals("Accept"))
			{
						
//-----------------------------Changes end by akshay--------------------------------------------------------------------------------------						
					CourseChoiceLoop : for(int k=lStartingChoiceLoopNo;k<lOfferedChoiceLoopNo;k++)//Running loop for Applicant's choices starting in ascending order from 1st Choice to 8th choice or less then Choice already offered.
					{
						lCourseOffers = 0;
						lCategoryCourseOffers = 0;
						lCategoryCourseOffersOrg = 0;
						lGATEScoreCurrent = 0;
						lGATEScorePrevious = 0;
						lGATECurrentPaperCode = "" ;
						lGATEPreviousPaperCode = "";
						lSeatTypeNumber = 1;
						Integer llLoopVar = 1;
						String lCourseChoice = "";
						lOtherDiscipline = "";
						lDisciplineForUpdate = "";			
						boolean lSuitabilityFlag = false ;
						boolean lHTTAFlag = false;
						boolean starCourseFlag = false ;
						boolean lSeatAvailableFlag = false;
						if(pApplicantsDataMap.get(lApp).get("txtCourseChoice" + k)!=null && !pApplicantsDataMap.get(lApp).get("txtCourseChoice" + k).equals(""))
						{
							lCourseChoice = pApplicantsDataMap.get(lApp).get("txtCourseChoice" + k);
							logger.error(":lCourseChoice::"+lCourseChoice);
						}
						else
							break CourseChoiceLoop;
						
						lDisciplineCode = pApplicantsDataMap.get(lApp).get("txtQualifyingDiscpline");
						logger.error(":lDisciplineCode::"+lDisciplineCode);
						if(pApplicantsDataMap.get(lApp).get("txtGATE2013Score")!=null && !pApplicantsDataMap.get(lApp).get("txtGATE2013Score").equals(""))			
							lGATEScoreCurrent = Integer.parseInt(pApplicantsDataMap.get(lApp).get("txtGATE2013Score"));
						if(pApplicantsDataMap.get(lApp).get("txtGATE2014Score")!=null && !pApplicantsDataMap.get(lApp).get("txtGATE2014Score").equals(""))
							lGATEScorePrevious = Integer.parseInt(pApplicantsDataMap.get(lApp).get("txtGATE2014Score"));
						if(pApplicantsDataMap.get(lApp).get("txtGATE2013PaperCode")!=null && !pApplicantsDataMap.get(lApp).get("txtGATE2013PaperCode").equals(""))			
							lGATECurrentPaperCode = pApplicantsDataMap.get(lApp).get("txtGATE2013PaperCode");
						if(pApplicantsDataMap.get(lApp).get("txtGATE2014PaperCode")!=null && !pApplicantsDataMap.get(lApp).get("txtGATE2014PaperCode").equals(""))
							lGATEPreviousPaperCode = pApplicantsDataMap.get(lApp).get("txtGATE2014PaperCode");


						HashMap<ArrayList<String>, ArrayList<String>> lEligibilityMap = new HashMap<ArrayList<String>, ArrayList<String>>();
						lEligibilityMap = pCoursewiseGATEPaperEligibilityMap.get(lCourseChoice);
						Iterator<ArrayList<String>> it8 = lEligibilityMap.keySet().iterator();
						ArrayList<String> disciplineList = new ArrayList<String>();

						boolean categoryValid = false;
						if(pApplicantsDataMap.get(lApp).get("txtCategoryValid")!=null && pApplicantsDataMap.get(lApp).get("txtCategoryValid").equals("Y")) //---- Checking if applicant should be given Category Reservation Benefit ----//
						{
							categoryValid = true;
							llLoopVar = 2 ;
						}

						if(lGATEScoreCurrent.compareTo(lGATEScr)==0)//Checking this GATE Score is of which year.
						{
							while(it8.hasNext())
							{
								disciplineList = it8.next();
								if(disciplineList.contains(lDisciplineCode))
								{
									if(lEligibilityMap.get(disciplineList).contains(lGATECurrentPaperCode))
										lSuitabilityFlag = true;
								}
							}
						}
						if(lGATEScorePrevious.compareTo(lGATEScr)==0) //Checking this GATE Score is of which year.
						{
							while(it8.hasNext())
							{
								disciplineList = it8.next();
								if(disciplineList.contains(lDisciplineCode))
								{
									if(lEligibilityMap.get(disciplineList).contains(lGATEPreviousPaperCode))
										lSuitabilityFlag = true;
								}
							}
						}
						if(lSuitabilityFlag)
						{
							if(pApplicantsDataMap.get(lApp).get("txtSuitabilityTestRequired" + k).equals("Y"))//----Checking if Course requires Suitability Test.----//
							{
								if(pApplicantsDataMap.get(lApp).get("txtSuitabilityTestResult" + k).equals("Y"))//----Checking if Applicant has passed Suitability test.----//
									lSuitabilityFlag = true ;
								else
									lSuitabilityFlag = false;
							}
							else
								lSuitabilityFlag = true ;
						}

						SeatEligibility: if(lSuitabilityFlag == true && lSeatOfferedFlag != true)//----checking if Applicant is eligible and has not been already offered a seat.----//
						{

							if(pOfferDetailsMap.containsKey(lCourseChoice))
							{
								lDisciplineOfferMap = pOfferDetailsMap.get(lCourseChoice);
								logger.error(":lDisciplineOfferMap in SeatEligibilityLoop:"+lDisciplineOfferMap);
							}
							else
							{
								lSeatOfferedFlag = false ;
								break SeatEligibility;
							}
							Iterator<ArrayList<String>> i1 = lDisciplineOfferMap.keySet().iterator();
							DisciplineLoop : while(i1.hasNext())
							{
								lDisciplineList=i1.next();
								if(lDisciplineList.contains(lDisciplineCode))
								{
									lSeatQuantityList = lDisciplineOfferMap.get(lDisciplineList);
									lSeatQuantityListOrg = lDisciplineOfferMap.get(lDisciplineList);
									lDisciplineListOrg.clear();
									lDisciplineListOrg.addAll(lDisciplineList);
									break DisciplineLoop;
								}

							}
								
							if(lCourseChoice.equals("PH1Y"))
								System.out.println("hiiii");
							
							logger.error(":lSeatQuantityList:"+lSeatQuantityList);
							logger.error(":lSeatQuantityList.get(9):"+lSeatQuantityList.get(9));
							
							if(lSeatQuantityList.get(9)==null || lSeatQuantityList.get(9).equals(""))
							{
								lSeatQuantityList.set(9,"0");
							}

							lCourseOffers = Integer.parseInt(lSeatQuantityList.get(9));
							if(lCourseOffers>0)//----Checking if seat available for course----//
							{
								if(lSeatQuantityList.get(0).equals("Y"))
								{
									starCourseFlag = true;
									if(Integer.parseInt(lSeatQuantityList.get(10))>0)//----checking if there is seat available for Applicants in Star Discipline.----// 
										lSeatAvailableFlag = true;
								}
								else
									lSeatAvailableFlag = true;
							}
							logger.error(":lSeatAvailableFlag:"+lSeatAvailableFlag);
							if(pApplicantsDataMap.get(lApp).get("txtDisabilityValid").equals("Y"))//----Checking if Applicant is eligible for Disability Reservation Benefit----//
								lLoopVariable = 2;
							else
								lLoopVariable = 1;
							lApplicantCategory = pApplicantsDataMap.get(lApp).get("txtAppCategory");


							if(lSeatAvailableFlag)//if seat available in program proceed.
							{
								if(lCourseChoice.charAt(3)=='Y')//Checking course is of Type HTTA or not.
									lHTTAFlag = true;
								else
									lHTTAFlag = false;

								do
								{	
									lLoopVariable--;
									logger.error(":lSeatOfferedFlag:"+lSeatOfferedFlag);
									while(llLoopVar >0 && !lSeatOfferedFlag)
									{
										logger.error(":lLoopVar:"+llLoopVar);
										llLoopVar--;
										logger.error(":lLoopVar:"+llLoopVar);
										lSeatQuantityList = lDisciplineOfferMap.get(lDisciplineListOrg);
										lDisciplineList = lDisciplineListOrg;
										Integer lNoOfDisciplines = lDisciplineOfferMap.keySet().size();
										Iterator<ArrayList<String>> i2 = lDisciplineOfferMap.keySet().iterator();
										StarLoop :	do
										{
											lCategoryCourseOffers = Integer.parseInt(lSeatQuantityList.get(lSeatTypeNumber));//First time for General ,, 2nd time for PwD General
											logger.error(":lSeatTypeNumber:"+lSeatTypeNumber);
											logger.error(":lCategoryCourseOffers:"+lCategoryCourseOffers); //no. of seats for this particular course offer in that particular category
											lCourseOffers = Integer.parseInt(lSeatQuantityList.get(9)); //total no. of seats for all course offers

											if(starCourseFlag)
												lCategoryCourseOffersOrg = Integer.parseInt(lSeatQuantityListOrg.get(lSeatTypeNumber + 10));
											else
												lCategoryCourseOffersOrg = 1;
											if(lCategoryCourseOffers > 0 && lCategoryCourseOffersOrg > 0)
											{
												lSeatOfferedFlag = true;
												lCourseOffers--;
												lCategoryCourseOffers--;
												lSeatCategoryFlag = lSeatTypeNumber;
												logger.error(":lCourseOffers:"+lCourseOffers);
												logger.error(":lCategoryCourseOffers:"+lCategoryCourseOffers);
												logger.error(":lSeatQuantityList  but not updated:"+lSeatQuantityList);
																								
											}
											logger.error(":lNoOfDisciplines before loop of disciplines:"+lNoOfDisciplines);
											if(lNoOfDisciplines > 0)
											{
												if(lSeatOfferedFlag==false)//Trying to allocate seat to Applicant from Star Discipline Pool. 
												{									
													lDisciplineList = i2.next();
													if(lDisciplineList.contains(lDisciplineCode)== false)
													{
														if(lDisciplineOfferMap.get(lDisciplineList).get(0).equals("Y"))
														{
															lSeatQuantityList=lDisciplineOfferMap.get(lDisciplineList);
															logger.error(":lSeatQuantityList:"+lSeatQuantityList);
															lOtherDiscipline = lDisciplineList.get(0);
															logger.error("lOtherDiscipline"+lOtherDiscipline);
														}
													}
												}
												else
													break StarLoop;
											}
											lNoOfDisciplines--;
											logger.error(":lNoOfDisciplines after decrement:"+lNoOfDisciplines);
											logger.error(":lSeatOfferedFlag:"+lSeatOfferedFlag);
										}while(lNoOfDisciplines + 1 > 0 && !lSeatOfferedFlag);
										if(!lSeatOfferedFlag && lHTTAFlag && llLoopVar !=0)
										{
											if(lApplicantCategory.equals("OBC"))
												lSeatTypeNumber = lSeatTypeNumber + 1;
											else if(lApplicantCategory.equals("SC"))
												lSeatTypeNumber = lSeatTypeNumber + 2;
											else if(lApplicantCategory.equals("ST"))
												lSeatTypeNumber = lSeatTypeNumber + 3;
										}
									}
									if(!lSeatOfferedFlag && lLoopVariable!=0)
									{
										lSeatTypeNumber = 5;
										if(categoryValid)
											llLoopVar = 2;
										else
											llLoopVar = 1;
									}
									lCourseOffered = lCourseChoice ;
								}while(lLoopVariable > 0 && lSeatOfferedFlag != true);//Loop will always run 1 time for Non PwD Applicants and 2 times for PwD Applicants if Required.
							}
						}
						if(lSeatOfferedFlag)
						{
							lOfferedChoiceNo = k ;
							break CourseChoiceLoop;
						}
					}
//---------------------------Akshay changes here 
				}		
//--------------------------Akshay chnages end here				
					logger.error(":previouslyOffered:"+previouslyOffered);
					logger.error(":lApp:"+lApp);
					logger.error(":lSeatOfferedFlag:"+lSeatOfferedFlag);
					logger.error(":lOfferedChoiceNo:"+lOfferedChoiceNo);
					logger.error(":lOfferedChoiceLoopNo:"+lOfferedChoiceLoopNo);
					logger.error(":pApplicantsDataMap.get(lApp).get('form_status'):"+pApplicantsDataMap.get(lApp).get("form_status"));
					if(lSeatOfferedFlag)
					{
						//----- Checking whether Dynamic Update is required or not.-----//
						if((lOfferedChoiceNo < lOfferedChoiceLoopNo) && lOfferedChoiceLoopNo < 9 && pApplicantsDataMap.get(lApp).get("form_status").equals("AcceptWU"))
						{
							storeUpdateOfferDetails(lApp, previouslyOffered);
							logger.error("details updated in storeUpdateOfferDetails");
						}
							
						lChoice = lChoice + 80 ;
						logger.error(":lChoice:"+lChoice);
						lAlloted = lAlloted + (8-lOfferedChoiceNo+1)*10 ;
						logger.error(":lOfferedChoiceNo:"+lOfferedChoiceNo);
						logger.error(":lAlloted:"+lAlloted);
						lReturnValueMap.put(1, lCourseOffered);
						lReturnValueMap.put(2, lOfferedChoiceNo + "");
						lReturnValueMap.put(3, lSeatCategoryFlag + "");
						logger.error(":lReturnValueMap:"+lReturnValueMap);
						logger.error(":lSeatQuantityList:"+lSeatQuantityList);
						lSeatQuantityList.set(9, lCourseOffers+"");
						lSeatQuantityList.set(lSeatCategoryFlag, lCategoryCourseOffers+"");
						logger.error(":lSeatQuantityList updated now:"+lSeatQuantityList);
						lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
						logger.error(":lDisciplineOfferMap updated now:"+lDisciplineOfferMap);
						if(lDisciplineList.contains(lDisciplineListOrg.get(0)))
						{
							lSeatQuantityList.set(lSeatCategoryFlag + 10, lCategoryCourseOffers+"");
							lDisciplineForUpdate = lDisciplineCode ;
							if(lSeatQuantityList.get(0).equals("Y"))
							{
								lMaxOfferOfDisciplines = Integer.parseInt(lSeatQuantityList.get(10));
								lMaxOfferOfDisciplines--;
								lSeatQuantityList.set(10, lMaxOfferOfDisciplines+"");
								lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
								Iterator<ArrayList<String>> i3 = lDisciplineOfferMap.keySet().iterator();	
								while(i3.hasNext())
								{
									lDisciplineList = i3.next();
									if(lDisciplineList.contains(lDisciplineForUpdate)== false)
									{
										lSeatQuantityList=lDisciplineOfferMap.get(lDisciplineList);					
										Integer lOtherSeats = Integer.parseInt(lSeatQuantityList.get(9));
										lOtherSeats--;
										lSeatQuantityList.set(9,lOtherSeats + "");
										lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
									}					
								}
							}
						}
						else
						{
							lSeatQuantityListOrg.set(lSeatCategoryFlag + 10, lCategoryCourseOffersOrg-1 + "");
							lDisciplineOfferMap.put(lDisciplineListOrg, lSeatQuantityListOrg);
							lDisciplineForUpdate = lDisciplineCode ;
					
							Iterator<ArrayList<String>> i3 = lDisciplineOfferMap.keySet().iterator();	
							while(i3.hasNext())
							{
								lDisciplineList = i3.next();
								if(lDisciplineList.contains(lOtherDiscipline)== false)
								{
									lSeatQuantityList=lDisciplineOfferMap.get(lDisciplineList);					
									Integer lOtherSeats = Integer.parseInt(lSeatQuantityList.get(9));
									lOtherSeats--;
									lDisciplineOfferMap.get(lDisciplineList).set(9,lOtherSeats + "");
									lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
								}					
							}
						}		
						lReturnValueMap.put(4, lDisciplineForUpdate);
						pOfferDetailsMap.put(lCourseOffered, lDisciplineOfferMap);
						logger.error("pOfferDetailsMap"+pOfferDetailsMap);	
						logger.error("lReturnValueMap"+lReturnValueMap);
						pUpdateValueMap.put(lApp, lReturnValueMap);
						logger.error(":pUpdateValueMap:"+pUpdateValueMap);
					}
					logger.error("pOfferDetailsMap after mayank code:"+pOfferDetailsMap);
//-------------------------------------------------------------Started chnages by akshay-----------------------------------------------------------------------------------------------------------------------------------------
				//if(pApplicantsDataMap.get(lApp).get("form_status").equals("Accept") || pApplicantsDataMap.get(lApp).get("form_status").equals("AcceptWU") || pApplicantsDataMap.get(lApp).get("form_status").equals("RejectWU"))
			//		if(pApplicantsDataMap.get(lApp).get("form_status").equals("AcceptWU") && (lTempSet.size()== 1))
				if(pApplicantsDataMap.get(lApp).get("form_status").equals("AcceptWU") )
				{
					if((roundNumber.intValue()==2 || roundNumber.intValue()==3 || roundNumber.intValue()==4 || roundNumber.intValue()==5 || roundNumber.intValue()==6 || roundNumber.intValue()==7 || roundNumber.intValue()==8 || roundNumber.intValue()==9 || roundNumber.intValue()==10)&& (pUpdateValueMap.containsKey(lApp)== false)  && pApplicantsDataMap.get(lApp).get("txtCategoryValid")!=null && pApplicantsDataMap.get(lApp).get("txtCategoryValid").equals("Y"))
					{
						logger.error("in mine code");
							//	CategoryValidLoop :	if(pApplicantsDataMap.get(lApp).get("txtCategoryValid")!=null && pApplicantsDataMap.get(lApp).get("txtCategoryValid").equals("Y")) //---- Checking if applicant should be given Category Reservation Benefit ----//
						CategoryValidLoop: if (pApplicantsDataMap.get(lApp).get("txtCategoryValid")!=null && pApplicantsDataMap.get(lApp).get("txtCategoryValid").equals("Y"))
									{
										int m=1;
										int n=1;
										for(int i=1;i<9;i++)
										{
											logger.error("seat category"+"i="+pApplicantsDataMap.get(lApp).get("seatCategoryR"+i));
											if(pApplicantsDataMap.get(lApp).get("seatCategoryR"+i)!=null && !pApplicantsDataMap.get(lApp).get("seatCategoryR"+i).equals(""))
												m=i;
										}
									//	m=lOfferedChoiceLoopNo;
										logger.error("lOfferedChoiceLoopNo:"+lOfferedChoiceLoopNo);
										logger.error("m:"+m);
										logger.error(pApplicantsDataMap.get(lApp).get("seatCategoryR"+m));
										if(pApplicantsDataMap.get(lApp).get("choiceOfferedR"+m)!=null && !pApplicantsDataMap.get(lApp).get("choiceOfferedR"+m).equals(""))
											n=Integer.parseInt(pApplicantsDataMap.get(lApp).get("choiceOfferedR"+m));
										logger.error("n:"+n);
										String lCourseChoiceofferedakshay = "";
										lCourseOffers = 0;
										lCategoryCourseOffers = 0;
										lCategoryCourseOffersakshay=0;
										lCategoryCourseOffersOrg = 0;
										boolean lSuitabilityFlag = false ;
										boolean lHTTAFlag = false;
										boolean starCourseFlag = false ;
										boolean lSeatAvailableFlag = false;
										lSeatOfferedFlag = false;
										lSeatTypeNumber=1;
										lSeatTypeNumberakshay=1;
//---------------by akshay start---------------------------------------------------------------------------------										
					//					if(pApplicantsDataMap.get(lApp).get("seatCategoryR"+m)!=null && !pApplicantsDataMap.get(lApp).get("seatCategoryR"+m).equals(""))
					//					{

											if(Integer.parseInt(pApplicantsDataMap.get(lApp).get("seatCategoryR"+m))!= 1)
													{		
														logger.error("Inside CategoryValidLoop");
																							
														lDisciplineCode = pApplicantsDataMap.get(lApp).get("txtQualifyingDiscpline");
														if(pApplicantsDataMap.get(lApp).get("txtCourseChoice"+n)!=null && !pApplicantsDataMap.get(lApp).get("txtCourseChoice"+n).equals(""))
															lCourseChoiceofferedakshay = pApplicantsDataMap.get(lApp).get("txtCourseChoice"+n);
																					
														SeatEligibilityakshay: if(lSeatOfferedFlag != true)//----checking if Applicant is eligible and has not been already offered a seat.----//
														{

															if(pOfferDetailsMap.containsKey(lCourseChoiceofferedakshay))
															{
																lDisciplineOfferMap = pOfferDetailsMap.get(lCourseChoiceofferedakshay);
																logger.error(":lDisciplineOfferMap:"+lDisciplineOfferMap);
															}
															else
															{
																lSeatOfferedFlag = false ;
																break SeatEligibilityakshay;
															}
															Iterator<ArrayList<String>> i1 = lDisciplineOfferMap.keySet().iterator();
															DisciplineLoopakshay : while(i1.hasNext())
															{
																lDisciplineList=i1.next();
																if(lDisciplineList.contains(lDisciplineCode))
																{
																	lSeatQuantityList = lDisciplineOfferMap.get(lDisciplineList);
																	lSeatQuantityListOrg = lDisciplineOfferMap.get(lDisciplineList);
																	lDisciplineListOrg.clear();
																	lDisciplineListOrg.addAll(lDisciplineList);
																	break DisciplineLoopakshay;
																}

															}
															logger.error(":lSeatQuantityList:"+lSeatQuantityList);
															if(lSeatQuantityList.get(9)==null || lSeatQuantityList.get(9).equals(""))
															{
																lSeatQuantityList.set(9,"0");
															}

															lCourseOffers = Integer.parseInt(lSeatQuantityList.get(9));
															if(lCourseOffers>0)//----Checking if seat available for course----//
															{
																if(lSeatQuantityList.get(0).equals("Y"))
																{
																	starCourseFlag = true;
																	if(Integer.parseInt(lSeatQuantityList.get(10))>0)//----checking if there is seat available for Applicants in Star Discipline.----// 
																		lSeatAvailableFlag = true;
																}
																else
																	lSeatAvailableFlag = true;
															}
															lApplicantCategory = pApplicantsDataMap.get(lApp).get("txtAppCategory");
															if(lSeatAvailableFlag)//if seat available in program proceed.
															{
																
																if(lCourseChoiceofferedakshay.charAt(3)=='Y')//Checking course is of Type HTTA or not.
																	lHTTAFlag = true;
																else
																	lHTTAFlag = false;
																
																lSeatQuantityList = lDisciplineOfferMap.get(lDisciplineListOrg);
																lDisciplineList = lDisciplineListOrg;
																logger.error(":lDisciplineList:"+lDisciplineList);
																Integer lNoOfDisciplines = lDisciplineOfferMap.keySet().size();
																logger.error(":lNoOfDisciplines:"+lNoOfDisciplines);
																Iterator<ArrayList<String>> i2 = lDisciplineOfferMap.keySet().iterator();
															
															if(lApplicantCategory.equals("OBC"))
																lSeatTypeNumberakshay = lSeatTypeNumberakshay + 1;
															else if(lApplicantCategory.equals("SC"))
																lSeatTypeNumberakshay = lSeatTypeNumberakshay + 2;
															else if(lApplicantCategory.equals("ST"))
																lSeatTypeNumberakshay = lSeatTypeNumberakshay + 3;
															
												StarLoopakshay:	do
												{				
																lCategoryCourseOffers = Integer.parseInt(lSeatQuantityList.get(lSeatTypeNumber));
																lCategoryCourseOffersakshay=Integer.parseInt(lSeatQuantityList.get(lSeatTypeNumberakshay));
																logger.error(":lSeatQuantityList:"+lSeatQuantityList);
																logger.error(":lSeatTypeNumber:"+lSeatTypeNumber);
																logger.error(":lSeatTypeNumberakshay:"+lSeatTypeNumberakshay);
																logger.error(":lCategoryCourseOffers:"+lCategoryCourseOffers);
																logger.error(":lCategoryCourseOffersakshay:"+lCategoryCourseOffersakshay);
																lCourseOffers = Integer.parseInt(lSeatQuantityList.get(9));
																if(starCourseFlag)
																{
																	lCategoryCourseOffersOrg = Integer.parseInt(lSeatQuantityListOrg.get(lSeatTypeNumber + 10));
																	logger.error("lSeatQuantityListOrg if starcourse:"+lSeatQuantityListOrg);
																}
																else
																	lCategoryCourseOffersOrg = 1;
																
																logger.error("lCategoryCourseOffersOrg:"+lCategoryCourseOffersOrg);
																if(lCategoryCourseOffers > 0 && lCategoryCourseOffersOrg > 0)
																{
																	lSeatOfferedFlag = true;
																	lCourseOffers--;
																	lCategoryCourseOffers--;
																	lCategoryCourseOffersakshay++;
																	lSeatCategoryFlag = lSeatTypeNumber;
																	lSeatCategoryFlagakshay = lSeatTypeNumberakshay;
																	logger.error("lCourseOfferse after decrement:"+lCourseOffers);
																	logger.error("lCategoryCourseOffers after decrement:"+lCategoryCourseOffers);
																	logger.error("lCategoryCourseOffersakshay after increment:"+lCategoryCourseOffersakshay);
																	logger.error("lSeatQuantityListOrg:"+lSeatQuantityListOrg);
																	logger.error(":lSeatQuantityList:"+lSeatQuantityList);
																	
																}
																logger.error(":lNoOfDisciplines before loop of disciplines:"+lNoOfDisciplines);
																if(lNoOfDisciplines > 0)
																{
																	if(lSeatOfferedFlag==false)//Trying to allocate seat to Applicant from Star Discipline Pool. 
																	{									
																		lDisciplineList = i2.next();
																		if(lDisciplineList.contains(lDisciplineCode)== false)
																		{
																			logger.error(":lDisciplineCode:"+lDisciplineCode);
																			if(lDisciplineOfferMap.get(lDisciplineList).get(0).equals("Y"))
																			{
																				lSeatQuantityList=lDisciplineOfferMap.get(lDisciplineList);
																				lOtherDiscipline = lDisciplineList.get(0);
																			}
																		}
																	}
																	else
																		break StarLoopakshay;
																}
																lNoOfDisciplines--;
																logger.error(":lNoOfDisciplines after decrement:"+lNoOfDisciplines);
																logger.error(":lSeatOfferedFlag:"+lSeatOfferedFlag);
												}while(lNoOfDisciplines + 1 > 0 && !lSeatOfferedFlag);
																
																if(lApplicantCategory.equals("OBC"))
																		lSeatTypeNumber = lSeatTypeNumber + 1;
																else if(lApplicantCategory.equals("SC"))
																		lSeatTypeNumber = lSeatTypeNumber + 2;
																else if(lApplicantCategory.equals("ST"))
																		lSeatTypeNumber = lSeatTypeNumber + 3;
																
																lCourseOffered = lCourseChoiceofferedakshay;
																logger.error(":lCourseOffered or lCourseChoiceofferedakshay:"+lCourseOffered);
															}
															
													}
														if(lSeatOfferedFlag)
														{
															lOfferedChoiceNo = n ;
													//		break CategoryValidLoop;
														}
														
											//	}
													}	
					//					}
//-----------------by akshay end-----------------------------------------------------------------------------------										
										
									}
										if(lSeatOfferedFlag)
										{
											if(pApplicantsDataMap.get(lApp).get("form_status").equals("AcceptWU") || pApplicantsDataMap.get(lApp).get("form_status").equals("RejectWU") || pApplicantsDataMap.get(lApp).get("form_status").equals("Accept"))
											{
												logger.error("lApp"+lApp);
												logger.error("previouslyOffered:"+previouslyOffered);
												storeUpdateOfferDetails(lApp, previouslyOffered);
											}
																	
										}
									/*	if(lSeatQuantityList.get(9)==null || lSeatQuantityList.get(9).equals(""))
										{
											lSeatQuantityList.set(9,"0");
										}*/
										if(lSeatOfferedFlag)
										{
											lChoice = lChoice + 80 ;
											lAlloted = lAlloted + (8-lOfferedChoiceNo+1)*10 ;
											lReturnValueMap.put(1, lCourseOffered);
											lReturnValueMap.put(2, lOfferedChoiceNo + "");
											lReturnValueMap.put(3, lSeatCategoryFlag + "");
											logger.error(":lReturnValueMap:"+lReturnValueMap);
											logger.error(":lCourseOffers:"+lCourseOffers);
											logger.error(":lSeatQuantityList:"+lSeatQuantityList);
											lSeatQuantityList.set(9, lCourseOffers+"");
											lSeatQuantityList.set(lSeatCategoryFlag, lCategoryCourseOffers+"");	
											lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
											if(lDisciplineList.contains(lDisciplineListOrg.get(0)))
											{
												lSeatQuantityList.set(lSeatCategoryFlag + 10, lCategoryCourseOffers+"");
												lDisciplineForUpdate = lDisciplineCode ;
												if(lSeatQuantityList.get(0).equals("Y"))
												{
													lMaxOfferOfDisciplines = Integer.parseInt(lSeatQuantityList.get(10));
													lMaxOfferOfDisciplines--;
													lSeatQuantityList.set(10, lMaxOfferOfDisciplines+"");
													lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
													Iterator<ArrayList<String>> i3 = lDisciplineOfferMap.keySet().iterator();	
													while(i3.hasNext())
													{
														lDisciplineList = i3.next();
														if(lDisciplineList.contains(lDisciplineForUpdate)== false)
														{
															lSeatQuantityList=lDisciplineOfferMap.get(lDisciplineList);					
															Integer lOtherSeats = Integer.parseInt(lSeatQuantityList.get(9));
															lOtherSeats--;
															lSeatQuantityList.set(9,lOtherSeats + "");
															lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
														}					
													}
												}
//-------------------Added by akshay------------------------------------------------------------------------------------------------------												
												else
												{
													lCourseOffers++;
													lSeatQuantityList.set(9, lCourseOffers+"");
													lSeatQuantityList.set(lSeatCategoryFlagakshay, lCategoryCourseOffersakshay+"");	
													lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
												}
//---------------------End by akshay-------------------------------------------------------------------------------------------------------												
												
											}
											else
											{
												lSeatQuantityListOrg.set(lSeatCategoryFlag + 10, lCategoryCourseOffersOrg-1 + "");
												lDisciplineOfferMap.put(lDisciplineListOrg, lSeatQuantityListOrg);
												lDisciplineForUpdate = lDisciplineCode ;
										
												Iterator<ArrayList<String>> i3 = lDisciplineOfferMap.keySet().iterator();	
												while(i3.hasNext())
												{
													lDisciplineList = i3.next();
													if(lDisciplineList.contains(lOtherDiscipline)== false)
													{
														lSeatQuantityList=lDisciplineOfferMap.get(lDisciplineList);					
														Integer lOtherSeats = Integer.parseInt(lSeatQuantityList.get(9));
														lOtherSeats--;
														lDisciplineOfferMap.get(lDisciplineList).set(9,lOtherSeats + "");
														lDisciplineOfferMap.put(lDisciplineList, lSeatQuantityList);
													}					
												}
											}
											logger.error(":lSeatQuantityList:"+lSeatQuantityList);
											lReturnValueMap.put(4, lDisciplineForUpdate);
											pOfferDetailsMap.put(lCourseOffered, lDisciplineOfferMap);
											pUpdateValueMap.put(lApp, lReturnValueMap);
										}
										
												
										
										logger.error("in mine code pOfferDetailsMap"+pOfferDetailsMap);
										logger.error("in mine code+ pUpdateValueMap"+pUpdateValueMap);
					}
				}
//-----------------------------------------------------end changes by akshay------------------------------------------------------------------------					
				
					
					
					
				}
				lMeritListMap.pollLastEntry();
			}
			lChoiceAllotmentRatio = lAlloted/lChoice ;
			logger.error("The Choice Allotment Ratio = "+ lChoiceAllotmentRatio);	
		}
		catch(Exception e)
		{
			logger.error("Exception in Method generateValuesForEform");
			e.printStackTrace();
		}
	}
	
	//------ Function is for creating Merit List on the basis of GATE Scores -----//
	public static TreeMap<Integer, TreeSet<String>> createMeritList(TreeMap<Integer, TreeSet<String>> lMeritListMap)
	{
		Integer lGATEScore = 0;
		for(String lAppSeqNo:pApplicantsDataMap.keySet())
		{
			lGATEScore = 0;
			//----- This tree set will store Application Sequence Numbers mapped against a GATE score. ----//
			TreeSet<String> lAppSeqTreeSet = new TreeSet<String>();
			//----- This map is used to store data of an Applicant ----//
			HashMap<String, String> lApplicantDataMap = new HashMap<String, String>();
			lApplicantDataMap = pApplicantsDataMap.get(lAppSeqNo);

			if(lApplicantDataMap.get("txtGATE2013Score")!=null && !lApplicantDataMap.get("txtGATE2013Score").equals(""))			
				lGATEScore = Integer.parseInt(lApplicantDataMap.get("txtGATE2013Score"));
			else
			{
				if(lApplicantDataMap.get("txtGATE2014Score")!=null && !lApplicantDataMap.get("txtGATE2014Score").equals(""))
					lGATEScore = Integer.parseInt(lApplicantDataMap.get("txtGATE2014Score"));
			}

			if(lMeritListMap.containsKey(lGATEScore) && lGATEScore > 0)
			{
				lAppSeqTreeSet = lMeritListMap.get(lGATEScore);
				lAppSeqTreeSet.add(lAppSeqNo);
				lMeritListMap.put(lGATEScore, lAppSeqTreeSet);	   
			}
			else if(lGATEScore > 0)
			{
				lAppSeqTreeSet.add(lAppSeqNo);
				lMeritListMap.put(lGATEScore, lAppSeqTreeSet);
			}		
		}
		return lMeritListMap ;
	}

	//------ Function is for resolving tie condition and give a list of eligible(who are getting seats) Applicants -----//
	public static HashMap<String, Integer> resolveTieCondition(ArrayList<String> lLoopList, TreeSet<String> lTempSet, Integer lGATEScr, HashMap<String , Integer> lStartChoiceNoMap)
	{ 
		logger.error("Inside class : IITMSeatAllocation -- Status : In resolveTieCondition");
		TreeMap<String, ArrayList<String>> courseAllocation = new TreeMap<String, ArrayList<String>>();
		ArrayList<String> lTempSet1 = new ArrayList<String>();
		ArrayList<String> lTempSet3 = new ArrayList<String>();
		lTempSet1.addAll(lTempSet);
		Iterator<String> it9 = lTempSet1.iterator();
		Integer lGATEScoreCurrent = 0;
		Integer lGATEScorePrevious = 0;
		String lGATECurrentPaperCode = "";
		String lGATEPreviousPaperCode = "";
		while(it9.hasNext())
		{
			String lAppSeq = "";	
			lAppSeq = it9.next();
			String lCourseChoices = "";
			ArrayList<String> choiceList = new ArrayList<String>();
			for(int h=1;h<9;h++)
			{
				if(!pApplicantsDataMap.get(lAppSeq).get("txtCourseChoice" + h).equals("")&& pApplicantsDataMap.get(lAppSeq).get("txtCourseChoice" + h)!=null)
				{
					lCourseChoices = pApplicantsDataMap.get(lAppSeq).get("txtCourseChoice" + h).trim();
					choiceList.add(h-1,lCourseChoices);
				}
			}
			courseAllocation.put(lAppSeq, choiceList);
		}
		if(lGATEScr == 721)
			System.out.println("hi");
		else
			logger.error("ur gate score is :"+lGATEScr);
		HashMap<String , HashMap<ArrayList<String>, ArrayList<String>>> lOfferedCourseMap = new HashMap<String , HashMap<ArrayList<String>, ArrayList<String>>>(); 
		HashMap<String , HashMap<ArrayList<String>, HashMap<ArrayList<String>, HashMap<Integer,String>>>> lOfferedSeatCourseMap = new HashMap<String , HashMap<ArrayList<String>, HashMap<ArrayList<String>, HashMap<Integer,String>>>>(); 
		HashMap<String, Integer> lDeallocationMap = new HashMap<String, Integer>();
		HashMap<String, Integer> lReallocationMap = new HashMap<String, Integer>();

		AllTieResolved : for(int g=1;g<16;g++)
		{
			if(lGATEScr == 775)
			{
				logger.error("value of g in :"+g);
				System.out.println("g");
			}
			if(lGATEScr == 522)
			{
				logger.error("value of g in :"+g);
				System.out.println("g");
			}
			if(lGATEScr == 509)
			{
				logger.error("value of g in :"+g);
				System.out.println("g");
			}
			if(lGATEScr == 831)
			{
				logger.error("value of g in :"+g);
				System.out.println("g");
			}
			logger.error("value of g:"+g);
			String lSeqNo = "";
			String lDisicipline = "";
			Integer lCourseTie ;
			
			boolean lEligibleFlag ;
			HashMap<String , HashMap<ArrayList<String>, Integer>> lCourseMap = new HashMap<String, HashMap<ArrayList<String>, Integer>>(32);
			HashMap<String , HashMap<ArrayList<String>, ArrayList<String>>> lCourseAppMap = new HashMap<String , HashMap<ArrayList<String>, ArrayList<String>>>(32); 

			lTempSet1.removeAll(lTempSet3);
			logger.error(":lTempSet1 in AllTieResolved loop:"+lTempSet1);
			lTempSet3.clear();
			Iterator<String> it = lTempSet1.iterator();
			if(g<9)
			{
				while(it.hasNext())//checking for their choices whether they are same or different
				{
					lSeqNo = it.next();
					String lCourseChoice ="";
					String lCourseOffered = "";
				//	logger.error("lSeqNo in checkin their chpoices same or different::"+lSeqNo);
				//	logger.error("::pApplicantsDataMap.get(lSeqNo).get('txtCourseChoice' + g):::"+"G="+g+"& pApplicantsDataMap.get(lSeqNo)"+pApplicantsDataMap.get(lSeqNo)+":::::"+pApplicantsDataMap.get(lSeqNo).get("txtCourseChoice" + g));
					if(!pApplicantsDataMap.get(lSeqNo).get("txtCourseChoice" + g).equals(""))
					{
						lGATEScoreCurrent = 0;
						lGATEScorePrevious = 0;
						if(pApplicantsDataMap.get(lSeqNo).get("txtCourseChoice" + g)!=null)
						{
							lCourseChoice = pApplicantsDataMap.get(lSeqNo).get("txtCourseChoice" + g).trim();
			//				logger.error(":lCourseChoice:"+pApplicantsDataMap.get(lSeqNo).get("txtCourseChoice" + g)+":::");
						}							
							lDisicipline = pApplicantsDataMap.get(lSeqNo).get("txtQualifyingDiscpline");
				//			logger.error(":lDisicipline:"+pApplicantsDataMap.get(lSeqNo).get("txtQualifyingDiscpline")+":::");

						//-----We will check if Applicant's Choice is valid for this GATE Score.------//
						if(pApplicantsDataMap.get(lSeqNo).get("txtGATE2013Score")!=null && !pApplicantsDataMap.get(lSeqNo).get("txtGATE2013Score").equals(""))
						{
							lGATEScoreCurrent = Integer.parseInt(pApplicantsDataMap.get(lSeqNo).get("txtGATE2013Score").trim());
					//	logger.error(":lGATEScoreCurrent:"+lGATEScoreCurrent);
						}
						if(pApplicantsDataMap.get(lSeqNo).get("txtGATE2014Score")!=null && !pApplicantsDataMap.get(lSeqNo).get("txtGATE2014Score").equals(""))
						{	lGATEScorePrevious = Integer.parseInt(pApplicantsDataMap.get(lSeqNo).get("txtGATE2014Score").trim());
					//	logger.error(":lGATEScorePrevious:"+lGATEScorePrevious);
						}
						if(pApplicantsDataMap.get(lSeqNo).get("txtGATE2013PaperCode")!=null && !pApplicantsDataMap.get(lSeqNo).get("txtGATE2013PaperCode").equals(""))			
						{	lGATECurrentPaperCode = pApplicantsDataMap.get(lSeqNo).get("txtGATE2013PaperCode").trim();
					//	logger.error(":lGATECurrentPaperCode:"+lGATECurrentPaperCode);
						}
						if(pApplicantsDataMap.get(lSeqNo).get("txtGATE2014PaperCode")!=null && !pApplicantsDataMap.get(lSeqNo).get("txtGATE2014PaperCode").equals(""))
						{	lGATEPreviousPaperCode = pApplicantsDataMap.get(lSeqNo).get("txtGATE2014PaperCode").trim();
					//	logger.error(":lGATEPreviousPaperCode:"+lGATEPreviousPaperCode);
						}
						HashMap<ArrayList<String>, ArrayList<String>> lEligibilityMap = new HashMap<ArrayList<String>, ArrayList<String>>();
						lEligibilityMap = pCoursewiseGATEPaperEligibilityMap.get(lCourseChoice);
				//		logger.error(":lEligibilityMap:"+lEligibilityMap);
						Iterator<ArrayList<String>> it8 = lEligibilityMap.keySet().iterator();
						ArrayList<String> disciplineList = new ArrayList<String>();
						lEligibleFlag = false ;
						if(lGATEScoreCurrent.compareTo(lGATEScr)==0)//Checking this GATE Score is of which year.
						{
							while(it8.hasNext())
							{
								disciplineList = it8.next();
					//			logger.error(":disciplineList:"+disciplineList);
								if(disciplineList.contains(lDisicipline))
								{
									if(lEligibilityMap.get(disciplineList).contains(lGATECurrentPaperCode))
										lEligibleFlag = true;
								}
							}
						}
						if(lGATEScorePrevious.compareTo(lGATEScr)==0) //Checking this GATE Score is of which year.
						{
							while(it8.hasNext())
							{
								disciplineList = it8.next();
					//			logger.error(":disciplineList:"+disciplineList);
								if(disciplineList.contains(lDisicipline))
								{
									if(lEligibilityMap.get(disciplineList).contains(lGATEPreviousPaperCode))
										lEligibleFlag = true;
								}
							}
						}
						if(pApplicantsDataMap.get(lSeqNo).get("txtOfflineOfferedCourse")!=null && !pApplicantsDataMap.get(lSeqNo).get("txtOfflineOfferedCourse").equals(""))
							{
							lCourseOffered = pApplicantsDataMap.get(lSeqNo).get("txtOfflineOfferedCourse");
					//		logger.error(":lCourseOfferedinoffline:"+lCourseOffered);
							}
						
						if(pApplicantsDataMap.get(lSeqNo).get("choiceOfferedR"+roundNumber)==null || pApplicantsDataMap.get(lSeqNo).get("choiceOfferedR"+roundNumber).equals(""))
						{
							if(pApplicantsDataMap.get(lSeqNo).get("txtOnlineOfferedCourse")!=null && !pApplicantsDataMap.get(lSeqNo).get("txtOnlineOfferedCourse").equals(""))
							{
								lCourseOffered = pApplicantsDataMap.get(lSeqNo).get("txtOnlineOfferedCourse");
					//			logger.error(":lCourseOfferedinonline:"+lCourseOffered);
							}
								
						}

						if(lCourseOffered.equalsIgnoreCase(lCourseChoice))
						{
							lEligibleFlag = false;
							lTempSet3.add(lSeqNo);
					//		logger.error(":lTempSet3:"+lTempSet3);
						}
						
						if(pApplicantsDataMap.get(lSeqNo).get("txtSuitabilityTestRequired" + g).equals("Y"))//----Checking if Course requires Suitability Test.----//
						{
							if(pApplicantsDataMap.get(lSeqNo).get("txtSuitabilityTestResult" + g).equals("Y"))//----Checking if Applicant has passed Suitability test.----//
								lEligibleFlag = true ;
							else
								lEligibleFlag = false;
						}
						else
							lEligibleFlag = true ;
						
						if(lEligibleFlag)
						{	
							HashMap<ArrayList<String>, Integer> lDisciplineAppMap = new HashMap<ArrayList<String>, Integer>();
							HashMap<ArrayList<String>, ArrayList<String>> lDisciplineAppSeqMap = new HashMap<ArrayList<String>,ArrayList<String>>();
							ArrayList<String> lDisAppList = new ArrayList<String>();	
							ArrayList<String> lAppList = new ArrayList<String>();
							lAppList.add(lSeqNo); //logger.error(":lAppList:"+lAppList);
							lCourseTie = 1;
				//			logger.error(":lCourseMap:"+lCourseMap);
							if(lCourseMap.containsKey(lCourseChoice))
							{
								lDisciplineAppMap = lCourseMap.get(lCourseChoice);
				//				logger.error("lDisciplineAppMap"+lDisciplineAppMap);
								lDisciplineAppSeqMap = lCourseAppMap.get(lCourseChoice);
				//				logger.error("lDisciplineAppSeqMap"+lDisciplineAppSeqMap);
								Iterator<ArrayList<String>> it1 = pOfferDetailsMap.get(lCourseChoice).keySet().iterator();					
								Dis :while(it1.hasNext())
								{

									lDisAppList = it1.next();
									if(lDisAppList.contains(lDisicipline))
									{
										if(lDisciplineAppMap.containsKey(lDisAppList))
										{							
											lCourseTie = lDisciplineAppMap.get(lDisAppList);
				//							logger.error("lCourseTie:"+lCourseTie);
											lCourseTie++;
											lDisciplineAppMap.put(lDisAppList, lCourseTie);
											lAppList.addAll(lDisciplineAppSeqMap.get(lDisAppList));
											lDisciplineAppSeqMap.put(lDisAppList, lAppList);
										}
										else
										{
											lDisciplineAppMap.put(lDisAppList, lCourseTie);
											lDisciplineAppSeqMap.put(lDisAppList, lAppList);
										}
										break Dis;
									}
								}
							}
							else
							{ 
								Iterator<ArrayList<String>> it1 = pOfferDetailsMap.get(lCourseChoice).keySet().iterator();	
					//			logger.error(":it1:"+it1);
								Dis : while(it1.hasNext())
								{
									lDisAppList = it1.next();
					//				logger.error(":it1:"+it1);
					//				logger.error(":lDisAppList:"+lDisAppList);
									if(lDisAppList.contains(lDisicipline))
									{
										lDisciplineAppMap.put(lDisAppList, lCourseTie);
					//					logger.error("lDisciplineAppMap:"+lDisciplineAppMap);
										lDisciplineAppSeqMap.put(lDisAppList, lAppList);
					//					logger.error("lDisciplineAppSeqMap:"+lDisciplineAppSeqMap);
										break Dis ;
									}
								}
							}
					//		logger.error("lDisciplineAppMap:"+lDisciplineAppMap);
					//		logger.error("lDisciplineAppSeqMap:"+lDisciplineAppSeqMap);
							if(lOfferedCourseMap.containsKey(lCourseChoice))
							{
								if(lOfferedCourseMap.get(lCourseChoice).containsKey(lDisAppList))
								{
									ArrayList<String> lDisciplineOffersList = new ArrayList<String>();

									lDisciplineOffersList = lOfferedCourseMap.get(lCourseChoice).get(lDisAppList);
									int apps = 0;
									apps = lDisciplineAppMap.get(lDisAppList) + lDisciplineOffersList.size();

									lDisciplineAppMap.put(lDisAppList, apps);
									for(String app :lDisciplineOffersList)
									{
										lDisciplineAppSeqMap.get(lDisAppList).add(app);
										lDeallocationMap.put(app, lStartChoiceNoMap.get(app));
										lStartChoiceNoMap.remove(app);
										lLoopList.remove(app);
									}
									lOfferedCourseMap.get(lCourseChoice).remove(lDisAppList);
								}

								if(lOfferedSeatCourseMap.get(lCourseChoice).containsKey(lDisAppList))
								{
									ArrayList<String> tempo = new ArrayList<String>();
									Iterator<ArrayList<String>> it10 = lOfferedSeatCourseMap.get(lCourseChoice).get(lDisAppList).keySet().iterator();					
									while(it10.hasNext())
									{
										tempo = it10.next();
										Integer totalseat = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(9));
										Integer maxseat = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(10));
										Integer totalseatorg = totalseat;
										for(Integer t:lOfferedSeatCourseMap.get(lCourseChoice).get(lDisAppList).get(tempo).keySet())
										{
											Integer tempseat = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(t));
											tempseat-- ;
											pOfferDetailsMap.get(lCourseChoice).get(tempo).set(t,tempseat + "");
											totalseat-- ;
											pOfferDetailsMap.get(lCourseChoice).get(tempo).set(9,totalseat + "");	
											maxseat-- ;
											pOfferDetailsMap.get(lCourseChoice).get(tempo).set(10,maxseat + "");
										}
										if(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(0).equals("Y"))
										{
											ArrayList<String> tempo1 = new ArrayList<String>();
											Iterator<ArrayList<String>> it11 = pOfferDetailsMap.get(lCourseChoice).keySet().iterator();					
											while(it11.hasNext())
											{
												tempo1 = it11.next();
												if(!tempo.get(0).equals(tempo1.get(0)))
												{
													Integer temporary = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo1).get(9));
													temporary = temporary - totalseatorg + totalseat;
													pOfferDetailsMap.get(lCourseChoice).get(tempo1).set(9,temporary + "");
												}
											}
										}
									}
									lOfferedSeatCourseMap.get(lCourseChoice).remove(lDisAppList);
								}
							}

							lCourseMap.put(lCourseChoice, lDisciplineAppMap);							
							lCourseAppMap.put(lCourseChoice, lDisciplineAppSeqMap);		
						}
					}
				}
			}
			logger.error(":lCourseMap:"+lCourseMap);
			logger.error(":lCourseAppMap:"+lCourseAppMap);
			logger.error(":lDeallocationMap:"+lDeallocationMap);
			logger.error(":lReallocationMap:"+lReallocationMap);
			for(String lApp:lReallocationMap.keySet())
			{
				String lCourseChoice = "";
				String lDisicipline2 = "";
				lDisicipline2 = pApplicantsDataMap.get(lApp).get("txtQualifyingDiscpline");
				ArrayList<String> temp1 = new ArrayList<String>();
				temp1 = courseAllocation.get(lApp);
				if(temp1.size()> lReallocationMap.get(lApp))
				{
					lDeallocationMap.put(lApp, lReallocationMap.get(lApp)+1);
					if(temp1.get(lReallocationMap.get(lApp))!=null)
					{
						lCourseChoice = temp1.get(lReallocationMap.get(lApp));
						HashMap<ArrayList<String>, Integer> lDisciplineAppMap = new HashMap<ArrayList<String>, Integer>();
						HashMap<ArrayList<String>, ArrayList<String>> lDisciplineAppSeqMap = new HashMap<ArrayList<String>,ArrayList<String>>();
						ArrayList<String> lDisAppList = new ArrayList<String>();	
						ArrayList<String> lAppList = new ArrayList<String>();
						lAppList.add(lApp);
						lCourseTie = 1;
						if(lCourseMap.containsKey(lCourseChoice))
						{
							lDisciplineAppMap = lCourseMap.get(lCourseChoice);
							lDisciplineAppSeqMap = lCourseAppMap.get(lCourseChoice);
							Iterator<ArrayList<String>> it1 = pOfferDetailsMap.get(lCourseChoice).keySet().iterator();					
							Dis :while(it1.hasNext())
							{

								lDisAppList = it1.next();
								if(lDisAppList.contains(lDisicipline2))
								{
									if(lDisciplineAppMap.containsKey(lDisAppList))
									{							
										lCourseTie = lDisciplineAppMap.get(lDisAppList);
										lCourseTie++;
										lDisciplineAppMap.put(lDisAppList, lCourseTie);
										lAppList.addAll(lDisciplineAppSeqMap.get(lDisAppList));
										lDisciplineAppSeqMap.put(lDisAppList, lAppList);
									}
									else
									{
										lDisciplineAppMap.put(lDisAppList, lCourseTie);
										lDisciplineAppSeqMap.put(lDisAppList, lAppList);
									}
									break Dis;
								}
							}
						}
						else if(lOfferedCourseMap.containsKey(lCourseChoice))
						{
							Iterator<ArrayList<String>> it1 = pOfferDetailsMap.get(lCourseChoice).keySet().iterator();					
							Dis :while(it1.hasNext())
							{
								ArrayList<String> ltemplist = new ArrayList<String>();
								ltemplist = it1.next();
								if(ltemplist.contains(lDisicipline2))
								{
									lDisAppList.addAll(ltemplist);
									break Dis;
								}
							}
							if(lOfferedCourseMap.get(lCourseChoice).containsKey(lDisAppList))
							{
								ArrayList<String> lDisciplineOffersList = new ArrayList<String>();

								lDisciplineOffersList = lOfferedCourseMap.get(lCourseChoice).get(lDisAppList);
								int apps = 0;
								if(lDisciplineAppMap.containsKey(lDisAppList))
									apps = lDisciplineAppMap.get(lDisAppList) + lDisciplineOffersList.size();
								else
									apps = lDisciplineOffersList.size() + 1;
								lDisciplineAppMap.put(lDisAppList, apps);

								for(String app :lDisciplineOffersList)
								{
									lAppList.add(app);
									lDisciplineAppSeqMap.put(lDisAppList, lAppList);
									lDeallocationMap.put(app, lStartChoiceNoMap.get(app));
									lStartChoiceNoMap.remove(app);
									lLoopList.remove(app);
								}
								lOfferedCourseMap.get(lCourseChoice).remove(lDisAppList);
							}

							if(lOfferedSeatCourseMap.get(lCourseChoice).containsKey(lDisAppList))
							{
								ArrayList<String> tempo = new ArrayList<String>();
								Iterator<ArrayList<String>> it10 = lOfferedSeatCourseMap.get(lCourseChoice).get(lDisAppList).keySet().iterator();					
								while(it10.hasNext())
								{
									tempo = it10.next();
									Integer totalseat = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(9));
									Integer maxseat = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(10));
									Integer totalseatorg = totalseat;
									for(Integer t:lOfferedSeatCourseMap.get(lCourseChoice).get(lDisAppList).get(tempo).keySet())
									{
										Integer tempseat = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(t));
										tempseat-- ;
										pOfferDetailsMap.get(lCourseChoice).get(tempo).set(t,tempseat + "");
										totalseat-- ;
										pOfferDetailsMap.get(lCourseChoice).get(tempo).set(9,totalseat + "");	
										maxseat-- ;
										pOfferDetailsMap.get(lCourseChoice).get(tempo).set(10,maxseat + "");
									}
									if(pOfferDetailsMap.get(lCourseChoice).get(tempo).get(0).equals("Y"))
									{
										ArrayList<String> tempo1 = new ArrayList<String>();
										Iterator<ArrayList<String>> it11 = pOfferDetailsMap.get(lCourseChoice).keySet().iterator();					
										while(it11.hasNext())
										{
											tempo1 = it11.next();
											if(!tempo.get(0).equals(tempo1.get(0)))
											{
												Integer temporary = Integer.parseInt(pOfferDetailsMap.get(lCourseChoice).get(tempo1).get(9));
												temporary = temporary - totalseatorg + totalseat;
												pOfferDetailsMap.get(lCourseChoice).get(tempo1).set(9,temporary + "");
											}
										}
									}
								}
								lOfferedSeatCourseMap.get(lCourseChoice).remove(lDisAppList);
							}
						}
						else
						{ 
							Iterator<ArrayList<String>> it1 = pOfferDetailsMap.get(lCourseChoice).keySet().iterator();					
							Dis : while(it1.hasNext())
							{
								lDisAppList = it1.next();
								if(lDisAppList.contains(lDisicipline2))
								{
									lDisciplineAppMap.put(lDisAppList, lCourseTie);
									lDisciplineAppSeqMap.put(lDisAppList, lAppList);
									break Dis ;
								}
							}
						}
						lCourseMap.put(lCourseChoice, lDisciplineAppMap);							
						lCourseAppMap.put(lCourseChoice, lDisciplineAppSeqMap);
					}
				}
			}
			lReallocationMap.clear();
			
			logger.error(":::::Preparing a List on which loop will run for seat Allocation::::::");
			HashMap<ArrayList<String>, ArrayList<String>> lDisciplineOfferMap = new HashMap<ArrayList<String>, ArrayList<String>>();
			for(String course:lCourseMap.keySet())//Preparing a List on which loop will run for seat Allocation.
			{
				if(course.equals("PH1Y"))
					System.out.println("hiiiiiii");
				HashMap<ArrayList<String>, Integer> lDisciplineAppMap1 = new HashMap<ArrayList<String>, Integer>();
				lDisciplineAppMap1 = lCourseMap.get(course);
		//		logger.error(":lDisciplineAppMap1:"+lDisciplineAppMap1);
				HashMap<ArrayList<String>, ArrayList<String>> lDisciplineOfferedMap = new HashMap<ArrayList<String>, ArrayList<String>>();
				HashMap<ArrayList<String>, HashMap<ArrayList<String>, HashMap<Integer,String>>> lSeatOfferedMap = new HashMap<ArrayList<String>, HashMap<ArrayList<String>, HashMap<Integer,String>>>();

				Iterator<ArrayList<String>> it2 = lDisciplineAppMap1.keySet().iterator();				
				lDisciplineOfferMap = pOfferDetailsMap.get(course);
				while(it2.hasNext())
				{	
					ArrayList<String> disciList = new ArrayList<String>();
					disciList = it2.next();	
					ArrayList<String> ldisciList = new ArrayList<String>();
					ArrayList<String> lldisciList = new ArrayList<String>();
					ldisciList.addAll(disciList);
					lldisciList.addAll(disciList);
					Integer lGN = 0;
					Integer lOBC = 0;
					Integer lSC = 0;
					Integer lST = 0;
					Integer lSCST = 0;

					ArrayList<String> courseOfferedList = new ArrayList<String>();
					HashMap<ArrayList<String>, HashMap<Integer,String>> starseatcategoryMap = new HashMap<ArrayList<String>, HashMap<Integer,String>>();
					HashMap<Integer, String> seatCategoryMap = new HashMap<Integer, String>();

					ArrayList<String> lGNList = new ArrayList<String>();
					ArrayList<String> lOBCList = new ArrayList<String>();
					ArrayList<String> lSCList = new ArrayList<String>();
					ArrayList<String> lSTList = new ArrayList<String>();
					String AppNo = "";
					Iterator<String> it3 = lCourseAppMap.get(course).get(disciList).iterator();
		//			logger.error(":it3:"+it3);
		//			logger.error(":lCourseAppMap:"+lCourseAppMap);
					while(it3.hasNext())
					{
						boolean categoryValid = false;
						AppNo = it3.next();
						logger.error(":AppNo:"+AppNo);
						if(pApplicantsDataMap.get(AppNo).get("txtCategoryValid")!=null && pApplicantsDataMap.get(AppNo).get("txtCategoryValid").equals("Y")) //---- Checking if applicant should be given Category Reservation Benefit ----//
							categoryValid = true;
						if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("OBC") && categoryValid)
						{
							lOBCList.add(AppNo);
							lOBC++;
						}
						else if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("SC") && categoryValid)
						{
							lSCList.add(AppNo);
							lSC++;
						}
						else if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("ST")&& categoryValid)
						{
							lSTList.add(AppNo);
							lST++;
						}
						else
						{
							lGNList.add(AppNo);
							lGN++;
						}
					}
					boolean scstFlag = false;
					boolean obcFlag = false;
					boolean genFlag = false;
					boolean scFlag = false;
					boolean stFlag = false;
					boolean seatEligibility = false ;

					lSCST = lSC + lST ;
		//			logger.error(":lSCST:"+lSCST);
					Integer genSeatsOrg = 0;
					Integer obcSeatsOrg = 0;
					Integer scSeatsOrg = 0;
					Integer stSeatsOrg = 0;

					Integer genSeats = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(1));
					Integer obcSeats = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(2));
					Integer scSeats = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(3));
					Integer stSeats = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(4));
					Integer totalSeats = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(9));	
					Integer totalSeatsPrev = totalSeats ;
					Integer maxSeats = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(10));									
					Integer genSeat = genSeats;
					Integer obcSeat = obcSeats ;
					Integer scSeat = scSeats ;
					Integer stSeat = stSeats;
					logger.error(":genSeats:"+genSeats+":obcSeats:"+obcSeats+":scSeats:"+scSeats+":stSeats:"+stSeats+":totalSeats:"+totalSeats+":maxSeats:"+maxSeats+"::");

					Iterator<ArrayList<String>> it8 = lDisciplineOfferMap.keySet().iterator();
		//			logger.error(":it8:"+it8);
					while(it8.hasNext())
					{
						ArrayList<String> tempDiscilist = new ArrayList<String>();
						tempDiscilist = it8.next();
		//				logger.error(":tempDiscilist:"+tempDiscilist);
						if(tempDiscilist.contains(ldisciList.get(0)) == false && lDisciplineOfferMap.get(tempDiscilist).get(0).equals("Y"))
						{
							genSeats = genSeats + Integer.parseInt(lDisciplineOfferMap.get(tempDiscilist).get(1));
							obcSeats = obcSeats + Integer.parseInt(lDisciplineOfferMap.get(tempDiscilist).get(2));
							scSeats = scSeats + Integer.parseInt(lDisciplineOfferMap.get(tempDiscilist).get(3));
							stSeats = stSeats + Integer.parseInt(lDisciplineOfferMap.get(tempDiscilist).get(4));
						}
					}
					logger.error("After Iterator :genSeats:"+genSeats+":obcSeats:"+obcSeats+":scSeats:"+scSeats+":stSeats:"+stSeats);
					if(lDisciplineOfferMap.get(ldisciList).get(0).equals("N"))
					{
				//----------------------		Commented start by akshay-----------------------------------
					seatEligibility = true ;
				//----------------------		Commented end by akshay-----------------------------------		
						genSeatsOrg = 1;
						obcSeatsOrg = 1;
						scSeatsOrg = 1;
						stSeatsOrg = 1;
						lDisciplineOfferMap.get(ldisciList).set(11,1+"");
						lDisciplineOfferMap.get(ldisciList).set(12,1+"");
						lDisciplineOfferMap.get(ldisciList).set(13,1+"");
						lDisciplineOfferMap.get(ldisciList).set(14,1+"");
					}
					else
					{	
						genSeatsOrg = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(11));
						obcSeatsOrg = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(12));
						scSeatsOrg = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(13));
						stSeatsOrg = Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(14));

					}
					logger.error(":lDisciplineOfferMap.get(ldisciList):"+lDisciplineOfferMap.get(ldisciList));
					logger.error(":genSeatsOrg:"+genSeatsOrg);
					logger.error(":obcSeatsOrg:"+obcSeatsOrg);
					logger.error(":scSeatsOrg:"+scSeatsOrg);
					logger.error(":stSeatsOrg:"+stSeatsOrg);logger.error(":lDisciplineAppMap1:"+lDisciplineAppMap1);
					if(lDisciplineAppMap1.keySet().size()>1)
					{
						Iterator<ArrayList<String>> it14 = lDisciplineAppMap1.keySet().iterator();
						while (it14.hasNext())
						{
							if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
							{
								ArrayList<String> disciList1 = new ArrayList<String>();
								disciList1 = it14.next();
								if(disciList1.contains(ldisciList.get(0))== false)
								{
									if(lDisciplineOfferMap.get(disciList1).get(0).equals("N"))
									{
										Integer genTemp = lDisciplineAppMap1.get(disciList1);
										int genSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(1));

										if(lDisciplineAppMap1.get(ldisciList)> genSeatsOrg + 1)
										{
											lDisciplineOfferMap.get(ldisciList).set(1,0 + "");
											lDisciplineOfferMap.get(ldisciList).set(11,0 + "");
											genSeat = 0;
											genSeatsOrg = 0 ;
										}

										if(genTemp > genSeatTemp+genSeats + 1)
										{
											Iterator<ArrayList<String>> it15 = lDisciplineOfferMap.keySet().iterator();
											ArrayList<String> disciList2 = new ArrayList<String>();
											while(it15.hasNext())
											{
												disciList2 = it15.next();
												lDisciplineOfferMap.get(disciList2).set(1,0 + "");
												lDisciplineOfferMap.get(disciList2).set(11,0 + "");
											}
											genSeat = 0;
											genSeatsOrg = 0 ;
										}
										if(genSeat != 0)
										{
											if(lDisciplineAppMap1.get(ldisciList) + genTemp > genSeatTemp+genSeats + 1)
											{
												Iterator<ArrayList<String>> it15 = lDisciplineOfferMap.keySet().iterator();
												ArrayList<String> disciList2 = new ArrayList<String>();
												while(it15.hasNext())
												{
													disciList2 = it15.next();
													lDisciplineOfferMap.get(disciList2).set(1,0 + "");
													lDisciplineOfferMap.get(disciList2).set(11,0 + "");
												}
												genSeat = 0;
												genSeatsOrg = 0 ;
											}
										}
										Iterator<String> it16 = lCourseAppMap.get(course).get(disciList1).iterator();
										Integer obcTemp = 0;
										Integer scTemp = 0;
										Integer stTemp = 0;
										while(it16.hasNext())
										{
											boolean categoryValid = false;
											AppNo = it16.next();
											if(pApplicantsDataMap.get(AppNo).get("txtCategoryValid")!=null && pApplicantsDataMap.get(AppNo).get("txtCategoryValid").equals("Y")) //---- Checking if applicant should be given Category Reservation Benefit ----//
												categoryValid = true;
											if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("OBC") && categoryValid)
											{
												obcTemp++;
											}
											else if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("SC") && categoryValid)
											{
												scTemp++;
											}
											else if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("ST")&& categoryValid)
											{
												stTemp++;
											}
										}
										int obcSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(2));
										if(lOBC > obcSeatsOrg + 1)
										{
											lDisciplineOfferMap.get(ldisciList).set(2,0 + "");
											lDisciplineOfferMap.get(ldisciList).set(12,0 + "");
											obcSeat = 0;
											obcSeatsOrg = 0 ;
										}
										if(obcTemp > obcSeatTemp+obcSeats + 1)
										{
											Iterator<ArrayList<String>> it15 = lDisciplineOfferMap.keySet().iterator();
											ArrayList<String> disciList2 = new ArrayList<String>();
											while(it15.hasNext())
											{
												disciList2 = it15.next();
												lDisciplineOfferMap.get(disciList2).set(2,0 + "");
												lDisciplineOfferMap.get(disciList2).set(12,0 + "");
											}
											obcSeat = 0;
											obcSeatsOrg = 0 ;
										}
										if(obcSeat!=0)
										{
											if(obcTemp + lOBC > obcSeatTemp+obcSeats + 1)
											{
												Iterator<ArrayList<String>> it17 = lDisciplineOfferMap.keySet().iterator();
												ArrayList<String> disciList3 = new ArrayList<String>();
												while(it17.hasNext())
												{
													disciList3 = it17.next();
													lDisciplineOfferMap.get(disciList3).set(2,0 + "");
													lDisciplineOfferMap.get(disciList3).set(12,0 + "");
												}
												obcSeat = 0;
												obcSeatsOrg = 0 ;
											}
										}
										int scSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(3));

										if(lSC > scSeatsOrg + 1)
										{
											lDisciplineOfferMap.get(ldisciList).set(3,0 + "");
											lDisciplineOfferMap.get(ldisciList).set(13,0 + "");
											scSeat = 0;
											scSeatsOrg = 0 ;
										}
										if(scTemp > scSeatTemp+scSeats + 1)
										{
											Iterator<ArrayList<String>> it15 = lDisciplineOfferMap.keySet().iterator();
											ArrayList<String> disciList2 = new ArrayList<String>();
											while(it15.hasNext())
											{
												disciList2 = it15.next();
												lDisciplineOfferMap.get(disciList2).set(3,0 + "");
												lDisciplineOfferMap.get(disciList2).set(13,0 + "");
											}
											scSeat = 0;
											scSeatsOrg = 0 ;
										}
										if(scSeat!=0)
										{
											if(scTemp + lSC > scSeatTemp+scSeats + 1)
											{
												Iterator<ArrayList<String>> it17 = lDisciplineOfferMap.keySet().iterator();
												ArrayList<String> disciList3 = new ArrayList<String>();
												while(it17.hasNext())
												{
													disciList3 = it17.next();
													lDisciplineOfferMap.get(disciList3).set(3,0 + "");
													lDisciplineOfferMap.get(disciList3).set(13,0 + "");
												}
												scSeat = 0;
												scSeatsOrg = 0 ;
											}
										}
										int stSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(4));
										if(lST > stSeatsOrg + 1)
										{
											lDisciplineOfferMap.get(ldisciList).set(4,0 + "");
											lDisciplineOfferMap.get(ldisciList).set(14,0 + "");
											stSeat = 0;
											stSeatsOrg = 0 ;
										}
										if(stTemp > stSeatTemp+stSeats + 1)
										{
											Iterator<ArrayList<String>> it15 = lDisciplineOfferMap.keySet().iterator();
											ArrayList<String> disciList2 = new ArrayList<String>();
											while(it15.hasNext())
											{
												disciList2 = it15.next();
												lDisciplineOfferMap.get(disciList2).set(4,0 + "");
												lDisciplineOfferMap.get(disciList2).set(14,0 + "");
											}
											stSeat = 0;
											stSeatsOrg = 0 ;
										}
										if(stSeat!=0)
										{
											if(stTemp + lST > stSeatTemp+stSeats + 1)
											{
												Iterator<ArrayList<String>> it17 = lDisciplineOfferMap.keySet().iterator();
												ArrayList<String> disciList3 = new ArrayList<String>();
												while(it17.hasNext())
												{
													disciList3 = it17.next();
													lDisciplineOfferMap.get(disciList3).set(4,0 + "");
													lDisciplineOfferMap.get(disciList3).set(14,0 + "");
												}
												stSeat = 0;
												stSeatsOrg = 0 ;
											}
										}

									}
								}
							}
							else
							{
								ArrayList<String> disciList1 = new ArrayList<String>();
								disciList1 = it14.next();
								if(disciList1.contains(ldisciList.get(0))== false)
								{
									if(lDisciplineOfferMap.get(disciList1).get(0).equals("Y"))
									{
										Integer genTemp = lDisciplineAppMap1.get(disciList1);

										int genSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(11));
										if(genTemp  > genSeatTemp + 1)
										{
											genSeats = genSeats - Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(1));
											lDisciplineOfferMap.get(disciList1).set(1,0 + "");
											lDisciplineOfferMap.get(disciList1).set(11,0 + "");
										}
										Iterator<String> it16 = lCourseAppMap.get(course).get(disciList1).iterator();
										Integer obcTemp = 0;
										Integer scTemp = 0;
										Integer stTemp = 0;
										while(it16.hasNext())
										{
											boolean categoryValid = false;
											AppNo = it16.next();
											if(pApplicantsDataMap.get(AppNo).get("txtCategoryValid")!=null && pApplicantsDataMap.get(AppNo).get("txtCategoryValid").equals("Y")) //---- Checking if applicant should be given Category Reservation Benefit ----//
												categoryValid = true;
											if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("OBC") && categoryValid)
											{
												obcTemp++;
											}
											else if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("SC") && categoryValid)
											{
												scTemp++;
											}
											else if(pApplicantsDataMap.get(AppNo).get("txtAppCategory").equals("ST")&& categoryValid)
											{
												stTemp++;
											}
										}
										int obcSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(12));
										if(obcTemp  > obcSeatTemp + 1)
										{
											obcSeats = obcSeats - Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(2));
											lDisciplineOfferMap.get(disciList1).set(2,0 + "");
											lDisciplineOfferMap.get(disciList1).set(12,0 + "");
										}
										int scSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(13));
										if(scTemp + lSC > scSeatTemp + 1)
										{
											scSeats = scSeats - Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(3));
											lDisciplineOfferMap.get(disciList1).set(3,0 + "");
											lDisciplineOfferMap.get(disciList1).set(13,0 + "");
										}
										int stSeatTemp = Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(14));
										if(stTemp + lST > stSeatTemp + 1)
										{
											stSeats = stSeats - Integer.parseInt(lDisciplineOfferMap.get(disciList1).get(4));
											lDisciplineOfferMap.get(disciList1).set(4,0 + "");
											lDisciplineOfferMap.get(disciList1).set(14,0 + "");
										}

									}
								}
							}
						}
					}				

					if(genSeats > 0  && genSeatsOrg > 0)
					{
						if(lDisciplineAppMap1.get(disciList) == genSeats + 1)
						{
							if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
							{
								if(lDisciplineAppMap1.get(disciList) - genSeatsOrg < 2)
									seatEligibility = true ;
								if(lDisciplineAppMap1.get(disciList) - genSeatsOrg == 1)
									genSeatsOrg++;
							}
							if(seatEligibility)
							{	
								genFlag = true;
								scstFlag = true;
								obcFlag = true;
								lGN = 0;
								lSCST = 0;
								lOBC = 0;
								courseOfferedList.addAll(lGNList);
								courseOfferedList.addAll(lOBCList);
								courseOfferedList.addAll(lSCList);
								courseOfferedList.addAll(lSTList);
								genSeat++;
								totalSeats++;
								maxSeats++;
								seatCategoryMap.put(1, "Y");
							}
							else
							{
								genSeat = 0;
								genSeatsOrg = 0;
							}
						}
						else if(lDisciplineAppMap1.get(disciList) > genSeats + 1)
						{	
							if(lSCST > 0 )
							{
								if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
								{
									if(lSCST - genSeatsOrg < 2)
										seatEligibility = true ;
									if(lSCST - genSeatsOrg == 1)
										genSeatsOrg++;
								}
//----------------------Started by akshay for Tie usecase-----------------------------------------------								
			//					else
			//					{
			//						if(roundNumber.intValue()==1)
			//							seatEligibility = false ;
			//					}	
								
//------------------------End by akshay for tie use case------------------------------------------------									
								if(seatEligibility)
								{
									if(lSCST <= genSeats)
									{	
										scstFlag = true;
										if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(11))< genSeatsOrg )
											genSeat++;
										courseOfferedList.addAll(lSCList);
										courseOfferedList.addAll(lSTList);
										totalSeats = totalSeats - (genSeats - lSCST);
										while(genSeats-lSCST > 0)
										{
											if(genSeat>0)
											{
												genSeat-- ;
											}
											else
											{
												Iterator<ArrayList<String>> it13 = lDisciplineOfferMap.keySet().iterator();
												SeatFix : while(it13.hasNext())
												{
													ArrayList<String> tempDiscilist = new ArrayList<String>();
													tempDiscilist = it13.next();
													if(tempDiscilist.contains(ldisciList.get(0)) == false && lDisciplineOfferMap.get(tempDiscilist).get(0).equals("Y"))
													{
														Integer temp = Integer.parseInt(lDisciplineOfferMap.get(tempDiscilist).get(1));
														if(temp>0)
														{
															temp--;
															lDisciplineOfferMap.get(tempDiscilist).set(1,temp + "");
															break SeatFix;
														}
													}
												}
											}
										}
										lSCST = 0;

									}
									else if(lSCST == genSeats + 1)
									{
										scstFlag = true;
										courseOfferedList.addAll(lSCList);
										courseOfferedList.addAll(lSTList);
										lSCST = 0;
										genSeat++;
										totalSeats++;
										maxSeats++;
										seatCategoryMap.put(1, "Y");
									}
									else
									{
										Iterator<ArrayList<String>> it12 = lDisciplineOfferMap.keySet().iterator();
										while(it12.hasNext())
										{
											ArrayList<String> tempDiscilist = new ArrayList<String>();
											tempDiscilist = it12.next();
											if(tempDiscilist.contains(ldisciList.get(0)) == false && lDisciplineOfferMap.get(tempDiscilist).get(0).equals("Y"))
											{
												lDisciplineOfferMap.get(tempDiscilist).set(1,0 + "");
											}
										}
										genSeat = 0;
										totalSeats = totalSeats - genSeats;
										if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(11))< genSeatsOrg )
											genSeatsOrg--;
									}
									seatEligibility = false;
								}
								else
								{
									genSeat = 0;
									genSeatsOrg = 0;
								}
							}
							else
							{
								Iterator<ArrayList<String>> it12 = lDisciplineOfferMap.keySet().iterator();
								while(it12.hasNext())
								{
									ArrayList<String> tempDiscilist = new ArrayList<String>();
									tempDiscilist = it12.next();
									if(tempDiscilist.contains(ldisciList.get(0)) == false && lDisciplineOfferMap.get(tempDiscilist).get(0).equals("Y"))
									{
										lDisciplineOfferMap.get(tempDiscilist).set(1,0 + "");
									}
								}
								genSeat = 0;
								totalSeats = totalSeats - genSeats;
							}
						}
						else
						{
							if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
							{
								if(lDisciplineAppMap1.get(disciList) - genSeatsOrg < 2)
									seatEligibility = true ;
								if(lDisciplineAppMap1.get(disciList) - genSeatsOrg == 1)
								{
									genSeatsOrg++;
									genSeat++;
									totalSeats++;
									maxSeats++;
								}

							}
							if(seatEligibility)
							{
								genFlag = true;
								scstFlag = true;
								obcFlag = true;
								lSCST = 0;
								lGN= 0;
								lOBC = 0;
								courseOfferedList.addAll(lGNList);
								courseOfferedList.addAll(lOBCList);
								courseOfferedList.addAll(lSCList);
								courseOfferedList.addAll(lSTList);
							}
							else
							{
								genSeat = 0;
								genSeatsOrg = 0;
							}
						}	
						lDisciplineOfferMap.get(ldisciList).set(1,genSeat + "");
						lDisciplineOfferMap.get(ldisciList).set(11,genSeatsOrg + "");
					}

					if(obcFlag == false)
					{
						if(obcSeats > 0 && obcSeatsOrg > 0)
						{
							if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
							{
								if(lOBC - obcSeatsOrg < 2)
									seatEligibility = true ;
								if(lOBC - obcSeatsOrg == 1)
									obcSeatsOrg++;
							}
							else
							{
								seatEligibility = true;
							}
							if(seatEligibility)
							{
								if(lOBC == obcSeats + 1)
								{	
									obcFlag = true;
									courseOfferedList.addAll(lOBCList);
									lOBC = 0;
									obcSeat++;
									totalSeats++ ;
									maxSeats++ ;
									seatCategoryMap.put(2, "Y");
								}
								else if(lOBC > obcSeats + 1)
								{
									obcFlag = false;

									Iterator<ArrayList<String>> it12 = lDisciplineOfferMap.keySet().iterator();
									while(it12.hasNext())
									{
										ArrayList<String> tempDiscilist = new ArrayList<String>();
										tempDiscilist = it12.next();
										if(tempDiscilist.contains(ldisciList.get(0)) == false && lDisciplineOfferMap.get(tempDiscilist).get(0).equals("Y"))
										{
											lDisciplineOfferMap.get(tempDiscilist).set(2,0 + "");
										}
									}
									obcSeat = 0;
									totalSeats = totalSeats - obcSeats;
									if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(12))< obcSeatsOrg )
										obcSeatsOrg--;

								}
								else
								{
									obcFlag = true;
									courseOfferedList.addAll(lOBCList);
									lOBC = 0;	
									if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(12))< obcSeatsOrg )
									{
										obcSeat++;
										totalSeats++;
										maxSeats++;
									}
								}
								seatEligibility = false;
							}
							else
							{
								obcSeat = 0;
								obcSeatsOrg = 0;
							}
							lDisciplineOfferMap.get(ldisciList).set(2,obcSeat+"");
							lDisciplineOfferMap.get(ldisciList).set(12,obcSeatsOrg+"");
						}
					}
					scFlag = false;
					stFlag = false;
					if(scstFlag == false)
					{
						if(scSeats > 0 && scSeatsOrg > 0)
						{
							if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
							{
								if(lSC - scSeatsOrg < 2)
									seatEligibility = true ;
								if(lSC - scSeatsOrg == 1)
									scSeatsOrg++;
							}
							else
							{
								seatEligibility = true ;
							}
							if(seatEligibility)
							{
								if(lSC == scSeats + 1)
								{
									scFlag = true;
									courseOfferedList.addAll(lSCList);
									lSC = 0;
									scSeat++;
									totalSeats++ ;
									maxSeats++ ;
									seatCategoryMap.put(3, "Y");

								}
								else if(lSC > scSeats + 1)
								{
									scFlag = false;
									Iterator<ArrayList<String>> it12 = lDisciplineOfferMap.keySet().iterator();
									while(it12.hasNext())
									{
										ArrayList<String> tempDiscilist = new ArrayList<String>();
										tempDiscilist = it12.next();
										if(tempDiscilist.contains(ldisciList.get(0)) == false && lDisciplineOfferMap.get(tempDiscilist).get(0).equals("Y"))
										{
											lDisciplineOfferMap.get(tempDiscilist).set(3,0 + "");
										}
									}
									totalSeats = totalSeats - scSeats;
									scSeat = 0;
									if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(13))< scSeatsOrg )
										scSeatsOrg--;
								}
								else
								{
									scFlag = true;
									courseOfferedList.addAll(lSCList);
									lSC = 0;
									if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(13))< scSeatsOrg )
									{
										scSeat++;
										totalSeats++;
										maxSeats++;
									}
								}
								seatEligibility = false;
							}
							else
							{
								scSeat = 0;
								scSeatsOrg = 0;
							}
							lDisciplineOfferMap.get(ldisciList).set(3,scSeat+"");
							lDisciplineOfferMap.get(ldisciList).set(13,scSeatsOrg+"");
						}
						if(stSeats > 0 && stSeatsOrg > 0)
						{
							if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
							{
								if(lST - stSeatsOrg < 2)
									seatEligibility = true ;
								if(lST - stSeatsOrg == 1)
									stSeatsOrg++;
							}
							else
							{
								seatEligibility = true ;
							}
							if(seatEligibility)
							{
								if(lST == stSeats + 1)
								{
									stFlag = true;
									courseOfferedList.addAll(lSTList);
									lST = 0;
									stSeat++;
									totalSeats++ ;
									maxSeats++ ;
									seatCategoryMap.put(4, "Y");
								}
								else if(lST > stSeats)
								{
									stFlag = false;
									Iterator<ArrayList<String>> it12 = lDisciplineOfferMap.keySet().iterator();
									while(it12.hasNext())
									{
										ArrayList<String> tempDiscilist = new ArrayList<String>();
										tempDiscilist = it12.next();
										if(tempDiscilist.contains(ldisciList.get(0)) == false && lDisciplineOfferMap.get(tempDiscilist).get(0).equals("Y"))
										{
											lDisciplineOfferMap.get(tempDiscilist).set(4,0 + "");
										}
									}
									stSeat = 0;
									totalSeats = totalSeats - stSeats;
									if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(14))< stSeatsOrg )
										stSeatsOrg--;
								}
								else
								{
									stFlag = true;
									courseOfferedList.addAll(lSTList);
									lST = 0;
									if(Integer.parseInt(lDisciplineOfferMap.get(ldisciList).get(14))< stSeatsOrg )
									{
										stSeat++;
										totalSeats++;
										maxSeats++;
									}
								}
							}
							else
							{
								stSeat = 0;
								stSeatsOrg = 0;
							}
							lDisciplineOfferMap.get(ldisciList).set(4,stSeat+"");
							lDisciplineOfferMap.get(ldisciList).set(14,stSeatsOrg+"");
						}
					}
					if(courseOfferedList.size() > 0)
					{
						starseatcategoryMap.put(ldisciList, seatCategoryMap);
						lSeatOfferedMap.put(disciList, starseatcategoryMap);
						lOfferedSeatCourseMap.put(course, lSeatOfferedMap);
						lDisciplineOfferedMap.put(disciList, courseOfferedList);
						lOfferedCourseMap.put(course, lDisciplineOfferedMap);
					}
					lDisciplineOfferMap.get(ldisciList).set(9, totalSeats+"");
					lDisciplineOfferMap.get(ldisciList).set(10, maxSeats+"");
					if(lDisciplineOfferMap.get(ldisciList).get(0).equals("Y"))
					{
						Iterator<ArrayList<String>> it4 = lDisciplineOfferMap.keySet().iterator();
						while(it4.hasNext())
						{
							ArrayList<String> tempList = it4.next();
							if(!tempList.get(0).equals(ldisciList.get(0)))
							{
								Integer temp = Integer.parseInt(lDisciplineOfferMap.get(tempList).get(9));
								temp = temp + totalSeats - totalSeatsPrev ;
								lDisciplineOfferMap.get(tempList).set(9, temp+"");
							}
						}
					}
					if(scstFlag)
					{
						lLoopList.addAll(lSCList);
						for(String appseq:lSCList)
						{
							ArrayList<String> temp = new ArrayList<String>();
							temp = courseAllocation.get(appseq);
							Course:for(int d=0;d<8;d++)
							{
								if(temp.get(d).equals(course))
								{
									lStartChoiceNoMap.put(appseq, d+1);
									break Course;
								}
							}
						}
						lLoopList.addAll(lSTList);
						for(String appseq:lSTList)
						{
							ArrayList<String> temp = new ArrayList<String>();
							temp = courseAllocation.get(appseq);
							Course:for(int d=0;d<8;d++)
							{
								if(temp.get(d).equals(course))
								{
									lStartChoiceNoMap.put(appseq, d+1);
									break Course;
								}
							}
						}
						lSCList.clear();
						lSTList.clear();
					}
					else
					{
						if(scFlag)
						{
							lLoopList.addAll(lSCList);
							for(String appseq:lSCList)
							{
								ArrayList<String> temp = new ArrayList<String>();
								temp = courseAllocation.get(appseq);
								Course:for(int d=0;d<8;d++)
								{
									if(temp.get(d).equals(course))
									{
										lStartChoiceNoMap.put(appseq, d+1);
										break Course;
									}
								}
							}
							lSCList.clear();
						}
						if(stFlag)
						{
							lLoopList.addAll(lSTList);
							for(String appseq:lSTList)
							{
								ArrayList<String> temp = new ArrayList<String>();
								temp = courseAllocation.get(appseq);
								Course:for(int d=0;d<8;d++)
								{
									if(temp.get(d).equals(course))
									{
										lStartChoiceNoMap.put(appseq, d+1);
										break Course;
									}
								}
							}
							lSTList.clear();
						}
					}
					if(genFlag)
					{
						lLoopList.addAll(lGNList);
						for(String appseq:lGNList)
						{
							ArrayList<String> temp = new ArrayList<String>();
							temp = courseAllocation.get(appseq);
							Course:for(int d=0;d<8;d++)
							{
								if(temp.get(d).equals(course))
								{
									lStartChoiceNoMap.put(appseq, d+1);
									break Course;
								}
							}
						}
						lGNList.clear();
					}
					if(obcFlag)
					{
						lLoopList.addAll(lOBCList);
						for(String appseq:lOBCList)
						{
							ArrayList<String> temp = new ArrayList<String>();
							temp = courseAllocation.get(appseq);
							Course:for(int d=0;d<8;d++)
							{
								if(temp.get(d).equals(course))
								{
									lStartChoiceNoMap.put(appseq, d+1);
									break Course;
								}
							}
						}
						lOBCList.clear();
					}
				}	
			}
			for(String Appli:lDeallocationMap.keySet())
			{
				if(!lStartChoiceNoMap.containsKey(Appli))
				{
					lReallocationMap.put(Appli, lDeallocationMap.get(Appli));
				}
			}
			lDeallocationMap.clear();
			if(lLoopList.size()== lTempSet.size())
			{
				break AllTieResolved;
			}
			else
			{
				lTempSet1.removeAll(lLoopList);
			}
		}
		logger.error("::::lStartChoiceNoMap in resolve Tie condition:::"+lStartChoiceNoMap);
		return lStartChoiceNoMap;
	}

	//------ Function is for updating Offer details in case an applicant gets upgraded choice -----//
	public static void storeUpdateOfferDetails(String appSeqNo, String previouslyOffered)
	{
		logger.error("Store updated offer details");
		DBManager dm = DBManager.init();
		
		HashMap<String,String> applicantDataMap=new HashMap<String,String>();
		applicantDataMap = getApplicantData(dm,appSeqNo);

		//----- The category under which seat was offered last time. ----//
		Integer seatOfferedCategory = 0;
		SeatCat : for(int i=roundNumber-1;i>0;i--)
		{
			if(!applicantDataMap.get("seatCategoryR"+ i).equals("") && !applicantDataMap.get("seatCategoryR" + i).equals("0"))
			{
				seatOfferedCategory = Integer.parseInt(applicantDataMap.get("seatCategoryR" + i));
			//	logger.error(":seatOfferedCategory:"+seatOfferedCategory);
				break SeatCat;
			}
		}
		//----- The discipline under which seat was offered last time. ----//
		String previousDiscipline = "";
		previousDiscipline = applicantDataMap.get("previousDiscipline");

		String columnName = "";	
		if(seatOfferedCategory == 1)
			columnName = "txtGeneral";
		else if(seatOfferedCategory == 2)
			columnName = "txtOBC";
		else if(seatOfferedCategory == 3)	
			columnName = "txtSC";
		else
			columnName = "txtST";

		//----- Getting the seat list of the discipline under which seat was offered last time ----//
		ArrayList<String> tempSeatList = new ArrayList<String>();
		ArrayList<String> tempDisciplineList = new ArrayList<String>();
			
		if(previouslyOffered.equals("PH1Y"))
			System.out.println("hiiii");
		Temp : for(ArrayList<String> tempList:pDuplicateOfferDetailsMap.get(previouslyOffered).keySet())
		{
			if(tempList.contains(previousDiscipline))
			{
				tempDisciplineList.addAll(tempList);
				tempSeatList = pDuplicateOfferDetailsMap.get(previouslyOffered).get(tempList);
				break Temp;
			}
		}
		System.out.println("Application number:"+appSeqNo);
		previousDiscipline = tempDisciplineList.get(0);
		for(int i=1;i<tempDisciplineList.size();i++)
		{
			previousDiscipline = previousDiscipline + "," + tempDisciplineList.get(i); 
		}
	//	logger.error("::tempSeatList::"+tempSeatList);
		Integer tempSeatCount = Integer.parseInt(tempSeatList.get(seatOfferedCategory));
		tempSeatCount++;
			
		//----- Storing updated seat numbers in a map -----//
		updateOfferDetailsMap.put(previouslyOffered+"@@"+previousDiscipline+"@@"+columnName, tempSeatCount);
//		logger.error("updateOfferDetailsMap"+updateOfferDetailsMap);
			
		//----- Updating seat count in pOfferDetailsMap -----//
		HashMap<ArrayList<String>, ArrayList<String>> offerMap=new HashMap<ArrayList<String>, ArrayList<String>>();
		offerMap = pOfferDetailsMap.get(previouslyOffered);
			
		ArrayList<String> tempSeatList1 = offerMap.get(tempDisciplineList);
//		logger.error(":tempDisciplineList:"+tempDisciplineList);
//		logger.error(":tempSeatList1:"+tempSeatList1);
		Integer tempCount = Integer.parseInt(tempSeatList1.get(seatOfferedCategory));
		Integer tempCount1 = Integer.parseInt(tempSeatList1.get(9));
		Integer tempCount2 = Integer.parseInt(tempSeatList1.get(10));
		Integer tempCount3 = Integer.parseInt(tempSeatList1.get(seatOfferedCategory + 10));
		tempCount++;
		tempCount1++;
		tempCount2++;
		tempCount3++;
		tempSeatList1.set(seatOfferedCategory, tempCount + "");
		tempSeatList1.set(9, tempCount1 + "");
		tempSeatList1.set(10, tempCount2 + "");
		tempSeatList1.set(seatOfferedCategory + 10, tempCount3 + "");
			
		if(tempSeatList1.get(0).equals("Y"))
		{
			for(ArrayList<String> tempList1:offerMap.keySet())
			{
				if(!tempList1.contains(tempDisciplineList.get(0)))
				{
					ArrayList<String> tempSeatList2 = offerMap.get(tempList1);
					Integer temp = Integer.parseInt(tempSeatList2.get(9));
					temp++;
					tempSeatList2.set(9, temp+"");
					offerMap.put(tempList1, tempSeatList2);
				}
			}
		}
		offerMap.put(tempDisciplineList, tempSeatList1);
		pOfferDetailsMap.put(previouslyOffered, offerMap);
	}
	
	//------ Function to get Applicant's data from database ----- //
	public static HashMap<String, String> getApplicantData(DBManager dm,String appSeqNo)
	{
		logger.error("getApplicantData");
		//-----This map is used for storing Applicant Data.------//
		HashMap<String,String> applicantDataMap=new HashMap<String,String>();
		PreparedStatement ps = null;
		ResultSet rs=null;	
		String query = "";
		query = "SELECT * FROM app_form3580_data WHERE app_seq_no='"+appSeqNo+"' and rowstate>-1 ;";	   

		Connection con = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alm_iit","root", "password");
			//con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/eforms_global", "root", "password");
			ps = con.prepareStatement(query);
			rs=ps.executeQuery();
			while(rs.next())
			{
				applicantDataMap.put("seatCategoryR1", rs.getString("seatCategoryR1")==null? "":rs.getString("seatCategoryR1").trim());
				applicantDataMap.put("seatCategoryR2", rs.getString("seatCategoryR2")==null? "":rs.getString("seatCategoryR2").trim());
				applicantDataMap.put("seatCategoryR3", rs.getString("seatCategoryR3")==null? "":rs.getString("seatCategoryR3").trim());
				applicantDataMap.put("seatCategoryR4", rs.getString("seatCategoryR4")==null? "":rs.getString("seatCategoryR4").trim());
				applicantDataMap.put("seatCategoryR5", rs.getString("seatCategoryR5")==null? "":rs.getString("seatCategoryR5").trim());
				applicantDataMap.put("seatCategoryR6", rs.getString("seatCategoryR6")==null? "":rs.getString("seatCategoryR6").trim());
				applicantDataMap.put("seatCategoryR7", rs.getString("seatCategoryR7")==null? "":rs.getString("seatCategoryR7").trim());
				applicantDataMap.put("seatCategoryR8", rs.getString("seatCategoryR8")==null? "":rs.getString("seatCategoryR8").trim());
				applicantDataMap.put("seatCategoryR9", rs.getString("seatCategoryR9")==null? "":rs.getString("seatCategoryR9").trim());
				applicantDataMap.put("seatCategoryR10", rs.getString("seatCategoryR10")==null? "":rs.getString("seatCategoryR10").trim());
				applicantDataMap.put("previousDiscipline", rs.getString("previousDiscipline")==null? "":rs.getString("previousDiscipline").trim());
		    }
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after getting Applicant's data");
			}
		}
		catch(Exception e)
		{
			logger.error("Error in getting Applicant's Data");
		}
		finally
		{
			try
			{
				con.close();
			}
			catch(Exception e)
			{
				logger.error("Error in Disconnecting after getting Applicant's data");
			}
		}
		return applicantDataMap ;
	}
//Started by akshay	
	public static Set<String> testFeasabilityLogic(HashMap<String, HashMap<String, String>> pApplicantDataMap,HashMap<String,HashMap<ArrayList<String>, ArrayList<String>>> pCourseGATEPaperEligibilityMap,HashMap<String, HashMap<Integer, String>> lUpdateValueMap,HashMap<String, HashMap<ArrayList<String>, ArrayList<String>>> pOfferDataMap,Integer roundNum,HashMap<String,HashMap<ArrayList<String>,ArrayList<String>>> pfinalmap)
	{


		logger.error("Inside  the testFeasabilityLogic Method");
		Map<String,String> lGeneralCategoryMap = new HashMap<String,String>();
		Map<String,String> lOBCCategoryMap = new HashMap<String,String>();
		Map<String,String> lSCCategoryMap = new HashMap<String,String>();
		Map<String,String> lSTCategoryMap = new HashMap<String,String>();

		Set<String> lSet = new HashSet<String>();
		
		try{
			
		
		Iterator GATEPaperEligibilityMapitr = pCourseGATEPaperEligibilityMap.keySet().iterator();	
		while(GATEPaperEligibilityMapitr.hasNext())
		{ 
			String lProg = (String) GATEPaperEligibilityMapitr.next();
			
			// FETCHNG THE PROGRAM (AE1Y)
			HashMap<ArrayList<String>, ArrayList<String>> lValueMap =  pCourseGATEPaperEligibilityMap.get(lProg);
			//   Iterator lValueMapitr = pCourseGATEPaperEligibilityMap.keySet().iterator();
			Iterator lValueMapitr = lValueMap.keySet().iterator();
			while(lValueMapitr.hasNext())
			{  
				ArrayList<String> ldiscipList =  (ArrayList<String>) lValueMapitr.next();
				
       // disciplist is the value of pCourseGATEPaperEligibilityMap
				//ArrayList<String> ldiscipList =  (ArrayList<String>) GATEPaperEligibilityMapitr.next(); 
				ArrayList<String> leliggatepapercodeList =   lValueMap.get(ldiscipList); // this will give the list of the eligible gate paper code for that course
				
               // Iterating over the update value map
				Iterator itr1 =  lUpdateValueMap.keySet().iterator();

				//   Iterator itr2 =  pApplicantDataMap.keySet().iterator();
				while(itr1.hasNext())
				{
					Double lScore = 0D;
					String lAppSeqNo = (String) itr1.next();
					logger.error("The App seq num from update value map"+lAppSeqNo);
					// FETCHING THE APP SEQUENCE NUM  FROM UPDATE VALUE MAP WHICH CONTAINS THE STUDENTS WHICH HAS BEEN ALLOCATED



					HashMap<Integer, String> lUpdMap = lUpdateValueMap.get(lAppSeqNo);
					
					// the value of Update value map 
					HashMap<String, String> lApplicantDataValueMap = pApplicantDataMap.get(lAppSeqNo);
					
					String lGatescore = "";
					String lCourseOfferred =  lUpdMap.get(1);
					logger.error("The Course offerred"+lCourseOfferred);
					String lCategoryfromSeatAcquired = lUpdMap.get(3);
					logger.error("The Category from Seat Acquired"+lCategoryfromSeatAcquired);
					//	Iterator lofferdatamapitr = pOfferDataMap.keySet().iterator();

					if(lProg.equalsIgnoreCase(lCourseOfferred))  // now we are checking for the course offered what is the highest cutoff marks				
					{
						// if the program from gate paper eligibility map is equals to the course offerred from update value map
						logger.error("If the Program from gate paper elig map is equal to course offerred"+lProg);
						for(String lkeyofferdatamap: pOfferDataMap.keySet())
						{
							if(lkeyofferdatamap.equalsIgnoreCase(lCourseOfferred))
							{
								HashMap<ArrayList<String>,ArrayList<String>> lofferdatamapvalue =  pOfferDataMap.get(lkeyofferdatamap);
								Iterator lofferdatamapvalueiterator = lofferdatamapvalue.keySet().iterator();
								while(lofferdatamapvalueiterator.hasNext())
								{
									ArrayList<String> llist1 = (ArrayList<String>) lofferdatamapvalueiterator.next();
									ArrayList<String> llist2 = lofferdatamapvalue.get(llist1); //  will contain star or non star and then seats
									if(llist1.equals(ldiscipList))
									{

										String lAppCategory = lApplicantDataValueMap.get("txtAppCategory");
										logger.error("The Applicant Category"+lAppCategory);
										if(lAppCategory.equals(null) || lAppCategory.isEmpty())
										{

											lAppCategory = "NA";
										}
										String lCategoryValid = lApplicantDataValueMap.get("txtCategoryValid");
										
										if(lCategoryValid.equals(null) || lCategoryValid.isEmpty() )
										{
											lCategoryValid = "NA";


										}
										
										String lAppGatePaperCode = "";
										String lAppGatePaperCode2014 = lApplicantDataValueMap.get("txtGATE2014PaperCode");
										String lAppGatePaperCode2013 = lApplicantDataValueMap.get("txtGATE2013PaperCode");
										if(lAppGatePaperCode2014 != null && !lAppGatePaperCode2014.isEmpty())
										{

											lAppGatePaperCode = lAppGatePaperCode2014;
										}
										else
										{
											lAppGatePaperCode = lAppGatePaperCode2013;
										}
										
								        logger.error("The gate paper code is" +lAppGatePaperCode);
										if(lCategoryfromSeatAcquired.equalsIgnoreCase("1") || lCategoryValid.equalsIgnoreCase("N") )
										{
											logger.error("The category from  seat acquired is general");
									
																					
											if(leliggatepapercodeList.contains(lAppGatePaperCode)) 
											{ 
												String lQualifyingDiscipline =  lApplicantDataValueMap.get("txtQualifyingDiscpline");
                                              logger.error("The qualifying discipl"+lQualifyingDiscipline);
												if(ldiscipList.contains(lQualifyingDiscipline))
												{
													String str = llist2.get(0);
													if(str==null || str.isEmpty())
													{
														str = "Z";

													}
													String lKey = "";
													String ldiscip = "";
													for(int i = 0; i<ldiscipList.size();i++)
													{
														if(i == 0)
														{
															ldiscip = ldiscipList.get(i);

														}

														else
														{
															ldiscip = ldiscip + "," + ldiscipList.get(i);

														}


													}

													lKey = lProg+":" +ldiscip+":"+str;
													logger.error("The key for the for generalcutoff map" +lKey);
													String lGate2014score = lApplicantDataValueMap.get("txtGATE2014Score");
													String lGate2013score = lApplicantDataValueMap.get("txtGATE2013Score");
													if(lGate2014score != null && !lGate2014score.isEmpty())
													{

														lGatescore = lGate2014score;
													}
													else
													{
														lGatescore = lGate2013score;
													}
													Double value = Double.parseDouble(lGatescore);
													if(!lGeneralCategoryMap.containsKey(lKey)) // 
													{
														
														lScore = value; 
													}
													else
													{

														Double lScore1 =Double.parseDouble(lGeneralCategoryMap.get(lKey)) ;
														if(lScore1 < value)
														{
															value = lScore1;

														}


													}


													String lGateScoreFinal = Double.toString(value);
													lGeneralCategoryMap.put(lKey, lGateScoreFinal); // STRUCTURE OF MAP --> AE1Y:AE,300 
													// we are putting in the map for the general category what is the minimum cutoff marks for that course

												}
											}

										}					
										else if(lCategoryfromSeatAcquired.equalsIgnoreCase("2") && lCategoryValid.equalsIgnoreCase("Y"))
										{
											logger.error("The category from seat seat acquired is obc");
											if(leliggatepapercodeList.contains(lAppGatePaperCode))
											{ 
												String lQualifyingDiscipline =  lApplicantDataValueMap.get("txtQualifyingDiscpline");
												if(ldiscipList.contains(lQualifyingDiscipline))
												{
													String str = llist2.get(0);
													if(str==null || str.isEmpty())
													{
														str = "Z";

													}
													String lKey = "";
													String ldiscip = "";
													for(int i = 0; i<ldiscipList.size();i++)
													{
														if(i == 0)
														{
															ldiscip = ldiscipList.get(i);

														}

														else
														{
															ldiscip = ldiscip + "," + ldiscipList.get(i);

														}

													}

													lKey = lProg+":" +ldiscip+":"+str;
													String lGate2014score = lApplicantDataValueMap.get("txtGATE2014Score");
													String lGate2013score = lApplicantDataValueMap.get("txtGATE2013Score");
													if(lGate2014score != null && !lGate2014score.isEmpty())
													{

														lGatescore = lGate2014score;
													}
													else
													{
														lGatescore = lGate2013score;
													}
													Double value = Double.parseDouble(lGatescore);
													if(!lOBCCategoryMap.containsKey(lKey)) // for the first applicant
													{
														lScore = value; 
													}
													else
													{

														Double lScore1 =Double.parseDouble(lOBCCategoryMap.get(lKey)) ;
														if(lScore1 < value)
														{
															value = lScore1;

														}


													}


													String lGateScoreFinal = Double.toString(value);
													lOBCCategoryMap.put(lKey, lGateScoreFinal); // STRUCTURE OF MAP --> AE1Y:AE,300 
													// we are putting in the map for the OBC category what is the minimum cutoff marks for that course

												}

											}

										}


										else if(lCategoryfromSeatAcquired.equalsIgnoreCase("3") && lCategoryValid.equalsIgnoreCase("Y"))
										{
											logger.error("The category from seat seat acquired is SC");
											if(leliggatepapercodeList.contains(lAppGatePaperCode))
											{ 
												String lQualifyingDiscipline =  lApplicantDataValueMap.get("txtQualifyingDiscpline");

												if(ldiscipList.contains(lQualifyingDiscipline))
												{
													String str = llist2.get(0);
													if(str==null || str.isEmpty())
													{
														str = "Z";

													}
													String lKey = "";
													String ldiscip = "";
													for(int i = 0; i<ldiscipList.size();i++)
													{
														if(i == 0)
														{
															ldiscip = ldiscipList.get(i);

														}

														else
														{
															ldiscip = ldiscip + "," + ldiscipList.get(i);

														}

													}

													lKey = lProg+":" +ldiscip+":"+str;
													String lGate2014score = lApplicantDataValueMap.get("txtGATE2014Score");
													String lGate2013score = lApplicantDataValueMap.get("txtGATE2013Score");
													if(lGate2014score != null && !lGate2014score.isEmpty())
													{

														lGatescore = lGate2014score;
													}
													else
													{
														lGatescore = lGate2013score;
													}
													Double value = Double.parseDouble(lGatescore);
													if(!lSCCategoryMap.containsKey(lKey)) // for the first applicant
													{
														lScore = value; 
													}
													else
													{

														Double lScore1 =Double.parseDouble(lSCCategoryMap.get(lKey)) ;
														if(lScore1 < value)
														{
															value = lScore1;

														}


													}


													String lGateScoreFinal = Double.toString(value);
													lSCCategoryMap.put(lKey, lGateScoreFinal); // STRUCTURE OF MAP --> AE1Y:AE,300 
													// we are putting in the map for the OBC category what is the minimum cutoff marks for that course

												}



											}


										}
										else if(lCategoryfromSeatAcquired.equalsIgnoreCase("4") && lCategoryValid.equalsIgnoreCase("Y"))
										{
											
											if(leliggatepapercodeList.contains(lAppGatePaperCode))
											{ 
												String lQualifyingDiscipline =  lApplicantDataValueMap.get("txtQualifyingDiscpline");
												if(ldiscipList.contains(lQualifyingDiscipline))
												{
													String str = llist2.get(0);
													if(str==null || str.isEmpty())
													{
														str = "Z";

													}
													String lKey = "";
													String ldiscip = "";
													for(int i = 0; i<ldiscipList.size();i++)
													{
														if(i == 0)
														{
															ldiscip = ldiscipList.get(i);

														}

														else
														{
															ldiscip = ldiscip + "," + ldiscipList.get(i);

														}

													}

													lKey = lProg+":" +ldiscip+":"+str;
													String lGate2014score = lApplicantDataValueMap.get("txtGATE2014Score");
													String lGate2013score = lApplicantDataValueMap.get("txtGATE2013Score");
													if(lGate2014score != null && !lGate2014score.isEmpty())
													{

														lGatescore = lGate2014score;
													}
													else
													{
														lGatescore = lGate2013score;
													}
													Double value = Double.parseDouble(lGatescore);
													if(!lSTCategoryMap.containsKey(lKey)) // for the first applicant
													{
														lScore = value; 
													}
													else
													{

														Double lScore1 =Double.parseDouble(lSTCategoryMap.get(lKey)) ;
														if(lScore1 < value)
														{
															value = lScore1;

														}


													}


													String lGateScoreFinal = Double.toString(value);
												
													lSTCategoryMap.put(lKey, lGateScoreFinal); // STRUCTURE OF MAP --> AE1Y:AE,300 
													// we are putting in the map for the ST category what is the minimum cutoff marks for that course

												}

											}

										}
									}

								}

							}

						}
					}



				}

			}

		}
		try{
		logger.error("The gencutoff maps are ready" +lGeneralCategoryMap.size());
		logger.error("lGeneralCategoryMap::"+lGeneralCategoryMap);
		logger.error("The obccutoff maps are ready" +lOBCCategoryMap.size()); 
		logger.error("lOBCCategoryMap::"+lOBCCategoryMap);
		logger.error("The sccutoff maps are ready" +lSCCategoryMap.size());
		logger.error("lSCCategoryMap::"+lSCCategoryMap);
		logger.error("The stcutoff maps are ready" +lSTCategoryMap.size());
		logger.error("lSTCategoryMap::"+lSTCategoryMap);
		}
		
		catch(Exception e)
		{
			logger.error("Error in generating the cutoff sheet");
		}
		}
		
		catch(Exception e)
		{
			logger.error("Exception in Method testFeasability Logic while generating the cutoffs");
		}
		
		return lSet;

	
	}
//end by akshay	
}
/**
@ author
675240 mayank.saklani@tcs.com
*/