package com.doodlegrid.app;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.doodlegrid.data.DataGrid;
import com.doodlegrid.exceptions.DGException;
import com.doodlegrid.translation.DataGridBinder;
import com.doodlegrid.translation.XmlUtils;

//The application instance
//this has a single DataGrid
//TODO:  Decide whether we need a DataGridManager class instead
//TODO:  Add methods for updating DataGrid...when called these will signal GridUpdateListeners
public class DoodleGridApplication {
	
	private static Logger logger = Logger.getLogger(DoodleGridApplication.class);
	private static DataGrid dg;
	private static DataGridBinder dgb = new DataGridBinder();
	private static final String DEFAULT_DATA_GRID_FILE = "/default_grid.xml";
	public static final String DGA_ATTRIBUTE = "DGA";	//used for retrieving the DoodleGrid app from a hash
	
	//Array of listening waiting Objects to be notified upon update..
	private Object listeners[];
	private int numListeners = 0; //used to keep track of how many in 'queue'
	
	//TODO:  Make this configurable
	private int maxListeners = 10;
	private static String updatedGridItem = "";
	
	public DoodleGridApplication() {

	}
	
	/* Load the default DataGrid */
	//TODO:  Update this to load the simpler comma delim'd string rep of DataGrid
	public void loadDefaultDataGrid() throws DGException {
		logger.debug("Loading default DataGrid");
		try {

			//get the default_grid file from the top-level classpath
			
			URL gridUrl = DoodleGridApplication.class.getResource(DEFAULT_DATA_GRID_FILE);		
			File defaultGridFile = new File(gridUrl.getFile());
			
			Document doc = XmlUtils.getDocumentForFile(defaultGridFile);
			dg = (DataGrid) dgb.documentToJava(doc);
		}
		catch(Exception e) {
			throw new DGException(e);
		}
		//setup the listener array
		listeners = new Object[maxListeners];
		numListeners = 0;
		
		logger.info("Loaded default DataGrid, size: " + dg.getGridSize());
		logger.debug("DataGrid details: " + dg.gridToStr());	
	}
	
	/**
	 * Add a listener to be updated upon receiving a GridItemUpdate
	 * @param message a String passed in from the Servlet that will be updated
	 * with a gridItem update upon update from another client
	 */
	public void addUpdateListener(Object message) throws DGException {
		if(!isDataGridLoaded())
			throw new DGException("Data Grid has not been loaded");
		if(numListeners >= maxListeners)
			throw new DGException("Too many listeners");
		listeners[numListeners] = message;
		numListeners++;

	}
	
	/**
	 * Updates the DGA Grid and sends out the update to all listeners
	 * @param gridItem String representation of gridItem update ("x,y,value" where x is x coord, y is y coord and value is the 6 digit hex color)
	 * TODO:  Figure this out!: This method is synchronized as only ONE update and subsequent notification
	 * should be happening at a time
	 */
	public void updateGridItem(String gridItem) throws DGException {
		if(!isDataGridLoaded())
			throw new DGException("Data Grid has not been loaded");		
		String[] values = gridItem.split(",");
		if(values.length < 3) {
			throw new DGException("Invalid GridItem update string: " + gridItem);
		}
		try {
			int x = Integer.parseInt(values[0]);
			int y = Integer.parseInt(values[1]);
			String color = values[2];
			//TODO:  Add Validation that ints are within grid and color is of correct format (via RegEx)
			dg.setGridItem(x, y, color);
		}
		catch(Exception e) {
			throw new DGException(e);
		}
		
		//respond with update to all listeners
		sendOutUpdate(gridItem);
		
	}
	
	public String getUpdatedGridItem() {
		return updatedGridItem;
	}
	
	private void sendOutUpdate(String gridItem) throws DGException {
		String exceptionStr = ""; //tally up any exceptions in here so that we at least get through attempting to update ALL listeners
		updatedGridItem = gridItem;		
		for(int i = 0; i < numListeners; i++) {
			Object message = listeners[i];
			if(message != null) {
				synchronized(message) {			
					message.notify();
				}
			}
			else {
				logger.debug("Message " + i + " was null");
				exceptionStr += " Response " + i + " was null.  Look for corresponding Interrupted wait exception.";
			}
		}
		
		numListeners = 0; //reset all listeners
		if(!exceptionStr.equals("")) {
			throw new DGException("Exception occurred while updating listeners: " + exceptionStr);
		}
	}
	
	public boolean isDataGridLoaded() {
		return (dg != null);
	}
	
	public String getDataGridXmlStr() throws DGException {
		if(isDataGridLoaded()) {
			Document doc = dgb.javaToDocument(dg);
			return XmlUtils.domToXMLString(doc);
		}
		else 
			throw new DGException("DataGrid has not been loaded.");
	}
	
	public String getDataGridStr() throws DGException {
		if(isDataGridLoaded()) {
			String gridValues = dgb.gridValuesToString(dg);
			return gridValues;
		}
		else 
			throw new DGException("DataGrid has not been loaded.");
	}

	public Object[] getListeners() {
		return listeners;
	}

	public void setListeners(Object[] listeners) {
		this.listeners = listeners;
	}

	public int getMaxListeners() {
		return maxListeners;
	}

	public void setMaxListeners(int maxListeners) {
		this.maxListeners = maxListeners;
	}

	public int getNumListeners() {
		return numListeners;
	}

	public void setNumListeners(int numListeners) {
		this.numListeners = numListeners;
	}	

}
