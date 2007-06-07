package common.util;

import java.awt.Color;

/*
 * MekWars - Copyright (C) 2005 
 * 
 * Original author - nmorris (urgru@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

public final class StringUtils {
	
	public static String aOrAn(String s, boolean lowerCase) {
		return aOrAn(s, lowerCase, true);
	}
	
	/**
	 * Method which is used to determine whether "a" or "an"
	 * should be used in a string.
	 */
	public static String aOrAn(String s, boolean lowerCase, boolean returnString) {
		
		//get proper into ("A" or "An")
		String AorAn = "A ";
		String checkString = s.toLowerCase();
		if (checkString.toLowerCase().startsWith("a") ||
				checkString.startsWith("e") || 
				checkString.startsWith("i") || 
				checkString.startsWith("o") ||
				checkString.startsWith("u")) {
			AorAn = "An ";
		}
		
		if (lowerCase)
			AorAn = AorAn.toLowerCase();
		
		if (returnString)
			return AorAn + " " + s;
		//else
		return AorAn;
	}
	
	/**
	 * As above, but whether or not to pluraize based on a number.
	 */
	public static String addAnS(int i) {
		
		if (i > 1)
			return "s";
		//else
		return "";
	}
	
	/**
	 * Converts a html-color reference to a java.awt.Color. Will
	 * attempt to append a missing "#". If all else fails, will 
	 * return a light grey.
	 * 
	 * @param htmlColor color in format "#rrggbb"
	 */
	public static Color html2Color(String htmlColor) {
		try {
			return Color.decode(htmlColor);
		} catch (RuntimeException e) {
			try{
				return Color.decode("#"+htmlColor);
			} catch (RuntimeException ex) {
				return Color.lightGray;
			}
		}
	}
	
	/**
	 * Converts a java.awt.Color to a html-color 
	 * @return Color as String in format "#rrggbb"
	 */
	public static String color2html(Color color) {
		return "#" + int2hex(color.getRed()) +int2hex(color.getGreen()) +int2hex(color.getBlue());
	}
	
	/**
	 * Used by color2html
	 */
	private static String int2hex(int i) {
		String s = Integer.toHexString(i);
		return s.length() == 2 ? s : "0"+s;        
	}
	
	
}//end AorAnChecker class
