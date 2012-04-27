package com.lego.nxt;

import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

/**
 * Learner program state.
 * 
 * Corresponds to the phase when the robot is following the guiding line and
 * reading instructions from the data line.
 * 
 * This is the main controlling thread responsible for coordinating all the
 * motors and light sensors. It runs a Reader thread in parallel to read and
 * decode the instructions.
 * 
 * @author mcupak
 * @author Dejvino
 *
 */
public class Learner {
	// controller for motors
	private MotorController mc;
	// light
	private LightController navigationLight;
	private LightController readingLight;
	// time to find something useful on the ground in ms
	public static final int TIMEOUT_FIND = 5000;

	public static final boolean DATALINE_ON_LEFT = true;
	
	public Learner() {
		mc = MotorController.getInstance();
	}

	public void calibrate() {
		//int black = 37, grey = 52, white = 56, dark = 42;
		// black 38, dark 45, grey 54, white 58
		int black = 37, dark = 45, grey = 54, white = 58;

		// prepare navigation light
		navigationLight = new LightController(SensorPort.S4);
		navigationLight.start();
		
		/*/ calibrate light
		DisplayController
				.print("Calibrating RIGHT light. Put it on the BLACK color and press ENTER to confirm.");
		Button.ENTER.waitForPressAndRelease();
		black = navigationLight.getReading();
		DisplayController
				.print("Result: "+black+"\nCalibrating RIGHT light. Put it on the DARK GREY color and press ENTER to confirm.");
		Button.ENTER.waitForPressAndRelease();
		dark = navigationLight.getReading();
		DisplayController
				.print("Result: "+dark+"\nCalibrating RIGHT light. Put it on the GREY color and press ENTER to confirm.");
		Button.ENTER.waitForPressAndRelease();
		grey = navigationLight.getReading();
		DisplayController
				.print("Result: "+grey+"\nCalibrating RIGHT light. Put it on the WHITE color and press ENTER to confirm.");
		Button.ENTER.waitForPressAndRelease();
		white = navigationLight.getReading();
		DisplayController
				.print("Result: "+white+"\nPress ENTER to confirm.");
		Button.ENTER.waitForPressAndRelease();
		/**/
		
		// set threshold to average
		navigationLight.setThresholdGrey((white + grey) / 2);
		navigationLight.setThresholdDark((grey + dark) / 2);
		navigationLight.setThresholdBlack((black + dark) / 2);

		// calibrate reading light
		white += 2;
		grey += 3;
		dark += 7;
		black += 8;
		readingLight = new LightController(SensorPort.S3);
		readingLight.start();
		readingLight.setThresholdGrey((white + grey) / 2);
		readingLight.setThresholdDark((grey + dark) / 2);
		readingLight.setThresholdBlack((black + dark) / 2);
	}

	/**
	 * Execute 2 phases of the movement - find the line, follow the line.
	 */
	public void run() {
		//if (findLine()) {
			SoundController.play(0);
			follow();
		//}
		SoundController.play(1);
	}

	/**
	 * Find the line to sync with.
	 * 
	 * @return true if the line was found, false if the timeout was hit
	 */
	public Boolean findLine() {
		DisplayController.print("Looking for line.. Press ESC to stop.");
		mc.setDirection(0);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// start counting towards timeout
		long end = System.currentTimeMillis() + TIMEOUT_FIND;
		Color color = Color.WHITE;
		long time = System.currentTimeMillis();
		// start moving
		mc.forward();
		// busy waiting for color/timeout
		while ((color != Color.BLACK) && (time < end)
				&& (!Button.ESCAPE.isPressed())) {
			color = navigationLight.getColor();
			time = System.currentTimeMillis();
		}
		mc.stop();
		return (color == Color.BLACK) ? true : false;
	}

	/**
	 * Line follower and reader.
	 * 
	 * The actual code follows the guiding line and waits for the Reader to
	 * terminate, which indicates that the learning process finished.
	 * 
	 * @return true when finished by reading the end mark, false in case of
	 *         timeout
	 */
	public Boolean follow() {
		DisplayController.print("Reading...");
		
		Color color = Color.BLACK;
		int colorReading = 0;
		int direction = 0;

		Reader reader = new Reader(readingLight);
		reader.start();

		// ignore the beginning (so we start with a real color)
		try {
			for (int i = 0; i < 10; i++) {
				Thread.sleep(10);
				navigationLight.getColor();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DisplayController.print("Interrupted 135");
			Button.ENTER.waitForPressAndRelease();
		}
		/**/
		
		mc.forward();
		reader.setReadEnabled(true);
		while ((!Button.ESCAPE.isPressed()) && (reader.isAlive())) {
			color = navigationLight.getColor();
			colorReading = navigationLight.getReading();
			boolean changeDir = false;
			switch (color) {
			case BLACK:
				if (direction < 1) {
					direction = 2;
					changeDir = true;
				}
				break;
			case DARK:
			case GREY:
				if (direction != 0) {
					direction = 0;
					changeDir = true;
				}
				break;
			case WHITE:
				if (direction > -1) {
					direction = -2;
					changeDir = true;
				}
				break;
			default:
			}
			if (changeDir) {
				//DisplayController.print("DIR: "+direction+"\nColor: "+color+" ("+colorReading+")");
				if (mc.isMoving()) {
					mc.stop();
					reader.setReadEnabled(false);
				}
				if (DATALINE_ON_LEFT) {
					mc.setDirection(-direction);
				} else {
					mc.setDirection(direction);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
					DisplayController.print("Interrupted 567");
					Button.ENTER.waitForPressAndRelease();
				}
				mc.forward();
				reader.setReadEnabled(true);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				DisplayController.print("Interrupted 569");
				Button.ENTER.waitForPressAndRelease();
			}
		}

		// clean up
		mc.stop();
		mc.setDirection(0);

		//DisplayController.print("Learner finished.");
		//Button.ENTER.waitForPressAndRelease();
		//System.exit(0);
		
		navigationLight.interrupt();
		readingLight.interrupt();

		return true;
	}
}
