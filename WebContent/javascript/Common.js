//library of 'common' utility functions

var Common = {};

/**
 * Function to associate an object with an event
 * @param obj The object containing the method to associate with the event
 * @param methodName The method within the object to handle the event
 * Reference: look for this method here:  http://www.jibbering.com/faq/faq_notes/closures.html 
 */
Common.associateObjWithEvent = function(obj, methodName) {
	return (function(e) {
		e = e || window.event;
		return obj[methodName](e, this);
	});
}

