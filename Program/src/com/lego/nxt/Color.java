package com.lego.nxt;

/**
 * Available colors of the pad as recognized by the robot.
 * 
 * Every color has an integer number assigned. Two colors can be compared using
 * this value (compare the darkness of the two colors).
 * 
 * @author mcupak
 * @author Dejvino
 * 
 */
public enum Color
{
	WHITE (0),
	GREY (1),
	DARK (2),
	BLACK (3);
	
	private int value;
	
	public int getValue() {
		return value;
	}
	
	private Color(int value)
	{
		this.value = value;
	}
	
	public boolean isDarker(Color col)
	{
		return this.value > col.value;  
	}
	
	public static Color getByValue(int value)
	{
		switch (value) {
		case 0: return WHITE;
		case 1: return GREY;
		case 2: return DARK;
		case 3: return BLACK;
		default:
			throw new IllegalArgumentException("Unknown color: " + value);
		}
	}
}
