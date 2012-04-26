package com.lego.nxt;

import lejos.nxt.Sound;
import lejos.robotics.subsumption.Behavior;

public class SoundController {

	public static void play(int tone) {
		Sound.setVolume(100);
		switch (tone) {
		case 1:
			Sound.twoBeeps();
			break;
		default:
			Sound.beep();
			break;
		}
	}
}
