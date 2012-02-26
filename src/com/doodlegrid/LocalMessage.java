package com.doodlegrid; 

public class LocalMessage {
 
     private static ThreadLocal message = new ThreadLocal();
     
     private LocalMessage() {}
     
     public static String getMessage() {
    	 return (String) message.get();
     }
     
     public static void setMessage(String message) {
    	 LocalMessage.message.set(message);
     }
 }