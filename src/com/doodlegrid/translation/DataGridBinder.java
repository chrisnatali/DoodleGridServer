package com.doodlegrid.translation;

import org.w3c.dom.Document;
import com.doodlegrid.data.DataGrid;
import com.doodlegrid.exceptions.*;
import com.doodlegrid.doodleGrid.schema.*;


public class DataGridBinder implements Binder {

	public Object documentToJava(Document doc) throws BinderException {
		DataGrid dGrid;
		if(doc == null)
			throw new BinderException("Document to be bound is null");
		try {
			DoodleGridDocument xDgd = DoodleGridDocument.Factory.parse(doc.getDocumentElement());
			Grid xGrid = xDgd.getDoodleGrid();
			int size = xGrid.getSize();
			if(size > DataGrid.MAX_SIZE)
				throw new Exception("Grid size too large to create");
			dGrid = new DataGrid(size);
			//now populate all the cells
			
			String gridAsStr = xGrid.getGridItems();
			
			String rows[] = gridAsStr.split("\\|");
			String columns[];
			for(int i = 0; i < size; i++) {
				
				if(i >= rows.length) 
					columns = new String[0];
				else 
					columns = rows[i].split(",");
				for(int j = 0; j < size; j++) {
					String color;
					if(j >= columns.length)
						color = "#FFFFFF"; //set unset colors to white
					else
						color = columns[j];
					dGrid.setGridItem(i, j, color);
				}
					
			}
		}
		catch(Exception e) {
			throw new BinderException(e);
		}
		return dGrid;
	}
	
	/**
	 * Create a new grid from a string of values
	 * @param values bar delimited row, comma delimited column set of values for the grid
	 * The size of the grid is equal to the number of rows in the string of values
	 */
	public DataGrid stringToDataGrid(String values) {
		String rows[] = values.split("\\|");
		int size = rows.length;
		DataGrid dGrid = new DataGrid(size);		
		String columns[];
		for(int i = 0; i < size; i++) {
			
			if(i >= rows.length) 
				columns = new String[0];
			else 
				columns = rows[i].split(",");
			if(columns.length > size)
				size = columns.length;
			for(int j = 0; j < size; j++) {
				String color;
				if(j >= columns.length)
					color = "#FFFFFF"; //set unset colors to white
				else
					color = columns[j];
				dGrid.setGridItem(i, j, color);
			}
				
		}		
		return dGrid;
	}

	public Document javaToDocument(Object obj) throws BinderException {
		DataGrid dGrid = (DataGrid) obj;
		DoodleGridDocument xDgd = DoodleGridDocument.Factory.newInstance();	
		String gridItems = "";
		try {			
			Grid xGrid = xDgd.addNewDoodleGrid();
			xGrid.setSize(dGrid.getGridSize());
			for(int i = 0; i < dGrid.getGridSize(); i++) {
				for(int j = 0; j < dGrid.getGridSize(); j++) {
					if(j == dGrid.getGridSize() - 1)
						gridItems += dGrid.getGridItem(i, j);
					else
						gridItems += dGrid.getGridItem(i, j) + ",";
				}
				if(i != dGrid.getGridSize() - 1)
					gridItems += "|"; 
			}
			xGrid.setGridItems(gridItems);
			
		}
		catch(Exception e) {
			throw new BinderException(e.getMessage());
		}
		return XmlUtils.getDocumentForXmlObject(xDgd);
	}
	
	//returns the values from the Grid as 
	//"|" delimited rows
	//"," delimited columns
	public String gridValuesToString(DataGrid dGrid) {
		String gridItems = "";		
		for(int i = 0; i < dGrid.getGridSize(); i++) {
			for(int j = 0; j < dGrid.getGridSize(); j++) {
				if(j == dGrid.getGridSize() - 1)
					gridItems += dGrid.getGridItem(i, j);
				else
					gridItems += dGrid.getGridItem(i, j) + ",";
			}
			if(i != dGrid.getGridSize() - 1)
				gridItems += "|"; 
		}
		return gridItems;
	}

}
