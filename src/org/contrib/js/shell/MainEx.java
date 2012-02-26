package org.contrib.js.shell;

import java.util.HashMap;
import java.util.Iterator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.shell.Main;
import org.mozilla.javascript.tools.shell.QuitAction;


//extends shell.Main so that it can be invoked for
//"one-off" script processing more easily...
//i.e. from a Servlet's doGet or doPost method...where
//we can call setIn, setOut to the appropriate streams
public class MainEx extends Main {
	
    /**
     * Proxy class to avoid proliferation of anonymous classes.
     */
    private static class IProxyEx implements ContextAction, QuitAction
    {


        public Object run(Context cx)
        {
        	processSource(cx, "-");
            return null;
        }

        public void quit(Context cx, int exitCode)
        {
            System.exit(exitCode);
            return;
        }
    }

    /**
     *  initialize the shell with the context, but do NOT
     *  start processing input

     */
    public static void init()
    {
        errorReporter = new ToolErrorReporter(false, global.getErr());
        shellContextFactory.setErrorReporter(errorReporter);

        
        global.init(shellContextFactory);
      
    }
    
    /**
     *  Adds objects to the global scope for when script is executed
     *  @param scopeObjects a Hash of key strings to Object.  The keys are 
     *  the variable names associated with the Objects to be exposed by 
     *  the script engine 
     */
    public static void addToScope(HashMap scopeObjects) {
        if(scopeObjects != null) {
        	Iterator keys = scopeObjects.keySet().iterator();
        	while(keys.hasNext()) {
        		String key = (String) keys.next();
        		global.put(key, global, scopeObjects.get(key));
        	}
        }     	
    }
    
    /**
     *  Adds a single object to the global scope for when script is executed
     *  @param varName the name of the variable (as it will be exposed to script) associated with the object
     *  @param scopeObject the object to be exposed to the script
     */
    public static void addToScope(String varName, Object scopeObject) {
    	global.put(varName, global, scopeObject);
    }    
    
    /**
     * process the current stdin and send result to stdout given the context set in init()
     */
    public static void processSource() {
        IProxyEx iproxy = new IProxyEx();
        
        shellContextFactory.call(iproxy);
    }
    
    //test this out
	public static void main(String[] args) {
		
		HashMap test = new HashMap();
		test.put("name", "chris");
		test.put("occupation", "theatrical production");
		
		HashMap scopeObjects = new HashMap();
		scopeObjects.put("test", test);
		MainEx.init();
		MainEx.addToScope(scopeObjects);
		Main.setIn(System.in);
		Main.setOut(System.out);
		MainEx.processSource();

	}    
}
