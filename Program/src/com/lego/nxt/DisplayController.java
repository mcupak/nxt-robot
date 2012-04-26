package com.lego.nxt;

import lejos.nxt.LCD;

public class DisplayController {

	private static final int LINE_LENGTH=16;
	private static final int LINE_NO=8;
	
	/**
	 * Prints a short message on the screen.
	 * 
	 * @param s
	 *            message
	 */
	public static void print(String s) {
		LCD.clear();
		int i=0;
		int j=0;
		while (i<s.length()&&j<LINE_NO){
			LCD.drawString(s.substring(i,(i+LINE_LENGTH)>s.length()?s.length():(i+LINE_LENGTH)), 0, j);
			j++;
			i+=LINE_LENGTH;
		}
	}
}
