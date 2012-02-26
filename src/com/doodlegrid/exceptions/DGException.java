package com.doodlegrid.exceptions;

public class DGException extends Exception {

	public DGException(Exception e) {
		super(e);
	}
	public DGException(String str) {
		super(str);
	}
}
