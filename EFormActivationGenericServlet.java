package com.tcs.EformsTesting;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcs.EformsTesting.ActionDetails;
import com.tcs.EformsTesting.EFormActivationException;

public class EFormActivationGenericServlet extends HttpServlet implements Servlet {
	static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(EFormActivationGenericServlet.class);

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		String getStr = null;
		String actionId = null;
		int argnum = 0;
		String methodName = null;
		String className = null;
		String parameterType = null;
		
		try {
				if ((request.getParameter("actionId") == null) || (request.getParameter("actionId").equals("")))
				{
					throw new EFormActivationException("Error:Action ID is null/empty.");
				}
			actionId = request.getParameter("actionId");
			ActionDetails ad = new ActionDetails();
			ArrayList<String> actionDetailList = ad.getActionDetails(actionId);
			
			if ((actionDetailList != null) && (!actionDetailList.isEmpty())) 
			{
				methodName = actionDetailList.get(0);
				className = actionDetailList.get(1);
				parameterType = actionDetailList.get(2);
			} 
			else 
			{
				throw new EFormActivationException("Error:SQL Exception occured in getting method/class/parameterType name corresponding to actionId.");
			}

			if ((request.getParameter("argnum").equals("")) || (request.getParameter("argnum") == null)) {
				throw new EFormActivationException("Error:Number of arguments is null/empty.");
			}
			else
				argnum = Integer.parseInt(request.getParameter("argnum"));

			
			Class[] parTypes = new Class[argnum];
			Object[] arglist = new Object[argnum];
			ArrayList<Class> parTypesL = new  ArrayList<Class>();
			ArrayList<Object> arglistL = new ArrayList<Object>();
			String temp = null;
			if(parameterType.equals("RequestfrmGenServ"))
			{
				try
		          {
						 Object[] args = new Object[argnum + 1];
				         Class[] params = new Class[argnum + 1];
				         ArrayList<Class> paramsL = new  ArrayList<Class>();
						ArrayList<Object> argsL = new ArrayList<Object>();
						for (int i = 0; i < argnum; i++) 
						{
							temp = request.getParameter("args" + (i + 1));
							params[i] = String.class;
							args[i] = temp;
							paramsL.add(String.class);
							argsL.add(temp);
							
						}
						params[argnum]=HttpServletRequest.class;
						args[argnum] = request;
						//parTypes=params;
						//arglist=args;
						paramsL.add(HttpServletRequest.class);
						argsL.add(request);
						parTypesL=paramsL;
						arglistL=argsL;
						parTypes=parTypesL.toArray(parTypes);
						arglist=arglistL.toArray(arglist);
		          }
				catch(Exception e)
				 {
					 logger.error("Exception in Eforms Generic Servlet for request code", e);
				 }
			}
			else if(parameterType.equals("RequestfrmGenServ,ResponsefrmGenServ"))
			{
				 try
		          {
						Object[] args = new Object[argnum + 2];
				        Class[] params = new Class[argnum + 2];
				        ArrayList<Class> paramsL = new  ArrayList<Class>();
						ArrayList<Object> argsL = new ArrayList<Object>();
						for (int i = 0; i < argnum; i++) 
						{
							temp = request.getParameter("args" + (i + 1));
							params[i] = String.class;
							args[i] = temp;
							paramsL.add(String.class);
							argsL.add(temp);
							
						}
						params[argnum]=HttpServletRequest.class;
						params[argnum+1]=HttpServletResponse.class;
						args[argnum] = request;				
						args[argnum+1] = response;
					//	parTypes=params;
					//	arglist=args;
						paramsL.add(HttpServletRequest.class);
						paramsL.add(HttpServletResponse.class);
						argsL.add(request);
						argsL.add(response);
						parTypesL=paramsL;
						arglistL=argsL;
						parTypes=parTypesL.toArray(parTypes);
						arglist=arglistL.toArray(arglist);
				}
				 catch(Exception e)
				 {
					 logger.error("Exception in Eforms Generic Servlet for request response code", e);
				 }
			}
			else
			{
				for (int i = 0; i < argnum; i++) 
				{
					temp = request.getParameter("args" + (i + 1));
					parTypes[i] = String.class;
					arglist[i] = temp;
					
				}
			}
			

			try {
				logger.error("Loading Class :: " + className);
				Class<?> applicationClass = Class.forName(className);
				Object object = applicationClass.newInstance();
				Method method = applicationClass.getMethod(methodName, parTypes);
				
				getStr = (String) method.invoke(object, arglist);
			}
			catch (ClassNotFoundException e) {
				logger.error("EFormActivationGenericServlet >> ClassNotFoundException::" + e);
				throw new EFormActivationException("Error:Class Not Found Exception.");
			} 
			catch (InstantiationException e) {
				logger.error("EFormActivationGenericServlet >> InstantiationException::" + e);
				throw new EFormActivationException("Error:Instantiation Exception.");
			} 
			catch (IllegalAccessException e) {
				logger.error("EFormActivationGenericServlet >> IllegalAccessException::" + e);
				throw new EFormActivationException("Error:Illegal Access Exception.");
			} 
			catch (NoSuchMethodException e) {
				logger.error("EFormActivationGenericServlet >> NoSuchMethodException::" + e);
				throw new EFormActivationException("Error:No Such Method Exception.");
			}
			catch (SecurityException e) {
				logger.error("EFormActivationGenericServlet >> SecurityException::" + e);
				throw new EFormActivationException("Error:Security Exception.");
			}
			catch (IllegalArgumentException e) {
				logger.error("EFormActivationGenericServlet >> IllegalArgumentException::" + e);
				throw new EFormActivationException("Error:Illegal Argument Exception.");
			} 
			catch (InvocationTargetException e) {
				logger.error("EFormActivationGenericServlet >> InvocationTargetException::" + e);
				throw new EFormActivationException("Error:Invocation Target Exception.");
			} 
			catch (Exception e) {
				logger.error("EFormActivationGenericServlet >> Exception::" + e);
				throw new EFormActivationException("Error:Please try again.");
			}

		}
		catch (EFormActivationException e)
		{
			logger.error("EFormActivationGenericServlet >> EFormActivationException::" + e);
			getStr = e.getMessage();
		}
		catch (Exception e)
		{
			logger.error("EFormActivationGenericServlet >> Exception::" + e);
			getStr = "Error:Please try again.";
			logger.error(getStr);
		} 
		pw.write(getStr);
		pw.close();
	}
}