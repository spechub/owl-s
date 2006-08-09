/*
 * Created on Apr 15, 2005
 */
package org.owlet.helpers;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class offers a few helpful methods for handling primitive datatypes in the Java Reflection API 
 * 
 * @author Michael Daenzer
 */
public class ReflectionHelpers {
	
    /**
     * Takes a Class Variable for primitive Datatype or its Wrapper-Classes and a String. 
     * Then an instance of the Class (for primitive Datatypes an instance of its Wrapper-Class) 
     * is created and the string value is assigned to the instance. 
     * 
	 * @param lexicalValue
     * @param type
	 * @return An instance of type with the assigned lexicalValue
	 */
	public static Object getCastedObjectFromStringValue(String lexicalValue, Class type) {
		String strClass = type.getName();
		Object retVal = null;

    	if ("String".equals(strClass) || "java.lang.String".equals(strClass))
    		return lexicalValue;
    	else if ("java.net.URL".equals(strClass)) {
    		try {
                return new URL(lexicalValue);
            }
            catch( MalformedURLException e ) {
                e.printStackTrace();
            }
    	}
    	else if ("short".equals(strClass) || "java.lang.Short".equals(strClass))
    		return new Short(lexicalValue);
    	else if ("byte".equals(strClass) || "java.lang.Byte".equals(strClass))
    		return new Byte(lexicalValue);
    	else if ("int".equals(strClass) || "java.lang.Integer".equals(strClass))
    		//return new Integer(lexicalValue);
    		retVal = new Integer(lexicalValue);
    	else if ("long".equals(strClass) || "java.lang.Long".equals(strClass))
    		return new Long(lexicalValue);
    	else if ("float".equals(strClass) || "java.lang.Float".equals(strClass))
    		return new Float(lexicalValue);
    	else if ("double".equals(strClass) || "java.lang.Double".equals(strClass))
    		return new Double(lexicalValue);
    	else if ("char".equals(strClass) || "java.lang.Character".equals(strClass))
    		return new Character(lexicalValue.charAt(0));
    	else if ("boolean".equals(strClass) || "java.lang.Boolean".equals(strClass))
    		return new Boolean(lexicalValue);
    	
    	// TODO dmi catch classes with a constructor with one string param
		return retVal;
	}

	/**
	 * Takes a String and returns a Class Object represented by the value of the String. This is
	 * intended for use with Class.getDeclaredMethods()
	 * Returns xx.TYPE Classes for primitive Datatypes
	 * 
	 * @return Class Object specified by strClass
	 * @see java.lang.Class#getDeclaredMethod(java.lang.String, java.lang.Class[])
	 */
    public static Class getClassFromString(String strClass) {
    	Class javaClass = null;
    	
    	// treat primitive datatypes
    	if ("short".equals(strClass))
    		return Short.TYPE;
    	else if ("byte".equals(strClass))
    		return Byte.TYPE;
    	else if ("int".equals(strClass))
    		return Integer.TYPE;
    	else if ("long".equals(strClass))
    		return Long.TYPE;
    	else if ("float".equals(strClass))
    		return Float.TYPE;
    	else if ("double".equals(strClass))
    		return Double.TYPE;
    	else if ("char".equals(strClass))
    		return Character.TYPE;
    	else if ("boolean".equals(strClass))
    		return Boolean.TYPE;
    	// treat real classes
		try {
			javaClass = Class.forName(strClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return javaClass;
    }
	
}
