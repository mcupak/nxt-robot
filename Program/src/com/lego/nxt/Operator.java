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
		DisplayController.print("Press any key to start...");
		Learner learner = new Learner();
		Button.waitForAnyPress();
		
		learner.calibrate();
		learner.run();
		DisplayController.print(Operator.getInstructionsAsString());
		Button.ENTER.waitForPressAndRelease();
		
		/*try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Executor executor = new Executor(instructions);
		/**/
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
		sb.append("Instructions: ");
		for (int i = 0; i < instructions.size(); i++) {
			sb.append(instructions.get(i).getValue());
		}
		return sb.toString();
	}
}
