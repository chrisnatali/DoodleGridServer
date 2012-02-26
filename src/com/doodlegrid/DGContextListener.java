package com.doodlegrid;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.doodlegrid.app.DoodleGridApplication;
import com.doodlegrid.exceptions.DGException;

public class DGContextListener implements javax.servlet.ServletContextListener {

	static Logger logger = Logger.getLogger(DGContextListener.class);

	public void contextInitialized(ServletContextEvent evt) {
		// init the dataGrid
		logger.debug("Initializing ServletContext, Event: " + evt);		
		ServletContext context = evt.getServletContext();
		if(context != null) {
			DoodleGridApplication dga = new DoodleGridApplication();
			try {
				dga.loadDefaultDataGrid();
			} catch (DGException e) {
				logger.fatal("Fatal Exception upon initializing DoodleGrid application: " + e);
			}
			context.setAttribute(DoodleGridApplication.DGA_ATTRIBUTE, dga);

			logger.info("Doodle Grid App is initialized....");
		}
		else {
			logger.debug("contextInitialized called and ServletContext is null");
		}
		logger.debug("ServletContextEvent: " + evt);
			
	}

	public void contextDestroyed(ServletContextEvent evt) {
		logger.info("Doodle Grid App is destroyed....");
	}
}