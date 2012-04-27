package com.lego.nxt;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.Sound;

/**
 * Thread controlling the data-line light. It reads data from the light sensor
 * and decodes them into a binary array. This array is later interpreted
 * by an Executor.
 * 
 * Reading has a timeout value which is periodically check and when there is
 * a long period of "no-data" state (no signal change), it terminates.
 * 
 * Because the light sensor returns values in the form of a continuous function,
 * we are unable to use simple data encodings such as: BLACK = 1, GREY = 2, ...
 * 
 * We therefore use a "peak value encoding". The base color is WHITE and it
 * bears no data value. It serves as a synchronization element.
 * Base color is interleaved by other two colors: BLACK and GREY.
 * We measure the darkest color in every interval of the form WHITE|XXX|WHITE
 * and based on the result we obtain a single value.
 * This encoding therefore delivers 1 bit of information for every two signal
 * elements (changes).
 * 
 * @author mcupak
 * @author Dejvino
 * 
 */
public class Reader extends Thread
{
	private LightController light;
	private List<Color> instructions = new ArrayList<Color>();

	/**
	 * Timeout in ms before the reading is terminated when there is a period
	 * of "no-data" state.
	 */
	public static final int TIMEOUT_LENGTH = 5000;
	
	/**
	 * Time of the next timeout.
	 */
	private long timeout;
	
	public Reader(LightController light) {
		super();
		this.light = light;
	}

	private boolean readEnabled;
	
	public synchronized void setReadEnabled(boolean readEnabled) {
		this.readEnabled = readEnabled;
	}
	
	public synchronized boolean isReadEnabled() {
		return readEnabled;
	}
	
	@Override
	public void run()
	{
		Color current, lastMax;

		BitSet bits = new BitSet();
		int bitNumber = 0;
		int testnum = 0;
		
		// ignore the beginning (so we start with white color)
		try {
			for (int i = 0; i < 15; i++) {
				Thread.sleep(10);
				light.getColor();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DisplayController.print("Interrupted 435");
			Button.ENTER.waitForPressAndRelease();
		}
		/**/
					
		this.resetTimeout();
		
		// monitor till timeout
		lastMax=current=Color.WHITE;
		do {
			// read color
			if (isReadEnabled()) {
				current = light.getColor();
			}
			
			// fix collor
			if (current == Color.GREY) {
				current = Color.WHITE;
			}
			
			//DisplayController.print("Scanned color: "+current);
			Sound.playTone(current == Color.WHITE ? 100 : (
					current == Color.DARK ? 300 : 500) , 10);
			
			if (lastMax == current && current == Color.WHITE) {
				// waiting for a new signal
			} else {
				// we are scanning a signal...
				if (current == Color.WHITE) {
					// scanning ended - save the new value
					bits.set(bitNumber, lastMax == Color.BLACK);
					bitNumber++;
					// show current data
					testnum *= 2;
					testnum += lastMax == Color.BLACK ? 1 : 0;
					DisplayController.print("Number: "+testnum);
					// signal the value
					//Sound.playTone(lastMax == Color.BLACK ? 420 : 220, 300);
					//DisplayController.print("Scanned color "+bits.length()+" = " + lastMax);
					// reset the max value
					lastMax = Color.WHITE;
					// reset the timeout
					this.resetTimeout();
				} else if (current.isDarker(lastMax)) {
					// it is the new max value
					lastMax = current;
				}
			}
			
			// sleep a bit
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				DisplayController.print("Interrupted 985");
				Button.ENTER.waitForPressAndRelease();
			}
			/**/
			
			//Thread.yield();
			
		} while (!isTimeout());
		
		Sound.playTone(530, 400);
		
		// decode bits to colors
		for (int i = 0; i < bitNumber; i += 2) {
			//
			if (i+1 >= bits.size() || i+1 >= bitNumber) {
				//wrong bit count
				DisplayController.print("(=== ERROR ===) Wrong bit count: " + bitNumber);
				Sound.playTone(230, 1000);
				return;
			}
			int value = ((bits.get(i) ? 1 : 0) << 1) + (bits.get(i+1) ? 1 : 0); 
			instructions.add(Color.getByValue(value));
		}
		Operator.setInstructions(instructions);
	}
	
	/**
	 * Reset the timeout value. This moves the time of the next timeout.
	 * 
	 * This method should be called as an initialization and then every time
	 * there is a signal change.
	 * When this method is not called less then every TIMEOUT_LENGTH ms,
	 * the reading thread terminates.
	 */
	private void resetTimeout()
	{
		this.timeout = System.currentTimeMillis() + TIMEOUT_LENGTH;
	}
	
	/**
	 * Has a timeout occurred?
	 * 
	 * @return
	 */
	private boolean isTimeout()
	{
		return System.currentTimeMillis() >= this.timeout;
	}
}
