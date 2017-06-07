package com.tcs.EformsTesting;

import com.tcs.EForms.DBManager;
import com.tcs.exception.DBException;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionDetails {
	private static Log logger = LogFactory.getLog(ActionDetails.class);

	public ArrayList<String> getActionDetails(String actionId) {
		ArrayList<String> actionDetailList = new ArrayList<String>();
		String query = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int orgId = 1;
		String userId = "1";
		String appId = "9518";
		DBManager dbManager = null;
		Connection con=null;
		try {
		//	dbManager = DBManager.init();
		//	dbManager.doConnect(orgId, userId, appId);
			Class.forName("com.mysql.jdbc.Driver");
	        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eforms_global","root", "password");
	        ps = con.prepareStatement(query.toString());

			query = "select method_name,class_name,type_of_parameters from eformactivationusecasemapping where action_id=?";
		//	ps = dbManager.getPreparedStatement(query);
			ps = con.prepareStatement(query.toString());
			ps.setString(1, actionId);

			rs = ps.executeQuery();
			if (rs.next()) {
				actionDetailList.add(rs.getString(1) == null ? "" : rs.getString(1).trim());
				actionDetailList.add(rs.getString(2) == null ? "" : rs.getString(2).trim());
				actionDetailList.add(rs.getString(3) == null ? "" : rs.getString(2).trim());
			}
		}
		catch (SQLException e) 
		{
			actionDetailList = null;
			logger.error("SQLException ActionDetails :" + e);
		}

		

		catch (Exception e) {
			actionDetailList = null;
		}
		
		finally 
		{
			try {
			//	dbManager.doDisconnect();
				con.close();
			} catch (Exception e) {
				logger.error("Exception ActionDetails:: Closing DB Connection:" + e);
			}
			
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				logger.error("SQLException ActionDetails:: Closing rs or ps :" + e);
			}
		}
		return actionDetailList;
	}
}