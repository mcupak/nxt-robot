package com.lego.nxt;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Button;

public class Operator {

	private static List<Color> instructions;

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
		DisplayController.print(instructions.toString());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Executor executor = new Executor(instructions);
	}

	public static List<Color> getInstructions() {
		return instructions;
	}

	public static void setInstructions(List<Color> instructions) {
		Operator.instructions = instructions;
	}
}
