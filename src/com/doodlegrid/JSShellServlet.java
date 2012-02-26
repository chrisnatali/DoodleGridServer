package com.doodlegrid;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.contrib.js.shell.MainEx;

/**
 * Servlet implementation class for Servlet: JSShellServlet
 *
 */
 public class JSShellServlet extends DGServlet  {
	 
	static Logger logger = Logger.getLogger(JSShellServlet.class);
	
	static {
		MainEx.init(); //init the extended JS shell
	}
	
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public JSShellServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		logger.debug("Received request");
		// TODO Auto-generated method stub
		MainEx.addToScope("request", request);
		MainEx.addToScope("dga", getCurrentDGA());
		MainEx.setIn(request.getInputStream());
		PrintStream ps = new PrintStream(response.getOutputStream());
		MainEx.setOut(ps);
		MainEx.setErr(ps);
		MainEx.processSource();

	}   	  	    
}