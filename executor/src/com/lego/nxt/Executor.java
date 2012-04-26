package com.lego.nxt;

import java.util.List;

/**
 * Instruction interpreter, created after Learner.
 * 
 * Instructions: - B - start/end - DW - forward right - DG - forward straight -
 * DB - forward left - GW - backward right - GD - backward straight - GB -
 * backward left
 * 
 * (B - black, D - dark grey, G - grey, W - white)
 * 
 * @author mcupak
 * 
 */
public class Executor {

	private List<Color> instructions;

	public Executor(List<Color> instructions) {
		this.instructions = instructions;
	}

	public void run() {
		// TODO: execute instructions
	}
}
