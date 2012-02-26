package com.doodlegrid.translation;

import com.doodlegrid.exceptions.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.*;


import org.apache.xmlbeans.XmlException;
import java.io.*;
import java.net.URL;

public class XmlUtils {

    private XmlUtils() {}

    public static Document getDocumentForXmlObject(XmlObject obj) {
        XmlOptions opts = new XmlOptions();
        //opts.setSavePrettyPrint();
        opts.setSaveAggressiveNamespaces();
        return (Document) obj.newDomNode(opts);
    }

    public static Document getDocumentForString(String xml) throws XmlException {
        XmlObject obj = XmlObject.Factory.parse(xml);
        XmlOptions opts = new XmlOptions();
        //opts.setSavePrettyPrint();
        opts.setSaveAggressiveNamespaces();
        return (Document) obj.newDomNode(opts);
    }
    
    public static Document getDocumentForFile(File document) throws XmlException {
    	Document doc;
    	try {
	        FileInputStream fis = new FileInputStream(document);
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	        DocumentBuilder docBuilder = factory.newDocumentBuilder();
	        doc = docBuilder.parse(fis);
    	}
    	catch(Exception e) {
    		throw new XmlException(e);
    	}
        return doc;	
    }

    public static List validateDocument(XmlObject obj) throws InvalidXMLDocumentException {
        // Set up the validation error listener.
        List validationErrors = new ArrayList();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);

        boolean isValid = obj.validate(validationOptions);
        if (!isValid) {
            StringBuffer sb = new StringBuffer("XML Document is invalid: ");
            for (int i = 0; i < validationErrors.size(); i++) {
                XmlError item = (XmlError) validationErrors.get(i);
                sb.append(" (").append(i+1).append(") ").append(item.getMessage());
            }
            throw new InvalidXMLDocumentException(sb.toString());
        }

        return validationErrors;
    }

    public static String domToXMLString(Node doc) {
        return sourceToXMLString(new DOMSource(doc));
    }

    public static String sourceToXMLString(Source result) {

        String xmlResult = null;
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            StringWriter out = new StringWriter();
            StreamResult streamResult = new StreamResult(out);
            transformer.transform(result, streamResult);
            xmlResult = streamResult.getWriter().toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return xmlResult;
    }
    
    
}
