package com.doodlegrid.exceptions;

public class BinderException extends DGException {

	public BinderException(Exception e) {
		super(e);
	}
	public BinderException(String str) {
		super(str);
	}	
}
