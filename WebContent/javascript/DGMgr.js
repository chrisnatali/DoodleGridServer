/**
 * DGMgr.js: Utility functions for managing a DoodleGrid on the client
 *           Required Libraries/js files:  
 *           HTTP.js (HTTP functionality from David Flanagan)
 *           Common.js (utility functions)
 *           DG.js (DoodleGrid implementation)
 * TODO:
 * 0.  Add Validation
 *           
 */

var DGMgr = {};

DGMgr.dGrid = null;  //initialize the grid in init


/**
 * Initialize the DGMgr and the DoodleGrid
 * @param divElement The html div to render the DoodleGrid within
 */
DGMgr.init = function(divElement) {

	DGMgr.dGrid = new DoodleGrid(divElement);
	//TODO:  get the current DoodleGrid from the server
	DGMgr.dGrid.renderAll();
	DGMgr.listen();
}

/**
 * Turn debugging on/off
 * @param onOff boolean indicating whether debugging is on or off
 */
DGMgr.setDebug = function(onOff) {
	DGMgr.dGrid.setDebug(onOff);
}

/**
 * Add debugging info to Underlying DoodleGrid
 */
DGMgr.addDebug = function(debugInfo) {
	DGMgr.dGrid.addDebug(debugInfo);
}

/**
 * Start listening for updates from the server
 */
DGMgr.listen = function() {
	HTTP.getText("GridItemUpdateServlet", DGMgr.handleGridItemUpdate);
}

/**
 * Make call to get the current grid from the server
 */
DGMgr.getCurrentGrid = function() {
	HTTP.getText("GetGridServlet", DGMgr.handleGridUpdate);
}

/**
 * Handle a GridItem update from the server and then listen
 */
DGMgr.handleGridItemUpdate = function(responseText) {
	//keep listening...
	//this doesn't block so the rest of the procesing
	//will continue while we wait for more updates
	DGMgr.addDebug("received update: " + responseText);
	HTTP._inHandler = 1;
	DGMgr.listen();
	var valArray = responseText.split(",");
	//if response does not have 3 elements, don't bother with it
	if(valArray.length == 3) {
		var x = valArray[0];
		var y = valArray[1];
		var value = valArray[2];
		DGMgr.processGridItemUpdate(x, y, value);
	}	
	HTTP._inHandler = 0;
	DGMgr.addDebug("HTTP._inHandler: " + HTTP._inHandler);	
}


/**
 * Handle a Grid update from the server and render it
 */
DGMgr.handleGridUpdate = function(responseText) {
	DGMgr.addDebug("Grid Received: " + responseText);
	DGMgr.processGridUpdate(responseText);
	DGMgr.addDebug("GridUpdate Processed");
	DGMgr.dGrid.renderAll();
	DGMgr.addDebug("GridUpdate Rendered");
}

/**
 * Process GridItem updates from the server (i.e. update the DoodleGrid)
 * @param x the x coord of the gridItem to update
 * @param y the y coord of the gridItem to update
 * @param value the updated value of the gridItem
 */
DGMgr.processGridItemUpdate = function(x, y, value) {
	//check if x and y are even in the grid, if not ignore
	DGMgr.addDebug("GridUpdate x: " + x + " y " + y + " value " + value);	
	if(x < DGMgr.dGrid.gridSize && y < DGMgr.dGrid.gridSize) {
		DGMgr.dGrid.grid[x][y].color = value;
		DGMgr.dGrid.render(x, y);
		DGMgr.addDebug("GridItem Rendered x: " + x + " y " + y + " value " + value);			
	}
}

/**
 * Update the entire grid
 * @param values bar delimited row, comma delimited column set of values for the grid
 * The size of the grid is max(numRows,numCols)
 */
DGMgr.processGridUpdate = function(values) {
	var rows = values.split("\\|");
	var size = rows.length;
	
	var columns;
	for(var i = 0; i < size; i++) {
		
		if(i >= rows.length) 
			columns = new Array[0];
		else 
			columns = rows[i].split(",");
		if(columns.length > size)
			size = columns.length;
		for(var j = 0; j < size; j++) {
			var color;
			if(j >= columns.length)
				color = "#FFFFFF"; //set unset colors to white
			else
				color = columns[j];
			DGMgr.dGrid.grid[i][j].color = color;
		}
			
	}		
}

/**
 * Submit GridItem updates to the server 
 * (i.e. update the ServerSide DoodleGrid and trigger all listening
 * clients to be updated)
 * @param x the x coord of the gridItem to update
 * @param y the y coord of the gridItem to update
 * @param value the updated value of the gridItem
 */
DGMgr.submitGridItemUpdate = function(x, y, value) {
	var submitValue = x + "," + y + "," + value;
	var values = [];
	values["gridItem"] = submitValue;
	HTTP.post("GridItemUpdateServlet", values, DGMgr.handleSubmitGridItemResponse);
}

/**
 * Handle submitGridItemUpdate response 
 * Do Nothing for now since transactional integrity is NOT a concern here
 */
DGMgr.handleSubmitGridItemResponse = function(responseText) {
	//do nothing
	return;
}	
