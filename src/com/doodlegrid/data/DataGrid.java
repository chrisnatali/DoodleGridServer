package com.doodlegrid.data;

public class DataGrid {
	
	private String[][] grid;
	private int gridSize;
	
	public static final int MAX_SIZE=20;
	
	public DataGrid(int size) {
		grid = new String[size][size];
		gridSize = size;
	}

	public void setGridItem(int row, int column, String color) {
		grid[row][column] = color;
	}
	
	public String getGridItem(int row, int column) {
		return grid[row][column];
	}
	
	public int getGridSize() {
		return gridSize;
	}
	
	public String gridToStr() {
		String str = "";
		for(int i = 0; i < getGridSize(); i++) {
			for(int j = 0; j < getGridSize(); j++) {

				str += getGridItem(i, j) + " ";
			}
			str += "\r\n";
		}
		return str;		
	}
}
