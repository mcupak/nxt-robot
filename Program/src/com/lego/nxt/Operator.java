package com.lego.nxt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lejos.nxt.Button;

public class Operator {

	private static List<Color> instructions = new LinkedList<Color>();

	/**
	 * Main controller of the robot.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// wait for start
		DisplayController.print("Press ENTER to start...");
		Learner learner = new Learner();
		Button.waitForAnyPress();
		
		learner.calibrate();
		learner.run();
		DisplayController.print(Operator.getInstructionsAsString() + "     Pres ENTER to execute.");
		Button.ENTER.waitForPressAndRelease();
		
		Executor executor = new Executor(instructions);
		
		try {
			executor.run();
		} catch (Exception e) {
			DisplayController.print("Program exception: " + e.getMessage());
			Button.ENTER.waitForPressAndRelease();
		}
		/**/
		
		DisplayController.print("Program finished" + "Pres ENTER to end.");
		Button.ENTER.waitForPressAndRelease();
		
		System.exit(0);
	}

	public static List<Color> getInstructions() {
		return instructions;
	}

	public static void setInstructions(List<Color> instructions) {
		Operator.instructions = instructions;
	}
	
	public static String getInstructionsAsString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Instructions:   ");
		for (int i = 0; i < instructions.size(); i++) {
			sb.append(instructions.get(i).getValue());
		}
		return sb.toString();
	}
}
