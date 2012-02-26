package com.doodlegrid;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doodlegrid.app.DoodleGridApplication;

/**
 * Servlet implementation class for Servlet: DGServlet
 *
 */
 public class DGServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public DGServlet() {
		super();
	}   	
	
	/**
	 * Get the DoodleGridApplication object from the ServletContext
	 * @return
	 */
	protected DoodleGridApplication getCurrentDGA() {
		DoodleGridApplication dga = (DoodleGridApplication) getServletContext().getAttribute(DoodleGridApplication.DGA_ATTRIBUTE);
		return dga;
	}	
}