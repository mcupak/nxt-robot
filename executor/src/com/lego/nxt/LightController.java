package com.lego.nxt;

import lejos.nxt.ADSensorPort;
import lejos.nxt.LightSensor;

/**
 * Thread controlling a light.
 * 
 * @author mcupak
 * 
 */
public class LightController extends Thread {
	private LightSensor ls;
	private int reading = 0;
	private int thresholdBlack = 40;
	private int thresholdGrey = 50;
	private int thresholdDark = 45;

	public LightController(ADSensorPort port) {
		super();
		ls = new LightSensor(port);
		// use the light sensor as a reflection sensor
		ls.setFloodlight(true);
	}

	@Override
	public void interrupt() {
		ls.setFloodlight(false);
		super.interrupt();
	}

	public void run() {
		do {
			reading = ls.readValue();
		} while (!isInterrupted());
	}

	public int getThresholdBlack() {
		return thresholdBlack;
	}

	public void setThresholdBlack(int threshold) {
		this.thresholdBlack = threshold;
	}

	public int getThresholdGrey() {
		return thresholdGrey;
	}

	public void setThresholdGrey(int threshold) {
		this.thresholdGrey = threshold;
	}

	public int getThresholdDark() {
		return thresholdDark;
	}

	public void setThresholdDark(int thresholdDark) {
		this.thresholdDark = thresholdDark;
	}

	public int getReading() {
		return reading;
	}

	public Color getColor() {
		int currentValue = reading;
		if (currentValue < thresholdBlack) {
			return Color.BLACK;
		} else if (currentValue < thresholdDark) {
			return Color.DARK;
		} else if (currentValue < thresholdGrey) {
			return Color.GREY;
		}
		return Color.WHITE;
	}
}
