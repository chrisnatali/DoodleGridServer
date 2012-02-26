package com.doodlegrid.translation;

import org.w3c.dom.Document;
import com.doodlegrid.exceptions.BinderException;

//TODO:  Make this interface type-safe using generics
public interface Binder {
	
	public Document javaToDocument(Object obj) throws BinderException;
	
	public Object documentToJava(Document doc) throws BinderException; 

}
