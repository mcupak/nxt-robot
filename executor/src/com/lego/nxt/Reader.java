package com.lego.nxt;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread controlling a light.
 * 
 * @author mcupak
 * 
 */
public class Reader extends Thread {
	private LightController light;
	private List<Color> instructions = new ArrayList<Color>();

	public Reader(LightController light) {
		super();
		this.light = light;
	}

	@Override
	public void run() {
		Color current, last;

		// wait till start
		do {
			current = light.getColor();
		} while (current != Color.BLACK);

		// monitor till end
		last=current;
		do {
			while (current==last){
				current = light.getColor();
			}
			// add new colors
			instructions.add(current);
			last=current;
		} while (current != Color.BLACK);
		Operator.setInstructions(instructions);
	}
}
