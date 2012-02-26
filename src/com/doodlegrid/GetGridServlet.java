package com.doodlegrid;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.doodlegrid.app.DoodleGridApplication;
import com.doodlegrid.exceptions.DGException;

/**
 * Servlet implementation class for Servlet: GetDoodleGrid
 *
 */
 public class GetGridServlet extends DGServlet {
	 
	static Logger logger = Logger.getLogger(GetGridServlet.class);
	 
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO:  Update this to only queue up the requests and respond to all when the DoodleGrid is updated
		logger.debug("Request for DoodleGrid");
		response.setContentType("text");

		PrintWriter writer = response.getWriter();
		try {
			String dgStr = getCurrentDGA().getDataGridStr();
			logger.debug("Response DataGrid: " + dgStr);
			writer.print(dgStr);
		}
		catch(DGException e) {
			logger.error("Could not get current DoodleGrid: " + e);
		}
		writer.close();		
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}   	  	    
}