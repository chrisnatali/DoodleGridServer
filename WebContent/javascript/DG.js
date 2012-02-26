//TODO: 
//0.  Update the documentation for this (i.e. how it can be used with/without DGMgr, debugging, etc)
//1.  Test for incompatibilities cross browsers...see 'incompat' comments
//2.  Ensure that all event handlers are detached upon unload to avoid leaks...revisit event attachment model
//3.  Ensure that the containing div width calc as unit agnostic.  Currently it assumes pixels
//4.  Add alt key to certain commands

//object representing a grid
//of values that can be rendered/scaled
//within a square view based on its width
function DoodleGrid(divElement) {


	this.currentScale = 4;
	this.gridSize = 10;
	this.divElement = divElement;
	this.drawMode = 0;
	this.drawColor = "#000000";
 	this.greyScaleArray = ["#000000", "#111111", "#333333", "#555555", "#777777", "#999999", "#BBBBBB", "#DDDDDD", "#EEEEEE", "#FFFFFF"];	
	this.debug = false;
	
	//init the grid
	this.grid = new Array(this.gridSize);
	for(var i = 0; i < this.gridSize; i++) {
		this.grid[i] = new Array(this.gridSize);
	}
	
	
	//catch keypress events
	document.onkeypress = Common.associateObjWithEvent(this, "doKeyPress");
		
	//populate the grid with the divs
	for(var j = 0; j < this.gridSize; j++) {
		for(k = 0; k < this.gridSize; k++) {
			//compat
			var gridItem = document.createElement("div");
			gridItem.setAttribute("gridRow", j);
			gridItem.setAttribute("gridCol", k);
			gridItem.color = "#FFFFFF";  //eventually this should be set via the server
			gridItem.style.position = "absolute";
			gridItem.onmouseover = Common.associateObjWithEvent(this, "doMouseOver");
			this.divElement.appendChild(gridItem);
			this.grid[j][k] = gridItem; //keep this element around so we can relay to server and cleanup later
		}
	}

}

DoodleGrid.prototype.gridToStr = function() {
	var str = "";
	for(var i = 0; i < this.gridSize; i++) {
		for(var j = 0; j < this.gridSize; j++) {
			var hv = "h";
			if(this.withinView(i, j)) {
				hv = "v";
			}
			str += this.grid[i][j].color + hv + " ";
		}
		str += "<br>";
	}
	return str;
}

DoodleGrid.prototype.gridAsStr = function() {
	var str = "Grid scale: " + this.currentScale + " color: " + this.drawColor + " drawMode: " + this.drawMode + " gridSize: " + this.grid.length;
	return str;
}

DoodleGrid.prototype.setDebug = function(debug) {	
	this.debug = debug;
}

DoodleGrid.prototype.addDebug = function(strOutput) {
	if(this.debug) {
		if(!this.debugDiv) {
			this.debugDiv = document.createElement("div");
			this.debugDiv.style.position = "absolute";
			this.debugDiv.style.top = "0px";
			this.debugDiv.style.left = this.getWidth() + 20 + "px";
			this.debugDiv.style.width = "400px";
			this.debugDiv.style.visibility = "visible";
			this.divElement.appendChild(this.debugDiv);
		}
		this.debugDiv.innerHTML += strOutput + "<br>";
	}
}

DoodleGrid.prototype.clearDebug = function() {
	if(this.debugDiv) {
		this.debugDiv.innerHTML = "";
	}
}

DoodleGrid.prototype.getWidth = function() {
	var w = parseInt(this.divElement.style.width);
	var h = parseInt(this.divElement.style.height);
	return (w > h) ? w : h;	
}

DoodleGrid.prototype.startIndex = function() {
	return this.currentScale - 1;
}

DoodleGrid.prototype.endIndex = function() {
	return this.gridSize - this.currentScale;
}

DoodleGrid.prototype.withinView = function(row, col) {
	return (row >= this.startIndex() && col >= this.startIndex() && row <= this.endIndex() && col <= this.endIndex());
}

DoodleGrid.prototype.maxZoom = function() {
	//doesn't make sense to zoom in any more than 	
	//half the grid
	return this.gridSize / 2;  
}

DoodleGrid.prototype.minZoom = function() {
	return 1;  
}


DoodleGrid.prototype.zoomIn = function() {
	//don't let user zoom in past 
	if(this.currentScale < this.maxZoom()) {
		this.currentScale += 1;
		this.addDebug("Current Scale: " + this.currentScale);
	}
}

DoodleGrid.prototype.zoomOut = function() {
	if(this.currentScale > this.minZoom()) {
		this.currentScale -= 1;
		this.addDebug("Current Scale: " + this.currentScale);		
	}
}

DoodleGrid.prototype.renderWidth = function() {	
	var rW = (this.getWidth() / (this.gridSize - ((this.currentScale - 1) * 2)));
	//this.addDebug("Rend Width: " + rW);
	return rW;
}

DoodleGrid.prototype.getTopLeft = function(index) {
	return ((index - this.startIndex()) * this.renderWidth());
}

DoodleGrid.prototype.renderAll = function() {
	for(var i = 0; i < this.gridSize; i++) {
		for(var j = 0; j < this.gridSize; j++) {
			this.render(i, j);					
		}
	}
}

DoodleGrid.prototype.render = function(row, col) {
	var gridItem = this.grid[row][col];
	if(!this.withinView(row, col)) {
		gridItem.style.visibility = "hidden";
		//this.addDebug("Row: " + row + " Col: " + col + " Hidden");		
	}
	else {
		var color = gridItem.color;	
		//this.addDebug("Color: " + color);			
		gridItem.style.top = this.getTopLeft(row) + "px";
		//this.addDebug("Top: " + gridItem.style.top);		
		gridItem.style.left = this.getTopLeft(col) + "px";
		//this.addDebug("left: " + gridItem.style.left);		
		gridItem.style.width = this.renderWidth() + "px";
		//this.addDebug("width: " + gridItem.style.width);				
		gridItem.style.height = this.renderWidth() + "px";	
		//this.addDebug("height: " + gridItem.style.height);				
		gridItem.style.border = "solid #AAAAAA 1px";
		//this.addDebug("border: " + gridItem.style.border);				
		gridItem.style.backgroundColor = color;
		//this.addDebug("backgroundColor: " + gridItem.style.backgroundColor);				
		gridItem.style.visibility = "visible";	
		//this.addDebug("visibility: " + gridItem.style.visibility);				

	}
}

DoodleGrid.prototype.printGridItemStyle = function(gridItem) {
	var str = "";
	var style = gridItem.style;
	str += "top:" + style.top + ";";
	str += "left:" + style.left + ";";
	str += "width:" + style.width + ";";
	str += "height:" + style.height + ";";
	str += "border:" + style.border + ";";
	str += "backgroundColor:" + style.backgroundColor + ";";	
	str += "visibility:" + style.visibility + ";";	
	return str;					
}

//invoked when a gridItem has been moused over
DoodleGrid.prototype.doMouseOver = function(event, element) {
	//this.addDebug("doMouseOver");
	if(this.drawMode) {
		element.style.backgroundColor = this.drawColor;
		element.color = this.drawColor;
		
		//if there's a DGMgr, call its submitGridItemUpdate method with x, y, color
		if(DGMgr != null) {
			var x = element.getAttribute("gridRow");
			var y = element.getAttribute("gridCol");
			DGMgr.submitGridItemUpdate(x, y, element.color);
		}
		this.addDebug("Changed element drawColor to: " + this.drawColor);
	}
}	

//invoked when a key pressed
DoodleGrid.prototype.doKeyPress = function(event, element) {
	  var keyCode = event.keyCode ? event.keyCode : event.charCode; 
	  var charForCode = String.fromCharCode(keyCode);
	  //this.addDebug("Char entered: " + charForCode);
	  if(charForCode == "d") {
		  this.drawMode = !this.drawMode;
		  this.addDebug("DrawMode: " + this.drawMode);
	  }
	  else if(charForCode == "c") {
	  	this.clearDebug();
	  }
	  else if(charForCode == "<") {
	  	//zoom in and redraw
	  	this.zoomIn();
	  	this.renderAll();
	  }
	  else if(charForCode == ">") {
	  	this.zoomOut();
	  	this.renderAll();
	  }
	  else {
		  //alert("Key Code: " + keyCode);
	  	if(this.drawMode && this.isNumericKeyCode(keyCode)) {
			//alert("setting draw color, current color: " + gDrawColor);
			var drawColorCode = this.getNumberFromKeyCode(keyCode);
			this.drawColor = this.greyScaleArray[drawColorCode];
			//alert("newdraw color: " + gDrawColor);
		 }
	  }
	  return false;
}	

DoodleGrid.prototype.isNumericKeyCode = function(keyCode) {
	return (keyCode > 47 && keyCode < 58);
}

DoodleGrid.prototype.getNumberFromKeyCode = function(keyCode) {
	return keyCode - 48;
}