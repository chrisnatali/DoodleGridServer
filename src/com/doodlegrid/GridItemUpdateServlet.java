package com.doodlegrid;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.doodlegrid.exceptions.DGException;

/**
 * Servlet implementation class for Servlet: GetGridUpdateSubscriberServlet
 * This servlet works in conjunction with the PostGridUpdateServlet and 
 * the DoodleGridApplication object to implement a simple 'COMET' ajax pattern
 * for real-time Doodling  
 * 
 * Client Requirements:
 * 1.  Perform a get to subscribe
 * 2.  Re-perform the get on either timeout or grid update
 *
 */
 public class GridItemUpdateServlet extends DGServlet {
	 
	static Logger logger = Logger.getLogger(GridItemUpdateServlet.class);
	
	//create a threadLocal message that will be used 
	//as the waiting object for each invocation of doGet
	//the message will be updated for each client gridItem update
	//and used to respond to the listener
	
	
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GridItemUpdateServlet() {
		super();		
	}   	
	
	/* (non-Java-doc)
	 * add the response to the list of listeners
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Adding new listener");
		try {
			//Send a LocalMessage to the DGA to be updated
			//when an update comes in
			//LocalMessage.setMessage("");
			Object message = new Object();
			//getCurrentDGA().addUpdateListener(LocalMessage.getMessage());
			getCurrentDGA().addUpdateListener(message);
			
			//now wait
			logger.debug("Listener waiting");
			
			//LocalMessage.getMessage()

			//LocalMessage.getMessage().wait();

			synchronized(message) {
				logger.debug("waiting on message");
				message.wait();
			}		
			//logger.debug("Listener being notified with: " + LocalMessage.getMessage());
			logger.debug("Listener being notified with: " + getCurrentDGA().getUpdatedGridItem());
			
			//disable caching on the client
			//so that request actually makes it to this server
			//each time (i.e. bug with XMLHTTPRequest, see wikipedia for XMLHttpRequest)
			response.setHeader( "Pragma", "no-cache" );
			response.addHeader( "Cache-Control", "must-revalidate" );
			response.addHeader( "Cache-Control", "no-cache" );
			response.addHeader( "Cache-Control", "no-store" );
			response.setDateHeader("Expires", 0);			
			
			//once this is notified, send out the current gridItem
			response.setContentType("text");
		
			PrintWriter writer = response.getWriter();
			//writer.print(LocalMessage.getMessage());
			writer.print(getCurrentDGA().getUpdatedGridItem());
			writer.close();				
		
		}
		catch(Exception e) {
			logger.warn("Exception occurred while adding listener: " + e);
			response.setStatus(500);
		}
		finally {
			logger.debug("Successfully updated listener");
		}
	}  	  	  	  
	
	/* Attempt to Update the DoodleGrid and all listeners
	 * Request should have an attribute named gridItem with a value in "x,y,color" format where color is a string of form "#XXXXXX" where X is 0-F hex
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get the gridItem string
		String gridItem = request.getParameter("gridItem");
		logger.debug("Updating Grid with: " + gridItem);
		if(gridItem == null)
			logger.warn("Update submitted without gridItem parameter");
		else {
			try {
				getCurrentDGA().updateGridItem(gridItem);
			}
			catch(DGException e) {
				logger.warn("Exception occurred while updating DoodleGrid: " + e);
			}
		}
			
	}   	
}