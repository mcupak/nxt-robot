package com.lego.nxt;

import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.Sound;

/**
 * Instruction interpreter, created after Learner.
 * 
 * Every instruction is normally a 2-Color number. Although this is not
 * mandatory as it is possible to have variable length instructions.
 * 
 * Instruction set:
 *  - 0 - 0 : go forward
 *      - 1 : go forward left
 *      - 2 : go forward right
 *      - 3 : repeat 2x
 *  - 1 - 0 : go back
 *      - 1 : go back left
 *      - 2 : go back right
 *      - 3 : repeat 5x
 *  - 2 - X : play tone X * 100 Hz
 *  - 3 - X : delay time X * 100 ms
 * 
 * @author Dejvino
 * 
 */
public class Executor
{
	/**
	 * The time spent in one instruction.
	 */
	public static final int INSTRUCTION_TIME = 1000;
	
	public static final int MOVING_SPEED = 40;
	private List<Color> instructions;

	private MotorController motors;
	
	public Executor(List<Color> instructions) {
		if (instructions == null) {
			throw new NullPointerException("instructions");
		}
		this.instructions = instructions;
	}

	public void run()
	{
		// load commonly used instances
		motors = MotorController.getInstance();
		
		// init the motors
		motors.setSpeed(MOVING_SPEED);
		
		for (int i = 0; i < instructions.size(); i++) {
			try {
				i = performInstruction(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
				DisplayController.print("Instruction #"+i+" was interrupted.");
				Button.ENTER.waitForPressAndRelease();
			}
		}
	}
	
	/**
	 * Performs a single instruction on the position _i_.
	 * The return value is the new value of _i_. It is possible to perform
	 * cycles this way.
	 * 
	 * @param i Program Counter.
	 * @return New Program Counter.
	 */
	private int performInstruction(int i) throws InterruptedException
	{
		if (instructions.size() <= i+1) {
			throw new RuntimeException("(PI) Out of program bounds: "
					+ i + "/" + instructions.size());
		}
		// fetch instruction codes
		int val1 = instructions.get(i).getValue();
		int val2 = instructions.get(i+1).getValue();
		
		// perform instruction
		i = this.performInstruction(i, val1, val2);
		
		// return new Program Counter
		return i+1;
	}
	
	/**
	 * Performs a single instruction based on the already fetched codes.
	 * 
	 * @param i Program Counter
	 * @param val1 Code 1
	 * @param val2 Code 2
	 * @return New Program Counter
	 * @throws InterruptedException
	 */
	private int performInstruction(int i, int val1, int val2) throws InterruptedException
	{
		switch (val1) {
			case 0: // GO FORWARD / REPEAT 2
				switch (val2) {
					case 0: this.instGoForward(); slai(i); break;
					case 1: this.instGoForwardLeft(); slai(i); break;
					case 2: this.instGoForwardRight(); slai(i); break;
					case 3: this.instRepeat2Times(); break;
					default:
						throw new RuntimeException("Invalid instruction " + val1 +":"+ val2);
				}
				break;
			case 1: // GO BACK / REPEAT 5
				switch (val2) {
					case 0: this.instGoBack(); slai(i); break;
					case 1: this.instGoBackLeft(); slai(i); break;
					case 2: this.instGoBackRight(); slai(i); break;
					case 3: this.instRepeat5Times(); break;
					default:
						throw new RuntimeException("Invalid instruction " + val1 +":"+ val2);
				}
				break;
			case 2: // PLAY X
				Sound.playTone((val2 + 1) * 100, INSTRUCTION_TIME);
				slai(i); 
				break;
			case 3: // DELAY X
				Thread.sleep((val2 + 1) * INSTRUCTION_TIME);
				slai(i); 
				break;
			default:
				throw new RuntimeException("Invalid instruction " + val1);
		}
		
		// return new Program Counter
		return i;
	}
	
	//==========================================================================
	
	private int slaiI;
	private int slai1;
	private int slai2;
	
	/**
	 * SLAI = Save Last Action Instruction.
	 * 
	 * Saves the last action instruction for Repeat_X_Times instructions.
	 * 
	 * @param i Program Counter
	 */
	private void slai(int i)
	{
		if (instructions.size() <= i+1) {
			throw new IllegalArgumentException("(SLAI) Out of program bounds: "
					+ i + "/" + instructions.size());
		}
		slaiI = i;
		slai1 = instructions.get(i).getValue();
		slai2 = instructions.get(i+1).getValue();	
	}
	
	//==========================================================================
	
	private void instGoForward() throws InterruptedException
	{
		motors.setDirection(0);
		motors.forward();
		Thread.sleep(INSTRUCTION_TIME);
		motors.stop();
	}
	
	private void instGoForwardLeft() throws InterruptedException
	{
		motors.setDirection(-2);
		motors.forward();
		Thread.sleep(INSTRUCTION_TIME);
		motors.stop();
	}
	
	private void instGoForwardRight() throws InterruptedException
	{
		motors.setDirection(2);
		motors.forward();
		Thread.sleep(INSTRUCTION_TIME);
		motors.stop();
	}
	
	private void instGoBack() throws InterruptedException
	{
		motors.setDirection(0);
		motors.backward();
		Thread.sleep(INSTRUCTION_TIME);
		motors.stop();
	}
	
	private void instGoBackLeft() throws InterruptedException
	{
		motors.setDirection(-2);
		motors.backward();
		Thread.sleep(INSTRUCTION_TIME);
		motors.stop();
	}
	
	private void instGoBackRight() throws InterruptedException
	{
		motors.setDirection(2);
		motors.backward();
		Thread.sleep(INSTRUCTION_TIME);
		motors.stop();
	}
	
	private void instRepeat2Times() throws InterruptedException
	{
		this.performInstruction(slaiI, slai1, slai2);
		this.performInstruction(slaiI, slai1, slai2);
	}
	
	private void instRepeat5Times() throws InterruptedException
	{
		instRepeat2Times();
		this.performInstruction(slaiI, slai1, slai2);
		instRepeat2Times();
	}
}
