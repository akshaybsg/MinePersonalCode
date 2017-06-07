package com.tcs.EformsTesting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;

public class CLATUGSeatAllocation_2 {

	public static final Logger logger = Logger.getLogger(CLATUGSeatAllocation_2.class);
		
		//-----This map is used for storing Applicants Data.------//
		private static TreeMap<Integer, HashMap<String, String>> pApplicantsDataMap = new TreeMap<Integer, HashMap<String,String>>();
		//-----This map is used for storing seats to be allocated in Each University under various categories: for permanent Allocation.-------//
		private static HashMap<String, ArrayList<String>> pUGSeatDetailsMap = new HashMap<String, ArrayList<String>>();
		//-----This map is used for storing seats to be allocated in Each University under various categories: for temporary Allocation.-------//
		private static HashMap<String, ArrayList<String>> tUGSeatDetailsMap = new HashMap<String, ArrayList<String>>();
		//-----This map is used for storing seats to be allocated in Each University under various categories: for Seat Update in case of Locked Status.-------//
		private static HashMap<String, ArrayList<String>> oUGSeatDetailsMap = new HashMap<String, ArrayList<String>>();
		//-----This map will contain data to be updated in database for all Applicants.---------//
		private static HashMap<Integer, HashMap<Integer, String>> pUpdateValueMap = new HashMap<Integer, HashMap<Integer, String>>();
		//-----This map will contain data to be updated in database for all Applicants.---------//
		private static HashMap<Integer, HashMap<Integer, String>> tUpdateValueMap = new HashMap<Integer, HashMap<Integer, String>>();
		//-----This is used to know for which round Seat Allocation will be done -----//
		private static Integer roundNumber = 0;
		//-----This map is used for storing information about updating offers details-------//
		private static HashMap<String, String> updateOfferDetailsMap = new HashMap<String, String>();
		
		private static HashMap<Integer, Integer> deAllocationCategoryMap = new HashMap<Integer, Integer>();
		private static HashMap<Integer, Integer> rankPreferenceMap = new HashMap<Integer, Integer>();
		public static void main(String[] args) 
		{ 
			logger.error("Inside CLATUGSeatAllocation class");
		
			
			Connection con = null ;
			try
			{	Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/alm_1049","root", "password");
				getApplicantsDetails(con);
			}
			catch(Exception e)
			{
				logger.error("CLATUGSeatAllocation ::: Error in getting Applicants details from Database.");
			}

			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://172.17.221.117:3306/alm_1049","root", "password");
				getOfferDetails(con);  
			}
			catch(Exception e)
			{
				logger.error("CLATUGSeatAllocation ::: Error in getting Seat Details from Database.");
			}
			
			try
			{
				seatAllocationMethod();
			}
			catch(Exception e)
			{
				logger.error("CLATUGSeatAllocation ::: Allocation Method Completed.");
			}
			//----- Now we will update the database with allocation result of applicants.----//	
			try
			{	
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql:/172.17.221.117:3306/alm_1049","root", "password");
				logger.error("CLATUGSeatAllocation ::: Started updating Applicants Data.");
				PreparedStatement ps2 = null;
				ps2 = con.prepareStatement("Update app_form2301_data set txtAllottedPreference=? , preferenceOfferedR"
						+roundNumber+"=? ,seatCategoryR"+roundNumber+"=? ,verticalSeatCategoryR"+roundNumber+"=? , roundNumber=? where txtAllIndiaRank=? ;");		
					
				for(Integer rank:tUpdateValueMap.keySet())
				{	
					HashMap<Integer, String> lReturnMap = new HashMap<Integer, String>();
					lReturnMap = tUpdateValueMap.get(rank);
					ps2.setString(1, lReturnMap.get(1));//----- The offered Preference for that round will be updated. ----//
					ps2.setString(2, lReturnMap.get(2));//----- The offered choice number for that round will be updated. .----//
					ps2.setString(3, lReturnMap.get(3));//----- Seat Category number for that round will be updated. ----//
					ps2.setString(4, lReturnMap.get(4));//----- Vertical Seat Category number for that round will be updated. ----//
					ps2.setString(5, roundNumber + "");//----- The round number for which scheduler has been run.
					ps2.setString(6, rank + "");
					ps2.addBatch();
				}	
				for(Integer rank:pUpdateValueMap.keySet())
				{	
					HashMap<Integer, String> lReturnMap = new HashMap<Integer, String>();
					lReturnMap = pUpdateValueMap.get(rank);
					ps2.setString(1, lReturnMap.get(1));//----- The offered Preference for that round will be updated. ----//
					ps2.setString(2, lReturnMap.get(2));//----- The offered choice number for that round will be updated. .----//
					ps2.setString(3, lReturnMap.get(3));//----- Seat Category number for that round will be updated. ----//
					ps2.setString(4, lReturnMap.get(4));//----- Vertical Seat Category number for that round will be updated. ----//
					ps2.setString(5, roundNumber + "");//----- The round number for which scheduler has been run.
					ps2.setString(6, rank + "");
					ps2.addBatch();
				}
				ps2.executeBatch();
				logger.error("Inside class : CLATUGSeatAllocation -- Status : Finished updating Applicants Data.");
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					logger.error("CLATUGSeatAllocation ::: Error in Disconnecting after Updating Applicants database.");
				}
			}
			catch(Exception e)
			{
				logger.error("CLATUGSeatAllocation ::: Error in updating Database");
			}
			finally
			{
				pApplicantsDataMap.clear();
				pUGSeatDetailsMap.clear();
				tUGSeatDetailsMap.clear();
				oUGSeatDetailsMap.clear();
				pUpdateValueMap.clear();
				tUpdateValueMap.clear();
				roundNumber = 0;
				updateOfferDetailsMap.clear();
				try
				{
					if(con != null)
						con.close();
				}
				catch(Exception e)
				{
					logger.error("CLATUGSeatAllocation ::: Error in Disconnecting after Updating database.");
				}
			}
		}
		
		//------ Function to get all Applicants data from database ----- //
		public static void getApplicantsDetails(Connection con)
		{
			ResultSet rs=null;	
			PreparedStatement ps = null;
			String query = "SELECT * FROM app_form2301_data WHERE app_status in ('Locked','Upgrade','Submitted') and txtCourseAppFor='U.G. Course' and rowstate > -1 ;";
			try
			{
				ps = con.prepareStatement(query);
				rs=ps.executeQuery();
				while(rs.next())
				{
					HashMap<String,String> lMap=new HashMap<String,String>();
					lMap.put("txtAllottedPreference", rs.getString("txtAllottedPreference")==null? "":rs.getString("txtAllottedPreference").trim());
					lMap.put("AllIndiaCategory", rs.getString("txtOther1")==null? "":rs.getString("txtOther1").trim());
					lMap.put("AllIndiaSubCategory", rs.getString("txtOther2")==null? "":rs.getString("txtOther2").trim());
					lMap.put("txtAPRegion", rs.getString("txtAPRegion")==null? "":rs.getString("txtAPRegion").trim());
					lMap.put("txtStateofDomicile", rs.getString("txtStateofDomicile")==null? "":rs.getString("txtStateofDomicile").trim());
					lMap.put("StateCategory", rs.getString("txtOther3")==null? "":rs.getString("txtOther3").trim());
					lMap.put("StateSubCategory", rs.getString("txtOther4")==null? "":rs.getString("txtOther4").trim());
					lMap.put("SpecialCategory", rs.getString("txtOther5")==null? "":rs.getString("txtOther5").trim());
					lMap.put("txtCourseAppFor", rs.getString("txtCourseAppFor")==null? "":rs.getString("txtCourseAppFor").trim());
					lMap.put("txtPreference1", rs.getString("txtPreference1")==null? "":rs.getString("txtPreference1").trim());
					lMap.put("txtPreference2", rs.getString("txtPreference2")==null? "":rs.getString("txtPreference2").trim());
					lMap.put("txtPreference3", rs.getString("txtPreference3")==null? "":rs.getString("txtPreference3").trim());
					lMap.put("txtPreference4", rs.getString("txtPreference4")==null? "":rs.getString("txtPreference4").trim());
					lMap.put("txtPreference5", rs.getString("txtPreference5")==null? "":rs.getString("txtPreference5").trim());
					lMap.put("txtPreference6", rs.getString("txtPreference6")==null? "":rs.getString("txtPreference6").trim());
					lMap.put("txtPreference7", rs.getString("txtPreference7")==null? "":rs.getString("txtPreference7").trim());
					lMap.put("txtPreference8", rs.getString("txtPreference8")==null? "":rs.getString("txtPreference8").trim());
					lMap.put("txtPreference9", rs.getString("txtPreference9")==null? "":rs.getString("txtPreference9").trim());
					lMap.put("txtPreference10", rs.getString("txtPreference10")==null? "":rs.getString("txtPreference10").trim());
					lMap.put("txtPreference11", rs.getString("txtPreference11")==null? "":rs.getString("txtPreference11").trim());
					lMap.put("txtPreference12", rs.getString("txtPreference12")==null? "":rs.getString("txtPreference12").trim());
					lMap.put("txtPreference13", rs.getString("txtPreference13")==null? "":rs.getString("txtPreference13").trim());
					lMap.put("txtPreference14", rs.getString("txtPreference14")==null? "":rs.getString("txtPreference14").trim());
					lMap.put("txtPreference15", rs.getString("txtPreference15")==null? "":rs.getString("txtPreference15").trim());
					lMap.put("txtPreference16", rs.getString("txtPreference16")==null? "":rs.getString("txtPreference16").trim());
					lMap.put("preferenceOfferedR1", rs.getString("preferenceOfferedR1")==null? "":rs.getString("preferenceOfferedR1").trim());
					lMap.put("preferenceOfferedR2", rs.getString("preferenceOfferedR2")==null? "":rs.getString("preferenceOfferedR2").trim());
					lMap.put("preferenceOfferedR3", rs.getString("preferenceOfferedR3")==null? "":rs.getString("preferenceOfferedR3").trim());
					lMap.put("verticalSeatCategoryR1", rs.getString("verticalSeatCategoryR1")==null? "":rs.getString("verticalSeatCategoryR1").trim());
					lMap.put("verticalSeatCategoryR2", rs.getString("verticalSeatCategoryR2")==null? "":rs.getString("verticalSeatCategoryR2").trim());
					lMap.put("verticalSeatCategoryR3", rs.getString("verticalSeatCategoryR3")==null? "":rs.getString("verticalSeatCategoryR3").trim());
					
					lMap.put("app_status", rs.getString("app_status")==null? "":rs.getString("app_status").trim());
					pApplicantsDataMap.put(Integer.parseInt(rs.getString("txtAllIndiaRank").trim()), lMap);
					
				}
				logger.error("CLATUGSeatAllocation ::: Data Map Ready");
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					logger.error("CLATUGSeatAllocation ::: Error in Disconnecting after getting Applicant's data");
				}
			}
			catch(Exception e)
			{
				logger.error("CLATUGSeatAllocation ::: Error in getting Applicant's Data");
			}
			finally
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					logger.error("CLATUGSeatAllocation ::: Error in Disconnecting after getting Applicant's data");
				}
			}
		}
		
		//------ Function to get Offer data from database ----- //
		public static void getOfferDetails(Connection con)
		{	
			ResultSet rs1=null;
			PreparedStatement ps1 = null;
			String query1 = "SELECT * FROM app_form2398_data WHERE txtCourse = 'UG' and rowstate>-1 ;";
			try
			{
				ps1 = con.prepareStatement(query1);
				rs1=ps1.executeQuery();	    
				while(rs1.next())
				{
					String university = "";		
					university = rs1.getString("txtUniversity")==null? "":rs1.getString("txtUniversity").trim();				
					roundNumber = Integer.parseInt(rs1.getString("txtRoundNumber")==null? "":rs1.getString("txtRoundNumber").trim());
					
					//----This array list will store amount of offers to be made, mapped against each discipline list of each course.----//
					ArrayList<String> seatList= new ArrayList<String>();
					//----- First we will store the state name to which university belongs. ----//
					seatList.add(rs1.getString("txtUniversityState")==null? "N":rs1.getString("txtUniversityState").trim());
					//----- Now we will add seats category wise ----//
					seatList.add(rs1.getString("Unreserved")==null? "0":rs1.getString("Unreserved").trim());
					seatList.add(rs1.getString("OBC")==null? "0":rs1.getString("OBC").trim());
					seatList.add(rs1.getString("SC")==null? "0":rs1.getString("SC").trim());
					seatList.add(rs1.getString("ST")==null? "0":rs1.getString("ST").trim());
					seatList.add(rs1.getString("STHills")==null? "0":rs1.getString("STHills").trim());
					
					seatList.add(rs1.getString("AIWomen")==null? "0":rs1.getString("AIWomen").trim());
					seatList.add(rs1.getString("AIPWD")==null? "0":rs1.getString("AIPWD").trim());
					seatList.add(rs1.getString("PunjabDomicile")==null? "0":rs1.getString("PunjabDomicile").trim());
					seatList.add(rs1.getString("AncestralResidentofvillage")==null? "0":rs1.getString("AncestralResidentofvillage").trim());
					seatList.add(rs1.getString("GujaratDomicile")==null? "0":rs1.getString("GujaratDomicile").trim());
					seatList.add(rs1.getString("AssamDomicile")==null? "0":rs1.getString("AssamDomicile").trim());
					seatList.add(rs1.getString("StateUnreserved")==null? "0":rs1.getString("StateUnreserved").trim());
					seatList.add(rs1.getString("StateOBC")==null? "0":rs1.getString("StateOBC").trim());
					seatList.add(rs1.getString("StateSC")==null? "0":rs1.getString("StateSC").trim());
					seatList.add(rs1.getString("StateST")==null? "0":rs1.getString("StateST").trim());
					seatList.add(rs1.getString("BC")==null? "0":rs1.getString("BC").trim());
					seatList.add(rs1.getString("EBC")==null? "0":rs1.getString("EBC").trim());
					seatList.add(rs1.getString("WBC")==null? "0":rs1.getString("WBC").trim());
					seatList.add(rs1.getString("BCGroupA")==null? "0":rs1.getString("BCGroupA").trim());
					seatList.add(rs1.getString("BCGroupB")==null? "0":rs1.getString("BCGroupB").trim());
					seatList.add(rs1.getString("BCM")==null? "0":rs1.getString("BCM").trim());
					seatList.add(rs1.getString("MBC")==null? "0":rs1.getString("MBC").trim());
					seatList.add(rs1.getString("SCA")==null? "0":rs1.getString("SCA").trim());
					seatList.add(rs1.getString("SEBCETB")==null? "0":rs1.getString("SEBCETB").trim());
					seatList.add(rs1.getString("SEBCMU")==null? "0":rs1.getString("SEBCMU").trim());
					seatList.add(rs1.getString("SEBCOBH")==null? "0":rs1.getString("SEBCOBH").trim());
					seatList.add(rs1.getString("SEBCLCAI")==null? "0":rs1.getString("SEBCLCAI").trim());
					seatList.add(rs1.getString("SEBCOBX")==null? "0":rs1.getString("SEBCOBX").trim());
					seatList.add(rs1.getString("SEBCKudumbi")==null? "0":rs1.getString("SEBCKudumbi").trim());
					seatList.add(rs1.getString("SEBCKKKNKVOKAA")==null? "0":rs1.getString("SEBCKKKNKVOKAA").trim());
					seatList.add(rs1.getString("SEBCDheevara")==null? "0":rs1.getString("SEBCDheevara").trim());
					seatList.add(rs1.getString("SEBCVishwakarma")==null? "0":rs1.getString("SEBCVishwakarma").trim());
					
					seatList.add(rs1.getString("APStateUnreserved")==null? "0":rs1.getString("APStateUnreserved").trim());
					seatList.add(rs1.getString("APStateBCGroupA")==null? "0":rs1.getString("APStateBCGroupA").trim());
					seatList.add(rs1.getString("APStateBCGroupB")==null? "0":rs1.getString("APStateBCGroupB").trim());
					seatList.add(rs1.getString("APStateBCGroupC")==null? "0":rs1.getString("APStateBCGroupC").trim());
					seatList.add(rs1.getString("APStateBCGroupD")==null? "0":rs1.getString("APStateBCGroupD").trim());
					seatList.add(rs1.getString("APStateBCGroupE")==null? "0":rs1.getString("APStateBCGroupE").trim());
					seatList.add(rs1.getString("APStateSC")==null? "0":rs1.getString("APStateSC").trim());
					seatList.add(rs1.getString("APStateST")==null? "0":rs1.getString("APStateST").trim());
					seatList.add(rs1.getString("AURegionUnreserved")==null? "0":rs1.getString("AURegionUnreserved").trim());
					seatList.add(rs1.getString("AURegionBCGroupA")==null? "0":rs1.getString("AURegionBCGroupA").trim());
					seatList.add(rs1.getString("AURegionBCGroupB")==null? "0":rs1.getString("AURegionBCGroupB").trim());
					seatList.add(rs1.getString("AURegionBCGroupC")==null? "0":rs1.getString("AURegionBCGroupC").trim());
					seatList.add(rs1.getString("AURegionBCGroupD")==null? "0":rs1.getString("AURegionBCGroupD").trim());
					seatList.add(rs1.getString("AURegionBCGroupE")==null? "0":rs1.getString("AURegionBCGroupE").trim());
					seatList.add(rs1.getString("AURegionSC")==null? "0":rs1.getString("AURegionSC").trim());
					seatList.add(rs1.getString("AURegionST")==null? "0":rs1.getString("AURegionST").trim());
					seatList.add(rs1.getString("OURegionUnreserved")==null? "0":rs1.getString("OURegionUnreserved").trim());
					seatList.add(rs1.getString("OURegionBCGroupA")==null? "0":rs1.getString("OURegionBCGroupA").trim());
					seatList.add(rs1.getString("OURegionBCGroupB")==null? "0":rs1.getString("OURegionBCGroupB").trim());
					seatList.add(rs1.getString("OURegionBCGroupC")==null? "0":rs1.getString("OURegionBCGroupC").trim());
					seatList.add(rs1.getString("OURegionBCGroupD")==null? "0":rs1.getString("OURegionBCGroupD").trim());
					seatList.add(rs1.getString("OURegionBCGroupE")==null? "0":rs1.getString("OURegionBCGroupE").trim());
					seatList.add(rs1.getString("OURegionSC")==null? "0":rs1.getString("OURegionSC").trim());
					seatList.add(rs1.getString("OURegionST")==null? "0":rs1.getString("OURegionST").trim());
					seatList.add(rs1.getString("SVURegionUnreserved")==null? "0":rs1.getString("SVURegionUnreserved").trim());
					seatList.add(rs1.getString("SVURegionBCGroupA")==null? "0":rs1.getString("SVURegionBCGroupA").trim());
					seatList.add(rs1.getString("SVURegionBCGroupB")==null? "0":rs1.getString("SVURegionBCGroupB").trim());
					seatList.add(rs1.getString("SVURegionBCGroupC")==null? "0":rs1.getString("SVURegionBCGroupC").trim());
					seatList.add(rs1.getString("SVURegionBCGroupD")==null? "0":rs1.getString("SVURegionBCGroupD").trim());
					seatList.add(rs1.getString("SVURegionBCGroupE")==null? "0":rs1.getString("SVURegionBCGroupE").trim());
					seatList.add(rs1.getString("SVURegionSC")==null? "0":rs1.getString("SVURegionSC").trim());
					seatList.add(rs1.getString("SVURegionST")==null? "0":rs1.getString("SVURegionST").trim());
					
					seatList.add(rs1.getString("StateWomen")==null? "0":rs1.getString("StateWomen").trim());
					seatList.add(rs1.getString("StatePWD")==null? "0":rs1.getString("StatePWD").trim());
					seatList.add(rs1.getString("DefencePersonnal")==null? "0":rs1.getString("DefencePersonnal").trim());
					seatList.add(rs1.getString("EminentSportsPersons")==null? "0":rs1.getString("EminentSportsPersons").trim());
					seatList.add(rs1.getString("DFF")==null? "0":rs1.getString("DFF").trim());
					seatList.add(rs1.getString("ChildrenofArmedPersonnel")==null? "0":rs1.getString("ChildrenofArmedPersonnel").trim());
					seatList.add(rs1.getString("NCC")==null? "0":rs1.getString("NCC").trim());
					seatList.add(rs1.getString("ChildrenofExServiceman")==null? "0":rs1.getString("ChildrenofExServiceman").trim());

					//----- Now we will add all these individual category seats to get total number of seats. ----//	    	
					Integer maxSeats = 0 ;
					for(int i=1;i<73;i++)
						maxSeats = maxSeats + Integer.parseInt(seatList.get(i));

					seatList.add(maxSeats+"");
					
					Integer verticalSeats = 0;
					for(int i=1;i<6;i++)
						verticalSeats = verticalSeats + Integer.parseInt(seatList.get(i));
					for(int i=12;i<33;i++)
						verticalSeats = verticalSeats + Integer.parseInt(seatList.get(i));
					seatList.add(verticalSeats+"");
					
					Integer apStateSeats = 0;
					for(int i=33;i<65;i++)
						apStateSeats = apStateSeats + Integer.parseInt(seatList.get(i));
					seatList.add(apStateSeats+"");
					
					String tempUniversity = university;
					ArrayList<String> tempSeatList = new ArrayList<String>(seatList);
					pUGSeatDetailsMap.put(university, seatList);
					tUGSeatDetailsMap.put(tempUniversity, tempSeatList);
				}
				
				logger.error("CLATUGSeatAllocation ::: OfferData Map Ready ");
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					logger.error("CLATUGSeatAllocation ::: Error in Disconnecting after getting Offer data.");
				}
			}
			catch(Exception e)
			{
				logger.error("CLATUGSeatAllocation ::: Error in getting Offer Data");
			}
			finally
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					logger.error("CLATUGSeatAllocation ::: Error in Disconnecting after getting Offer data.");
				}
			}
		}

		private static boolean isFemale = false ;
		private static boolean isPWD = false ;
		private static boolean isPunjabDomicile = false ;
		private static boolean isAncestralResidentofvillage = false;
		private static boolean isGujaratDomicile = false ;
		private static boolean isAssamDomicile = false ;
		private static boolean isDefencePersonnal = false;
		private static boolean isEminentSportsPersons = false ;
		private static boolean isDFF = false ;
		private static boolean isChildrenofArmedPersonnel = false;
		private static boolean isNCC = false;
		private static boolean isChildrenofExServiceman = false ;
		private static boolean seatAllocatedFlag = false;
		//-----THis will tell trough which reservation category horizontal or vertical seat has been offered.-----//
		private static boolean seatOfferedByHorizontal = false;
		private static boolean recurrsionNeeded = false;
		private static boolean stateChecked = false;
		
		private static String applicantCategory = null;
		private static String status = null;
		private static String domicileState = null;
		private static String universityState = null;
		private static String apRegion = null;
		private static String applicantStateCategory = null;
		
		private static int offeredPreferenceNo;						
		private static int stateVariable ;
		private static int categoryVariable ;
		
		//------ Function to allocate seats to Applicants.----- //
		public static void seatAllocationMethod() throws Exception
		{
			recurrsionNeeded = false;
			//------ We will run loop on Applicant Data Map Rank wise in ascending order -----//
			for(int rank:pApplicantsDataMap.keySet())
			{
				seatOfferedByHorizontal= false;
				HashMap<Integer, String> updateMap = new HashMap<Integer, String>();
				ArrayList<String> tSeatList = new ArrayList<String>();
				ArrayList<String> pSeatList = new ArrayList<String>();

				ArrayList<Integer> catList = new ArrayList<Integer>();
				ArrayList<Integer> cat1List = new ArrayList<Integer>();
				ArrayList<Integer> cat2List = new ArrayList<Integer>();
				ArrayList<Integer> rankList = new ArrayList<Integer>();
				//---- Check if applicant has been already allocated permanent seat, if yes then skip else proceed. -----//  
				if(!pUpdateValueMap.containsKey(rank))
				{
					applicantCategory = pApplicantsDataMap.get(rank).get("AllIndiaCategory");
					status = pApplicantsDataMap.get(rank).get("app_status");
					//----- Check for applicant's status, if it is not "Locked" proceed. -----//
					if(!status.equalsIgnoreCase("Locked"))
					{
						//- Check up to which preference loop has to be run, if it's upgrade then up to last offered preference else to 16th preference. -//
						
						if(status.equalsIgnoreCase("Upgrade"))
							offeredPreferenceNo = Integer.parseInt(pApplicantsDataMap.get(rank).get("preferenceOfferedR" + (roundNumber-1)));
						else
							offeredPreferenceNo = 17;
						
						//- Checking for various Horizontal categories for which applicant in eligible. -//
						
						if(pApplicantsDataMap.get(rank).get("AllIndiaSubCategory").contains("Women"))
							isFemale = true;
						else 
							isFemale =false;
						if(pApplicantsDataMap.get(rank).get("AllIndiaSubCategory").contains("PWD"))
							isPWD = true;
						else 
							isPWD =false;
						if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Defence Personnal"))
							isDefencePersonnal = true;
						else 
							isDefencePersonnal =false;
						if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Eminent Sports Persons"))
							isEminentSportsPersons = true;
						else 
							isEminentSportsPersons =false;
						if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("DFF"))
							isDFF = true;
						else 
							isDFF =false;
						if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Children of Armed Personnel"))
							isChildrenofArmedPersonnel = true;
						else 
							isChildrenofArmedPersonnel =false;
						if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("NCC"))
							isNCC = true;
						else 
							isNCC =false;
						if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Service"))
							isChildrenofExServiceman = true;
						else 
							isChildrenofExServiceman =false;
						
						apRegion = pApplicantsDataMap.get(rank).get("txtAPRegion");
						
						applicantStateCategory = pApplicantsDataMap.get(rank).get("StateCategory");

						//----- Checking for applicant's domicile State/UT. -----//
						domicileState = pApplicantsDataMap.get(rank).get("txtStateofDomicile") ;
						int seatCategory ;
						int verticalSeatCategory;
							//----- Preference Loop ----//	
							
							PreferenceLoop : for(int preferenceNo=1; preferenceNo<offeredPreferenceNo; preferenceNo++)
							{
								String preference = pApplicantsDataMap.get(rank).get("txtPreference"+preferenceNo);
								if(preference == null || preference.equals(""))
									break PreferenceLoop;
								
								tSeatList = tUGSeatDetailsMap.get(preference);						
								pSeatList = pUGSeatDetailsMap.get(preference);
								universityState = pSeatList.get(0);
								if(domicileState.equalsIgnoreCase("Punjab") && tSeatList.get(0).equalsIgnoreCase("Punjab"))
									isPunjabDomicile = true;
								else 
									isPunjabDomicile =false;
								
								if(domicileState.equalsIgnoreCase("Gujarat") && tSeatList.get(0).equalsIgnoreCase("Gujarat"))
									isGujaratDomicile = true;
								else 
									isGujaratDomicile =false;
								
								if(domicileState.equalsIgnoreCase("Assam") && tSeatList.get(0).equalsIgnoreCase("Assam"))
									isAssamDomicile = true;
								else
									isAssamDomicile = false;
								
								if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Ancestral Resident of village") && tSeatList.get(0).equalsIgnoreCase("Punjab"))
									isAncestralResidentofvillage = true;
								else 
									isAncestralResidentofvillage =false;
								
								//----- Checking if applicant's domicile State/UT match with Preference University's state. -----//
								if(domicileState.equalsIgnoreCase(tSeatList.get(0)))
									stateVariable = 2 ;
								else
									stateVariable = 1;
								
								//----- Checking for applicant's category. -----//
								if(applicantCategory.equalsIgnoreCase("Unreserved"))
									categoryVariable = 1;
								else
									categoryVariable = 2;
								
								int totalSeats = 0 ;
								int seatCount = 0 ;
								int verticalSeats = 0;
								int apSeats = 0;
								int ptotalSeats = 0 ;
								int pverticalSeats = 0;
								totalSeats = Integer.parseInt(tSeatList.get(73));
								verticalSeats = Integer.parseInt(tSeatList.get(74));
								apSeats = Integer.parseInt(tSeatList.get(75));
								seatAllocatedFlag = false;
								stateChecked = false;
								seatCategory = 1;
								verticalSeatCategory = 1;
						//		if(rank == 2414)
						//			System.out.println("hi");
								if(totalSeats > 0)
								{
									do
									{
										stateVariable--;
										do
										{
											categoryVariable--;
											seatCount = Integer.parseInt(tSeatList.get(seatCategory));
											if(seatCount > 0)
											{

												System.out.println("Before Allocation TempList "+tSeatList);
												System.out.println("Before Allocation PermList "+pSeatList);
												if(seatCategory < 6 || (seatCategory > 11 && seatCategory < 65))
													seatAllocatedFlag = true;
												else if(universityState.equalsIgnoreCase("Chattisgarh") || universityState.equalsIgnoreCase("Andhra Pradesh"))
													seatAllocatedFlag = true;
												else
												{
													try
													{
														rank = deAllocationCategoryMap.get(seatCategory);
														preferenceNo = rankPreferenceMap.get(rank);
														allocateDeAllocated(preference, preferenceNo, seatCategory, rank, pSeatList, tSeatList,updateMap);
														
													}
													catch(Exception e)
													{
														logger.error("CLATUGSeatAllocation ::: Allocation Method Completed.");
														throw new Exception();
													}
												}	
											}
											else
											{
												stateChecked = false;
												if(categoryVariable == 1 && seatCategory == 1)//----- For All India vertical Reservation other then general.-----//
												{
													if(applicantCategory.equalsIgnoreCase("Other Backward Class(OBC)"))
													{
														seatCategory = seatCategory + 1;
														verticalSeatCategory = verticalSeatCategory + 1;
													}
													else if(applicantCategory.equalsIgnoreCase("Scheduled Caste(SC)"))
													{
														seatCategory = seatCategory + 2;
														verticalSeatCategory = verticalSeatCategory + 2;
													}
													else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (ST)"))
													{
														seatCategory = seatCategory + 3;
														verticalSeatCategory = verticalSeatCategory + 3;
													}
													else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (Hills)"))
													{
														seatCategory = seatCategory + 4;
														verticalSeatCategory = verticalSeatCategory + 4;
													}
													else
													{
														logger.error("CLATUGSeatAllocation ::: Error in All India Category of candidate with Rank " + rank);
													}
												}
												else if(categoryVariable == 0 && seatCategory < 12)//----- Loop for All India Horizontal Reservation.------//
												{
													if(universityState.equalsIgnoreCase("Chhattisgarh") && !domicileState.equalsIgnoreCase("Chhattisgarh"))
													{
														if(Integer.parseInt(tSeatList.get(1)) + Integer.parseInt(tSeatList.get(3))+ Integer.parseInt(tSeatList.get(4)) <= 0)
															stateChecked = true;
													}
													else if(verticalSeats <= 0)
														stateChecked = true;
													
													if(stateChecked)
													{
														categoryVariable = 1; //----- For running inner do loop many times for horizontal reservation.-----//
														if(isFemale && seatCategory < 6)
															seatCategory = 6;
														else if(isPWD && seatCategory < 7)
															seatCategory = 7;
														else if(isPunjabDomicile && seatCategory < 8)
															seatCategory = 8;
														else if(isAncestralResidentofvillage && seatCategory < 9)
															seatCategory = 9;
														else if(isGujaratDomicile && seatCategory < 10)
															seatCategory = 10;
														else if(isAssamDomicile && seatCategory < 11)
															seatCategory = 11;
														else
															categoryVariable = 0;
													}
													else
													{
														if(isFemale && Integer.parseInt(tSeatList.get(6)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(6))
															{
																deAllocationCategoryMap.put(6, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isPWD && Integer.parseInt(tSeatList.get(7)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(7))
															{
																deAllocationCategoryMap.put(7, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isPunjabDomicile && Integer.parseInt(tSeatList.get(8)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(8))
															{
																deAllocationCategoryMap.put(8, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isAncestralResidentofvillage  && Integer.parseInt(tSeatList.get(9)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(9))
															{
																deAllocationCategoryMap.put(9, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isGujaratDomicile  && Integer.parseInt(tSeatList.get(10)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(10))
															{
																deAllocationCategoryMap.put(10, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isAssamDomicile  && Integer.parseInt(tSeatList.get(11)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(11))
															{
																deAllocationCategoryMap.put(11, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														categoryVariable = 0;
													}
												}
												else if(categoryVariable == 1 && seatCategory > 12)//----- Loop for State Vertical Reservation.------//
												{	
													if(apRegion.equals(""))
													{
														if(applicantStateCategory.equalsIgnoreCase("OBC"))
														{
															seatCategory = seatCategory + 1;
															verticalSeatCategory = verticalSeatCategory + 1;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SC"))
														{
															seatCategory = seatCategory + 2;
															verticalSeatCategory = verticalSeatCategory + 2;
														}
														else if(applicantStateCategory.equalsIgnoreCase("ST"))
														{
															seatCategory = seatCategory + 3;
															verticalSeatCategory = verticalSeatCategory + 3;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC"))
														{
															seatCategory = seatCategory + 4;
															verticalSeatCategory = verticalSeatCategory + 4;
														}
														else if(applicantStateCategory.equalsIgnoreCase("EBC"))
														{
															seatCategory = seatCategory + 5;
															verticalSeatCategory = verticalSeatCategory + 5;
														}
														else if(applicantStateCategory.equalsIgnoreCase("WBC"))
														{
															seatCategory = seatCategory + 6;
															verticalSeatCategory = verticalSeatCategory + 6;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC-Group (A)"))
														{
															seatCategory = seatCategory + 7;
															verticalSeatCategory = verticalSeatCategory + 7;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC-Group (B)"))
														{
															seatCategory = seatCategory + 8;
															verticalSeatCategory = verticalSeatCategory + 8;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC(M)"))
														{
															seatCategory = seatCategory + 9;
															verticalSeatCategory = verticalSeatCategory + 9;
														}
														else if(applicantStateCategory.equalsIgnoreCase("MBC"))
														{
															seatCategory = seatCategory + 10;
															verticalSeatCategory = verticalSeatCategory + 10;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SC(A)"))
														{
															seatCategory = seatCategory + 11;
															verticalSeatCategory = verticalSeatCategory + 11;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-ETB"))
														{
															seatCategory = seatCategory + 12;
															verticalSeatCategory = verticalSeatCategory + 12;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-MU"))
														{
															seatCategory = seatCategory + 13;
															verticalSeatCategory = verticalSeatCategory + 13;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-OBH"))
														{
															seatCategory = seatCategory + 14;
															verticalSeatCategory = verticalSeatCategory + 14;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-LCAI"))
														{
															seatCategory = seatCategory + 15;
															verticalSeatCategory = verticalSeatCategory + 15;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-OBX"))
														{
															seatCategory = seatCategory + 16;
															verticalSeatCategory = verticalSeatCategory + 16;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-Kudumbi"))
														{
															seatCategory = seatCategory + 17;
															verticalSeatCategory = verticalSeatCategory + 17;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-Kusavan,Kulalan,Kulala Nair,Kumbharan,Velan,Oadan,Kulala,Andhra Nair,Aanthoor Nair"))
														{
															seatCategory = seatCategory + 18;
															verticalSeatCategory = verticalSeatCategory + 18;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-Dheevara"))
														{
															seatCategory = seatCategory + 19;
															verticalSeatCategory = verticalSeatCategory + 19;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SEBC-Vishwakarma"))
														{
															seatCategory = seatCategory + 20;
															verticalSeatCategory = verticalSeatCategory + 20;
														}
														else
														{
															logger.error("CLATUGSeatAllocation ::: Error in State Category of candidate with Rank " + rank);
														}
													}
													else
													{
														
														if(applicantStateCategory.equalsIgnoreCase("BC-Group (A)"))
														{
															seatCategory = seatCategory + 1;
															verticalSeatCategory = verticalSeatCategory + 1;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC-Group (B)"))
														{
															seatCategory = seatCategory + 2;
															verticalSeatCategory = verticalSeatCategory + 2;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC-Group (C)"))
														{
															seatCategory = seatCategory + 3;
															verticalSeatCategory = verticalSeatCategory + 3;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC-Group (D)"))
														{
															seatCategory = seatCategory + 4;
															verticalSeatCategory = verticalSeatCategory + 4;
														}
														else if(applicantStateCategory.equalsIgnoreCase("BC-Group (E)"))
														{
															seatCategory = seatCategory + 5;
															verticalSeatCategory = verticalSeatCategory + 5;
														}
														else if(applicantStateCategory.equalsIgnoreCase("SC"))
														{
															seatCategory = seatCategory + 6;
															verticalSeatCategory = verticalSeatCategory + 6;
														}
														else if(applicantStateCategory.equalsIgnoreCase("ST"))
														{
															seatCategory = seatCategory + 7;
															verticalSeatCategory = verticalSeatCategory + 7;
														}
														else
														{
															logger.error("CLATUGSeatAllocation ::: Error in State Category of AP Region candidate with Rank " + rank);
														}
													}
												}
												else if(categoryVariable == 0)//----- Loop for State Horizontal Reservation.------//
												{
													if(universityState.equalsIgnoreCase("Andhra Pradesh"))
													{
														if(apSeats <= 0)
															stateChecked = true;
														
													}
													else if(universityState.equalsIgnoreCase("Chhattisgarh"))
													{
														if(Integer.parseInt(tSeatList.get(12)) + Integer.parseInt(tSeatList.get(13))
																+ Integer.parseInt(tSeatList.get(14))+ Integer.parseInt(tSeatList.get(15)) <= 0)
															stateChecked = true;
													}
													else if(verticalSeats <= 0)
														stateChecked = true;
													
													if(stateChecked)
													{
														categoryVariable = 1; //----- For running inner do loop many times for horizontal reservation.-----//
														if(isFemale && seatCategory < 65)
															seatCategory = 65;
														else if(isPWD && seatCategory < 66)
															seatCategory = 66;
														else if(isDefencePersonnal && seatCategory < 67)
															seatCategory = 67;
														else if(isEminentSportsPersons && seatCategory < 68)
															seatCategory = 68;
														else if(isDFF && seatCategory < 69)
															seatCategory = 69;
														else if(isChildrenofArmedPersonnel && seatCategory < 70)
															seatCategory = 70;
														else if(isNCC && seatCategory < 71)
															seatCategory = 71;
														else if(isChildrenofExServiceman && seatCategory < 72)
															seatCategory = 72;
														else
															categoryVariable = 0;
													}
													else
													{
														if(isFemale  && Integer.parseInt(tSeatList.get(65)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(65))
															{
																deAllocationCategoryMap.put(65, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isPWD  && Integer.parseInt(tSeatList.get(66)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(66))
															{
																deAllocationCategoryMap.put(66, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isDefencePersonnal  && Integer.parseInt(tSeatList.get(67)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(67))
															{
																deAllocationCategoryMap.put(67, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isEminentSportsPersons  && Integer.parseInt(tSeatList.get(68)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(68))
															{
																deAllocationCategoryMap.put(68, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isDFF  && Integer.parseInt(tSeatList.get(69)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(69))
															{
																deAllocationCategoryMap.put(69, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isChildrenofArmedPersonnel  && Integer.parseInt(tSeatList.get(70)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(70))
															{
																deAllocationCategoryMap.put(70, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isNCC  && Integer.parseInt(tSeatList.get(71)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(71))
															{
																deAllocationCategoryMap.put(71, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														if(isChildrenofExServiceman  && Integer.parseInt(tSeatList.get(72)) > 0 && !rankPreferenceMap.containsKey(rank))
														{
															if(!deAllocationCategoryMap.containsKey(72))
															{
																deAllocationCategoryMap.put(72, rank);
															}
															
															rankPreferenceMap.put(rank, preferenceNo);
														}
														categoryVariable = 0;
													}
												}
											}
										}
										while(!seatAllocatedFlag && categoryVariable > 0);
										//-----If Applicant is eligible for State Quota Seats. ----//
										if(!seatAllocatedFlag && stateVariable == 1)
										{
											if(apRegion.equals(""))
											{
												seatCategory = 12;
												verticalSeatCategory = 12;
											}
											else if(apRegion.contains("State"))
											{
												seatCategory = 33;
												verticalSeatCategory = 33;
											}
											else if(apRegion.contains("AU"))
											{
												seatCategory = 41;
												verticalSeatCategory = 41;
											}
											else if(apRegion.contains("OU"))
											{
												seatCategory = 49;
												verticalSeatCategory = 49;
											}
											else if(apRegion.contains("SVU"))
											{
												seatCategory = 57;
												verticalSeatCategory = 57;
											}
											else
											{
												logger.error("CLATUGSeatAllocation ::: Error in AP State Region of candidate with Rank " + rank);
											}
											
											if(applicantStateCategory.equalsIgnoreCase("Unreserved"))
												categoryVariable = 1;
											else
												categoryVariable = 2;
										}
									}
									while(!seatAllocatedFlag && stateVariable > 0);//---- Run this until Applicant gets seat 
								}
								//------ If Applicant gets the seat .------//
								if(seatAllocatedFlag)
								{
								//	if(preference.equalsIgnoreCase("NLU-Gandhinagar"))
									//	System.out.println("okkk");
									//----- if applicant gets seat through All India Horizontal.-----//
									if(seatCategory > 5 && seatCategory < 12)
									{
										seatOfferedByHorizontal = true;
										recurrsionNeeded = true;
										ptotalSeats = Integer.parseInt(pSeatList.get(73));
										if(isFemale)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 6;
											tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
											if(tempSeatCount > 0)
												ptotalSeats--;
											tempSeatCount--;
											pSeatList.set(tempSeatCategory, tempSeatCount+"");
										}
										if(isPWD)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 7;
											tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
											if(tempSeatCount > 0)
												ptotalSeats--;
											tempSeatCount--;
											pSeatList.set(tempSeatCategory, tempSeatCount+"");
										}
										pSeatList.set(73, ptotalSeats+"");
									}
									else if(seatCategory < 6)//----- if applicant gets seat through All India Vertical.-----//
									{
										seatCount -- ;
										tSeatList.set(seatCategory, seatCount+"");
										verticalSeats --;
										tSeatList.set(74, verticalSeats+"");
										totalSeats--;
										tSeatList.set(73, totalSeats+"");
										if(isFemale && Integer.parseInt(tSeatList.get(6)) > 0)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 6;
											tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
											totalSeats = Integer.parseInt(tSeatList.get(73));
											if(tempSeatCount > 0)
												totalSeats--;
											tempSeatCount--;
											tSeatList.set(tempSeatCategory, tempSeatCount+"");
											tSeatList.set(73, totalSeats+"");
											if(!rankPreferenceMap.containsKey(rank))
											{
												int ptempSeatCount = 0;
												seatOfferedByHorizontal = true;
												ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												ptotalSeats = Integer.parseInt(pSeatList.get(73));
												if(ptempSeatCount > 0)
													ptotalSeats--;
												ptempSeatCount--;
												pSeatList.set(tempSeatCategory, ptempSeatCount+"");
												pSeatList.set(73, ptotalSeats+"");
											}
										}
										if(isPWD && Integer.parseInt(tSeatList.get(7)) > 0)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 7;
											tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
											totalSeats = Integer.parseInt(tSeatList.get(73));
											if(tempSeatCount > 0)
												totalSeats--;
											tempSeatCount--;
											tSeatList.set(tempSeatCategory, tempSeatCount+"");
											tSeatList.set(73, totalSeats+"");
											if(!rankPreferenceMap.containsKey(rank))
											{
												int ptempSeatCount = 0;
												seatOfferedByHorizontal = true;
												ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												ptotalSeats = Integer.parseInt(pSeatList.get(73));
												if(ptempSeatCount > 0)
													ptotalSeats--;
												ptempSeatCount--;
												pSeatList.set(tempSeatCategory, ptempSeatCount+"");
												pSeatList.set(73, ptotalSeats+"");
											}
										}
										if(isPunjabDomicile && Integer.parseInt(tSeatList.get(8)) > 0)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 8;
											tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
											totalSeats = Integer.parseInt(tSeatList.get(73));
											if(tempSeatCount > 0)
												totalSeats--;
											tempSeatCount--;
											tSeatList.set(tempSeatCategory, tempSeatCount+"");
											tSeatList.set(73, totalSeats+"");
											if(!rankPreferenceMap.containsKey(rank))
											{
												int ptempSeatCount = 0;
												seatOfferedByHorizontal = true;
												ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												ptotalSeats = Integer.parseInt(pSeatList.get(73));
												if(ptempSeatCount > 0)
													ptotalSeats--;
												ptempSeatCount--;
												pSeatList.set(tempSeatCategory, ptempSeatCount+"");
												pSeatList.set(73, ptotalSeats+"");
											}
										}
										if(isAncestralResidentofvillage && Integer.parseInt(tSeatList.get(9)) > 0)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 9;
											tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
											totalSeats = Integer.parseInt(tSeatList.get(73));
											if(tempSeatCount > 0)
												totalSeats--;
											tempSeatCount--;
											tSeatList.set(tempSeatCategory, tempSeatCount+"");
											tSeatList.set(73, totalSeats+"");
											if(!rankPreferenceMap.containsKey(rank))
											{
												int ptempSeatCount = 0;
												seatOfferedByHorizontal = true;
												ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												ptotalSeats = Integer.parseInt(pSeatList.get(73));
												if(ptempSeatCount > 0)
													ptotalSeats--;
												ptempSeatCount--;
												pSeatList.set(tempSeatCategory, ptempSeatCount+"");
												pSeatList.set(73, ptotalSeats+"");
											}
										}
										if(isGujaratDomicile && Integer.parseInt(tSeatList.get(10)) > 0)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 10;
											tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
											totalSeats = Integer.parseInt(tSeatList.get(73));
											if(tempSeatCount > 0)
												totalSeats--;
											tempSeatCount--;
											tSeatList.set(tempSeatCategory, tempSeatCount+"");
											tSeatList.set(73, totalSeats+"");
											if(!rankPreferenceMap.containsKey(rank))
											{
												int ptempSeatCount = 0;
												seatOfferedByHorizontal = true;
												ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												ptotalSeats = Integer.parseInt(pSeatList.get(73));
												if(ptempSeatCount > 0)
													ptotalSeats--;
												ptempSeatCount--;
												pSeatList.set(tempSeatCategory, ptempSeatCount+"");
												pSeatList.set(73,ptotalSeats+"");
											}
										}
										if(isAssamDomicile && Integer.parseInt(tSeatList.get(11)) > 0)
										{
											int tempSeatCount = 0;
											int tempSeatCategory = 0;
											tempSeatCategory = 11;
											tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
											totalSeats = Integer.parseInt(tSeatList.get(73));
											if(tempSeatCount > 0)
												totalSeats--;
											tempSeatCount--;
											tSeatList.set(tempSeatCategory, tempSeatCount+"");
											tSeatList.set(73, totalSeats+"");
											if(!rankPreferenceMap.containsKey(rank))
											{
												int ptempSeatCount = 0;
												seatOfferedByHorizontal = true;
												ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												ptotalSeats = Integer.parseInt(pSeatList.get(73));
												if(ptempSeatCount > 0)
													ptotalSeats--;
												ptempSeatCount--;
												pSeatList.set(tempSeatCategory, ptempSeatCount+"");
												pSeatList.set(73, totalSeats+"");
											}
										}		
									}		
										//----- if applicant gets seat through State Horizontal.-----//
									else if (seatCategory == 65 || seatCategory == 66 || seatCategory == 67 || seatCategory == 68 ||
												seatCategory == 69 || seatCategory == 70 || seatCategory == 71 || seatCategory == 72)
										{
											seatOfferedByHorizontal = true;
											recurrsionNeeded = true;
											totalSeats = Integer.parseInt(pSeatList.get(73));
											if(isFemale)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 65;
												tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												pSeatList.set(tempSeatCategory, tempSeatCount+"");
											}
											if(isPWD)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 66;
												tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												pSeatList.set(tempSeatCategory, tempSeatCount+"");
											}
											if(isDefencePersonnal)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 67;
												tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												pSeatList.set(tempSeatCategory, tempSeatCount+"");
											}
											if(isEminentSportsPersons)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 68;
												tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												pSeatList.set(tempSeatCategory, tempSeatCount+"");
											}
											if(isDFF)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 69;
												tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												pSeatList.set(tempSeatCategory, tempSeatCount+"");
											}
											if(isChildrenofExServiceman)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 72;
												tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												pSeatList.set(tempSeatCategory, tempSeatCount+"");
											}
											pSeatList.set(73, totalSeats+"");
										}
										else//----- if applicant gets seat through State Vertical.-----//
										{	

											seatCount -- ;
											tSeatList.set(seatCategory, seatCount+"");	
											totalSeats--;
											tSeatList.set(73, totalSeats+"");
											verticalSeats --;
											tSeatList.set(74, verticalSeats+"");
											if(apSeats > 0)
												apSeats -- ;
											tSeatList.set(75, apSeats+"");
											if(isFemale && Integer.parseInt(tSeatList.get(65)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 65;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, totalSeats+"");
												}
											}
											if(isPWD && Integer.parseInt(tSeatList.get(66)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 66;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, ptotalSeats+"");
												}
											}
											if(isDefencePersonnal && Integer.parseInt(tSeatList.get(67)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 67;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, ptotalSeats+"");
												}
											}
											if(isEminentSportsPersons && Integer.parseInt(tSeatList.get(68)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 68;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, ptotalSeats+"");
												}
											}
											if(isDFF && Integer.parseInt(tSeatList.get(69)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 69;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, ptotalSeats+"");
												}
											}
											if(isChildrenofArmedPersonnel && Integer.parseInt(tSeatList.get(70)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 70;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, ptotalSeats+"");
												}
											}
											if(isNCC && Integer.parseInt(tSeatList.get(71)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 71;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, ptotalSeats+"");
												}
											}
											if(isChildrenofExServiceman && Integer.parseInt(tSeatList.get(72)) > 0)
											{
												int tempSeatCount = 0;
												int tempSeatCategory = 0;
												tempSeatCategory = 72;
												tempSeatCount = Integer.parseInt(tSeatList.get(tempSeatCategory));
												totalSeats = Integer.parseInt(tSeatList.get(73));
												if(tempSeatCount > 0)
													totalSeats--;
												tempSeatCount--;
												tSeatList.set(tempSeatCategory, tempSeatCount+"");
												tSeatList.set(73, totalSeats+"");
												if(!rankPreferenceMap.containsKey(rank))
												{
													int ptempSeatCount = 0;
													seatOfferedByHorizontal = true;
													ptempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
													ptotalSeats = Integer.parseInt(pSeatList.get(73));
													if(ptempSeatCount > 0)
														ptotalSeats--;
													ptempSeatCount--;
													pSeatList.set(tempSeatCategory, ptempSeatCount+"");
													pSeatList.set(73, ptotalSeats+"");
												}
											}
										}
										
									//----- If allocation is through Horizontal category we will Update Seat detail under Vertical Category.-----//
									if(seatOfferedByHorizontal)
									{
										int tempSeatCount = 0;
										tempSeatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
										ptotalSeats = Integer.parseInt(pSeatList.get(73));
										pverticalSeats = Integer.parseInt(pSeatList.get(74));
										seatAllocatedFlag = false;
										if(seatCategory < 12)
										{
											if(tempSeatCount > 0)
											{
												tempSeatCount--;
												ptotalSeats--;
												pverticalSeats--;
												seatAllocatedFlag = true;
											}
											else if(verticalSeatCategory != 1)
											{
												verticalSeatCategory = 1;
												tempSeatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
												if(tempSeatCount > 0)
												{
													tempSeatCount--;
													ptotalSeats--;
													pverticalSeats--;
													seatAllocatedFlag = true;
												}
												else
													logger.error("CLATUGSeatAllocation ::: Error in getting Seat Offer Detail for Rank "+ rank);
											}
											else
												logger.error("CLATUGSeatAllocation ::: Error1 in getting Seat Offer Detail for Rank "+ rank);
										}
										else
										{
											int loopvar =1;
											do
											{
												loopvar--;
												tempSeatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
												if(tempSeatCount > 0)
												{
													tempSeatCount--;
													ptotalSeats--;
													pverticalSeats--;
													seatAllocatedFlag = true;
												}
												else
												{
													loopvar = 1;
													if(verticalSeatCategory > 5)
													{
														if(applicantCategory.equalsIgnoreCase("Other Backward Class(OBC)"))
															verticalSeatCategory = 2;
														else if(applicantCategory.equalsIgnoreCase("Scheduled Caste(SC)"))
															verticalSeatCategory = 3;
														else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (ST)"))
															verticalSeatCategory = 4;
														else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (Hills)"))
															verticalSeatCategory = 5;
														else
															verticalSeatCategory = 1;
													}
													else if(verticalSeatCategory > 1)
														verticalSeatCategory = 1;
													else
														loopvar = 0;
												}
											}
											while(loopvar > 0);
										}
										pSeatList.set(verticalSeatCategory, tempSeatCount+"");
										pSeatList.set(73, ptotalSeats+"");
										pSeatList.set(74, pverticalSeats+"");
									}
									
									if(seatAllocatedFlag)
									{
										System.out.println("Horizontal Allotment "+ seatOfferedByHorizontal);
										System.out.println("After Allocation TempList "+tSeatList);
										System.out.println("After Allocation PermList "+pSeatList);
										tUGSeatDetailsMap.put(preference, tSeatList);
										pUGSeatDetailsMap.put(preference, pSeatList);
										
										//---- we will store allocations details now. -----//
										updateMap.put(1, preference);
										updateMap.put(2, preferenceNo + "");
										updateMap.put(3, seatCategory + "");
										updateMap.put(4, verticalSeatCategory + "");
										if(seatOfferedByHorizontal)
											pUpdateValueMap.put(rank,updateMap);
										else
											tUpdateValueMap.put(rank,updateMap);
										
										//----- We will update seat Offer details in case this applicant has got upgraded preference.-----//
										try
										{
											if(status.equalsIgnoreCase("Upgrade"))
											{
												updateOfferDetail(rank, seatCategory, seatOfferedByHorizontal, isFemale, isPWD, isPunjabDomicile, isAncestralResidentofvillage,
														isGujaratDomicile, isAssamDomicile, isDefencePersonnal, isEminentSportsPersons, isDFF, isChildrenofArmedPersonnel,
														isNCC, isChildrenofExServiceman);
											}
										}
										catch(Exception e)
										{
											logger.error("CLATUGSeatAllocation ::: Error in Updating Seat Offer Detail for Rank "+ rank);
										}
										//----- If allocation is through Horizontal category we will need to call Allocation function again.-----//
										if(recurrsionNeeded)
										{
											logger.error("CLATUGSeatAllocation ::: recurrsion occurred because of rank "+ rank);
											tUGSeatDetailsMap.clear();
											for(String temp1:pUGSeatDetailsMap.keySet())
											{
												String tempUniversity = temp1;
												ArrayList<String> tempSeatList = new ArrayList<String>(pUGSeatDetailsMap.get(temp1));
												tUGSeatDetailsMap.put(tempUniversity, tempSeatList);
											}
											tUpdateValueMap.clear();
											deAllocationCategoryMap.clear();
											rankPreferenceMap.clear();
											logger.error("CLATUGSeatAllocation ::: Recurrsion due to this " + rank);
											seatAllocationMethod();
										}
										break PreferenceLoop;
									}
								}
							}
					}
				}
			}
			if(!recurrsionNeeded)
				throw new Exception();
		}

		//------Function To Allocate Applicants again.-----//
		public static void allocateDeAllocated(String preference, int preferenceNo,int catType, int rank, ArrayList <String> pSeatList, 
				ArrayList <String> tSeatList, HashMap<Integer, String> updateMap) throws Exception
		{
			int seatCount = 0;
			int totalSeats = Integer.parseInt(pSeatList.get(73));
			int verticalSeats = Integer.parseInt(pSeatList.get(74));
			int apSeats = Integer.parseInt(pSeatList.get(75));
			int verticalSeatCategory = 0;
			if(!pUpdateValueMap.containsKey(rank))
			{
				if(rank==6139)
					System.out.println("hi");
				domicileState = pApplicantsDataMap.get(rank).get("txtStateofDomicile") ;
				applicantStateCategory = pApplicantsDataMap.get(rank).get("StateCategory");
				applicantCategory = pApplicantsDataMap.get(rank).get("AllIndiaCategory");
				apRegion = pApplicantsDataMap.get(rank).get("txtAPRegion");
				seatAllocatedFlag = false;
				if(domicileState.equalsIgnoreCase(pSeatList.get(0)))
				{	
						if(apRegion.equalsIgnoreCase(""))
						{
							if(applicantStateCategory.equalsIgnoreCase("Unreserved"))
								verticalSeatCategory = 12;
							else if(applicantStateCategory.equalsIgnoreCase("OBC"))
								verticalSeatCategory = 13;
							else if(applicantStateCategory.equalsIgnoreCase("SC"))
								verticalSeatCategory = 14;
							else if(applicantStateCategory.equalsIgnoreCase("ST"))
								verticalSeatCategory = 15;
							else if(applicantStateCategory.equalsIgnoreCase("BC"))
								verticalSeatCategory = 16;
							else if(applicantStateCategory.equalsIgnoreCase("EBC"))
								verticalSeatCategory = 17;
							else if(applicantStateCategory.equalsIgnoreCase("WBC"))
								verticalSeatCategory = 18;
							else if(applicantStateCategory.equalsIgnoreCase("BC-Group (A)"))
								verticalSeatCategory = 19;
							else if(applicantStateCategory.equalsIgnoreCase("BC-Group (B)"))
								verticalSeatCategory = 20;
							else if(applicantStateCategory.equalsIgnoreCase("BC(M)"))
								verticalSeatCategory = 21;
							else if(applicantStateCategory.equalsIgnoreCase("MBC"))
								verticalSeatCategory = 22;
							else if(applicantStateCategory.equalsIgnoreCase("SC(A)"))
								verticalSeatCategory = 23;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-ETB"))
								verticalSeatCategory = 24;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-MU"))
								verticalSeatCategory = 25;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-OBH"))
								verticalSeatCategory = 26;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-LCAI"))
								verticalSeatCategory = 27;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-OBX"))
								verticalSeatCategory = 28;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-Kudumbi"))
								verticalSeatCategory = 29;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-Kusavan,Kulalan,Kulala Nair,Kumbharan,Velan,Oadan,Kulala,Andhra Nair,Aanthoor Nair"))
								verticalSeatCategory = 30;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-Dheevara"))
								verticalSeatCategory = 31;
							else if(applicantStateCategory.equalsIgnoreCase("SEBC-Vishwakarma"))
								verticalSeatCategory = 32;
							else if(applicantStateCategory.equalsIgnoreCase("None"))
								verticalSeatCategory = 12;
							else
							{
								logger.error("CLATUGSeatAllocation ::: Error20 in State Category of candidate with Rank " + rank);
							}
							
							seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
							if(seatCount > 0)
							{
								seatCount--;
								totalSeats--;
								verticalSeats--;
								seatAllocatedFlag = true;
							}
							else
							{
								if(applicantCategory.equalsIgnoreCase("Other Backward Class(OBC)"))
									verticalSeatCategory = 2;
								else if(applicantCategory.equalsIgnoreCase("Scheduled Caste(SC)"))
									verticalSeatCategory = 3;
								else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (ST)"))
									verticalSeatCategory = 4;
								else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (Hills)"))
									verticalSeatCategory = 5;
								else
									verticalSeatCategory = 1;
								seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
								if(seatCount > 0)
								{
									seatCount--;
									totalSeats--;
									verticalSeats--;
									seatAllocatedFlag = true;
								}
								else
								{
									verticalSeatCategory = 1;
									seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
									if(seatCount > 0)
									{
										seatCount--;
										totalSeats--;
										verticalSeats--;
										seatAllocatedFlag = true;
									}
									else
									{
										logger.error("CLATUGSeatAllocation ::: Error21 in State Category of candidate with Rank " + rank);
									}
								}
							}
							if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Women"))
							{
								int tempSeatCategory = 65;
								int tempSeatCount = 0;
								tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
								if(tempSeatCount > 0)
									totalSeats--;
								tempSeatCount--;
								pSeatList.set(tempSeatCategory, tempSeatCount+"");
							}
							if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("PWD"))
							{
								int tempSeatCategory = 66;
								int tempSeatCount = 0;
								tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
								if(tempSeatCount > 0)
									totalSeats--;
								tempSeatCount--;
								pSeatList.set(tempSeatCategory, tempSeatCount+"");
							}
							if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Defence Personnal"))
							{
								int tempSeatCategory = 67;
								int tempSeatCount = 0;
								tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
								if(tempSeatCount > 0)
									totalSeats--;
								tempSeatCount--;
								pSeatList.set(tempSeatCategory, tempSeatCount+"");
							}
							if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Eminent"))
							{
								int tempSeatCategory = 68;
								int tempSeatCount = 0;
								tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
								if(tempSeatCount > 0)
									totalSeats--;
								tempSeatCount--;
								pSeatList.set(tempSeatCategory, tempSeatCount+"");
							}
							if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("DFF"))
							{
								int tempSeatCategory = 69;
								int tempSeatCount = 0;
								tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
								if(tempSeatCount > 0)
									totalSeats--;
								tempSeatCount--;
								pSeatList.set(tempSeatCategory, tempSeatCount+"");
							}
							if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Service"))
							{
								int tempSeatCategory = 72;
								int tempSeatCount = 0;
								tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
								if(tempSeatCount > 0)
									totalSeats--;
								tempSeatCount--;
								pSeatList.set(tempSeatCategory, tempSeatCount+"");
							}
						}
						else
						{
							apSeats--;
							if(apRegion.contains("State"))
								verticalSeatCategory = 33;
							else if(apRegion.contains("AU"))
								verticalSeatCategory = 41;
							else if(apRegion.contains("OU"))
								verticalSeatCategory = 49;
							else if(apRegion.contains("SVU"))
								verticalSeatCategory = 57;
							
							if(applicantStateCategory.equalsIgnoreCase("BC-Group (A)"))
								verticalSeatCategory = verticalSeatCategory + 1;
							else if(applicantStateCategory.equalsIgnoreCase("BC-Group (B)"))
								verticalSeatCategory = verticalSeatCategory + 2;
							else if(applicantStateCategory.equalsIgnoreCase("BC-Group (C)"))
								verticalSeatCategory = verticalSeatCategory + 3;
							else if(applicantStateCategory.equalsIgnoreCase("BC-Group (D)"))
								verticalSeatCategory = verticalSeatCategory + 4;
							else if(applicantStateCategory.equalsIgnoreCase("BC-Group (E)"))
								verticalSeatCategory = verticalSeatCategory + 5;
							else if(applicantStateCategory.equalsIgnoreCase("SC"))
								verticalSeatCategory = verticalSeatCategory + 6;
							else if(applicantStateCategory.equalsIgnoreCase("ST"))
								verticalSeatCategory = verticalSeatCategory + 7;
							else
							{
								logger.error("CLATUGSeatAllocation ::: Error22 in State Category of AP Region candidate with Rank " + rank);
							}
							seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
							if(seatCount > 0)
							{
								seatCount--;
								totalSeats--;
								verticalSeats--;
								seatAllocatedFlag = true;
							}
							else if(verticalSeatCategory > 33 && verticalSeatCategory < 41)
							{
								verticalSeatCategory = 33;
								seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
								if(seatCount > 0)
								{
									seatCount--;
									totalSeats--;
									verticalSeats--;
									seatAllocatedFlag = true;
								}
								else
								{
									seatAllocatedFlag = false;
									logger.error("CLATUGSeatAllocation ::: Error23 in getting Seat Offer Detail for Rank "+ rank);
								}
							}
							else if(verticalSeatCategory > 41 && verticalSeatCategory < 49)
							{
								verticalSeatCategory = 41;
								seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
								if(seatCount > 0)
								{
									seatCount--;
									totalSeats--;
									verticalSeats--;
									seatAllocatedFlag = true;
								}
								else
								{
									seatAllocatedFlag = false;
									logger.error("CLATUGSeatAllocation ::: Error24 in getting Seat Offer Detail for Rank "+ rank);
								}
							}
							else if(verticalSeatCategory > 49 && verticalSeatCategory < 57)
							{
								verticalSeatCategory = 49;
								seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
								if(seatCount > 0)
								{
									seatCount--;
									totalSeats--;
									verticalSeats--;
									seatAllocatedFlag = true;
								}
								else
								{
									seatAllocatedFlag = false;
									logger.error("CLATUGSeatAllocation ::: Error25 in getting Seat Offer Detail for Rank "+ rank);
								}
							}
							else if(verticalSeatCategory > 57 && verticalSeatCategory < 65)
							{
								verticalSeatCategory = 57;
								seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
								if(seatCount > 0)
								{
									seatCount--;
									totalSeats--;
									verticalSeats--;
									seatAllocatedFlag = true;
								}
								else
								{
									seatAllocatedFlag = false;
									logger.error("CLATUGSeatAllocation ::: Error26 in getting Seat Offer Detail for Rank "+ rank);
								}
							}
							else
							{
								seatAllocatedFlag = false;
								logger.error("CLATUGSeatAllocation ::: Error27 in getting Seat Offer Detail for Rank "+ rank);
							}
						}	
					}	
					else
					{
						if(applicantCategory.equalsIgnoreCase("Other Backward Class(OBC)"))
							verticalSeatCategory = 2;
						else if(applicantCategory.equalsIgnoreCase("Scheduled Caste(SC)"))
							verticalSeatCategory = 3;
						else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (ST)"))
							verticalSeatCategory = 4;
						else if(applicantCategory.equalsIgnoreCase("Scheduled Tribe (Hills)"))
							verticalSeatCategory = 5;
						else
							verticalSeatCategory = 1;
						seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
						if(seatCount > 0)
						{
							seatCount--;
							totalSeats--;
							verticalSeats--;
							seatAllocatedFlag = true;
						}
						else
						{
							verticalSeatCategory = 1;
							seatCount = Integer.parseInt(pSeatList.get(verticalSeatCategory));
							if(seatCount > 0)
							{
								seatCount--;
								totalSeats--;
								verticalSeats--;
								seatAllocatedFlag = true;
							}
							else
							{
								logger.error("CLATUGSeatAllocation ::: Error28 in State Category of candidate with Rank " + rank);
							}
						}
						if(pApplicantsDataMap.get(rank).get("AllIndiaSubCategory").contains("Women"))
						{
							int tempSeatCategory = 6;
							int tempSeatCount = 0;
							tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
							if(tempSeatCount > 0)
								totalSeats--;
							tempSeatCount--;
							pSeatList.set(tempSeatCategory, tempSeatCount+"");
						}
						if(pApplicantsDataMap.get(rank).get("AllIndiaSubCategory").contains("PWD"))
						{
							int tempSeatCategory = 7;
							int tempSeatCount = 0;
							tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
							if(tempSeatCount > 0)
								totalSeats--;
							tempSeatCount--;
							pSeatList.set(tempSeatCategory, tempSeatCount+"");
						}
						if(domicileState.equalsIgnoreCase("Punjab") && tSeatList.get(0).equalsIgnoreCase("Punjab"))
						{
							int tempSeatCategory = 8;
							int tempSeatCount = 0;
							tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
							if(tempSeatCount > 0)
								totalSeats--;
							tempSeatCount--;
							pSeatList.set(tempSeatCategory, tempSeatCount+"");
						}
						if(pApplicantsDataMap.get(rank).get("StateSubCategory").contains("Ancestral Resident of village") && tSeatList.get(0).equalsIgnoreCase("Punjab"))
						{
							int tempSeatCategory = 9;
							int tempSeatCount = 0;
							tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
							if(tempSeatCount > 0)
								totalSeats--;
							tempSeatCount--;
							pSeatList.set(tempSeatCategory, tempSeatCount+"");
						}
						if(domicileState.equalsIgnoreCase("Gujarat") && tSeatList.get(0).equalsIgnoreCase("Gujarat"))
						{
							int tempSeatCategory = 10;
							int tempSeatCount = 0;
							tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
							if(tempSeatCount > 0)
								totalSeats--;
							tempSeatCount--;
							pSeatList.set(tempSeatCategory, tempSeatCount+"");
						}
						if(domicileState.equalsIgnoreCase("Assam") && tSeatList.get(0).equalsIgnoreCase("Assam"))
						{
							int tempSeatCategory = 11;
							int tempSeatCount = 0;
							tempSeatCount = Integer.parseInt(pSeatList.get(tempSeatCategory));
							if(tempSeatCount > 0)
								totalSeats--;
							tempSeatCount--;
							pSeatList.set(tempSeatCategory, tempSeatCount+"");
						}
					}
					
					if(seatAllocatedFlag)
					{
						pSeatList.set(verticalSeatCategory, seatCount +"");
						pSeatList.set(73, totalSeats +"");
						pSeatList.set(74, verticalSeats +"");	
						pSeatList.set(75, apSeats +"");	
						pUGSeatDetailsMap.put(preference, pSeatList);	
						
						//---- we will store allocations details now. -----//
						updateMap.put(1, preference);
						updateMap.put(2, preferenceNo+"");
						updateMap.put(3, catType + "");
						updateMap.put(4, verticalSeatCategory + "");
						pUpdateValueMap.put(rank,updateMap);
					}
				}
			
			logger.error("CLATUGSeatAllocation ::: recurrsion occurred because of rank "+ rank);
			tUGSeatDetailsMap.clear();
			for(String temp1:pUGSeatDetailsMap.keySet())
			{
				String tempUniversity = temp1;
				ArrayList<String> tempSeatList = new ArrayList<String>(pUGSeatDetailsMap.get(temp1));
				tUGSeatDetailsMap.put(tempUniversity, tempSeatList);
			}
			tUpdateValueMap.clear();
			deAllocationCategoryMap.clear();
			rankPreferenceMap.clear();
			logger.error("CLATUGSeatAllocation ::: Recurrsion due to this " + rank);
			
			try
			{
				
				seatAllocationMethod();
			}
			catch(Exception e)
			{
				logger.error("CLATUGSeatAllocation ::: Allocation Method Completed.");
				throw new Exception();
			}
		}
		
		//----- THis method is for updating seat released after preference upgradation.-----//
		public static void updateOfferDetail(Integer rank,int seatCategory, boolean offeredByHorizontalCategory, boolean isFemale, boolean isPWD, boolean isPunjabDomicile, 
				boolean isAncestralResidentofvillage, boolean isGujaratDomicile, boolean isAssamDomicile, boolean isDefencePersonnal, 
				boolean isEminentSportsPersons, boolean isDFF, boolean isChildrenofArmedPersonnel, boolean isNCC, boolean isChildrenofExServiceman)
		{
			int previousVerticalSeatCategory = 0;
			AllocatedVerticalSeatCategory:for(int i=1;i<4;i++)
			{
				if((roundNumber - i)>0 && !pApplicantsDataMap.get(rank).get("verticalSeatCategoryR" + (roundNumber - i)).equals(""))
				{
					previousVerticalSeatCategory = Integer.parseInt(pApplicantsDataMap.get(rank).get("verticalSeatCategoryR" + (roundNumber - i)));
					break AllocatedVerticalSeatCategory;
				}
			}
			
			int tempSeatCount = 0;
			int totalSeats = 0;
			int tempSeatCategory = 0;
			String allotedPreference = pApplicantsDataMap.get(rank).get("txtAllottedPreference");
			if(offeredByHorizontalCategory)
			{
				//----- Incrementing seat under previous vertical category in Permanent Map.------//
				tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(previousVerticalSeatCategory));
				tempSeatCount++;
				pUGSeatDetailsMap.get(allotedPreference).set(previousVerticalSeatCategory, tempSeatCount +"");
				
				totalSeats = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(73));
				totalSeats++;
				if(seatCategory < 12)
				{
					if(isFemale)
					{
						tempSeatCategory = 6;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isPWD)
					{
						tempSeatCategory = 7;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isPunjabDomicile)
					{
						tempSeatCategory = 8;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount--;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isAncestralResidentofvillage)
					{
						tempSeatCategory = 9;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount--;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isGujaratDomicile)
					{
						tempSeatCategory = 10;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount--;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isAssamDomicile)
					{
						tempSeatCategory = 11;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount--;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
				}
				else
				{
					if(isFemale)
					{
						tempSeatCategory = 65;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isPWD)
					{
						tempSeatCategory = 66;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isDefencePersonnal)
					{
						tempSeatCategory = 67;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isEminentSportsPersons)
					{
						tempSeatCategory = 68;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isDFF)
					{
						tempSeatCategory = 69;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isChildrenofArmedPersonnel)
					{
						tempSeatCategory = 70;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isNCC)
					{
						tempSeatCategory = 71;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isChildrenofExServiceman)
					{
						tempSeatCategory = 72;
						tempSeatCount = Integer.parseInt(pUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						pUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
				}
				pUGSeatDetailsMap.get(allotedPreference).set(73, totalSeats+"");
			}
			else
			{
				//----- Incrementing seat under previous vertical category in Temporary Map.------//
				tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(previousVerticalSeatCategory));
				tempSeatCount++;
				tUGSeatDetailsMap.get(allotedPreference).set(previousVerticalSeatCategory, tempSeatCount +"");
				
				totalSeats = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(73));
				totalSeats++;
				
				if(seatCategory < 12)
				{
					if(isFemale)
					{
						tempSeatCategory = 6;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isPWD)
					{
						tempSeatCategory = 7;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isPunjabDomicile)
					{
						tempSeatCategory = 8;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isAncestralResidentofvillage)
					{
						tempSeatCategory = 9;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isGujaratDomicile)
					{
						tempSeatCategory = 10;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
					if(isAssamDomicile)
					{
						tempSeatCategory = 11;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");

						totalSeats++;
					}
				}
				else
				{
					if(isFemale)
					{
						tempSeatCategory = 65;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isPWD)
					{
						tempSeatCategory = 66;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isDefencePersonnal)
					{
						tempSeatCategory = 67;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isEminentSportsPersons)
					{
						tempSeatCategory = 68;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isDFF)
					{
						tempSeatCategory = 69;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isChildrenofArmedPersonnel)
					{
						tempSeatCategory = 70;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isNCC)
					{
						tempSeatCategory = 71;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
					if(isChildrenofExServiceman)
					{
						tempSeatCategory = 72;
						tempSeatCount = Integer.parseInt(tUGSeatDetailsMap.get(allotedPreference).get(tempSeatCategory));
						tempSeatCount++;
						tUGSeatDetailsMap.get(allotedPreference).set(tempSeatCategory, tempSeatCount+"");
						
						totalSeats++;
					}
				}
				tUGSeatDetailsMap.get(allotedPreference).set(73, totalSeats+"");
			}	
		}
}
